package com.grendelscan.smashers;

import java.util.HashMap;
import java.util.Map;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * The type strings are not case-sensitive.
 * 
 * @author David Byrne
 * 
 */
public abstract class MultiSetCategorizer extends Categorizer
{
    protected Map<Object, CategoryTestModuleCollection> modulesByType;

    public MultiSetCategorizer(final Class<? extends TestType> categoryTestClass)
    {
        super(categoryTestClass);
        modulesByType = Collections.synchronizedMap(new HashMap<Object, CategoryTestModuleCollection>());
    }

    @Override
    public void addModule(final AbstractSmasher module)
    {
        if (categoryTestClass.isInstance(module))
        {
            for (Object type : getModuleTypes(module))
            {
                if (type instanceof String)
                {
                    type = ((String) type).toUpperCase();
                }

                if (!modulesByType.containsKey(type))
                {
                    CategoryTestModuleCollection testModules = new CategoryTestModuleCollection();
                    modulesByType.put(type, testModules);
                }
                modulesByType.get(type).add(module);
            }
        }
    }

    public abstract Object[] getModuleTypes(AbstractSmasher module);

    @Override
    public void resolveDependencies()
    {
        for (CategoryTestModuleCollection modules : modulesByType.values())
        {
            modules.resolveDependencies();
        }
    }
}
