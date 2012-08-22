/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  [2002] - [2007] Adobe Systems Incorporated
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
package flex.messaging.config;

/**
 * @exclude
 */
public class FlexClientSettings
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------

    /**
     * Constructs a FlexClientSettings instance.
     */
    public FlexClientSettings()
    {
        // Empty for now.
    }

    //--------------------------------------------------------------------------
    //
    // Properties
    //
    //--------------------------------------------------------------------------

    private long timeoutMinutes;

    /**
     * Returns the number of minutes before an idle FlexClient is timed out.
     *
     * @return The number of minutes before an idle FlexClient is timed out.
     */
    public long getTimeoutMinutes()
    {
        return timeoutMinutes;
    }

    /**
     * Sets the number of minutes before an idle FlexClient is timed out.
     *
     * @param value The number of minutes before an idle FlexClient is timed out.
     */
    public void setTimeoutMinutes(long value)
    {
        timeoutMinutes = value;
    }
}
