package com.grendelscan.smashers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.smashers.architecture.InputOutputFlows;
import com.grendelscan.smashers.architecture.WebsiteMirror;
import com.grendelscan.smashers.fileEnumeration.BackupFiles;
import com.grendelscan.smashers.fileEnumeration.DirectoryEnumerator;
import com.grendelscan.smashers.fileEnumeration.FileEnumerator;
import com.grendelscan.smashers.hidden.TokenSubmitter;
import com.grendelscan.smashers.informationLeakage.CommentLister;
import com.grendelscan.smashers.informationLeakage.DirectoryListing;
import com.grendelscan.smashers.informationLeakage.PlatformErrors;
import com.grendelscan.smashers.informationLeakage.PrivateIPAddresses;
import com.grendelscan.smashers.informationLeakage.RobotsTxt;
import com.grendelscan.smashers.miscellaneous.CRLFInjection;
import com.grendelscan.smashers.miscellaneous.CSRF;
import com.grendelscan.smashers.miscellaneous.DirectoryTraversal;
import com.grendelscan.smashers.miscellaneous.GenericFuzzer;
import com.grendelscan.smashers.miscellaneous.PlaintextHTTP;
import com.grendelscan.smashers.nikto.KnownVulnerabilities;
import com.grendelscan.smashers.nikto.SoftwareVersion;
import com.grendelscan.smashers.sessionManagement.AuthenticationBypass;
import com.grendelscan.smashers.sessionManagement.CookieStrength;
import com.grendelscan.smashers.sessionManagement.SessionFixation;
import com.grendelscan.smashers.sessionManagement.URLSessionIDs;
import com.grendelscan.smashers.spidering.AutoAuthentication;
import com.grendelscan.smashers.spidering.FormSubmitter;
import com.grendelscan.smashers.spidering.SearchEngineRecon;
import com.grendelscan.smashers.spidering.TagRequester;
import com.grendelscan.smashers.spidering.UrlRegex;
import com.grendelscan.smashers.sqlInjection.SingleQuoteQuery;
import com.grendelscan.smashers.sqlInjection.Tautologies;
import com.grendelscan.smashers.webServerConfiguration.ProxyDetection;
import com.grendelscan.smashers.webServerConfiguration.XST;
import com.grendelscan.smashers.xss.QueryXSS;

/**
 * @author David Byrne
 * 
 */
public class MasterTestModuleCollection
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MasterTestModuleCollection.class);
	private Map<Class, AbstractSmasher>	allModules		= new HashMap<Class, AbstractSmasher>();
	

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
		for (AbstractSmasher module: allModules.values())
		{
			for (Class prereq: module.getPrerequisites())
			{
				allModules.get(prereq).getDependents().add(module.getClass());
			}
		}
	}



	public Collection<AbstractSmasher> getAllTestModules()
	{
		return allModules.values();
	}

	public AbstractSmasher getTestModule(Class moduleClass)
	{
		if (!allModules.containsKey(moduleClass))
		{
			LOGGER.debug("Module "
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
				AbstractSmasher module = (AbstractSmasher) moduleClass.newInstance();
				allModules.put(module.getClass(), module);
			}
			catch (IllegalAccessException e)
			{
				LOGGER.error("Some sort of problem loading testing module " + moduleClass.getCanonicalName() + ": " + e.toString(), e);
				continue;
			}
			catch (InstantiationException e)
			{
				LOGGER.error("Some sort of problem loading testing module " + moduleClass.getCanonicalName() + ": " + e.toString(), e);
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
