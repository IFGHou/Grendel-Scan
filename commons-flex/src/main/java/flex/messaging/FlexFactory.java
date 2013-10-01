/*************************************************************************
 * 
 * ADOBE CONFIDENTIAL __________________
 * 
 * Copyright 2008 Adobe Systems Incorporated All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains the property of Adobe Systems Incorporated and its suppliers, if any. The intellectual and technical concepts contained herein are
 * proprietary to Adobe Systems Incorporated and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of
 * this information or reproduction of this material is strictly forbidden unless prior written permission is obtained from Adobe Systems Incorporated.
 **************************************************************************/
package flex.messaging;

import flex.messaging.config.ConfigMap;

/**
 * The FlexFactory interface is implemented by factory components that provide instances to the Flex messaging framework. You can implement this interface if you want to tie together Flex Data
 * Services with another system which maintains component instances (often called the "services layer" in a typical enterprise architecture). By implementing FlexFactory, you can configure a Flex
 * RemoteObject destination or a Flex Data Management Services assembler which uses a Java object instance in your services layer rather than having FDS create a new component instance. In some cases,
 * this means you avoid writing glue code for each service you want to expose to flex clients.
 */
public interface FlexFactory extends FlexConfigurable
{
    /** Request scope string. */
    String SCOPE_REQUEST = "request";
    /** Session scope string. */
    String SCOPE_SESSION = "session";
    /** Application scope string . */
    String SCOPE_APPLICATION = "application";
    /** Scope string. */
    String SCOPE = "scope";
    /** Source string. */
    String SOURCE = "source";

    /**
     * Called when the definition of an instance that this factory looks up is initialized. It should validate that the properties supplied are valid to define an instance and returns an instance of
     * the type FactoryInstance that contains all configuration necessary to construct an instance of this object. If the instance is application scoped, the FactoryInstance may contain a reference to
     * the instance directly.
     * <p>
     * Any valid properties used for this configuration must be accessed to avoid warnings about unused configuration elements. If your factory is only used for application scoped components, you do
     * not need to implement this method as the lookup method itself can be used to validate its configuration.
     * </p>
     * <p>
     * The id property is used as a name to help you identify this factory instance for any errors it might generate.
     * </p>
     * 
     */
    FactoryInstance createFactoryInstance(String id, ConfigMap properties);

    /**
     * This method is called by the default implementation of FactoryInstance.lookup. When FDS wants an instance of a given factory destination, it calls the FactoryInstance.lookup to retrieve that
     * instance. That method in turn calls this method by default.
     * 
     * For simple FlexFactory implementations which do not need to add additional configuration properties or logic to the FactoryInstance class, by implementing this method you can avoid having to
     * add an additional subclass of FactoryInstance for your factory. If you do extend FactoryInstance, it is recommended that you just override FactoryInstance.lookup and put your logic there to
     * avoid the extra level of indirection.
     * 
     * @param instanceInfo
     *            The FactoryInstance for this destination
     * @return the Object instance to use for the given operation for this destination.
     */
    Object lookup(FactoryInstance instanceInfo);
}
