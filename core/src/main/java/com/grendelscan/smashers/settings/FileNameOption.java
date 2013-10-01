package com.grendelscan.smashers.settings;


public class FileNameOption extends TextOption
{
    private final boolean directory;

    public FileNameOption(final String name, final String defaultValue, final String helpText, final boolean directory, final ConfigChangeHandler changeHandler)
    {
        super(name, defaultValue, helpText, false, changeHandler);
        this.directory = directory;
    }

    public boolean isDirectory()
    {
        return directory;
    }

    @Override
    public boolean isMultiLine()
    {
        return false;
    }

}
