package com.grendelscan.categorizers.interfaces;

import com.grendelscan.categorizers.Categorizer;
import com.grendelscan.categorizers.CategoryTestModuleCollection;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.TestType;


/**
 * 
 * @author David Byrne
 */
public abstract class SingleSetCategorizer extends Categorizer
{
    protected CategoryTestModuleCollection testModules;

    public SingleSetCategorizer(final Class<? extends TestType> categoryTestClass)
    {
        super(categoryTestClass);
        testModules = new CategoryTestModuleCollection();
    }

    @Override
    public void addModule(final AbstractTestModule module)
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
