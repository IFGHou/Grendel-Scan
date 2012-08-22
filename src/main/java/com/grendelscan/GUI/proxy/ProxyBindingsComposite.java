package com.grendelscan.GUI.proxy;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.cloudgarden.resource.SWTResourceManager;
import com.grendelscan.GUI.GUIConstants;
import com.grendelscan.GUI.Verifiers.EnforceIntegersOnly;
import com.grendelscan.GUI.Verifiers.EnforceLooseIPAddress;
import com.grendelscan.GUI.settings.GrendelSettingsControl;
import com.grendelscan.proxy.forwardProxy.ForwardProxy;
import com.grendelscan.proxy.reverseProxy.ReverseProxyConfig;
import com.grendelscan.requester.http.transactions.HttpTransactionFields;
import com.grendelscan.scan.Scan;

public class ProxyBindingsComposite extends org.eclipse.swt.widgets.Composite implements GrendelSettingsControl{

	protected Button startForwardProxyButton;
	
	protected Button enableProxyCheckBox;
	protected Label proxyBindAddressLabel;
	protected Text proxyBindAddressTextBox;
	protected Label proxyBindPortLabel;
	protected Text proxyBindPortTextBox;
//	protected Label maxProxyThreadLabel;
//	protected Text maxProxyThreadTextBox;
	protected Group reverseProxySettingsGroup;
	
	protected ReverseProxySettingsComposite reverseProxySettingsComposite;
	
	public ProxyBindingsComposite(org.eclipse.swt.widgets.Composite parent, int style) {
		super(parent, style);
		initGUI();
	}

