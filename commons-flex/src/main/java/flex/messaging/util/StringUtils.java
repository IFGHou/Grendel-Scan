/*************************************************************************
 * 
 * ADOBE CONFIDENTIAL __________________
 * 
 * Copyright 2002 - 2007 Adobe Systems Incorporated All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains the property of Adobe Systems Incorporated and its suppliers, if any. The intellectual and technical concepts contained herein are
 * proprietary to Adobe Systems Incorporated and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of
 * this information or reproduction of this material is strictly forbidden unless prior written permission is obtained from Adobe Systems Incorporated.
 **************************************************************************/
package flex.messaging.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * @exclude
 */
public class StringUtils
{
    /**
     * The String to use for an OS specific line separator.
     */
    public static final String NEWLINE = System.getProperty("line.separator");

    public static String substitute(String str, String from, String to)
    {
        if (from == null || from.equals("") || to == null)
            return str;

        int index = str.indexOf(from);

        if (index == -1)
            return str;

        StringBuffer buf = new StringBuffer(str.length());
        int lastIndex = 0;

        while (index != -1)
        {
            buf.append(str.substring(lastIndex, index));
            buf.append(to);
            lastIndex = index + from.length();
            index = str.indexOf(from, lastIndex);
        }

        // add in last chunk
        buf.append(str.substring(lastIndex));

        return buf.toString();
    }

    /*
     * TODO UCdetector: Remove unused code: public static boolean findMatchWithWildcard(char[] src, char[] pat) { if (src == null || pat == null) return false;
     * 
     * // we consider an empty pattern to be a don't-match-anything pattern if (pat.length == 0) return false;
     * 
     * if (src.length == 0) return (pat.length == 0 || (pat.length == 1 && (pat[0] == '*' || pat[0] == '?')));
     * 
     * boolean star = false;
     * 
     * int srcLen = src.length; int patLen = pat.length; int srcIdx = 0; int patIdx = 0;
     * 
     * for (; srcIdx < srcLen; srcIdx++) { if (patIdx == patLen) { if (patLen < (srcLen - srcIdx)) patIdx = 0; //Start the search again else return false; }
     * 
     * char s = src[srcIdx]; char m = pat[patIdx];
     * 
     * switch (m) { case '*': // star on the end if (patIdx == pat.length - 1) return true; star = true; ++patIdx; break;
     * 
     * case '?': ++patIdx; break;
     * 
     * default: if (s != m) { if (!star) { if (patLen < (srcLen - srcIdx)) patIdx = 0; //Start the search again else return false; } } else { star = false; ++patIdx; } break; } }
     * 
     * if (patIdx < patLen) { //read off the rest of the pattern and make sure it's all wildcard for (; patIdx < patLen; patLen++) { if (pat[patIdx] != '*') { return false; } } return true; }
     * 
     * 
     * return !star; }
     */

    /**
     * Returns a prettified version of the XML, with indentations and linefeeds. Returns the original string if there was an error.
     */
    public static String prettifyXML(String xml)
    {
        String result = xml;
        try
        {
            StringReader reader = new StringReader(xml);
            StringWriter writer = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new StreamSource(reader), new StreamResult(writer));
            writer.close();

            result = writer.toString();
        }
        catch (TransformerFactoryConfigurationError error)
        {
            // Ignore.
        }
        catch (TransformerException error)
        {
            // Ignore.
        }
        catch (IOException error)
        {
            // Ignore.
        }
        return result;
    }

    // TODO UCdetector: Remove unused code:
    // /**
    // * Returns a prettified version of the string, or the original
    // * string if the operation is not possible.
    // */
    // public static String prettifyString(String string)
    // {
    // String result = string;
    // if (string.startsWith("<?xml"))
    // {
    // result = prettifyXML(string);
    // }
    // return result;
    // }

    // TODO UCdetector: Remove unused code:
    // /**
    // * Returns true if a string is null or empty.
    // */
    // public static boolean isEmpty(String string)
    // {
    // return string == null || string.length() == 0;
    // }
}
