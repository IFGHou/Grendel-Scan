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
package flex.messaging.config;

import flex.messaging.log.LogCategories;

/**
 * ConfigurationManager interface
 * 
 * The default implementation of the configuration manager is FlexConfigurationManager. However, this value be specified in a servlet init-param &quot;services.configuration.manager&quot; to the
 * MessageBrokerServlet.
 * 
 * @exclude
 */
public interface ConfigurationManager
{
    String LOG_CATEGORY = LogCategories.CONFIGURATION;

    /*
     * TODO UCdetector: Remove unused code: MessagingConfiguration getMessagingConfiguration(ServletConfig servletConfig);
     */

    /*
     * TODO UCdetector: Remove unused code: void reportTokens();
     */
}
