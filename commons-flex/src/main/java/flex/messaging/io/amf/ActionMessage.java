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
package flex.messaging.io.amf;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class represents a protocol message with its collection of headers and bodies.
 * 
 * @author Simeon Simeonov (simeons@macromedia.com)
 * @exclude
 */
public class ActionMessage implements Serializable
{
    static final long serialVersionUID = 7970778672727624188L;

    /**
     * Current protocol version is 3.
     */
    public static final int CURRENT_VERSION = 3;

    /**
     * Protocol version for the message.
     */
    private int version;

    /**
     * Ordered collection of message headers.
     */
    private ArrayList headers = null;

    /**
     * Ordered collection of message bodies.
     */
    private ArrayList bodies = null;

    /**
     * Create a message using the current protocol version and UTF-8 encoding.
     */
    public ActionMessage()
    {
        version = CURRENT_VERSION;
        headers = new ArrayList();
        bodies = new ArrayList();
    }

    // TODO UCdetector: Remove unused code:
    // /**
    // * Create a message with a given protocol version and encoding.
    // *
    // * @param version Protocol version
    // */
    // public ActionMessage(int version)
    // {
    // this.version = version;
    // headers = new ArrayList();
    // bodies = new ArrayList();
    // }

    /**
     * Retrieve the protocol version of the message.
     * 
     * @return protocol version of current message
     */
    public int getVersion()
    {
        return version;
    }

    /**
     * Override the protocol version of the message.
     * 
     * @param version
     *            protocol version of current message
     */
    public void setVersion(int version)
    {
        this.version = version;
    }

    /**
     * Retrieve the number of headers in the message.
     * 
     * @return number of headers in message
     */
    public int getHeaderCount()
    {
        return headers.size();
    }

    /**
     * Retrieve a particular message header.
     * 
     * @param pos
     *            MessageHeader position [0, getHeaderCount())
     * @return The header at the specified position
     */
    public MessageHeader getHeader(int pos)
    {
        return (MessageHeader) headers.get(pos);
    }

    /**
     * Retrieve all message header.
     * 
     * @return The headers as an ArrayList Note: primarily for use by the MessageProcessor
     */
    public ArrayList getHeaders()
    {
        return headers;
    }

    /**
     * Add a header to the message.
     * 
     * @param h
     *            MessageHeader to add
     */
    public void addHeader(MessageHeader h)
    {
        headers.add(h);
    }

    /**
     * Retrieve the number of bodies in the message.
     * 
     * @return number of bodies in message
     */
    public int getBodyCount()
    {
        return bodies.size();
    }

    /**
     * Retrieve a particular message body.
     * 
     * @param pos
     *            MessageBody position [0, getBodyCount())
     * @return The body at the specified position
     */
    public MessageBody getBody(int pos)
    {
        return (MessageBody) bodies.get(pos);
    }

    /**
     * Retrieve all message bodies.
     * 
     * @return The bodies as an ArrayList Note: primarily for use by the MessageProcessor
     */
    public ArrayList getBodies()
    {
        return bodies;
    }

    /**
     * Add a body to the message.
     * 
     * @param b
     *            MessageBody to add
     */
    public void addBody(MessageBody b)
    {
        bodies.add(b);
    }
}
