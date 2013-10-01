package com.grendelscan.categorizers;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.grendelscan.tests.testModuleUtils.CategoryTestModuleCollection;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.TestType;

/**
 * The type strings are not case-sensitive.
 * 
 * @author David Byrne
 * 
 */
public abstract class MultiSetCategorizer extends Categorizer
{
	protected Map<Object, CategoryTestModuleCollection> modulesByType;


	public MultiSetCategorizer(Class<? extends TestType> categoryTestClass)
	{
		super(categoryTestClass);
		modulesByType = Collections.synchronizedMap(new HashMap<Object, CategoryTestModuleCollection>());
	}

	@Override
	public void addModule(TestModule module)
	{
		if (categoryTestClass.isInstance(module))
		{
			for (Object type: getModuleTypes(module))
			{
				if (type instanceof String)
				{
					type = ((String)type).toUpperCase();
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

	@Override
	public void resolveDependencies()
	{
		for (CategoryTestModuleCollection modules: modulesByType.values())
		{
			modules.resolveDependencies();
		}
	}

	public abstract Object[] getModuleTypes(TestModule module);
}
