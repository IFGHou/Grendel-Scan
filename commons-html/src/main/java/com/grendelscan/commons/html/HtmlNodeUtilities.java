package com.grendelscan.commons.html;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Comment;
import org.w3c.dom.Node;
import org.w3c.dom.html2.HTMLElement;

import com.grendelscan.commons.StringUtils;

/**
 * A collection of static methods to perform misc tasks against HTML Node objects
 * 
 * @author David Byrne
 * 
 */
public class HtmlNodeUtilities
{

    public static int compareDOMText(final Node nodeA, final String nodeBText, final int perfectScore, final boolean ignoreCase)
    {

        String nodeAText = HtmlNodeWriter.writeTextOnly(nodeA, false).replaceAll("\\s++", " ");

        int score;
        if (ignoreCase)
        {
            score = StringUtils.scoreStringDifferenceIgnoreCase(nodeAText, nodeBText, perfectScore);
        }
        else
        {
            score = StringUtils.scoreStringDifference(nodeAText, nodeBText, perfectScore);
        }
        return score;
    }

    /*
     * TODO UCdetector: Remove unused code: public static int compareDOMText(Node nodeA, Node nodeB, int perfectScore, boolean ignoreCase) {
     * 
     * String nodeAText = HtmlNodeWriter.writeTextOnly(nodeA, false).replaceAll("\\s++", " "); String nodeBText = HtmlNodeWriter.writeTextOnly(nodeB, false).replaceAll("\\s++", " ");
     * 
     * int score; if (ignoreCase) { score = StringUtils.scoreStringDifferenceIgnoreCase(nodeAText, nodeBText, perfectScore); } else { score = StringUtils.scoreStringDifference(nodeAText, nodeBText,
     * perfectScore); } return score; }
     */

    /**
     * Returns a list of all of the comment strings found in the node
     * 
     * @param node
     * @return
     */
    public static List<String> getAllComments(final Node node)
    {
        List<String> comments = new ArrayList<String>();
        searchChildrenForComments(comments, node);
        return comments;
    }

    /**
     * Returns a list of all HTML elements of the tag specified
     * 
     * @param element
     * @param tagName
     * @return
     */
    public static List<HTMLElement> getChildElements(final Node node, final String tagName)
    {
        List<HTMLElement> elements = new ArrayList<HTMLElement>(1);
        searchChildren(elements, node, tagName);
        return elements;
    }

    /**
     * Returns an MD5 hash of the HTML text of a node
     * 
     * @param node
     *            The node object to hash
     * @param processChildNodes
     *            Indicates whether child nodes should be included when calculating the hash. Ommiting them will sometimes be faster.
     * @return The computed MD5 hash as a string
     * 
     */
    public static String getNodeHash(final Node node, final boolean processChildNodes)
    {
        return StringUtils.md5Hash(HtmlNodeWriter.write(node, processChildNodes, null));
    }

    /**
     * Finds the first TITLE node and returns the text
     * 
     * @param node
     * @return
     */
    public static String getTitleText(final Node node)
    {
        String titleText = "";
        for (HTMLElement title : getChildElements(node, "title"))
        {
            titleText = title.getTextContent();
            break;
        }
        return titleText;

    }

    private static void searchChildren(final List<HTMLElement> elements, final Node node, final String tagName)
    {
        if (node != null)
        {
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                HTMLElement element = (HTMLElement) node;
                if (element.getTagName().equalsIgnoreCase(tagName))
                {
                    elements.add(element);
                }
            }

            Node child = node.getFirstChild();
            int count = 0;
            while (child != null && count++ < 1000)
            {
                searchChildren(elements, child, tagName);
                child = child.getNextSibling();
            }
        }
    }

    private static void searchChildrenForComments(final List<String> comments, final Node node)
    {
        if (node != null)
        {
            if (node.getNodeType() == Node.COMMENT_NODE)
            {
                Comment comment = (Comment) node;
                comments.add(comment.getNodeValue());
            }
            else
            {
                Node child = node.getFirstChild();
                while (child != null)
                {
                    searchChildrenForComments(comments, child);
                    child = child.getNextSibling();
                }
            }
        }
    }

}
