package com.grendelscan.html;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.cobra_grendel.html.domimpl.HTMLHtmlElementImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.html2.HTMLElement;

/**
 * The W3C DOM binding does not contain a way to convert
 * nodes into the corresponding HTML text. This class
 * performs that function. Based on the sample DOM writer in
 * Xerces by Andy Clark.
 * 
 * @author Andy Clark, David Byrne
 */
public class HtmlNodeWriter
{

	/** Default canonical output (false). */
	protected static final boolean DEFAULT_CANONICAL = false;

	/** Default dynamic validation support (false). */
	protected static final boolean DEFAULT_DYNAMIC_VALIDATION = false;

	/**
	 * Default generate synthetic schema annotations
	 * (false).
	 */
	protected static final boolean DEFAULT_GENERATE_SYNTHETIC_ANNOTATIONS = false;

	/** Default honour all schema locations (false). */
	protected static final boolean DEFAULT_HONOUR_ALL_SCHEMA_LOCATIONS = false;

	/** Default load external DTD (true). */
	protected static final boolean DEFAULT_LOAD_EXTERNAL_DTD = true;

	/** Default namespaces support (true). */
	protected static final boolean DEFAULT_NAMESPACES = true;

	/** Default parser name. */
	protected static final String DEFAULT_PARSER_NAME = "dom.wrappers.Xerces";

	/** Default Schema full checking support (false). */
	protected static final boolean DEFAULT_SCHEMA_FULL_CHECKING = false;

	/** Default Schema validation support (false). */
	protected static final boolean DEFAULT_SCHEMA_VALIDATION = false;

	/** Default validate schema annotations (false). */
	protected static final boolean DEFAULT_VALIDATE_ANNOTATIONS = false;

	/** Default validation support (false). */
	protected static final boolean DEFAULT_VALIDATION = false;

	/** Default XInclude processing support (false). */
	protected static final boolean DEFAULT_XINCLUDE = false;

	// default settings

	/** Default XInclude fixup base URIs support (true). */
	protected static final boolean DEFAULT_XINCLUDE_FIXUP_BASE_URIS = true;

	/** Default XInclude fixup language support (true). */
	protected static final boolean DEFAULT_XINCLUDE_FIXUP_LANGUAGE = true;

	/**
	 * Dynamic validation feature id
	 * (http://apache.org/xml/features/validation/dynamic).
	 */
	protected static final String DYNAMIC_VALIDATION_FEATURE_ID = "http://apache.org/xml/features/validation/dynamic";

	/**
	 * Generate synthetic schema annotations feature id
	 * (http://apache.org/xml/features/generate-synthetic-annotations).
	 */
	protected static final String GENERATE_SYNTHETIC_ANNOTATIONS_ID = "http://apache.org/xml/features/generate-synthetic-annotations";

	/**
	 * Honour all schema locations feature id
	 * (http://apache.org/xml/features/honour-all-schemaLocations).
	 */
	protected static final String HONOUR_ALL_SCHEMA_LOCATIONS_ID = "http://apache.org/xml/features/honour-all-schemaLocations";

	/**
	 * Load external DTD feature id
	 * (http://apache.org/xml/features/nonvalidating/load-external-dtd).
	 */
	protected static final String LOAD_EXTERNAL_DTD_FEATURE_ID = "http://apache.org/xml/features/nonvalidating/load-external-dtd";

	/**
	 * Namespaces feature id
	 * (http://xml.org/sax/features/namespaces).
	 */
	protected static final String NAMESPACES_FEATURE_ID = "http://xml.org/sax/features/namespaces";

	/**
	 * Schema full checking feature id
	 * (http://apache.org/xml/features/validation/schema-full-checking).
	 */
	protected static final String SCHEMA_FULL_CHECKING_FEATURE_ID = "http://apache.org/xml/features/validation/schema-full-checking";

	/**
	 * Schema validation feature id
	 * (http://apache.org/xml/features/validation/schema).
	 */
	protected static final String SCHEMA_VALIDATION_FEATURE_ID = "http://apache.org/xml/features/validation/schema";

