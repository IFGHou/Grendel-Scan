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

/**
 * Throwable instances are treated as a special type of Bean as usually properties are read only but need to be serialized.
 * 
 * @author Peter Farland
 */
public class ThrowableProxy extends BeanProxy
{
    static final long serialVersionUID = 6363249716988887262L;

    public ThrowableProxy()
    {
        super();
        includeReadOnly = true;
    }

    /*
     * TODO UCdetector: Remove unused code: public ThrowableProxy(Throwable defaultInstance) { super(defaultInstance); includeReadOnly = true; }
     */

    /** {@inheritDoc} */
    @Override
    public Object clone()
    {
        return super.clone();
    }
}
