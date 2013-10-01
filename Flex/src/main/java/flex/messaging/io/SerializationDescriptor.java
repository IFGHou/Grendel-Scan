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
package flex.messaging.io;

import java.util.HashMap;
import java.util.List;

/**
 * The SerializationProxy uses this descriptor to determine which
 * fields and properties should be excluded from an object graph
 * on an instance-by-instance basis. By default, all public instance
 * variables and properties will be serialized.
 *
 * If excludes need to be specified for a complex child property,
 * the property name is added to this dynamic descriptor and its value
 * set to another descriptor with its own set of excludes.
 *
 * The absence of excludes implies default serialization. The absence of
 * a child property implies default serialization.
 *  
 * @see flex.messaging.io.PropertyProxy
 */
public class SerializationDescriptor extends HashMap
{
    static final long serialVersionUID = 1828426777611186569L;

    private List excludes;

    public SerializationDescriptor()
    {
        super();
    }

    public List getExcludesForInstance(Object instance)
    {
        return excludes;
    }

    /* 
     * Deprecated in favor of getExcludesForInstance(instance).
     */
    public List getExcludes()
    {
        return excludes;
    }

    public void setExcludes(List excludes)
    {
        this.excludes = excludes;
    }

    @Override public String toString()
    {
        return "[ excludes: " + excludes + "]";
    }
}
