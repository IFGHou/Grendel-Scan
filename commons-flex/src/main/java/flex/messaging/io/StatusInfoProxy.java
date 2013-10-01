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
package flex.messaging.io;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import flex.messaging.MessageException;

/**
 * Serializes a Throwable as a Status Info object, which is sent back to the client as an ActionScript object with the following keys:
 * <ul>
 * <li>code: We report this as <code>Server.Processing</code>. To be expanded in a future version.</li>
 * <li>description: The error message.</li>
 * <li>details: The error stack trace.</li>
 * <li>type: The error class name.</li>
 * <li>rootcause: If the exception was a ServletException, the rootcause will be included as a nested Status Info object.</li>
 * </ul>
 */
public class StatusInfoProxy extends AbstractProxy
{
    static final long serialVersionUID = 8860353096401173320L;

    /**
     * Brief description of the error. This is intended to describe the problem in a short manner that may be displayed to a human during run-time of the client's application.
     */
    public static final String DESCRIPTION = "description";

    /**
     * Long description of the error. This may contain a verbose description that may be provided to a more intelligent human for diagnosis.
     */
    public static final String DETAILS = "details";

    /**
     * The class name of the Throwable instance, if allowed to be revealed to the client...
     */
    public static final String CLASS = "type";

    /**
     * Hierarchical dot "." string description of the problem.
     * <p>
     * The top error string shall be on of the following....
     * </p>
     * Client - Indicates the error was caused by something the client did. Possibly the message was not well formed and/or did not contain the proper information.
     * <p>
     * Server - Indicates the error was caused by something not directly related to the contents of the message. Possibly the server could not gain a needed resource or communicate with the desire
     * WebService.
     * </p>
     * <p>
     * VersionMismatch - The received AMF packet was of a version that the Server does not recognize.
     * </p>
     * <p>
     * The server can generate any number of sub errors to be attached to one of the 3 above roots. The idea is that these strings provide an intuitive description of where the problem happened and
     * what it was. Ultimately, finding and fixing the errors fast is the goal.
     * </p>
     */
    public static final String CODE = "code";

    public static final String ROOTCAUSE = "rootcause";

    public static final List propertyNameCache = new ArrayList();
    static
    {
        propertyNameCache.add(CODE);
        propertyNameCache.add(CLASS);
        propertyNameCache.add(DESCRIPTION);
        propertyNameCache.add(DETAILS);
        propertyNameCache.add(ROOTCAUSE);
    }

    protected boolean showStacktraces;

    public StatusInfoProxy()
    {
        super(null);
    }

    public StatusInfoProxy(Throwable defaultInstance)
    {
        super(defaultInstance);
    }

    public void setShowStacktraces(boolean value)
    {
        showStacktraces = value;
    }

    @Override
    public String getAlias(Object instance)
    {
        // Status Info objects are anonymous
        return null;
    }

    @Override
    public List getPropertyNames(Object instance)
    {
        return propertyNameCache;
    }

    @Override
    public Class getType(Object instance, String propertyName)
    {
        Class type = null;

        if (CODE.equals(propertyName))
        {
            type = String.class;
        }
        else if (CLASS.equals(propertyName))
        {
            type = String.class;
        }
        else if (DESCRIPTION.equals(propertyName))
        {
            type = String.class;
        }
        else if (DETAILS.equals(propertyName))
        {
            type = String.class;
        }
        else if (ROOTCAUSE.equals(propertyName))
        {
            type = Map.class;
        }

        return type;
    }

    @Override
    public Object getValue(Object instance, String propertyName)
    {
        Object value = null;

        if (CODE.equals(propertyName))
        {
            value = getCode(instance);
        }
        else if (CLASS.equals(propertyName))
        {
            value = getType(instance);
        }
        else if (DESCRIPTION.equals(propertyName))
        {
            value = getDescription(instance);
        }
        else if (DETAILS.equals(propertyName))
        {
            value = getDetails(instance);
        }
        else if (ROOTCAUSE.equals(propertyName))
        {
            value = getRootCause(instance);
        }

        return value;
    }

    @Override
    public void setValue(Object instance, String propertyName, Object value)
    {
        return; // Throwable is essentially read-only
    }

    private String getCode(Object ex)
    {
        String code = null;
        if (ex instanceof MessageException)
        {
            code = ((MessageException) ex).getCode();
        }

        if (code == null)
        {
            code = "Server.Processing";
        }

        return code;
    }

    private String getType(Object ex)
    {
        String type = "";
        if (ex != null && showStacktraces)
        {
            type = ex.getClass().getName();
        }
        return type;
    }

    private String getDescription(Object ex)
    {
        String desc = null;
        if (ex instanceof Throwable)
        {
            desc = ((Throwable) ex).getMessage();
        }

        return desc;
    }

    private String getDetails(Object ex)
    {
        StringBuffer details = new StringBuffer();
        if (ex instanceof MessageException)
        {
            MessageException e = (MessageException) ex;
            if (e.getDetails() != null)
                details.append(e.getDetails());
        }

        if (showStacktraces && ex instanceof Throwable)
            details.append(getTraceback((Throwable) ex));

        return details.toString();
    }

    private Map getRootCause(Object ex)
    {
        if (ex == null)
            return null;

        if (ex instanceof ServletException)
        {
            ex = ((ServletException) ex).getRootCause();
        }

        if (ex instanceof Throwable)
            return getExceptionInfo((Throwable) ex);
        else
            return null;
    }

    private Map getExceptionInfo(Throwable t)
    {
        Map info = new HashMap();
        info.put(CODE, getCode(t));
        info.put(CLASS, getType(t));
        info.put(DESCRIPTION, getDescription(t));
        info.put(DETAILS, getDetails(t));
        info.put(ROOTCAUSE, getRootCause(t));
        return info;
    }

    private static String getTraceback(Throwable e)
    {
        String trace = "";

        if (e != null)
        {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintWriter pr = new PrintWriter(outputStream);
            pr.println();
            e.printStackTrace(pr);
            pr.flush();
            trace = outputStream.toString();
        }

        return trace;
    }

    @Override
    public Object clone()
    {
        return super.clone();
    }
}
