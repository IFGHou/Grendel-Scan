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
package flex.management;

import flex.messaging.log.Log;
import flex.messaging.log.LogCategories;
import flex.messaging.util.ClassUtil;

/**
 * Factory to get a <code>MBeanServerLocator</code>.
 * 
 * @author shodgson
 */
public class MBeanServerLocatorFactory
{
    //--------------------------------------------------------------------------
    //
    // Private Static Variables
    //
    //--------------------------------------------------------------------------
    
    /**
     * The MBeanServerLocator impl to use; lazily init'ed on first access.
     */
    private static MBeanServerLocator locator;

    //--------------------------------------------------------------------------
    //
    // Public Static Methods
    //
    //--------------------------------------------------------------------------
    
    /**
     * Returns a <code>MBeanServerLocator</code> that exposes the <code>MBeanServer</code> to register MBeans with.
     * 
     * @return The <code>MBeanServerLocator</code> that exposes the <code>MBeanServer</code> to register MBeans with.
     */
    public static synchronized MBeanServerLocator getMBeanServerLocator()
    {
        if (locator == null)
        {
            // Try app-server specific locators.
            // WebSphere provides access to its MBeanServer via a custom admin API.
            instantiateLocator("flex.management.WebSphereMBeanServerLocator", new String[] {"com.ibm.websphere.management.AdminServiceFactory"});

            // Try Sun JRE 1.5 based implementation
            if (locator == null)
                instantiateLocator("flex.management.PlatformMBeanServerLocator", new String[] {"java.lang.management.ManagementFactory"});
            
            // Try Sun JRE 1.4 based default implementation
            if (locator == null)
                instantiateLocator("flex.management.DefaultMBeanServerLocator", null);
            
            if (Log.isDebug())
                Log.getLogger(LogCategories.MANAGEMENT_GENERAL).debug("Using MBeanServerLocator: " + locator.getClass().getName());
        }
        return locator;
    }


// TODO UCdetector: Remove unused code: 
//     /**
//      * Release static MBeanServerLocator
//      * Called on MessageBroker shutdown.
//      */
//     public static void clear()
//     {
//         locator = null;
//     }
    
    //--------------------------------------------------------------------------
    //
    // Private Static Methods
    //
    //--------------------------------------------------------------------------
    
    /**
     * Helper method that attempts to load a specific MBeanServerLocator.
     * 
     * @param locatorClassName The classname of the desired MBeanServerLocator.
     * @param dependencyClassNames Any additional dependent classnames that the desired locator depends upon
     *                            that should also be tested for availability.
     */
    private static void instantiateLocator(String locatorClassName, String[] dependencyClassNames)
    {
        try
        {
            if (dependencyClassNames != null)
            {
                for (int i = 0; i < dependencyClassNames.length; i++)
                    ClassUtil.createClass(dependencyClassNames[i]);
            }
            
            Class locatorClass = ClassUtil.createClass(locatorClassName);
            locator = (MBeanServerLocator)locatorClass.newInstance();
        }
        catch (Throwable t)
        {
            if (Log.isDebug())
                Log.getLogger(LogCategories.MANAGEMENT_MBEANSERVER).debug("Not using MBeanServerLocator: " + locatorClassName + ". Reason: " + t.getMessage());
        }
    }
    
    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------
    
    /**
     * Direct instantiation is not allowed.
     * Use <code>getMBeanServerLocator()</code> to obtain a <code>MBeanServerLocator</code> 
     * instance to lookup the proper MBean server to use.
     */
    private MBeanServerLocatorFactory() {}
    
}