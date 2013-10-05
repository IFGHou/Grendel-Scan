/*
 * AbstractSmasher.java
 * 
 * Created on September 15, 2007, 9:01 PM
 * 
 * To change this template, choose Tools | Template Manager and open the template in the editor.
 */

package com.grendelscan.testing.modules;

import java.util.ArrayList;
import java.util.List;

import com.grendelscan.commons.http.RequestOptions;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.testing.modules.settings.ConfigurationOption;
import com.grendelscan.testing.modules.settings.TestModuleGUIPath;

/**
 * 
 * @author Administrator
 */
public abstract class AbstractTestModule
{
    // public abstract boolean isSessionSensative();
    private final List<ConfigurationOption> configurationOptions;
    protected final RequestOptions requestOptions;
    private final List<Class<? extends AbstractTestModule>> dependents;

    public AbstractTestModule()
    {
        dependents = new ArrayList<Class<? extends AbstractTestModule>>(1);
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

    public final List<Class<? extends AbstractTestModule>> getDependents()
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

    public Class<? extends AbstractTestModule>[] getPrerequisites()
    {
        return new Class[0];
    }

    public Class<? extends AbstractTestModule>[] getSoftPrerequisites()
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
