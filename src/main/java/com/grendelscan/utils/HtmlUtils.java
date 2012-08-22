package com.grendelscan.utils;
import java.util.ArrayList;
import org.apache.commons.lang.StringEscapeUtils;
import org.cobra_grendel.html.domimpl.NodeImpl;
import org.w3c.dom.Text;
import org.w3c.dom.html2.HTMLAnchorElement;
import org.w3c.dom.html2.HTMLAppletElement;
import org.w3c.dom.html2.HTMLBaseElement;
import org.w3c.dom.html2.HTMLBodyElement;
import org.w3c.dom.html2.HTMLButtonElement;
import org.w3c.dom.html2.HTMLDocument;
import org.w3c.dom.html2.HTMLFormElement;
import org.w3c.dom.html2.HTMLFrameElement;
import org.w3c.dom.html2.HTMLFrameSetElement;
import org.w3c.dom.html2.HTMLHeadElement;
import org.w3c.dom.html2.HTMLHtmlElement;
import org.w3c.dom.html2.HTMLIFrameElement;
import org.w3c.dom.html2.HTMLImageElement;
import org.w3c.dom.html2.HTMLInputElement;
import org.w3c.dom.html2.HTMLIsIndexElement;
import org.w3c.dom.html2.HTMLLinkElement;
import org.w3c.dom.html2.HTMLMetaElement;
import org.w3c.dom.html2.HTMLObjectElement;
import org.w3c.dom.html2.HTMLOptGroupElement;
import org.w3c.dom.html2.HTMLOptionElement;
import org.w3c.dom.html2.HTMLParamElement;
import org.w3c.dom.html2.HTMLScriptElement;
import org.w3c.dom.html2.HTMLSelectElement;
import org.w3c.dom.html2.HTMLTextAreaElement;
public class HtmlUtils
{
	
	
	/**
	 * This will remove the parent node, the document field and all child 
	 * elements from a node tree, except the following:
	 * a, applet, base, body, button, document, form, frame, frame set, head, 
	 * html, IFrame, img, input, isindex, link, meta, object, optgroup, option,
	 * param, script, select, and textarea. It will also keep text nodes with
	 * textarea parents, since they are used as input.<br>
	 * <br>
	 * The idea is that these are the only tags that cause some kind of request 
	 * to the server. There are two exceptions: style tags can cause files to be
	 * requested, but style tags aren't parsed by Grendel-Scan anyway; event 
	 * handlers are another big exception.<br>
	 * <br>
	 * This is primarily to let the ByHtmlElementCategorizer strip out 
	 * unnecessary DOM elements, which saves a ton of memory. One result is that
	 * ByHtmlElement tests cannot interact with some tags. If this is necessary,
	 * they can run as an ByMimeType test and run through the entire DOM. 
	 * 
	 * @param element
	 */
	public static void CleanElement(NodeImpl node)
	{
		node.clearDocument(false);

		if (node.getRawNodeList() != null)
		{
			ArrayList<NodeImpl> tmpNodes = new ArrayList<NodeImpl>(node.getRawNodeList());
			
			for (NodeImpl child: tmpNodes)
			{
				CleanElement(child);
				if (unnecessaryElement(child))
				{
					int index = node.getRawNodeList().indexOf(child);
					node.getRawNodeList().remove(index);
					if (child.getRawNodeList() != null)
					{
						for (NodeImpl grandChild: child.getRawNodeList())
						{
							node.getRawNodeList().add(index, grandChild);
							index++;
						}
					}
				}
			}
			
			// If there are no more child nodes, kill the node list
			if (node.getRawNodeList().size() == 0)
			{
				node.setRawNodeList(null);
			}
		}
	}
	
