package com.grendelscan.tests.testModuleUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.grendelscan.logging.Log;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testModules.architecture.InputOutputFlows;
import com.grendelscan.tests.testModules.architecture.WebsiteMirror;
import com.grendelscan.tests.testModules.fileEnumeration.BackupFiles;
import com.grendelscan.tests.testModules.fileEnumeration.DirectoryEnumerator;
import com.grendelscan.tests.testModules.fileEnumeration.FileEnumerator;
import com.grendelscan.tests.testModules.hidden.TokenSubmitter;
import com.grendelscan.tests.testModules.informationLeakage.CommentLister;
import com.grendelscan.tests.testModules.informationLeakage.DirectoryListing;
import com.grendelscan.tests.testModules.informationLeakage.PlatformErrors;
import com.grendelscan.tests.testModules.informationLeakage.PrivateIPAddresses;
import com.grendelscan.tests.testModules.informationLeakage.RobotsTxt;
import com.grendelscan.tests.testModules.miscellaneous.CRLFInjection;
import com.grendelscan.tests.testModules.miscellaneous.CSRF;
import com.grendelscan.tests.testModules.miscellaneous.DirectoryTraversal;
import com.grendelscan.tests.testModules.miscellaneous.GenericFuzzer;
import com.grendelscan.tests.testModules.miscellaneous.PlaintextHTTP;
import com.grendelscan.tests.testModules.nikto.KnownVulnerabilities;
import com.grendelscan.tests.testModules.nikto.SoftwareVersion;
import com.grendelscan.tests.testModules.sessionManagement.AuthenticationBypass;
import com.grendelscan.tests.testModules.sessionManagement.CookieStrength;
import com.grendelscan.tests.testModules.sessionManagement.SessionFixation;
import com.grendelscan.tests.testModules.sessionManagement.URLSessionIDs;
import com.grendelscan.tests.testModules.spidering.AutoAuthentication;
import com.grendelscan.tests.testModules.spidering.FormSubmitter;
import com.grendelscan.tests.testModules.spidering.SearchEngineRecon;
import com.grendelscan.tests.testModules.spidering.TagRequester;
import com.grendelscan.tests.testModules.spidering.UrlRegex;
import com.grendelscan.tests.testModules.sqlInjection.SingleQuoteQuery;
import com.grendelscan.tests.testModules.sqlInjection.Tautologies;
import com.grendelscan.tests.testModules.webServerConfiguration.ProxyDetection;
import com.grendelscan.tests.testModules.webServerConfiguration.XST;
import com.grendelscan.tests.testModules.xss.QueryXSS;

/**
 * @author David Byrne
 * 
 */
public class MasterTestModuleCollection
{
	private Map<Class, TestModule>	allModules		= new HashMap<Class, TestModule>();
	

	private static MasterTestModuleCollection instance;
	
	public static void initialize()
	{
		instance = new MasterTestModuleCollection();
	}
	
	private MasterTestModuleCollection()
	{
		instantiateTestingModules();
		mapDependencies();
	}
	
	private void mapDependencies()
	{
		for (TestModule module: allModules.values())
		{
			for (Class prereq: module.getPrerequisites())
			{
				allModules.get(prereq).getDependents().add(module.getClass());
			}
		}
	}



	public Collection<TestModule> getAllTestModules()
	{
		return allModules.values();
	}

	public TestModule getTestModule(Class moduleClass)
	{
		if (!allModules.containsKey(moduleClass))
		{
			Log.debug("Module "
					+ moduleClass
					+ " doesn't seem to exist. This could be okay if it's a soft prerequisite. (MasterTestModuleCollection.getTestModule())");
		}
		return allModules.get(moduleClass);
	}



	private void instantiateTestingModules()
	{
		for (Class<?> moduleClass : getAllTestModuleClasses())
		{
			try
			{
				TestModule module = (TestModule) moduleClass.newInstance();
				allModules.put(module.getClass(), module);
			}
			catch (IllegalAccessException e)
			{
				Log.error("Some sort of problem loading testing module " + moduleClass.getCanonicalName() + ": " + e.toString(), e);
				continue;
			}
			catch (InstantiationException e)
			{
				Log.error("Some sort of problem loading testing module " + moduleClass.getCanonicalName() + ": " + e.toString(), e);
				continue;
			}

		}
	}
	
	private Class<?>[] getAllTestModuleClasses()
	{
		return new Class<?>[]{
			InputOutputFlows.class,
			WebsiteMirror.class,
			
			BackupFiles.class,
			DirectoryEnumerator.class,
			FileEnumerator.class,
			
			TokenSubmitter.class,
			
			CommentLister.class,
			DirectoryListing.class,
			PlatformErrors.class,
			PrivateIPAddresses.class,
			RobotsTxt.class,
			
			CRLFInjection.class,
			CSRF.class,
			DirectoryTraversal.class,
			GenericFuzzer.class,
			PlaintextHTTP.class,
			
			KnownVulnerabilities.class,
			SoftwareVersion.class,
			
			AuthenticationBypass.class,
			CookieStrength.class,
			SessionFixation.class,
			URLSessionIDs.class,
			
			AutoAuthentication.class,
			FormSubmitter.class,
			SearchEngineRecon.class,
			TagRequester.class,
			UrlRegex.class,
			
			SingleQuoteQuery.class,
			Tautologies.class,
			
			ProxyDetection.class,
			XST.class,
			
			QueryXSS.class
			
			
			
		};
	}

	public static MasterTestModuleCollection getInstance()
	{
		return instance;
	}

}