	private void initGUI() {
			this.setLayout(new FormLayout());
			{
				EnforceIntegersOnly numbersOnlyVerifyer = new EnforceIntegersOnly();
				EnforceLooseIPAddress ipAddressOnlyVerifyer = new EnforceLooseIPAddress();
				
				Group mainProxySettingsGroup = new Group(this, SWT.NONE);
				mainProxySettingsGroup.setLayout(new FormLayout());
				mainProxySettingsGroup.setText("Main proxy settings");
				mainProxySettingsGroup.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
				FormData groupLayoutData = new FormData();
				groupLayoutData.width = 450;
				groupLayoutData.height = 120;
				groupLayoutData.left =  new FormAttachment(0, 1000, 5);
				groupLayoutData.top =  new FormAttachment(0, 1000, 5);
				mainProxySettingsGroup.setLayoutData(groupLayoutData);
				{
					int currentLeft = 5;
					int currentTop = 5;
					{
						startForwardProxyButton = new Button(mainProxySettingsGroup, SWT.PUSH | SWT.CENTER);
						FormData layoutData = new FormData();
						layoutData.width = 60;
						layoutData.height = 27;
						layoutData.left =  new FormAttachment(0, 1000, currentLeft);
						layoutData.top =  new FormAttachment(0, 1000, currentTop);
						startForwardProxyButton.setLayoutData(layoutData);
//						startForwardProxyButton.setText("Start");
						currentTop += 8;
						startForwardProxyButton.addSelectionListener(new SelectionAdapter() {
							@Override
	                        public void widgetSelected(SelectionEvent evt) 
							{
								updateToSettings();
								if (startForwardProxyButton.getText().equals("Start"))
								{
									Scan.getInstance().getProxies().startForwardProxy();
								}
								else
								{
									Scan.getInstance().getProxies().stopForwardProxy();
								}
								updateForwardProxyGUIStatus();
							}
						});
					}
//					else
//					{
//						enableProxyCheckBox = new Button(mainProxySettingsGroup, SWT.CHECK | SWT.LEFT);
//						enableProxyCheckBox.setText("Enable internal proxy");
//						enableProxyCheckBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
//						FormData layoutData = new FormData();
//						layoutData.width = 150;
//						layoutData.height = 19;
//						layoutData.left =  new FormAttachment(0, 1000, currentLeft);
//						layoutData.top =  new FormAttachment(0, 1000, currentTop);
//						enableProxyCheckBox.setLayoutData(layoutData);
//						enableProxyCheckBox.addSelectionListener
//						(
//							new SelectionAdapter()
//							{
//								@Override
//		                        public void widgetSelected(SelectionEvent event)
//								{ 
//									updateForwardProxyGUIStatus();
//								}
//							}
//						);
//					}
					
					currentTop += 30;
					currentLeft = 5;
					{
						proxyBindAddressLabel = new Label(mainProxySettingsGroup, SWT.NONE);
						proxyBindAddressLabel.setText("Proxy bind address:");
						FormData layoutData = new FormData();
						layoutData.width = 115;
						layoutData.height = 19;
						layoutData.left =  new FormAttachment(0, 1000, currentLeft);
						layoutData.top =  new FormAttachment(0, 1000, currentTop);
						proxyBindAddressLabel.setLayoutData(layoutData);
						proxyBindAddressLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
						currentLeft += layoutData.width + 5;
					}
					{
						proxyBindAddressTextBox = new Text(mainProxySettingsGroup, SWT.BORDER);
						FormData layoutData = new FormData();
						layoutData.width = 100;
						layoutData.height = 19;
						layoutData.left =  new FormAttachment(0, 1000, currentLeft);
						layoutData.top =  new FormAttachment(0, 1000, currentTop);
						proxyBindAddressTextBox.setLayoutData(layoutData);
						proxyBindAddressTextBox.addVerifyListener(ipAddressOnlyVerifyer);
						proxyBindAddressTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
						currentLeft += layoutData.width + 25;
					}
					{
						proxyBindPortLabel = new Label(mainProxySettingsGroup, SWT.NONE);
						proxyBindPortLabel.setText("Proxy bind port:");
						FormData layoutData = new FormData();
						layoutData.width = 90;
						layoutData.height = 19;
						layoutData.left =  new FormAttachment(0, 1000, currentLeft);
						layoutData.top =  new FormAttachment(0, 1000, currentTop);
						proxyBindPortLabel.setLayoutData(layoutData);
						proxyBindPortLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
						currentLeft += layoutData.width + 5;
					}
					{
						proxyBindPortTextBox = new Text(mainProxySettingsGroup, SWT.BORDER);
						proxyBindPortTextBox.setTextLimit(5);
						FormData layoutData = new FormData();
						layoutData.width = 40;
						layoutData.height = 19;
						layoutData.left =  new FormAttachment(0, 1000, currentLeft);
						layoutData.top =  new FormAttachment(0, 1000, currentTop);
						proxyBindPortTextBox.setLayoutData(layoutData);
						proxyBindPortTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
						proxyBindPortTextBox.addVerifyListener(numbersOnlyVerifyer);
						currentLeft += layoutData.width + 15;
					}
					
					
					
					currentTop += 35;
					currentLeft = 5;
//					{
//						maxProxyThreadLabel = new Label(mainProxySettingsGroup, SWT.NONE);
//						maxProxyThreadLabel.setText("Max proxy threads:");
//						FormData layoutData = new FormData();
//						layoutData.width = 115;
//						layoutData.height = 19;
//						layoutData.left =  new FormAttachment(0, 1000, currentLeft);
//						layoutData.top =  new FormAttachment(0, 1000, currentTop);
//						maxProxyThreadLabel.setLayoutData(layoutData);
//						maxProxyThreadLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
//						currentLeft += layoutData.width + 5;
//					}
//					{
//						maxProxyThreadTextBox = new Text(mainProxySettingsGroup, SWT.BORDER);
//						FormData layoutData = new FormData();
//						layoutData.width = 30;
//						layoutData.height = 19;
//						layoutData.left =  new FormAttachment(0, 1000, currentLeft);
//						layoutData.top =  new FormAttachment(0, 1000, currentTop);
//						maxProxyThreadTextBox.setLayoutData(layoutData);
//						maxProxyThreadTextBox.setTextLimit(2);
//						maxProxyThreadTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
//						maxProxyThreadTextBox.addVerifyListener(numbersOnlyVerifyer);
//						currentLeft += layoutData.width + 15;
//					}
				}
			}
			
			
			
			
			{
				reverseProxySettingsGroup = new Group(this, SWT.NONE);
				reverseProxySettingsGroup.setLayout(new FormLayout());
				reverseProxySettingsGroup.setText("Reverse Proxy Settings");
				reverseProxySettingsGroup.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
				FormData groupLayoutData = new FormData();
				groupLayoutData.left =  new FormAttachment(0, 1000, 5);
				groupLayoutData.top =  new FormAttachment(0, 1000, 160);
				groupLayoutData.right =  new FormAttachment(1000, 1000, -5);
				groupLayoutData.bottom =  new FormAttachment(1000, 1000, -5);
				reverseProxySettingsGroup.setLayoutData(groupLayoutData);
				{
					reverseProxySettingsComposite = new ReverseProxySettingsComposite(reverseProxySettingsGroup, SWT.NONE);
					reverseProxySettingsComposite.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
					reverseProxySettingsComposite.setLayout(new FormLayout());
					FormData layoutData = new FormData();
					layoutData.left =  new FormAttachment(0, 1000, 5);
					layoutData.top =  new FormAttachment(0, 1000, 5);
					layoutData.right =  new FormAttachment(1000, 1000, -5);
					layoutData.bottom =  new FormAttachment(1000, 1000, -5);
					reverseProxySettingsComposite.setLayoutData(layoutData);
				}
			}
			this.layout();
	}
	
