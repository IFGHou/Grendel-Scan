package com.grendelscan.GUI.proxy;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.grendelscan.GUI.proxy.interception.InterceptionSettingsComposite;
import com.grendelscan.GUI.settings.GrendelSettingsControl;

public class ProxyTabs extends org.eclipse.swt.widgets.Composite implements GrendelSettingsControl
{

	private InterceptionSettingsComposite	interceptionComposite;
	private TabItem							interceptionTab;
	private ProxySettingsComposite			settingsComposite;
	private TabItem							settingsTab;
	private TabFolder						proxyTabs;

	/**
	 * Auto-generated main method to display this
	 * org.eclipse.swt.widgets.Composite inside a new Shell.
	 */

	/**
	 * Auto-generated method to display this
	 * org.eclipse.swt.widgets.Composite inside a new Shell.
	 */

	public ProxyTabs(org.eclipse.swt.widgets.Composite parent, int style)
	{
		super(parent, style);
		initGUI();
	}


	private void initGUI()
	{
			FormLayout thisLayout = new FormLayout();
			setLayout(thisLayout);

			proxyTabs = new TabFolder(this, SWT.NONE);

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
	 * org.eclipse.swt.widgets.Composite
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
