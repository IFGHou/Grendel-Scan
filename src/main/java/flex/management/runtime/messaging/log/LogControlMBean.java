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
package flex.management.runtime.messaging.log;

import flex.management.BaseControlMBean;


/**
 * Defines the exposed properties and operations of the LogControl.
 */
public interface LogControlMBean extends BaseControlMBean
{
    /**
     * Returns the array of log targets.
     *
     * @return The array of log targets.
     */
    String[] getTargets();

    /**
     * Returns the array of log target filters.
     *
     * @param targetId The target id.
     * @return The array of log target filters.
     */
    String[] getTargetFilters(String targetId);

    /**
     * Returns the array of log categories.
     *
     * @return The array of log categories.
     */
    String[] getCategories();

    /**
     * Returns the target level.
     *
     * @param targetId The target id.
     * @return The target level.
     */
    Integer getTargetLevel(String targetId);

    /**
     * Changes the target level.
     *
     * @param targetId The target id.
     * @param level The target level.
     */
    void changeTargetLevel(String targetId, String level);

    /**
     * Adds a filter for the target.
     *
     * @param filter The filter.
     * @param targetId The target id.
     */
    void addFilterForTarget(String filter, String targetId);

    /**
     * Removes a filter from the target.
     *
     * @param filter The filter.
     * @param targetId The target id.
     */
    void removeFilterForTarget(String filter, String targetId);
}