	public void displayReverseProxyConfigs(List<ReverseProxyConfig> configs)
    {
	    reverseProxySettingsComposite.displayReverseProxyConfigs(configs);
    }

	public ReverseProxyConfig[] getReverseProxyConfigs()
    {
	    return reverseProxySettingsComposite.getReverseProxyConfigs();
    }

	public boolean isProxyEnabled()
	{
		return enableProxyCheckBox.getSelection();
	}
	public void setProxyEnabled(boolean enabled)
	{
		enableProxyCheckBox.setSelection(enabled);
	}

	public String getLocalIP()
	{
		return proxyBindAddressTextBox.getText();
	}
	public void setLocalIP(String localIP)
	{
		proxyBindAddressTextBox.setText(localIP);
	}
	
	public int getLocalPort()
	{
		return Integer.valueOf(proxyBindPortTextBox.getText());
	}
	public void setLocalPort(int port)
	{
		proxyBindPortTextBox.setText(String.valueOf(port));
	}
	
//	public int getMaxProxyTreads()
//	{
//		return Integer.valueOf(maxProxyThreadTextBox.getText());
//	}
//	public void setMaxProxyTreads(int threadCount)
//	{
//		maxProxyThreadTextBox.setText(String.valueOf(threadCount));
//	}
	
	
	public void updateForwardProxyGUIStatus()
	{
		boolean enabled;
		if (Scan.getInstance().getProxies() == null)
		{
			enabled = !Scan.getScanSettings().isProxyEnabled();
		}
		else
		{
			ForwardProxy forwardProxy = Scan.getInstance().getProxies().getForwardProxy();
			if (forwardProxy == null)
			{
				enabled = false;
			}
			else
			{
				enabled = !forwardProxy.isRunning();
			}
		}
		startForwardProxyButton.setText(enabled ? "Start" : "Stop");
		proxyBindAddressLabel.setEnabled(enabled);
		proxyBindPortLabel.setEnabled(enabled);
		proxyBindAddressTextBox.setEnabled(enabled);
		proxyBindPortTextBox.setEnabled(enabled);
//		maxProxyThreadLabel.setEnabled(enabled);
//		maxProxyThreadTextBox.setEnabled(enabled);
	}

	public void externalUpdateForwardProxyGUIStatus()
	{
		if (!this.isDisposed())
		{
			getDisplay().asyncExec(new Runnable()
			{
				@Override
				public void run()
				{
					updateForwardProxyGUIStatus();
				}
			});
		}

	}


	@Override
	public void updateFromSettings()
	{
		proxyBindPortTextBox.setText(String.valueOf(Scan.getScanSettings().getProxyPort()));
		proxyBindAddressTextBox.setText(Scan.getScanSettings().getProxyIPAddress());
		reverseProxySettingsComposite.updateFromSettings();
	}

	@Override
	public String updateToSettings()
	{
		
		if (!proxyBindPortTextBox.getText().isEmpty())
		{
			Scan.getScanSettings().setProxyPort(Integer.valueOf(proxyBindPortTextBox.getText()));
		}
		Scan.getScanSettings().setProxyIPAddress(proxyBindAddressTextBox.getText());
		return reverseProxySettingsComposite.updateToSettings();
	}

}