	private static boolean unnecessaryElement(NodeImpl node)
	{
		boolean unneccissary = true;
		if 
		(
				node instanceof HTMLAnchorElement ||
				node instanceof HTMLAppletElement ||
				node instanceof HTMLBaseElement ||
				node instanceof HTMLBodyElement ||
				node instanceof HTMLButtonElement ||
				node instanceof HTMLDocument ||
				node instanceof HTMLFormElement ||
				node instanceof HTMLFrameElement ||
				node instanceof HTMLFrameSetElement ||
				node instanceof HTMLHeadElement ||
				node instanceof HTMLHtmlElement ||
				node instanceof HTMLIFrameElement ||
				node instanceof HTMLImageElement ||
				node instanceof HTMLInputElement ||
				node instanceof HTMLIsIndexElement ||
				node instanceof HTMLLinkElement ||
				node instanceof HTMLMetaElement ||
				node instanceof HTMLObjectElement ||
				node instanceof HTMLOptGroupElement ||
				node instanceof HTMLOptionElement ||
				node instanceof HTMLParamElement ||
				node instanceof HTMLScriptElement ||
				node instanceof HTMLSelectElement ||
				node instanceof HTMLTextAreaElement ||
				((node.getParentNode() instanceof HTMLTextAreaElement) &&
					(node instanceof Text))
		)
		{
			unneccissary = false;
		}
		return unneccissary;
	}
	
	
	public static void StripAllChildrenButInput(NodeImpl node)
	{
		node.clearDocument(false);

		if (node.getRawNodeList() != null)
		{
			ArrayList<NodeImpl> tmpNodes = new ArrayList<NodeImpl>(node.getRawNodeList());
			
			for (NodeImpl child: tmpNodes)
			{
				StripAllChildrenButInput(child);
				if (nonInputElement(child))
				{
					int index = node.getRawNodeList().indexOf(child);
					node.getRawNodeList().remove(index);
					if (child.getRawNodeList() != null)
					{
						for (NodeImpl grandChild: child.getRawNodeList())
						{
							node.getRawNodeList().add(index, grandChild);
							index++;
						}
					}
				}
			}
			
			// If there are no more child nodes, kill the node list
			if (node.getRawNodeList().size() == 0)
			{
				node.setRawNodeList(null);
			}
		}
	}
	
	public static void StripAllFamily(NodeImpl node)
	{
		node.clearDocument(false);
		node.clearParent();
		node.setRawNodeList(null);
	}
	
	private static boolean nonInputElement(NodeImpl node)
	{
		boolean unneccissary = true;
		if 
		(
				node instanceof HTMLButtonElement ||
				node instanceof HTMLInputElement ||
				node instanceof HTMLOptGroupElement ||
				node instanceof HTMLOptionElement ||
				node instanceof HTMLSelectElement ||
				node instanceof HTMLTextAreaElement ||
				((node.getParentNode() instanceof HTMLTextAreaElement) &&
					(node instanceof Text))
		)
		{
			unneccissary = false;
		}
		return unneccissary;
	}
	
	
	
	
	
	public static boolean isDOMEvent(String name)
	{
		return isStandardDOMEvent(name) || isMicrosoftOnlyDOMEvent(name) || isMozillaOnlyDOMEvent(name);
	}

	public static boolean isMicrosoftOnlyDOMEvent(String name)
	{
		if (name.equalsIgnoreCase("oncut") || name.equalsIgnoreCase("oncopy") || name.equalsIgnoreCase("onpaste")
		        || name.equalsIgnoreCase("onbeforecut") || name.equalsIgnoreCase("onbeforecopy")
		        || name.equalsIgnoreCase("onbeforepaste") || name.equalsIgnoreCase("onafterupdate")
		        || name.equalsIgnoreCase("onbeforeupdate") || name.equalsIgnoreCase("oncellchange")
		        || name.equalsIgnoreCase("ondataavailable") || name.equalsIgnoreCase("ondatasetchanged")
		        || name.equalsIgnoreCase("ondatasetcomplete") || name.equalsIgnoreCase("onerrorupdate")
		        || name.equalsIgnoreCase("onrowenter") || name.equalsIgnoreCase("onrowexit")
		        || name.equalsIgnoreCase("onrowsdelete") || name.equalsIgnoreCase("onrowinserted")
		        || name.equalsIgnoreCase("oncontextmenu") || name.equalsIgnoreCase("ondrag")
		        || name.equalsIgnoreCase("ondragstart") || name.equalsIgnoreCase("ondragenter")
		        || name.equalsIgnoreCase("ondragover") || name.equalsIgnoreCase("ondragleave")
		        || name.equalsIgnoreCase("ondragend") || name.equalsIgnoreCase("ondrop")
		        || name.equalsIgnoreCase("onselectstart") || name.equalsIgnoreCase("onhelp")
		        || name.equalsIgnoreCase("onbeforeunload") || name.equalsIgnoreCase("onstop")
		        || name.equalsIgnoreCase("onbeforeeditfocus") || name.equalsIgnoreCase("onstart")
		        || name.equalsIgnoreCase("onfinish") || name.equalsIgnoreCase("onbounce")
		        || name.equalsIgnoreCase("onbeforeprint") || name.equalsIgnoreCase("onafterprint")
		        || name.equalsIgnoreCase("onpropertychange") || name.equalsIgnoreCase("onfilterchange")
		        || name.equalsIgnoreCase("onreadystatechange") || name.equalsIgnoreCase("onlosecapture"))
		{
			return true;
		}
		return false;
	}

