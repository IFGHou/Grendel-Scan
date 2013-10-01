package com.grendelscan.ui.settings.scanSettings;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.TabItem;

import com.grendelscan.ui.customControls.basic.GComposite;
import com.grendelscan.ui.customControls.basic.GTabFolder;
import com.grendelscan.ui.settings.GeneralSettingsComposite;
import com.grendelscan.ui.settings.GrendelSettingsControl;
import com.grendelscan.ui.settings.HttpClientSettingsComposite;
import com.grendelscan.ui.settings.scanSettings.testModules.ModuleSettingsComposite;
import com.grendelscan.ui.statusComposites.FindingsTable.FindingsTable;

public class ScannerTabs extends GComposite implements GrendelSettingsControl
{

    private GTabFolder tabs;
    private TabItem generalTab;
    private GeneralSettingsComposite generalSettingsComposite;
    private TabItem httpClientTab;
    private HttpClientSettingsComposite httpClientComposite;
    private TabItem restrictionsTab;
    private ScanRestrictionsSettingsComposite restrictionsComposite;
    private TabItem modulesTab;
    private ModuleSettingsComposite modulesComposite;
    private TabItem authenticationTab;
    private AuthenticationSettingsComposite authenticationComposite;
    private TabItem findingsTab;
    private FindingsTable findingsTable;

    public ScannerTabs(final com.grendelscan.ui.customControls.basic.GComposite parent, final int style)
    {
        super(parent, style);
        initGUI();
    }

    /**
     * Overriding checkSubclass allows this class to extend com.grendelscan.ui.customControls.basic.GComposite
     */
    @Override
    protected void checkSubclass()
    {
    }

    public final FindingsTable getFindingsTable()
    {
        return findingsTable;
    }

    private void initGUI()
    {
        FillLayout thisLayout = new FillLayout(SWT.HORIZONTAL | SWT.VERTICAL);
        setLayout(thisLayout);

        tabs = new GTabFolder(this, SWT.NONE);

        {
            generalTab = new TabItem(tabs, SWT.NONE);
            generalTab.setText("General");
            generalSettingsComposite = new GeneralSettingsComposite(tabs, SWT.NONE);
            generalTab.setControl(generalSettingsComposite);
        }
        {
            findingsTab = new TabItem(tabs, SWT.NONE);
            findingsTab.setText("Findings");
            findingsTable = new FindingsTable(tabs, SWT.NONE);
            findingsTab.setControl(findingsTable);
        }
        {
            httpClientTab = new TabItem(tabs, SWT.NONE);
            httpClientTab.setText("HTTP Client");
            httpClientComposite = new HttpClientSettingsComposite(tabs, SWT.NONE);
            httpClientTab.setControl(httpClientComposite);
        }
        {
            restrictionsTab = new TabItem(tabs, SWT.NONE);
            restrictionsTab.setText("Restrictions");
            restrictionsComposite = new ScanRestrictionsSettingsComposite(tabs, SWT.NONE);
            restrictionsTab.setControl(restrictionsComposite);
        }
        {
            authenticationTab = new TabItem(tabs, SWT.NONE);
            authenticationTab.setText("Authentication");
            authenticationComposite = new AuthenticationSettingsComposite(tabs, SWT.NONE);
            authenticationTab.setControl(authenticationComposite);
        }
        {
            modulesTab = new TabItem(tabs, SWT.NONE);
            modulesTab.setText("Modules");
            modulesComposite = new ModuleSettingsComposite(tabs, SWT.NONE);
            modulesTab.setControl(modulesComposite);
        }

        this.layout();
    }

    @Override
    public void updateFromSettings()
    {
        generalSettingsComposite.updateFromSettings();
        httpClientComposite.updateFromSettings();
        restrictionsComposite.updateFromSettings();
        modulesComposite.updateFromSettings();
        authenticationComposite.updateFromSettings();

    }

    @Override
    public String updateToSettings()
    {
        return generalSettingsComposite.updateToSettings() + httpClientComposite.updateToSettings() + restrictionsComposite.updateToSettings() + modulesComposite.updateToSettings() + authenticationComposite.updateToSettings();
    }

}
