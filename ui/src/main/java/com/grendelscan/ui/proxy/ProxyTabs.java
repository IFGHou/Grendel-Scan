package com.grendelscan.ui.proxy;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.TabItem;

import com.grendelscan.ui.customControls.basic.GTabFolder;
import com.grendelscan.ui.proxy.interception.InterceptionSettingsComposite;
import com.grendelscan.ui.settings.GrendelSettingsControl;

public class ProxyTabs extends com.grendelscan.ui.customControls.basic.GComposite implements GrendelSettingsControl
{

    private InterceptionSettingsComposite interceptionComposite;
    private TabItem interceptionTab;
    private ProxySettingsComposite settingsComposite;
    private TabItem settingsTab;
    private GTabFolder proxyTabs;

    /**
     * Auto-generated main method to display this com.grendelscan.GUI.customControls.basic.GComposite inside a new GShell.
     */

    /**
     * Auto-generated method to display this com.grendelscan.GUI.customControls.basic.GComposite inside a new GShell.
     */

    public ProxyTabs(final com.grendelscan.ui.customControls.basic.GComposite parent, final int style)
    {
        super(parent, style);
        initGUI();
    }

    /**
     * Overriding checkSubclass allows this class to extend com.grendelscan.GUI.customControls.basic.GComposite
     */
    @Override
    protected void checkSubclass()
    {
    }

    public final InterceptionSettingsComposite getInterceptionComposite()
    {
        return interceptionComposite;
    }

    public final ProxySettingsComposite getSettingsComposite()
    {
        return settingsComposite;
    }

    private void initGUI()
    {
        FormLayout thisLayout = new FormLayout();
        setLayout(thisLayout);

        proxyTabs = new GTabFolder(this, SWT.NONE);

        {
            settingsTab = new TabItem(proxyTabs, SWT.NONE);
            settingsTab.setText("Settings");
            settingsComposite = new ProxySettingsComposite(proxyTabs, SWT.NONE);
            settingsTab.setControl(settingsComposite);
        }

        {
            interceptionTab = new TabItem(proxyTabs, SWT.NONE);
            interceptionTab.setText("Interception");
            interceptionComposite = new InterceptionSettingsComposite(proxyTabs, SWT.NONE);
            interceptionTab.setControl(interceptionComposite);
        }

        this.layout();
    }

    @Override
    public void updateFromSettings()
    {
        settingsComposite.updateFromSettings();
        interceptionComposite.updateFromSettings();
    }

    @Override
    public String updateToSettings()
    {
        return settingsComposite.updateToSettings() + interceptionComposite.updateToSettings();
    }
}