	/**
	 * Validate schema annotations feature id
	 * (http://apache.org/xml/features/validate-annotations).
	 */
	protected static final String VALIDATE_ANNOTATIONS_ID = "http://apache.org/xml/features/validate-annotations";

	/**
	 * Validation feature id
	 * (http://xml.org/sax/features/validation).
	 */
	protected static final String VALIDATION_FEATURE_ID = "http://xml.org/sax/features/validation";

	/**
	 * XInclude feature id
	 * (http://apache.org/xml/features/xinclude).
	 */
	protected static final String XINCLUDE_FEATURE_ID = "http://apache.org/xml/features/xinclude";

	/**
	 * XInclude fixup base URIs feature id
	 * (http://apache.org/xml/features/xinclude/fixup-base-uris).
	 */
	protected static final String XINCLUDE_FIXUP_BASE_URIS_FEATURE_ID = "http://apache.org/xml/features/xinclude/fixup-base-uris";

	/**
	 * XInclude fixup language feature id
	 * (http://apache.org/xml/features/xinclude/fixup-language).
	 */
	protected static final String XINCLUDE_FIXUP_LANGUAGE_FEATURE_ID = "http://apache.org/xml/features/xinclude/fixup-language";

	//
	// Data
	//


	/**
	 * Creates HTML of the supplied node, including any
	 * child nodes and attributes.
	 * 
	 * @param node
	 *            The HTML node to write
	 * @param fXML11
	 *            Sets XML version to 1.1. In other methods,
	 *            the default is 1.0
	 * @param fCanonical
	 *            Sets cannonical mode. Basically,
	 *            cannonical only outputs what is needed for
	 *            proper parsing. The primary impact of this
	 *            is that comments are not parsed.
	 * @param processChildNodes
	 *            Whether or not to recursively process
	 *            child nodes. Attributes are processed
	 *            regardless of this setting.
	 */

