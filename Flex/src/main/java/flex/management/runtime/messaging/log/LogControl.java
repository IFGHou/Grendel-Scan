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

import flex.management.BaseControl;
import flex.messaging.log.AbstractTarget;
import flex.messaging.log.Log;
import flex.messaging.log.Target;

/**
 * The <code>LogControl</code> class is the MBean implemenation
 * for monitoring and managing a <code>Log</code> at runtime through the <code>LogManager</code>.
 * @author majacobs
 *
 */
public class LogControl extends BaseControl implements
        LogControlMBean
{

    private static final String TYPE = "Log"; // The type registered with the mbean server
    private LogManager logManager; // Reference to the LogManager which interfaces with Log
        
    
    /**
     * Creates the mbean and registers it with the mbean server.
     * 
     * @param parent BaseControl
     * @param manager A reference to the LogManager
     */
    public LogControl(BaseControl parent, LogManager manager)
    {
        super(parent);
        this.logManager = manager;
        register();
    }
    
    
    /**
     * Sets the logging level for the target associated with the unique ID searchId.
     * @param searchId 
     * @param level
     */
    @Override public void changeTargetLevel(String searchId, String level)
    {
        Target selectedTarget = Log.getTarget(searchId);
        if (selectedTarget != null)
        {
            selectedTarget.setLevel(new Short(level).shortValue());
        }
    }
    
    /* (non-Javadoc)
     * @see flex.management.BaseControl#getId()
     */
    @Override public String getId()
    {
        return logManager.getId();
    }
    
    /* (non-Javadoc)
     * @see flex.management.BaseControl#getType()
     */
    @Override public String getType()
    {
        return TYPE;
    }

    /**
     * @return a string array of loggers
     */
    public String[] getLoggers()
    {
        return logManager.getLoggers();
    }

    /* (non-Javadoc)
     * @see flex.management.runtime.messaging.log.LogControlMBean#getTargets()
     */
    @Override public String[] getTargets()
    {
        return logManager.getTargetIds();
    }

    /* (non-Javadoc)
     * @see flex.management.runtime.messaging.log.LogControlMBean#addFilterForTarget(java.lang.String, java.lang.String)
     */
    @Override public void addFilterForTarget(String targetId, String filter)
    {
        AbstractTarget target = (AbstractTarget) logManager.getTarget(targetId);
        
        if (target != null)
        {
            if (logManager.checkFilter(filter))
                target.addFilter(filter);
        }
        
    }

    /* (non-Javadoc)
     * @see flex.management.runtime.messaging.log.LogControlMBean#getTargetFilters(java.lang.String)
     */
    @Override public String[] getTargetFilters(String targetId)
    {
        return logManager.getTargetFilters(targetId);
    }

    /* (non-Javadoc)
     * @see flex.management.runtime.messaging.log.LogControlMBean#removeFilterForTarget(java.lang.String, java.lang.String)
     */
    @Override public void removeFilterForTarget(String targetId, String filter)
    {
        AbstractTarget target = (AbstractTarget) logManager.getTarget(targetId);
        
        if (target != null)
        {
            if (target.containsFilter(filter))
                target.removeFilter(filter);
        }
    }
    
    /* (non-Javadoc)
     * @see flex.management.runtime.messaging.log.LogControlMBean#getCategories()
     */
    @Override public String[] getCategories()
    {
        return (String[]) logManager.getCategories().toArray(new String[0]);
    }


    @Override public Integer getTargetLevel(String searchId)
    {
        AbstractTarget target = (AbstractTarget) logManager.getTarget(searchId);
        
        if (target != null)
        {
            return new Integer(target.getLevel());
        } else
            return new Integer(-1);
    }
    
}