	public static boolean isMozillaOnlyDOMEvent(String name)
	{
		if (name.equalsIgnoreCase("DOMMouseScroll") || name.equalsIgnoreCase("ondragdrop")
		        || name.equalsIgnoreCase("ondragenter") || name.equalsIgnoreCase("ondragexit")
		        || name.equalsIgnoreCase("ondraggesture") || name.equalsIgnoreCase("ondragover")
		        || name.equalsIgnoreCase("onclose") || name.equalsIgnoreCase("oncommand")
		        || name.equalsIgnoreCase("oninput") || name.equalsIgnoreCase("DOMMenuItemActive")
		        || name.equalsIgnoreCase("DOMMenuItemInactive") || name.equalsIgnoreCase("oncontextmenu")
		        || name.equalsIgnoreCase("onoverflow") || name.equalsIgnoreCase("onoverflowchanged")
		        || name.equalsIgnoreCase("onunderflow") || name.equalsIgnoreCase("onpopuphidden")
		        || name.equalsIgnoreCase("onpopuphiding") || name.equalsIgnoreCase("onpopupshowing")
		        || name.equalsIgnoreCase("onpopupshown") || name.equalsIgnoreCase("onbroadcast")
		        || name.equalsIgnoreCase("oncommandupdate") || name.equalsIgnoreCase("DOMContentLoaded"))
		{
			return true;
		}
		return false;
	}

	public static boolean isStandardDOMEvent(String name)
	{
		if (name.equalsIgnoreCase("onclick") || name.equalsIgnoreCase("ondblclick")
		        || name.equalsIgnoreCase("onmousedown") || name.equalsIgnoreCase("onmouseup")
		        || name.equalsIgnoreCase("onmouseover") || name.equalsIgnoreCase("onmousemove")
		        || name.equalsIgnoreCase("onmouseout") || name.equalsIgnoreCase("onkeypress")
		        || name.equalsIgnoreCase("onkeydown") || name.equalsIgnoreCase("onkeyup")
		        || name.equalsIgnoreCase("onload") || name.equalsIgnoreCase("onunload")
		        || name.equalsIgnoreCase("onabort") || name.equalsIgnoreCase("onerror")
		        || name.equalsIgnoreCase("onresize") || name.equalsIgnoreCase("onscroll")
		        || name.equalsIgnoreCase("onselect") || name.equalsIgnoreCase("onchange")
		        || name.equalsIgnoreCase("onsubmit") || name.equalsIgnoreCase("onreset")
		        || name.equalsIgnoreCase("onfocus") || name.equalsIgnoreCase("onblur")
		        || name.equalsIgnoreCase("ondomfocusin") || name.equalsIgnoreCase("ondomfocusout")
		        || name.equalsIgnoreCase("ondomactivate") || name.equalsIgnoreCase("onsubtreemodified")
		        || name.equalsIgnoreCase("onnodeinserted") || name.equalsIgnoreCase("onnoderemoved")
		        || name.equalsIgnoreCase("ondomnoderemovedfromdocument")
		        || name.equalsIgnoreCase("ondomnodeinsertedintodocument") || name.equalsIgnoreCase("onattrmodified")
		        || name.equalsIgnoreCase("oncharacterdatamodified"))
		{
			return true;
		}
		return false;
	}
	
	public static String makeLink(String url)
	{
		return makeLink(url, null);
	}
	public static String makeLink(String url, String text)
	{
		if (text == null || text.equals(""))
		{
			text = url;
		}
		return "<a target=\"_blank\" href=\"" + url + "\">" + text + "</a>";
	}
	
	
	public static String escapeHTML(String text)
	{
		return StringEscapeUtils.escapeHtml(text);
	}

}