	public static String write(Node node, boolean processChildNodes, List<Class<? extends HTMLElement>> nodesToPrint)
	{
		boolean fCanonical = false;
		boolean fXML11 = false;
		String text = "";

		// is there anything to do?
		if (node == null)
		{
			return text;
		}

		short type = node.getNodeType();
		switch (type)
		{
			case Node.DOCUMENT_NODE:
			{
				Document document = (Document) node;
				fXML11 = "1.1".equals(getVersion(document));
				if (!fCanonical)
				{
					if (fXML11)
					{
						text += "<?xml version=\"1.1\" encoding=\"UTF-8\"?>";
					}
					else
					{
						text += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
					}
					if (processChildNodes)
					{
						text += write(document.getDoctype(), processChildNodes, nodesToPrint);
					}
				}
				if (processChildNodes)
				{
					Element e = document.getDocumentElement();
					if (! (e instanceof HTMLHtmlElementImpl))
					{
						// This is in case there is no HTML tag. Some of the DOM can still be salvaged.
						Node child = document.getFirstChild();
						while (child != null)
						{
							text += write(child, processChildNodes, nodesToPrint);
							child = child.getNextSibling();
						}
					}
					else
					{
						text += write(e, processChildNodes, nodesToPrint);
					}
				}
				break;
			}

			case Node.DOCUMENT_TYPE_NODE:
			{
				if ((nodesToPrint == null || nodesToPrint.contains(node.getClass())))
				{
					DocumentType doctype = (DocumentType) node;
					text += "<!DOCTYPE " + doctype.getName();
					String publicId = doctype.getPublicId();
					String systemId = doctype.getSystemId();
					if (publicId != null)
					{
						text += " PUBLIC '" + publicId + "' '" + systemId + '\'';
					}
					else if (systemId != null)
					{
						text += " SYSTEM '" + systemId + '\'';
					}
					String internalSubset = doctype.getInternalSubset();
					if (internalSubset != null)
					{
						text += " [\n" + internalSubset + ']';
					}
					text += ">\n";
				}
				break;
			}

			case Node.ATTRIBUTE_NODE:
			{
				text += ' ' + node.getNodeName() + "=\"";
				text += normalizeAndPrint(node.getNodeValue(), true, fCanonical, fXML11);
				text += '"';
				break;
			}
			
			case Node.ELEMENT_NODE:
			{
				if (nodesToPrint == null || nodesToPrint.contains(node.getClass()))
				{
					text += '<' + node.getNodeName();
					Attr attrs[] = sortAttributes(node.getAttributes());
					for (int i = 0; i < attrs.length; i++)
					{
						text += write(attrs[i], processChildNodes, nodesToPrint);
					}
					text += '>';
				}
				
				if (processChildNodes)
				{
					Node child = node.getFirstChild();
					while (child != null)
					{
						text += write(child, processChildNodes, nodesToPrint);
						child = child.getNextSibling();
					}
				}
				break;
			}

			case Node.ENTITY_REFERENCE_NODE:
			{
				if (fCanonical)
				{
					if (processChildNodes)
					{
						Node child = node.getFirstChild();
						while (child != null)
						{
							text += write(child, processChildNodes, nodesToPrint);
							child = child.getNextSibling();
						}
					}
				}
				else
				{
					text += '&' + node.getNodeName() + ';';
				}
				break;
			}

			case Node.CDATA_SECTION_NODE:
			{
				if (nodesToPrint == null || nodesToPrint.contains(node.getClass()))
				{
					if (fCanonical)
					{
						text += normalizeAndPrint(node.getNodeValue(), false, fCanonical, fXML11);
					}
					else
					{
						text += "<![CDATA[" + node.getNodeValue() + "]]>";
					}
				}
				break;
			}

			case Node.TEXT_NODE:
			{
				if (nodesToPrint == null || nodesToPrint.contains(node.getClass()))
				{
					text += normalizeAndPrint(node.getNodeValue(), false, fCanonical, fXML11);
				}
				break;
			}

			case Node.PROCESSING_INSTRUCTION_NODE:
			{
				if (nodesToPrint == null || nodesToPrint.contains(node.getClass()))
				{
					text += "<?" + node.getNodeName();
					String data = node.getNodeValue();
					if ((data != null) && (data.length() > 0))
					{
						text += ' ' + data;
					}
					text += "?>";
				}
				break;
			}

			case Node.COMMENT_NODE:
			{
				if (!fCanonical && (nodesToPrint == null || nodesToPrint.contains(node.getClass())))
				{
					text += "<!--";
					String comment = node.getNodeValue();
					if ((comment != null) && (comment.length() > 0))
					{
						text += comment;
					}
					text += "-->";
				}
			}
		}

		if (type == Node.ELEMENT_NODE && (nodesToPrint == null || nodesToPrint.contains(node.getClass())))
		{
			text += "</" + node.getNodeName() + '>';
		}

		return text;
	} // write(Node)


	/** Extracts the XML version from the Document. */
	protected static String getVersion(Document document)
	{
		if (document == null)
		{
			return null;
		}
		String version = null;
		Method getXMLVersion = null;
		try
		{
			getXMLVersion = document.getClass().getMethod("getXmlVersion", new Class[] {});
			// If Document class implements DOM L3, this
			// method will exist.
			if (getXMLVersion != null)
			{
				version = (String) getXMLVersion.invoke(document, (Object[]) null);
			}
		}
		catch (InvocationTargetException e)
		{
		}
		catch (IllegalArgumentException e)
		{
		}
		catch (IllegalAccessException e)
		{
		}
		catch (SecurityException e)
		{
		}
		catch (NoSuchMethodException e)
		{
		}
		return version;
	} // getVersion(Document)

	//
	// Protected methods
	//

