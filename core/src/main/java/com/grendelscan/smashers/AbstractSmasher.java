/*
 * AbstractSmasher.java
 * 
 * Created on September 15, 2007, 9:01 PM
 * 
 * To change this template, choose Tools | Template Manager and open the template in the editor.
 */

package com.grendelscan.smashers;

import java.util.ArrayList;
import java.util.List;

import com.grendelscan.commons.http.RequestOptions;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.smashers.settings.ConfigurationOption;

/**
 * 
 * @author Administrator
 */
public abstract class AbstractSmasher
{
    // public abstract boolean isSessionSensative();
    private final List<ConfigurationOption> configurationOptions;
    protected final RequestOptions requestOptions;
    private final List<Class<? extends AbstractSmasher>> dependents;

    public AbstractSmasher()
    {
        dependents = new ArrayList<Class<? extends AbstractSmasher>>(1);
        configurationOptions = new ArrayList<ConfigurationOption>();
        requestOptions = new RequestOptions();
        requestOptions.reason = getName();
        requestOptions.testTransaction = false;
    }

    protected void addConfigurationOption(final ConfigurationOption option)
    {
        if (option == null)
        {
            throw new IllegalArgumentException("Cannot add a null option");
        }
        configurationOptions.add(option);
    }

    public boolean alwaysEnabled()
    {
        return false;
    }

    public final List<ConfigurationOption> getConfigurationOptions()
    {
        return configurationOptions;
    }

    public final List<Class<? extends AbstractSmasher>> getDependents()
    {
        return dependents;
    }

    public abstract String getDescription();

    public String getExperimentalText()
    {
        return "";
    }

    public abstract TestModuleGUIPath getGUIDisplayPath();

    public abstract String getName();

    public Class<? extends AbstractSmasher>[] getPrerequisites()
    {
        return new Class[0];
    }

    public Class<? extends AbstractSmasher>[] getSoftPrerequisites()
    {
        return new Class[0];
    }

    protected void handlePause_isRunning() throws InterruptedScanException
    {
        Scan.getInstance().getTesterQueue().handlePause_isRunning();
    }

    public boolean hidden()
    {
        return false;
    }

    public abstract boolean isExperimental();

}
