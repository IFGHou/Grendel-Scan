/**
 * 
 */
package com.grendelscan.testing.utils.spidering;

import com.grendelscan.testing.modules.settings.SelectableOption;
import com.grendelscan.testing.modules.settings.SingleSelectOptionGroup;
import com.grendelscan.testing.modules.settings.TextListOption;

/**
 * @author david
 * 
 */
public class SpiderConfig
{
    public static TextListOption ignoredParameters;
    public static SingleSelectOptionGroup spiderStyle;
    public static SelectableOption oncePerUrl;
    public static SelectableOption allParamNames;
    public static SelectableOption allParamValues;

    public static void initialize()
    {
        ignoredParameters = new TextListOption("Ignored parameters", "Parameter names to ignore for spidering.", null);
        oncePerUrl = new SelectableOption("Once per URL", true, "Spider each URL once, regardless of query parameters. " + "This will usually be the best option, unless a single URL is used for many purposes.", null);
        allParamNames = new SelectableOption("All params names", false, "Request each uniqe set of URL query parameter names once, but ignore the values. " + "Slower, but more thorough. Only use if needed.", null);
        allParamValues = new SelectableOption("All params values", false, "Request each uniqe set of URL query parameter names and values. Usually very slow.", null);
        spiderStyle = new SingleSelectOptionGroup("Spidering Style", "", null);
        spiderStyle.addOption(oncePerUrl);
        spiderStyle.addOption(allParamNames);
        spiderStyle.addOption(allParamValues);
    }
}
