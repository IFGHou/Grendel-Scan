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
package flex.management.runtime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import flex.management.BaseControl;

/**
 * @exclude
 */
public class AdminConsoleDisplayRegistrar extends BaseControl implements AdminConsoleDisplayRegistrarMBean
{
    public static final String ID = "AdminConsoleDisplay";
    
    private HashMap registeredExposedObjects;
    
    public AdminConsoleDisplayRegistrar(BaseControl parent)
    {
        super(parent);
        registeredExposedObjects = new HashMap();
        register();
    }

    public void registerObject(int type, String beanName, String propertyName)
    {
        Object objects = registeredExposedObjects.get(new Integer(type));
        if (objects != null)
        {
            ((ArrayList)objects).add(beanName + ":" + propertyName);
        }
        else
        {
            if (type < 1)
                return;
            
            objects = new ArrayList();
            ((ArrayList)objects).add(beanName + ":" + propertyName);
            registeredExposedObjects.put(new Integer(type), objects);
        }
    }
    
    public void registerObjects(int type, String beanName, String[] propertyNames)
    {
        for (int i = 0; i < propertyNames.length; i++)
        {
            registerObject(type, beanName, propertyNames[i]);
        }
    }
    
    public void registerObjects(int[] types, String beanName, String[] propertyNames)
    {
        for (int j = 0; j < types.length; j++)
        {
            registerObjects(types[j], beanName, propertyNames);
        }
    }
    
    @Override public Integer[] getSupportedTypes() throws IOException
    {
        Object[] array = registeredExposedObjects.keySet().toArray();
        Integer[] types = new Integer[array.length];
        for (int i = 0; i < array.length; i++)
        {
            types[i] = (Integer)array[i];
        }
        return types;
    }

    @Override public String[] listForType(int type) throws IOException
    {
        Object list = registeredExposedObjects.get(new Integer(type));
        
        return (list != null) ? (String[]) ((ArrayList)list).toArray(new String[0]) : new String[0];
    }

    @Override public String getId()
    {
        return ID;
    }

    @Override public String getType()
    {
        return ID;
    }


}
