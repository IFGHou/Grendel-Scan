package com.grendelscan.GUI2.proxy;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import com.grendelscan.GUI.customControls.basic.GTabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.grendelscan.GUI.proxy.interception.InterceptionSettingsComposite;
import com.grendelscan.GUI.settings.GrendelSettingsControl;

public class ProxyTabs extends com.grendelscan.GUI.customControls.basic.GComposite implements GrendelSettingsControl
{

	private InterceptionSettingsComposite	interceptionComposite;
	private TabItem							interceptionTab;
	private ProxySettingsComposite			settingsComposite;
	private TabItem							settingsTab;
	private GTabFolder						proxyTabs;

	/**
	 * Auto-generated main method to display this
	 * com.grendelscan.GUI.customControls.basic.GComposite inside a new GShell.
	 */

	/**
	 * Auto-generated method to display this
	 * com.grendelscan.GUI.customControls.basic.GComposite inside a new GShell.
	 */

	public ProxyTabs(com.grendelscan.GUI.customControls.basic.GComposite parent, int style)
	{
		super(parent, style);
		initGUI();
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

	/**
	 * Overriding checkSubclass allows this class to extend
	 * com.grendelscan.GUI.customControls.basic.GComposite
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

	@Override
	public String updateToSettings()
	{
		return settingsComposite.updateToSettings() + interceptionComposite.updateToSettings();
	}

	@Override
	public void updateFromSettings()
	{
		settingsComposite.updateFromSettings();
		interceptionComposite.updateFromSettings();
	}
}
