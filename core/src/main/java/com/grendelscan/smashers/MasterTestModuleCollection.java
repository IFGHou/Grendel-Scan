package com.grendelscan.smashers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author David Byrne
 * 
 */
public class MasterTestModuleCollection
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MasterTestModuleCollection.class);
    private final Map<Class, AbstractSmasher> allModules = new HashMap<Class, AbstractSmasher>();

    private static MasterTestModuleCollection instance;

    public static MasterTestModuleCollection getInstance()
    {
        return instance;
    }

    public static void initialize()
    {
        instance = new MasterTestModuleCollection();
    }

    private MasterTestModuleCollection()
    {
        instantiateTestingModules();
        mapDependencies();
    }

    private Class<?>[] getAllTestModuleClasses()
    {
        return new Class<?>[] { InputOutputFlows.class, WebsiteMirror.class,

        BackupFiles.class, DirectoryEnumerator.class, FileEnumerator.class,

        TokenSubmitter.class,

        CommentLister.class, DirectoryListing.class, PlatformErrors.class, PrivateIPAddresses.class, RobotsTxt.class,

        CRLFInjection.class, CSRF.class, DirectoryTraversal.class, GenericFuzzer.class, PlaintextHTTP.class,

        KnownVulnerabilities.class, SoftwareVersion.class,

        AuthenticationBypass.class, CookieStrength.class, SessionFixation.class, URLSessionIDs.class,

        AutoAuthentication.class, FormSubmitter.class, SearchEngineRecon.class, TagRequester.class, UrlRegex.class,

        SingleQuoteQuery.class, Tautologies.class,

        ProxyDetection.class, XST.class,

        QueryXSS.class

        };
    }

    public Collection<AbstractSmasher> getAllTestModules()
    {
        return allModules.values();
    }

    public AbstractSmasher getTestModule(final Class moduleClass)
    {
        if (!allModules.containsKey(moduleClass))
        {
            LOGGER.debug("Module " + moduleClass + " doesn't seem to exist. This could be okay if it's a soft prerequisite. (MasterTestModuleCollection.getTestModule())");
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

    private void mapDependencies()
    {
        for (AbstractSmasher module : allModules.values())
        {
            for (Class prereq : module.getPrerequisites())
            {
                allModules.get(prereq).getDependents().add(module.getClass());
            }
        }
    }

}
