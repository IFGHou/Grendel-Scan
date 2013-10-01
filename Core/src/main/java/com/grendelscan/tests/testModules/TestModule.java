/*
 * TestModule.java
 * 
 * Created on September 15, 2007, 9:01 PM
 * 
 * To change this template, choose Tools | Template Manager and open the
 * template in the editor.
 */

package com.grendelscan.tests.testModules;

import java.util.ArrayList;
import java.util.List;

import com.grendelscan.requester.RequestOptions;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.tests.testModuleUtils.TestModuleGUIPath;
import com.grendelscan.tests.testModuleUtils.settings.ConfigurationOption;

/**
 * 
 * @author Administrator
 */
public abstract class TestModule
{
	// public abstract boolean isSessionSensative();
	private final List<ConfigurationOption>	configurationOptions;
	protected final RequestOptions requestOptions;
	private List<Class<? extends TestModule>> dependents;
	
	
	public TestModule()
	{
		dependents = new ArrayList<Class<? extends TestModule>>(1);
		configurationOptions = new ArrayList<ConfigurationOption>();
		requestOptions = new RequestOptions();
		requestOptions.reason = getName();
		requestOptions.testTransaction = false;
	}
	
	protected void addConfigurationOption(ConfigurationOption option)
	{
		if (option == null)
		{
			throw new IllegalArgumentException("Cannot add a null option");
		}
		configurationOptions.add(option);
	}

	protected void handlePause_isRunning() throws InterruptedScanException 
	{
		Scan.getInstance().getTesterQueue().handlePause_isRunning();
	}
	
	public boolean alwaysEnabled()
	{
		return false;
	}

	public final List<ConfigurationOption> getConfigurationOptions()
	{
		return configurationOptions;
	}

	public abstract String getDescription();

	public String getExperimentalText()
	{
		return "";
	}

	public abstract TestModuleGUIPath getGUIDisplayPath();

	public abstract String getName();

	public Class<? extends TestModule>[] getPrerequisites()
	{
		return new Class[0];
	}

	public Class<? extends TestModule>[] getSoftPrerequisites()
	{
		return new Class[0];
	}

	public boolean hidden()
	{
		return false;
	}

	public abstract boolean isExperimental();

	public final List<Class<? extends TestModule>> getDependents()
	{
		return dependents;
	}

}
