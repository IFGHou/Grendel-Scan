package com.grendelscan.GUI2.settings.scanSettings;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import com.grendelscan.GUI.customControls.basic.GComposite;
import com.grendelscan.GUI.customControls.basic.GTabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.grendelscan.GUI.settings.*;
import com.grendelscan.GUI.settings.GrendelSettingsControl;
import com.grendelscan.GUI.settings.HttpClientSettingsComposite;
import com.grendelscan.GUI.settings.scanSettings.testModules.ModuleSettingsComposite;
import com.grendelscan.GUI.statusComposites.FindingsTable.FindingsTable;

public class ScannerTabs extends GComposite implements GrendelSettingsControl
{

	private GTabFolder	tabs;
	private TabItem	generalTab;
	private GeneralSettingsComposite	generalSettingsComposite;
	private TabItem	httpClientTab;
	private HttpClientSettingsComposite	httpClientComposite;
	private TabItem	restrictionsTab;
	private ScanRestrictionsSettingsComposite	restrictionsComposite;
	private TabItem	modulesTab;
	private ModuleSettingsComposite	modulesComposite;
	private TabItem	authenticationTab;
	private AuthenticationSettingsComposite	authenticationComposite;
	private TabItem findingsTab;
	private FindingsTable findingsTable;
	
	
	public ScannerTabs(com.grendelscan.GUI.customControls.basic.GComposite parent, int style)
	{
		super(parent, style);
		initGUI();
	}

	private void initGUI()
	{
		FillLayout thisLayout = new FillLayout(SWT.HORIZONTAL|SWT.VERTICAL);
		this.setLayout(thisLayout);
		
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

	/**
	 * Overriding checkSubclass allows this class to extend
	 * com.grendelscan.GUI.customControls.basic.GComposite
	 */
	@Override
	protected void checkSubclass()
	{
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
		return 
		generalSettingsComposite.updateToSettings() +
		httpClientComposite.updateToSettings() +
		restrictionsComposite.updateToSettings() +
		modulesComposite.updateToSettings() +
		authenticationComposite.updateToSettings();
	}

	public final FindingsTable getFindingsTable()
	{
		return findingsTable;
	}


}
