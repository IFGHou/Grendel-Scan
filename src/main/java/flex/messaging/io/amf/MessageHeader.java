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
package flex.messaging.io.amf;

import java.io.Serializable;

/**
 * Message Headers provide context for the processing of the
 * remainder of the AMF Packet and all subsequent Messages.
 * Notable uses for this construct would be for encryption of
 * the remaining AMF Packet and/or authentication of the user
 * to the server (username/password).
 * <p>
 * Multiple Message Headers may be included within an AMF Packet.
 * </p>
 *
 * @author Simeon Simeonov (simeons@macromedia.com)
 * @exclude
 */
public class MessageHeader implements Serializable
{
    static final long serialVersionUID = -4535655145953153945L;

    private String name;
    private boolean mustUnderstand;
    private Object data;

    public MessageHeader()
    {
    }

    public MessageHeader(String name, boolean mustUnderstand, Object data)
    {
        this.name = name;
        this.mustUnderstand = mustUnderstand;
        this.data = data;
    }


    public String getName()
    {
        return name;
    }


    public void setName(String name)
    {
        this.name = name;
    }


    public boolean getMustUnderstand()
    {
        return mustUnderstand;
    }


    public void setMustUnderstand(boolean mustUnderstand)
    {
        this.mustUnderstand = mustUnderstand;
    }


    public Object getData()
    {
        return data;
    }

    public void setData(Object data)
    {
        this.data = data;
    }

}

