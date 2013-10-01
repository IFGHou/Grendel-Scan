/*************************************************************************
 * 
 * ADOBE CONFIDENTIAL __________________
 * 
 * [2002] - [2007] Adobe Systems Incorporated All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains the property of Adobe Systems Incorporated and its suppliers, if any. The intellectual and technical concepts contained herein are
 * proprietary to Adobe Systems Incorporated and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of
 * this information or reproduction of this material is strictly forbidden unless prior written permission is obtained from Adobe Systems Incorporated.
 **************************************************************************/
package flex.messaging;

/**
 * Implementors of <code>FlexFactory</code> should also implement this interface if their factory has custom destruction behavior.
 */
public interface DestructibleFlexFactory
{
    /**
     * This method is called when a component that uses this factory is removed. This method gives the factory a chance to clean up resources that may have been allocated for the component and may now
     * be ready for destruction.
     * 
     * @param instanceInfo
     *            The FactoryInstance to be destroyed
     * 
     */
    void destroyFactoryInstance(FactoryInstance instanceInfo);
}
