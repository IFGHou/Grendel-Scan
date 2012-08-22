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
package flex.messaging.services.messaging;

/**
 * @exclude
 */
public interface MessagingConstants
{
    // General constants.
    /**
     * Default maximum cache size to use if MAX_CACHE_SIZE is not specified in
     * the configuration.
     */
    int DEFAULT_MAX_CACHE_SIZE = 0;
    /**
     * The default subtopic separator value.
     */
    String DEFAULT_SUBTOPIC_SEPARATOR = ".";

    // Configuration element constants (for properties in services-config.xml)
    /**
     * Constant for the allow-subtopics configuration element.
     */
    String ALLOW_SUBTOPICS_ELEMENT = "allow-subtopics";
    /**
     * Constant for the durable configuration element.
     */
    String IS_DURABLE_ELEMENT = "durable";
    /**
     * Constant for the <max-cache-size/> configuration element.
     */
    String MAX_CACHE_SIZE_ELEMENT = "max-cache-size";
    /**
     * Constant for the <message-time-to-live/> configuration element.
     */
    String TIME_TO_LIVE_ELEMENT = "message-time-to-live";
    /**
     * Constant for the <subtopic-separator/> configuration element.
     */
    String SUBTOPIC_SEPARATOR_ELEMENT = "subtopic-separator";
    /**
     * Constant for the cluster message routing element.
     */
    String CLUSTER_MESSAGE_ROUTING = "cluster-message-routing";
}
