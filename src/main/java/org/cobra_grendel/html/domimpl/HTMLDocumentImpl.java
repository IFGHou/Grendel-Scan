/*
 * GNU LESSER GENERAL PUBLIC LICENSE Copyright (C) 2006 The Lobo Project
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 * 
 * Contact info: xamjadmin@users.sourceforge.net
 */
/*
 * Created on Sep 3, 2005
 */
package org.cobra_grendel.html.domimpl;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cobra_grendel.html.HtmlRendererContext;
import org.cobra_grendel.html.HttpRequest;
import org.cobra_grendel.html.ReadyStateChangeListener;
import org.cobra_grendel.html.UserAgentContext;
import org.cobra_grendel.html.io.WritableLineReader;
import org.cobra_grendel.html.js.Executor;
import org.cobra_grendel.html.js.Location;
import org.cobra_grendel.html.js.Window;
import org.cobra_grendel.html.parser.HtmlParser;
import org.cobra_grendel.util.Domains;
import org.cobra_grendel.util.Urls;
import org.cobra_grendel.util.WeakValueHashMap;
import org.cobra_grendel.util.io.EmptyReader;
import org.mozilla.javascript.Function;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.html2.HTMLCollection;
import org.w3c.dom.html2.HTMLDocument;
import org.w3c.dom.html2.HTMLElement;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public class HTMLDocumentImpl extends NodeImpl implements HTMLDocument
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	private class AnchorFilter implements NodeFilter
	{
		@Override
		public boolean accept(Node node)
		{
			String nodeName = node.getNodeName();
			return "A".equalsIgnoreCase(nodeName) || "ANCHOR".equalsIgnoreCase(nodeName);
		}
	}
	
	private class AppletFilter implements NodeFilter
	{
		@Override
		public boolean accept(Node node)
		{
			// TODO: "OBJECT" elements that are applets too.
			return "APPLET".equalsIgnoreCase(node.getNodeName());
		}
	}
	
	private class ElementFilter implements NodeFilter
	{
		public ElementFilter()
		{
		}
		
		@Override
		public boolean accept(Node node)
		{
			return node instanceof Element;
		}
	}
	
	private class ElementNameFilter implements NodeFilter
	{
		private final String name;
		
		public ElementNameFilter(String name)
		{
			this.name = name;
		}
		
		@Override
		public boolean accept(Node node)
		{
			// TODO: Case sensitive?
			return (node instanceof Element) && name.equals(((Element) node).getAttribute("name"));
		}
	}
	
	private class FormFilter implements NodeFilter
	{
		@Override
		public boolean accept(Node node)
		{
			String nodeName = node.getNodeName();
			return "FORM".equalsIgnoreCase(nodeName);
		}
	}
	
	private class FrameFilter implements NodeFilter
	{
		@Override
		public boolean accept(Node node)
		{
			return (node instanceof org.w3c.dom.html2.HTMLFrameElement)
			        || (node instanceof org.w3c.dom.html2.HTMLIFrameElement);
		}
	}
	
	private class ImageFilter implements NodeFilter
	{
		@Override
		public boolean accept(Node node)
		{
			return "IMG".equalsIgnoreCase(node.getNodeName());
		}
	}
	
	private static class ImageInfo
	{
		// Access to this class is synchronized on imageInfos.
		public ImageEvent imageEvent;
		public boolean loaded;
		private ArrayList listeners = new ArrayList(1);
		
		void addListener(ImageListener listener)
		{
			listeners.add(listener);
		}
		
		ImageListener[] getListeners()
		{
			return (ImageListener[]) listeners.toArray(ImageListener.EMPTY_ARRAY);
		}
	}
	
	private class LinkFilter implements NodeFilter
	{
		@Override
		public boolean accept(Node node)
		{
			String nodeName = node.getNodeName();
			return "LINK".equalsIgnoreCase(nodeName);
		}
	}
	
	/**
	 * Tag class that also notifies document when text is written to an open
	 * buffer.
	 * 
	 * @author J. H. S.
	 */
	private class LocalWritableLineReader extends WritableLineReader
	{
		/**
		 * @param reader
		 */
		public LocalWritableLineReader(LineNumberReader reader)
		{
			super(reader);
		}
		
		/**
		 * @param reader
		 */
		public LocalWritableLineReader(Reader reader)
		{
			super(reader);
		}
		
		@Override
		public void write(String text) throws IOException
		{
			super.write(text);
			if ("".equals(text))
			{
				openBufferChanged(text);
			}
		}
	}
	
	private class TagNameFilter implements NodeFilter
	{
		private final String name;
		
		public TagNameFilter(String name)
		{
			this.name = name;
		}
		
		@Override
		public boolean accept(Node node)
		{
			if (!(node instanceof Element))
			{
				return false;
			}
			String n = name;
			return n.equalsIgnoreCase(((Element) node).getTagName());
		}
	}
	
	private static final Logger logger = Logger.getLogger(HTMLDocumentImpl.class.getName());
	
	private HTMLCollection anchors;
	
	private HTMLCollection applets;
	
	private volatile String baseURI;
	
	private final ImageEvent BLANK_IMAGE_EVENT = new ImageEvent(this, null);
	
	private HTMLElement body;
	
	private String defaultTarget;
	
	private DocumentType doctype;
	
	//
	// private final Collection styleSheets = new LinkedList();
	//	
	// final void addStyleSheet(CSSStyleSheet ss) {
	// synchronized(this.treeLock) {
	// this.styleSheets.add(ss);
	// // this.styleSheetAggregator = null;
	// // Need to invalidate all children up to
	// // this point.
	// // this.forgetRenderState();
	// //TODO: this might be ineffcient.
	// ArrayList nl = this.nodeList;
	// if(nl != null) {
	// Iterator i = nl.iterator();
	// while(i.hasNext()) {
	// Object node = i.next();
	// if(node instanceof HTMLElementImpl) {
	// // ((HTMLElementImpl) node).forgetStyle(true);
	// }
	// }
	// }
	// }
	// this.allInvalidated();
	// }
	//	
	/*
	 * private StyleSheetAggregator styleSheetAggregator = null;
	 * 
	 * final StyleSheetAggregator getStyleSheetAggregator() {
	 * synchronized(this.treeLock) { StyleSheetAggregator ssa =
	 * this.styleSheetAggregator; if(ssa == null) { ssa = new
	 * StyleSheetAggregator(this); try { ssa.addStyleSheets(this.styleSheets); }
	 * catch(MalformedURLException mfu) {
	 * logger.log(Level.WARNING,"getStyleSheetAggregator()", mfu); }
	 * this.styleSheetAggregator = ssa; } return ssa; } }
	 */
	private final ArrayList documentNotificationListeners = new ArrayList(1);
	
	private String documentURI;
	
	private java.net.URL documentURL;
	
	private String domain;
	
	private DOMConfiguration domConfig;
	
	private DOMImplementation domImplementation;
	
	private final Map elementsById = new WeakValueHashMap();
	
	private final Map elementsByName = new HashMap(0);
	
	private final ElementFactory factory;
	
	private HTMLCollection forms;
	
	private HTMLCollection frames;
	
	private final Map imageInfos = new HashMap(4);
	
	private HTMLCollection images;
	
	private String inputEncoding;
	
	private HTMLCollection links;
	
	private Function onloadHandler;
	
	private final HtmlRendererContext rcontext;
	private WritableLineReader reader;
	private String referrer;
	private boolean strictErrorChecking = true;
	private String title;
	private final UserAgentContext ucontext;
	
	private final Window window;
	
	private String xmlEncoding;
	
	private boolean xmlStandalone;
	
	private String xmlVersion = null;
	
	private String xssToken;
	
	public HTMLDocumentImpl(HtmlRendererContext rcontext, int transactionId)
	{
		this(rcontext.getUserAgentContext(), rcontext, null, null, transactionId);
	}
	
	public HTMLDocumentImpl(UserAgentContext ucontext, int transactionId)
	{
		this(ucontext, null, null, null, transactionId);
	}
	
	public HTMLDocumentImpl(final UserAgentContext ucontext, final HtmlRendererContext rcontext,
	        WritableLineReader reader, String documentURI, int transactionId)
	{
		super(transactionId);
		factory = ElementFactory.getInstance();
		this.rcontext = rcontext;
		this.ucontext = ucontext;
		this.reader = reader;
		this.documentURI = documentURI;
		try
		{
			java.net.URL docURL = new java.net.URL(documentURI);
			SecurityManager sm = System.getSecurityManager();
			if (sm != null)
			{
				// Do not allow creation of HTMLDocumentImpl if there's
				// no permission to connect to the host of the URL.
				// This is so that cookies cannot be written arbitrarily
				// with setCookie() method.
				sm.checkPermission(new java.net.SocketPermission(docURL.getHost(), "connect"));
			}
			documentURL = docURL;
			domain = docURL.getHost();
		}
		catch (java.net.MalformedURLException mfu)
		{
			logger.warning("HTMLDocumentImpl(): Document URI [" + documentURI + "] is malformed.");
		}
		document = this;
		// Get Window object
		Window window;
		if (rcontext != null)
		{
			window = Window.getWindow(rcontext);
		}
		else
		{
			// Plain parsers may use Javascript too.
			window = new Window(null, ucontext);
		}
		// Window must be retained or it will be garbage collected.
		this.window = window;
		window.setDocument(this);
		// Set up Javascript scope
		setUserData(Executor.SCOPE_KEY, window.getWindowScope(), null);
	}
	
	public void addDocumentNotificationListener(DocumentNotificationListener listener)
	{
		ArrayList listenersList = documentNotificationListeners;
		synchronized (listenersList)
		{
			listenersList.add(listener);
		}
	}
	
	@Override
	public Node adoptNode(Node source) throws DOMException
	{
		if (source instanceof NodeImpl)
		{
			NodeImpl node = (NodeImpl) source;
			node.setOwnerDocument(this, true);
			return node;
		}
		else
		{
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Invalid Node implementation");
		}
	}
	
	public void allInvalidated()
	{
		ArrayList listenersList = documentNotificationListeners;
		int size;
		synchronized (listenersList)
		{
			size = listenersList.size();
		}
		// Traverse list outside synchronized block.
		// (Shouldn't call listener methods in synchronized block.
		// Deadlock is possible). But assume list could have
		// been changed.
		for (int i = 0; i < size; i++)
		{
			try
			{
				DocumentNotificationListener dnl = (DocumentNotificationListener) listenersList.get(i);
				dnl.allInvalidated();
			}
			catch (IndexOutOfBoundsException iob)
			{
				// ignore
			}
		}
	}
	
	@Override
	public void close()
	{
		synchronized (treeLock)
		{
			if (reader instanceof LocalWritableLineReader)
			{
				try
				{
					reader.close();
				}
				catch (java.io.IOException ioe)
				{
					// ignore
				}
				reader = null;
			}
			else
			{
				// do nothing - could be parsing document off the web.
			}
			// TODO: cause it to render
		}
	}
	
	@Override
	public Attr createAttribute(String name) throws DOMException
	{
		return new AttrImpl(name, transactionId);
	}
	
	@Override
	public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException
	{
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "HTML document");
	}
	
	@Override
	public CDATASection createCDATASection(String data) throws DOMException
	{
		CDataSectionImpl node = new CDataSectionImpl(data, transactionId);
		node.setOwnerDocument(this);
		return node;
	}
	
	@Override
	public Comment createComment(String data)
	{
		CommentImpl node = new CommentImpl(data, transactionId);
		node.setOwnerDocument(this);
		return node;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.Document#createDocumentFragment()
	 */
	@Override
	public DocumentFragment createDocumentFragment()
	{
		// TODO: According to documentation, when a document
		// fragment is added to a node, its children are added,
		// not itself.
		DocumentFragmentImpl node = new DocumentFragmentImpl(transactionId);
		node.setOwnerDocument(this);
		return node;
	}
	
	@Override
	public Element createElement(String tagName) throws DOMException
	{
		return factory.createElement(this, tagName, transactionId);
	}
	
	@Override
	public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException
	{
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "HTML document");
	}
	
	@Override
	public EntityReference createEntityReference(String name) throws DOMException
	{
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "HTML document");
	}
	
	@Override
	public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException 
	{
		HTMLProcessingInstruction node = new HTMLProcessingInstruction(target, data, transactionId);
		node.setOwnerDocument(this);
		return node;
	}
	
	@Override
	public Text createTextNode(String data)
	{
		TextImpl node = new TextImpl(data, transactionId);
		node.setOwnerDocument(this);
		return node;
	}
	
	public void externalScriptLoading(NodeImpl node)
	{
		ArrayList listenersList = documentNotificationListeners;
		int size;
		synchronized (listenersList)
		{
			size = listenersList.size();
		}
		// Traverse list outside synchronized block.
		// (Shouldn't call listener methods in synchronized block.
		// Deadlock is possible). But assume list could have
		// been changed.
		for (int i = 0; i < size; i++)
		{
			try
			{
				DocumentNotificationListener dnl = (DocumentNotificationListener) listenersList.get(i);
				dnl.externalScriptLoading(node);
			}
			catch (IndexOutOfBoundsException iob)
			{
				// ignore
			}
		}
	}
	
	@Override
	public HTMLCollection getAnchors()
	{
		synchronized (this)
		{
			if (anchors == null)
			{
				anchors = new FilteredHTMLCollectionImpl(this, elementsById, new AnchorFilter(), treeLock, transactionId);
			}
			return anchors;
		}
	}
	
	@Override
	public HTMLCollection getApplets()
	{
		synchronized (this)
		{
			if (applets == null)
			{
				// TODO: Should include OBJECTs that are applets?
				applets = new FilteredHTMLCollectionImpl(this, elementsById, new AppletFilter(), treeLock, transactionId);
			}
			return applets;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xamjwg.html.domimpl.NodeImpl#getbaseURI()
	 */
	@Override
	public String getBaseURI()
	{
		String buri = baseURI;
		return buri == null ? documentURI : buri;
	}
	
	@Override
	public HTMLElement getBody()
	{
		synchronized (this)
		{
			return body;
		}
	}
	
	@Override
	public String getCookie()
	{
		SecurityManager sm = System.getSecurityManager();
		if (sm != null)
		{
			return (String) AccessController.doPrivileged(new PrivilegedAction()
			{
				// Justification: A caller (e.g. Google Analytics script)
				// might want to get cookies from the parent document.
				// If the caller has access to the document, it appears
				// they should be able to get cookies on that document.
				// Note that this Document instance cannot be created
				// with an arbitrary URL.
				@Override
				public Object run()
				{
					return ucontext.getCookie(documentURL);
				}
			});
		}
		else
		{
			return ucontext.getCookie(documentURL);
		}
	}
	
	public String getDefaultTarget()
	{
		return defaultTarget;
	}
	
	@Override
	public DocumentType getDoctype()
	{
		return doctype;
	}
	
	@Override
	public Element getDocumentElement()
	{
		synchronized (treeLock)
		{
			ArrayList nl = nodeList;
			if (nl != null)
			{
				Iterator i = nl.iterator();
				while (i.hasNext())
				{
					Object node = i.next();
					if (node instanceof Element)
					{
						return (Element) node;
					}
				}
			}
			return null;
		}
	}
	
	@Override
	public String getDocumentURI()
	{
		return documentURI;
	}
	
	@Override
	public URL getDocumentURL()
	{
		// TODO: Security considerations?
		return documentURL;
	}
	
	@Override
	public String getDomain()
	{
		return domain;
	}
	
	@Override
	public DOMConfiguration getDomConfig()
	{
		synchronized (this)
		{
			if (domConfig == null)
			{
				domConfig = new DOMConfigurationImpl();
			}
			return domConfig;
		}
	}
	
	@Override
	public Element getElementById(String elementId)
	{
		Element element;
		synchronized (this)
		{
			element = (Element) elementsById.get(elementId);
		}
		return element;
	}
	
	/**
	 * Gets the collection of elements whose <code>name</code> attribute is
	 * <code>elementName</code>.
	 */
	@Override
	public NodeList getElementsByName(String elementName)
	{
		return getNodeList(new ElementNameFilter(elementName));
	}
	
	/**
	 * Gets all elements that match the given tag name.
	 * 
	 * @param tagname
	 *            The element tag name or an asterisk character (*) to match all
	 *            elements.
	 */
	@Override
	public NodeList getElementsByTagName(String tagname)
	{
		if ("*".equals(tagname))
		{
			return getNodeList(new ElementFilter());
		}
		else
		{
			return getNodeList(new TagNameFilter(tagname));
		}
	}
	
	@Override
	public NodeList getElementsByTagNameNS(String namespaceURI, String localName)
	{
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "HTML document");
	}
	
	@Override
	public HTMLCollection getForms()
	{
		synchronized (this)
		{
			if (forms == null)
			{
				forms = new FilteredHTMLCollectionImpl(this, elementsById, new FormFilter(), treeLock, transactionId);
			}
			return forms;
		}
	}
	
	public HTMLCollection getFrames()
	{
		synchronized (this)
		{
			if (frames == null)
			{
				frames = new FilteredHTMLCollectionImpl(this, elementsById, new FrameFilter(), treeLock, transactionId);
			}
			return frames;
		}
	}
	
	@Override
	public final URL getFullURL(String uri)
	{
		try
		{
			String baseURI = getBaseURI();
			URL documentURL = baseURI == null ? null : new URL(baseURI);
			return Urls.createURL(documentURL, uri);
		}
		catch (MalformedURLException mfu)
		{
			// Try agan, without the baseURI.
			try
			{
				return new URL(uri);
			}
			catch (MalformedURLException mfu2)
			{
				logger.log(Level.WARNING, "Unable to create URL for URI=[" + uri + "], with base=[" + getBaseURI()
				        + "].", mfu);
				return null;
			}
		}
	}
	
	@Override
	public final HtmlRendererContext getHtmlRendererContext()
	{
		return rcontext;
	}
	
	@Override
	public HTMLCollection getImages()
	{
		synchronized (this)
		{
			if (images == null)
			{
				images = new FilteredHTMLCollectionImpl(this, elementsById, new ImageFilter(), treeLock, transactionId);
			}
			return images;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.Document#getImplementation()
	 */
	@Override
	public DOMImplementation getImplementation()
	{
		synchronized (this)
		{
			if (domImplementation == null)
			{
				domImplementation = new DOMImplementationImpl(ucontext, transactionId);
			}
			return domImplementation;
		}
	}
	
	@Override
	public String getInputEncoding()
	{
		return inputEncoding;
	}
	
	@Override
	public HTMLCollection getLinks()
	{
		synchronized (this)
		{
			if (links == null)
			{
				links = new FilteredHTMLCollectionImpl(this, elementsById, new LinkFilter(), treeLock, transactionId);
			}
			return links;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xamjwg.html.domimpl.NodeImpl#getLocalName()
	 */
	@Override
	public String getLocalName()
	{
		// Always null for document
		return null;
	}
	
	public final Location getLocation()
	{
		return window.getLocation();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xamjwg.html.domimpl.NodeImpl#getNodeName()
	 */
	@Override
	public String getNodeName()
	{
		return "#document";
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xamjwg.html.domimpl.NodeImpl#getNodeType()
	 */
	@Override
	public short getNodeType()
	{
		return Node.DOCUMENT_NODE;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xamjwg.html.domimpl.NodeImpl#getNodeValue()
	 */
	@Override
	public String getNodeValue() throws DOMException
	{
		// Always null for document
		return null;
	}
	
	public Function getOnloadHandler()
	{
		return onloadHandler;
	}
	
	@Override
	public String getReferrer()
	{
		return referrer;
	}
	
	@Override
	public boolean getStrictErrorChecking()
	{
		return strictErrorChecking;
	}
	
	@Override
	public String getTextContent() throws DOMException
	{
		return null;
	}
	
	@Override
	public String getTitle()
	{
		return title;
	}
	
	@Override
	public String getURL()
	{
		return documentURI;
	}
	
	@Override
	public UserAgentContext getUserAgentContext()
	{
		return ucontext;
	}
	
	@Override
	public String getXmlEncoding()
	{
		return xmlEncoding;
	}
	
	@Override
	public boolean getXmlStandalone()
	{
		return xmlStandalone;
	}
	
	@Override
	public String getXmlVersion()
	{
		return xmlVersion;
	}
	
	public String getXssToken()
	{
		return xssToken;
	}
	
	@Override
	public Node importNode(Node importedNode, boolean deep) throws DOMException
	{
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented");
	}
	
	/**
	 * This is called when the node has changed, but it is unclear if it's a
	 * size change or a look change.
	 * 
	 * @param node
	 */
	public void invalidated(NodeImpl node)
	{
		ArrayList listenersList = documentNotificationListeners;
		int size;
		synchronized (listenersList)
		{
			size = listenersList.size();
		}
		// Traverse list outside synchronized block.
		// (Shouldn't call listener methods in synchronized block.
		// Deadlock is possible). But assume list could have
		// been changed.
		for (int i = 0; i < size; i++)
		{
			try
			{
				DocumentNotificationListener dnl = (DocumentNotificationListener) listenersList.get(i);
				dnl.invalidated(node);
			}
			catch (IndexOutOfBoundsException iob)
			{
				// ignore
			}
		}
	}
	
	/**
	 * Loads the document from the reader provided when it was constructed. It
	 * then closes the reader.
	 * 
	 * @throws IOException
	 * @throws SAXException
	 * @throws UnsupportedEncodingException
	 */
	public void load() throws IOException, SAXException, UnsupportedEncodingException
	{
		this.load(true);
	}
	
	public void load(boolean closeReader) throws IOException, SAXException, UnsupportedEncodingException
	{
		WritableLineReader reader;
		synchronized (treeLock)
		{
			removeAllChildrenImpl();
			setTitle(null);
			setBaseURI(null);
			setDefaultTarget(null);
			// this.styleSheets.clear();
			// this.styleSheetAggregator = null;
			reader = this.reader;
		}
		if (reader != null)
		{
			try
			{
				ErrorHandler errorHandler = new LocalErrorHandler();
				String systemId = documentURI;
				String publicId = systemId;
				HtmlParser parser = new HtmlParser(ucontext, this, errorHandler, publicId, systemId);
				parser.parse(reader);
			}
			finally
			{
				if (closeReader)
				{
					try
					{
						reader.close();
					}
					catch (Exception err)
					{
						logger.log(Level.WARNING, "load(): Unable to close stream", err);
					}
					synchronized (treeLock)
					{
						this.reader = null;
					}
				}
			}
		}
	}
	
	/**
	 * Called if something such as a color or decoration has changed. This would
	 * be something which does not affect the rendered size.
	 * 
	 * @param node
	 */
	public void lookInvalidated(NodeImpl node)
	{
		ArrayList listenersList = documentNotificationListeners;
		int size;
		synchronized (listenersList)
		{
			size = listenersList.size();
		}
		// Traverse list outside synchronized block.
		// (Shouldn't call listener methods in synchronized block.
		// Deadlock is possible). But assume list could have
		// been changed.
		for (int i = 0; i < size; i++)
		{
			try
			{
				DocumentNotificationListener dnl = (DocumentNotificationListener) listenersList.get(i);
				dnl.lookInvalidated(node);
			}
			catch (IndexOutOfBoundsException iob)
			{
				// ignore
			}
		}
		
	}
	
	public Element namedItem(String name)
	{
		Element element;
		synchronized (this)
		{
			element = (Element) elementsByName.get(name);
		}
		return element;
	}
	
	public void nodeLoaded(NodeImpl node)
	{
		ArrayList listenersList = documentNotificationListeners;
		int size;
		synchronized (listenersList)
		{
			size = listenersList.size();
		}
		// Traverse list outside synchronized block.
		// (Shouldn't call listener methods in synchronized block.
		// Deadlock is possible). But assume list could have
		// been changed.
		for (int i = 0; i < size; i++)
		{
			try
			{
				DocumentNotificationListener dnl = (DocumentNotificationListener) listenersList.get(i);
				dnl.nodeLoaded(node);
			}
			catch (IndexOutOfBoundsException iob)
			{
				// ignore
			}
		}
	}
	
	@Override
	public void normalizeDocument()
	{
		// TODO: Normalization options from domConfig
		synchronized (treeLock)
		{
			visitImpl(new NodeVisitor()
			{
				@Override
				public void visit(Node node)
				{
					node.normalize();
				}
			});
		}
	}
	
	@Override
	public void open()
	{
		synchronized (treeLock)
		{
			if (reader != null)
			{
				if (reader instanceof LocalWritableLineReader)
				{
					try
					{
						reader.close();
					}
					catch (IOException ioe)
					{
						// ignore
					}
					reader = null;
				}
				else
				{
					// Already open, return.
					// Do not close http/file documents in progress.
					return;
				}
			}
			removeAllChildrenImpl();
			reader = new LocalWritableLineReader(new EmptyReader());
		}
	}
	
	/**
	 * Changed if the position of the node in a parent has changed.
	 * 
	 * @param node
	 */
	public void positionInParentInvalidated(NodeImpl node)
	{
		ArrayList listenersList = documentNotificationListeners;
		int size;
		synchronized (listenersList)
		{
			size = listenersList.size();
		}
		// Traverse list outside synchronized block.
		// (Shouldn't call listener methods in synchronized block.
		// Deadlock is possible). But assume list could have
		// been changed.
		for (int i = 0; i < size; i++)
		{
			try
			{
				DocumentNotificationListener dnl = (DocumentNotificationListener) listenersList.get(i);
				dnl.positionInvalidated(node);
			}
			catch (IndexOutOfBoundsException iob)
			{
				// ignore
			}
		}
	}
	
	public void removeDocumentNotificationListener(DocumentNotificationListener listener)
	{
		ArrayList listenersList = documentNotificationListeners;
		synchronized (listenersList)
		{
			listenersList.remove(listener);
		}
	}
	
	@Override
	public Node renameNode(Node n, String namespaceURI, String qualifiedName) throws DOMException
	{
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "No renaming");
	}
	
	public void setBaseURI(String value)
	{
		baseURI = value;
	}
	
	@Override
	public void setBody(HTMLElement body)
	{
		synchronized (this)
		{
			this.body = body;
		}
	}
	
	@Override
	public void setCookie(final String cookie) throws DOMException
	{
		SecurityManager sm = System.getSecurityManager();
		if (sm != null)
		{
			AccessController.doPrivileged(new PrivilegedAction()
			{
				// Justification: A caller (e.g. Google Analytics script)
				// might want to set cookies on the parent document.
				// If the caller has access to the document, it appears
				// they should be able to set cookies on that document.
				// Note that this Document instance cannot be created
				// with an arbitrary URL.
				@Override
				public Object run()
				{
					ucontext.setCookie(documentURL, cookie);
					return null;
				}
			});
		}
		else
		{
			ucontext.setCookie(documentURL, cookie);
		}
	}
	
	public void setDefaultTarget(String value)
	{
		defaultTarget = value;
	}
	
	public void setDoctype(DocumentType doctype)
	{
		this.doctype = doctype;
	}
	
	@Override
	public void setDocumentURI(String documentURI)
	{
		// TODO: Security considerations? Chaging documentURL?
		this.documentURI = documentURI;
	}
	
	public void setDomain(String domain)
	{
		String oldDomain = this.domain;
		if ((oldDomain != null) && Domains.isValidCookieDomain(domain, oldDomain))
		{
			this.domain = domain;
		}
		else
		{
			throw new SecurityException("Cannot set domain to '" + domain + "' when current domain is '" + oldDomain
			        + "'");
		}
	}
	
	public void setLocation(String location)
	{
		getLocation().setHref(location);
	}
	
	// protected RenderState createRenderState(RenderState prevRenderState) {
	// return new StyleSheetRenderState(null, null);
	// }
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xamjwg.html.domimpl.NodeImpl#setNodeValue(java.lang.String)
	 */
	@Override
	public void setNodeValue(String nodeValue) throws DOMException
	{
		throw new DOMException(DOMException.INVALID_MODIFICATION_ERR, "Cannot set node value of document");
	}
	
	public void setOnloadHandler(Function onloadHandler)
	{
		this.onloadHandler = onloadHandler;
	}
	
	public void setReferrer(String value)
	{
		referrer = value;
	}
	
	@Override
	public void setStrictErrorChecking(boolean strictErrorChecking)
	{
		this.strictErrorChecking = strictErrorChecking;
	}
	
	@Override
	public void setTextContent(String textContent) throws DOMException
	{
		// NOP, per spec
	}
	
	@Override
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	@Override
	public Object setUserData(String key, Object data, UserDataHandler handler)
	{
		Function onloadHandler = this.onloadHandler;
		if (onloadHandler != null)
		{
			if (org.cobra_grendel.html.parser.HtmlParser.MODIFYING_KEY.equals(key) && (data == Boolean.FALSE))
			{
				// TODO: onload event object?
				Executor.executeFunction(this, onloadHandler, null);
			}
		}
		return super.setUserData(key, data, handler);
	}
	
	@Override
	public void setXmlStandalone(boolean xmlStandalone) throws DOMException
	{
		this.xmlStandalone = xmlStandalone;
	}
	
	@Override
	public void setXmlVersion(String xmlVersion) throws DOMException
	{
		this.xmlVersion = xmlVersion;
	}
	
	public void setXssToken(String xssToken)
	{
		this.xssToken = xssToken;
	}
	
	public void sizeInvalidated(NodeImpl node)
	{
		ArrayList listenersList = documentNotificationListeners;
		int size;
		synchronized (listenersList)
		{
			size = listenersList.size();
		}
		// Traverse list outside synchronized block.
		// (Shouldn't call listener methods in synchronized block.
		// Deadlock is possible). But assume list could have
		// been changed.
		for (int i = 0; i < size; i++)
		{
			try
			{
				DocumentNotificationListener dnl = (DocumentNotificationListener) listenersList.get(i);
				dnl.sizeInvalidated(node);
			}
			catch (IndexOutOfBoundsException iob)
			{
				// ignore
			}
		}
	}
	
	@Override
	public void write(String text)
	{
		synchronized (treeLock)
		{
			if (reader != null)
			{
				try
				{
					// This can end up in openBufferChanged
					reader.write(text);
				}
				catch (IOException ioe)
				{
					// ignore
				}
			}
		}
	}
	
	@Override
	public void writeln(String text)
	{
		synchronized (treeLock)
		{
			if (reader != null)
			{
				try
				{
					// This can end up in openBufferChanged
					reader.write(text + "\r\n");
				}
				catch (IOException ioe)
				{
					// ignore
				}
			}
		}
	}
	
	private void openBufferChanged(String text)
	{
		// Assumed to execute in a lock
		// Assumed that text is not broken up HTML.
		ErrorHandler errorHandler = new LocalErrorHandler();
		String systemId = documentURI;
		String publicId = systemId;
		HtmlParser parser = new HtmlParser(ucontext, this, errorHandler, publicId, systemId);
		StringReader strReader = new StringReader(text);
		try
		{
			// This sets up another Javascript scope Window. Does it matter?
			parser.parse(strReader);
		}
		catch (Exception err)
		{
			this.warn("Unable to parse written HTML text. BaseURI=[" + getBaseURI() + "].", err);
		}
	}
	
	@Override
	protected Node createSimilarNode()
	{
		return new HTMLDocumentImpl(ucontext, rcontext, reader, documentURI, transactionId);
	}
	
	// private class BodyFilter implements NodeFilter {
	// public boolean accept(Node node) {
	// return node instanceof org.w3c.dom.html2.HTMLBodyElement;
	// }
	// }
	
	String getDocumentHost()
	{
		URL docUrl = documentURL;
		return docUrl == null ? null : docUrl.getHost();
	}
	
	/**
	 * Loads images such that they are shared if from the same URI. Informs
	 * listener immediately if an image is already known.
	 * 
	 * @param relativeUri
	 * @param imageListener
	 */
	void loadImage(String relativeUri, ImageListener imageListener)
	{
		HtmlRendererContext rcontext = getHtmlRendererContext();
		if (rcontext == null)
		{
			// Ignore image loading when there's no renderer context.
			imageListener.imageLoaded(BLANK_IMAGE_EVENT);
			return;
		}
		final URL url = getFullURL(relativeUri);
		if (url == null)
		{
			imageListener.imageLoaded(BLANK_IMAGE_EVENT);
			return;
		}
		final String urlText = url.toExternalForm();
		final Map map = imageInfos;
		ImageEvent event = null;
		synchronized (map)
		{
			ImageInfo info = (ImageInfo) map.get(urlText);
			if (info != null)
			{
				if (info.loaded)
				{
					// TODO: This can't really happen because ImageInfo
					// is removed right after image is loaded.
					event = info.imageEvent;
				}
				else
				{
					info.addListener(imageListener);
				}
			}
			else
			{
				UserAgentContext uac = rcontext.getUserAgentContext();
				final HttpRequest httpRequest = uac.createHttpRequest(transactionId);
				final ImageInfo newInfo = new ImageInfo();
				map.put(urlText, newInfo);
				newInfo.addListener(imageListener);
				httpRequest.addReadyStateChangeListener(new ReadyStateChangeListener()
				{
					@Override
					public void readyStateChanged()
					{
						if (httpRequest.getReadyState() == HttpRequest.STATE_COMPLETE)
						{
							ImageEvent newEvent = new ImageEvent(HTMLDocumentImpl.this, httpRequest.getResponseImage());
							ImageListener[] listeners;
							synchronized (map)
							{
								newInfo.imageEvent = newEvent;
								newInfo.loaded = true;
								listeners = newInfo.getListeners();
								// Must remove from map in the locked block
								// that got the listeners. Otherwise a new
								// listener might miss the event??
								map.remove(urlText);
							}
							int llength = listeners.length;
							for (int i = 0; i < llength; i++)
							{
								// Call holding no locks
								listeners[i].imageLoaded(newEvent);
							}
						}
					}
				});
				SecurityManager sm = System.getSecurityManager();
				if (sm == null)
				{
					httpRequest.open("GET", url, true);
				}
				else
				{
					AccessController.doPrivileged(new PrivilegedAction()
					{
						@Override
						public Object run()
						{
							// Code might have restrictions on accessing
							// items from elsewhere.
							httpRequest.open("GET", url, true);
							return null;
						}
					});
				}
			}
		}
		if (event != null)
		{
			// Call holding no locks.
			imageListener.imageLoaded(event);
		}
	}
	
	void removeElementById(String id)
	{
		synchronized (this)
		{
			elementsById.remove(id);
		}
	}
	
	void removeNamedItem(String name)
	{
		synchronized (this)
		{
			elementsByName.remove(name);
		}
	}
	
	/**
	 * Caller should synchronize on document.
	 */
	void setElementById(String id, Element element)
	{
		synchronized (this)
		{
			elementsById.put(id, element);
		}
	}
	
	void setNamedItem(String name, Element element)
	{
		synchronized (this)
		{
			elementsByName.put(name, element);
		}
	}

	public final int getTransactionId()
	{
		return transactionId;
	}
}