	/** Normalizes and print the given character. */
	protected static String normalizeAndPrint(char c, boolean isAttValue, boolean fCanonical, boolean fXML11)
	{

		String text = "";
		switch (c)
		{
			case '<':
			{
				text += "&lt;";
				break;
			}
			case '>':
			{
				text += "&gt;";
				break;
			}
			case '&':
			{
				text += "&amp;";
				break;
			}
			case '"':
			{
				// A '"' that appears in character data
				// does not need to be escaped.
				if (isAttValue)
				{
					text += "&quot;";
				}
				else
				{
					text += "\"";
				}
				break;
			}
			case '\r':
			{
				// If CR is part of the document's content,
				// it
				// must not be printed as a literal
				// otherwise
				// it would be normalized to LF when the
				// document
				// is reparsed.
				text += "&#xD;";
				break;
			}
			case '\n':
			{
				if (fCanonical)
				{
					text += "&#xA;";
					break;
				}
				// else, default print char
			}
			default:
			{
				// In XML 1.1, control chars in the ranges
				// [#x1-#x1F, #x7F-#x9F] must be escaped.
				//
				// Escape space characters that would be
				// normalized to #x20 in attribute values
				// when the document is reparsed.
				//
				// Escape NEL (0x85) and LSEP (0x2028) that
				// appear in content
				// if the document is XML 1.1, since they
				// would
				// be normalized to LF
				// when the document is reparsed.
				if ((fXML11 && (((c >= 0x01) && (c <= 0x1F) && (c != 0x09) && (c != 0x0A)) || ((c >= 0x7F) && (c <= 0x9F)) || (c == 0x2028))) || (isAttValue && ((c == 0x09) || (c == 0x0A))))
				{
					text += "&#x" + Integer.toHexString(c).toUpperCase() + ";";
				}
				else
				{
					text += c;
				}
			}
		}
		return text;
	} // normalizeAndPrint(char,boolean)

	/** Normalizes and prints the given string. */
	protected static String normalizeAndPrint(String s, boolean isAttValue, boolean fCanonical, boolean fXML11)
	{
		String text = "";
		int len = (s != null) ? s.length() : 0;
		for (int i = 0; i < len; i++)
		{
			char c = s.charAt(i);
			text += normalizeAndPrint(c, isAttValue, fCanonical, fXML11);
		}
		return text;
	} // normalizeAndPrint(String,boolean)

	/** Returns a sorted list of attributes. */
	protected static Attr[] sortAttributes(NamedNodeMap attrs)
	{

		int len = (attrs != null) ? attrs.getLength() : 0;
		Attr array[] = new Attr[len];
		for (int i = 0; i < len; i++)
		{
			array[i] = (Attr) attrs.item(i);
		}
		for (int i = 0; i < len - 1; i++)
		{
			String name = array[i].getNodeName();
			int index = i;
			for (int j = i + 1; j < len; j++)
			{
				String curName = array[j].getNodeName();
				if (curName.compareTo(name) < 0)
				{
					name = curName;
					index = j;
				}
			}
			if (index != i)
			{
				Attr temp = array[i];
				array[i] = array[index];
				array[index] = temp;
			}
		}

		return array;

	} // sortAttributes(NamedNodeMap):Attr[]

	public static String writeTextOnly(Node node, boolean includeComments)
	{
			String text = "";

			// is there anything to do?
			if (node == null) return text;

			switch (node.getNodeType())
			{
				case Node.DOCUMENT_NODE:
				case Node.ELEMENT_NODE:
				case Node.ENTITY_REFERENCE_NODE:
				{
					String name = node.getNodeName();
					if (name.equalsIgnoreCase("script"))
					{
						break;
					}
					Node child = node.getFirstChild();
					int count = 0;
					int t = node.getNodeType();
					while (child != null && count++ < 1000)
					{
						if (child.getNodeType() == Node.TEXT_NODE)
						{
							text += child.getNodeValue();
						}
						else
						{
							text += writeTextOnly(child, includeComments);
						}
						child = child.getNextSibling();
					}
					break;
				}

				case Node.COMMENT_NODE:
				{
					if (includeComments)
					{
						text += " ";
						String comment = node.getNodeValue();
						if ((comment != null) && (comment.length() > 0))
						{
							text += comment;
						}
						text += " ";
					}
					break;
				}
				
				default:
				{
					// Don't do anything
				}
			}

			return text;
	}
	
} // class Writer

