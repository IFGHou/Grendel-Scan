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

import java.util.HashMap;
import java.util.Map;

/**
 * A simple registry that maps an alias to a concrete class name. This registry mimics the ActionScript 3 flash.net.registerClassAlias() functionality of the Flash Player. The registry is checked when
 * deserializing AMF object types.
 */
public class ClassAliasRegistry
{
    private Map aliasRegistry = new HashMap();
    private static final ClassAliasRegistry registry = new ClassAliasRegistry();

    /**
     * Constructs an empty registry.
     */
    private ClassAliasRegistry()
    {
    }

    /**
     * Returns the registry singleton.
     */
    public static ClassAliasRegistry getRegistry()
    {
        return registry;
    }

    /**
     * Looks for a concrete class name for an alias.
     * 
     * @param alias
     *            The alias used to search the registry.
     * @return a concrete class name, if registered for this alias, otherwise null.
     */
    public String getClassName(String alias)
    {
        return (String) aliasRegistry.get(alias);
    }

    // TODO UCdetector: Remove unused code:
    // /**
    // * Clears all items from the registry.
    // */
    // public void clear()
    // {
    // synchronized(aliasRegistry)
    // {
    // aliasRegistry.clear();
    // }
    // }

    /**
     * Registers a custom alias for a class name.
     * 
     * @param alias
     *            The alias for the class name.
     * @param className
     *            The concrete class name.
     */
    public void registerAlias(String alias, String className)
    {
        synchronized (aliasRegistry)
        {
            aliasRegistry.put(alias, className);
        }
    }

    // TODO UCdetector: Remove unused code:
    // /**
    // * Removes a class alias from the registry.
    // *
    // * @param alias The alias to be removed from the registry.
    // */
    // public void unregisterAlias(String alias)
    // {
    // synchronized(aliasRegistry)
    // {
    // aliasRegistry.remove(alias);
    // }
    // }
}
