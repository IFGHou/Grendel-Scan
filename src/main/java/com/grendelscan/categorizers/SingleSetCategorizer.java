package com.grendelscan.categorizers;

import com.grendelscan.tests.testModuleUtils.CategoryTestModuleCollection;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.TestType;

/**
 * 
 * @author David Byrne
 */
public abstract class SingleSetCategorizer extends Categorizer
{
	protected CategoryTestModuleCollection testModules;


	public SingleSetCategorizer(Class<? extends TestType> categoryTestClass)
	{
		super(categoryTestClass);
		testModules = new CategoryTestModuleCollection();
	}

	@Override
	public void addModule(TestModule module)
	{
		if (categoryTestClass.isInstance(module))
		{
			testModules.add(module);
		}
	}

	@Override
	public void resolveDependencies()
	{
		testModules.resolveDependencies();
	}

}
