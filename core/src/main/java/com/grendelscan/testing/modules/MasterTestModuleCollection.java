package com.grendelscan.testing.modules;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.testing.modules.impl.architecture.InputOutputFlows;
import com.grendelscan.testing.modules.impl.architecture.WebsiteMirror;
import com.grendelscan.testing.modules.impl.fileEnumeration.BackupFiles;
import com.grendelscan.testing.modules.impl.fileEnumeration.DirectoryEnumerator;
import com.grendelscan.testing.modules.impl.fileEnumeration.FileEnumerator;
import com.grendelscan.testing.modules.impl.hidden.TokenSubmitter;
import com.grendelscan.testing.modules.impl.informationLeakage.CommentLister;
import com.grendelscan.testing.modules.impl.informationLeakage.DirectoryListing;
import com.grendelscan.testing.modules.impl.informationLeakage.PlatformErrors;
import com.grendelscan.testing.modules.impl.informationLeakage.PrivateIPAddresses;
import com.grendelscan.testing.modules.impl.informationLeakage.RobotsTxt;
import com.grendelscan.testing.modules.impl.miscellaneous.CRLFInjection;
import com.grendelscan.testing.modules.impl.miscellaneous.CSRF;
import com.grendelscan.testing.modules.impl.miscellaneous.DirectoryTraversal;
import com.grendelscan.testing.modules.impl.miscellaneous.GenericFuzzer;
import com.grendelscan.testing.modules.impl.miscellaneous.PlaintextHTTP;
import com.grendelscan.testing.modules.impl.nikto.KnownVulnerabilities;
import com.grendelscan.testing.modules.impl.nikto.SoftwareVersion;
import com.grendelscan.testing.modules.impl.sessionManagement.AuthenticationBypass;
import com.grendelscan.testing.modules.impl.sessionManagement.CookieStrength;
import com.grendelscan.testing.modules.impl.sessionManagement.SessionFixation;
import com.grendelscan.testing.modules.impl.sessionManagement.URLSessionIDs;
import com.grendelscan.testing.modules.impl.spidering.AutoAuthentication;
import com.grendelscan.testing.modules.impl.spidering.FormSubmitter;
import com.grendelscan.testing.modules.impl.spidering.SearchEngineRecon;
import com.grendelscan.testing.modules.impl.spidering.TagRequester;
import com.grendelscan.testing.modules.impl.spidering.UrlRegex;
import com.grendelscan.testing.modules.impl.sqlInjection.SingleQuoteQuery;
import com.grendelscan.testing.modules.impl.sqlInjection.Tautologies;
import com.grendelscan.testing.modules.impl.webServerConfiguration.ProxyDetection;
import com.grendelscan.testing.modules.impl.webServerConfiguration.XST;
import com.grendelscan.testing.modules.impl.xss.QueryXSS;

/**
 * @author David Byrne
 * 
 */
public class MasterTestModuleCollection {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(MasterTestModuleCollection.class);
	private final Map<Class, AbstractTestModule> allModules = new HashMap<Class, AbstractTestModule>();

	private static MasterTestModuleCollection instance;

	public static MasterTestModuleCollection getInstance() {
		return instance;
	}

	public static void initialize() {
		instance = new MasterTestModuleCollection();
	}

	private MasterTestModuleCollection() {
		instantiateTestingModules();
		mapDependencies();
	}

	private Class<?>[] getAllTestModuleClasses() {
		return new Class<?>[] { InputOutputFlows.class, WebsiteMirror.class,

		BackupFiles.class, DirectoryEnumerator.class, FileEnumerator.class,

		TokenSubmitter.class,

		CommentLister.class, DirectoryListing.class, PlatformErrors.class,
				PrivateIPAddresses.class, RobotsTxt.class,

				CRLFInjection.class, CSRF.class, DirectoryTraversal.class,
				GenericFuzzer.class, PlaintextHTTP.class,

				KnownVulnerabilities.class, SoftwareVersion.class,

				AuthenticationBypass.class, CookieStrength.class,
				SessionFixation.class, URLSessionIDs.class,

				AutoAuthentication.class, FormSubmitter.class,
				SearchEngineRecon.class, TagRequester.class, UrlRegex.class,

				SingleQuoteQuery.class, Tautologies.class,

				ProxyDetection.class, XST.class,

				QueryXSS.class

		};
	}

	public Collection<AbstractTestModule> getAllTestModules() {
		return allModules.values();
	}

	public AbstractTestModule getTestModule(final Class moduleClass) {
		if (!allModules.containsKey(moduleClass)) {
			LOGGER.debug("Module "
					+ moduleClass
					+ " doesn't seem to exist. This could be okay if it's a soft prerequisite. (MasterTestModuleCollection.getTestModule())");
		}
		return allModules.get(moduleClass);
	}

	private void instantiateTestingModules() {
		for (Class<?> moduleClass : getAllTestModuleClasses()) {
			try {
				AbstractTestModule module = (AbstractTestModule) moduleClass
						.newInstance();
				allModules.put(module.getClass(), module);
			} catch (IllegalAccessException e) {
				LOGGER.error("Some sort of problem loading testing module "
						+ moduleClass.getCanonicalName() + ": " + e.toString(),
						e);
				continue;
			} catch (InstantiationException e) {
				LOGGER.error("Some sort of problem loading testing module "
						+ moduleClass.getCanonicalName() + ": " + e.toString(),
						e);
				continue;
			}

		}
	}

	private void mapDependencies() {
		for (AbstractTestModule module : allModules.values()) {
			for (Class prereq : module.getPrerequisites()) {
				allModules.get(prereq).getDependents().add(module.getClass());
			}
		}
	}

}
