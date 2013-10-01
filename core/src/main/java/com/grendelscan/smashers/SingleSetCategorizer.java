package com.grendelscan.smashers;


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
    public void addModule(final AbstractSmasher module)
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
