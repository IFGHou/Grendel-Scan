/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2002 - 2007 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/
package flex.messaging.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import flex.messaging.MessageException;

/**
 * Utility class for converting strings to XML documents and
 * vice versa.  
 *
 * @exclude
 */
public class XMLUtil
{
    public static String INDENT_XML = "no";
    public static String OMIT_XML_DECLARATION = "yes";

    private XMLUtil()
    {
    }

    /**
     * Uses a TransformerFactory with an identity transformation to convert a
     * Document into a String representation of the XML.
     *
     * @param document Document.
     * @return An XML String.
     * @throws IOException if an error occurs during transformation.
     */
    public static String documentToString(Document document) throws IOException
    {
        String xml = null;

        try
        {
            DOMSource dom = new DOMSource(document);
            StringWriter writer = new StringWriter();
            StreamResult output = new StreamResult(writer);

            // Use Transformer to serialize a DOM
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();

            // No need for pretty printing
            transformer.setOutputProperty(OutputKeys.INDENT, INDENT_XML);

            // XML Declarations unexpected whitespace for legacy AS XMLDocument type,
            // so we always omit it. We can't tell whether one was present when
            // constructing the Document in the first place anyway...
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, OMIT_XML_DECLARATION);

            transformer.transform(dom, output);

            xml = writer.toString();
        }
        catch (TransformerException te)
        {
            throw new IOException("Error serializing Document as String: " + te.getMessageAndLocation());
        }
        return xml;
    }

    /**
     * Uses the current DocumentBuilderFactory to converts a String
     * representation of XML into a Document.
     *
     * @param xml XML serialized as a String
     * @return Document
     */
    public static Document stringToDocument(String xml)
    {
        return stringToDocument(xml, true);
    }

    /**
     * Uses the current DocumentBuilderFactory to converts a String
     * representation of XML into a Document.
     *
     * @param xml XML serialized as a String
     * @param nameSpaceAware determines whether the constructed Document
     * is name-space aware
     * @return Document
     */
    public static Document stringToDocument(String xml, boolean nameSpaceAware)
    {
        Document document = null;
        try
        {
            if (xml != null)
            {
                StringReader reader = new StringReader(xml);
                InputSource input = new InputSource(reader);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(nameSpaceAware);
                factory.setValidating(false);
                DocumentBuilder builder = factory.newDocumentBuilder();

                document = builder.parse(input);
            }
        }
        catch (Exception ex)
        {
            throw new MessageException("Error deserializing XML type " + ex.getMessage());
        }

        return document;
    }
}
