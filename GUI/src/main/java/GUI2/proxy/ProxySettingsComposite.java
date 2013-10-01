package com.grendelscan.GUI2.proxy;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;

import com.grendelscan.GUI.customControls.basic.GGroup;

import com.cloudgarden.resource.SWTResourceManager;
import com.grendelscan.GUI.GUIConstants;
import com.grendelscan.GUI.settings.GrendelSettingsControl;
import com.grendelscan.scan.Scan;

public class ProxySettingsComposite extends com.grendelscan.GUI.customControls.basic.GComposite implements GrendelSettingsControl{


	private GButton	revealHiddenFieldsButton;
	private GButton	testProxyRequestsCheckbox;
	private GButton	allowAllProxyRequests;
	private GButton	testIntecptedRequestsCheckbox;
	private GGroup	proxySettingsGroup;
	private ProxyBindingsComposite	proxyBindingsComposite;
	

	public ProxySettingsComposite(com.grendelscan.GUI.customControls.basic.GComposite parent, int style) {
		super(parent, style);
		initGUI();
	}

	private void initGUI() {
			FormLayout thisLayout = new FormLayout();
			this.setLayout(thisLayout);
			
			{
				revealHiddenFieldsButton = new GButton(this, SWT.CHECK | SWT.LEFT);
				revealHiddenFieldsButton.setText("Reveal hidden HTML form fields");
				revealHiddenFieldsButton.setSelection(true);
				FormData revealHiddenFieldsButtonLData = new FormData();
				revealHiddenFieldsButtonLData.width = 246;
				revealHiddenFieldsButtonLData.height = 21;
				revealHiddenFieldsButtonLData.left =  new FormAttachment(0, 1000, 10);
				revealHiddenFieldsButtonLData.top =  new FormAttachment(0, 1000, 10);
				revealHiddenFieldsButton.setLayoutData(revealHiddenFieldsButtonLData);
				revealHiddenFieldsButton.addSelectionListener(new SelectionAdapter() {
					@Override
                    public void widgetSelected(SelectionEvent evt) {
						revealHiddenFieldsButtonWidgetSelected(evt);
					}
				});
			}
			{
				testProxyRequestsCheckbox = new GButton(this, SWT.CHECK | SWT.LEFT);
				FormData testProxyRequestsCheckboxLData = new FormData();
				testProxyRequestsCheckboxLData.width = 149;
				testProxyRequestsCheckboxLData.height = 19;
				testProxyRequestsCheckboxLData.left =  new FormAttachment(0, 1000, 279);
				testProxyRequestsCheckboxLData.top =  new FormAttachment(0, 1000, 10);
				testProxyRequestsCheckbox.setLayoutData(testProxyRequestsCheckboxLData);
				testProxyRequestsCheckbox.setText("Test proxy requests");
				testProxyRequestsCheckbox.addSelectionListener(new SelectionAdapter() {
					@Override
                    public void widgetSelected(SelectionEvent evt) 
					{
						Scan.getScanSettings().setTestProxyRequests(testProxyRequestsCheckbox.getSelection());
					}
				});
			}
			{
				allowAllProxyRequests = new GButton(this, SWT.CHECK | SWT.LEFT);
				allowAllProxyRequests.setText("Allow all proxy requests");
				FormData allowAllProxyRequestsLData = new FormData();
				allowAllProxyRequestsLData.width = 240;
				allowAllProxyRequestsLData.height = 23;
				allowAllProxyRequestsLData.left =  new FormAttachment(0, 1000, 10);
				allowAllProxyRequestsLData.top =  new FormAttachment(0, 1000, 35);
				allowAllProxyRequests.setLayoutData(allowAllProxyRequestsLData);
				allowAllProxyRequests.addSelectionListener(new SelectionAdapter() {
					@Override
                    public void widgetSelected(SelectionEvent evt) {
						allowAllProxyRequestsWidgetSelected(evt);
					}
				});
			}
			{
				testIntecptedRequestsCheckbox = new GButton(this, SWT.CHECK | SWT.LEFT);
				FormData testIntecptedRequestsCheckboxLData = new FormData();
				testIntecptedRequestsCheckboxLData.width = 186;
				testIntecptedRequestsCheckboxLData.height = 19;
				testIntecptedRequestsCheckboxLData.left =  new FormAttachment(0, 1000, 279);
				testIntecptedRequestsCheckboxLData.top =  new FormAttachment(0, 1000, 35);
				testIntecptedRequestsCheckbox.setLayoutData(testIntecptedRequestsCheckboxLData);
				testIntecptedRequestsCheckbox.setText("Test intercepted requests");
				testIntecptedRequestsCheckbox.addSelectionListener(new SelectionAdapter() {
					@Override
                    public void widgetSelected(SelectionEvent evt) 
					{
						Scan.getScanSettings().setTestManualRequests(testIntecptedRequestsCheckbox.getSelection());
					}
				});
			}
			{
				proxySettingsGroup = new GGroup(this, SWT.NONE);
				proxySettingsGroup.setText("Internal Proxy Settings");
				proxySettingsGroup.setFontf(SWTResourceManager.getFont(GUIConstants.fontName,GUIConstants.fontSize,0,false,false));
				proxySettingsGroup.setToolTipText("The built-in proxy server allows you to guide \nthe scan by navigating the website from your\nbrowser, while it's pointed at the proxy. This is\nan alternative to the spidering modules.");
				FormData proxySettingsGroupLData = new FormData();
				proxySettingsGroupLData.left =  new FormAttachment(0, 1000, 5);
				proxySettingsGroupLData.top =  new FormAttachment(0, 1000, 100);
				proxySettingsGroupLData.right =  new FormAttachment(1000, 1000, -5);
				proxySettingsGroupLData.bottom =  new FormAttachment(1000, 1000, -5);
				
				proxySettingsGroup.setLayout(new FillLayout());
				proxySettingsGroup.setLayoutData(proxySettingsGroupLData);
				{
					proxyBindingsComposite = new ProxyBindingsComposite(proxySettingsGroup, SWT.NONE);
				}
//				{
//					proxyBindAddressLabel = new GLabel(proxySettingsGroup, SWT.NONE);
//					proxyBindAddressLabel.setText("Proxy bind address:");
//					proxyBindAddressLabel.setBounds(12, 63, 153, 25);
//					proxyBindAddressLabel.setFontf(SWTResourceManager.getFont(GUIConstants.fontName,GUIConstants.fontSize,0,false,false));
//				}
//				{
//					proxyBindAddressTextBox = new GText(proxySettingsGroup, SWT.BORDER);
//					proxyBindAddressTextBox.setFontf(SWTResourceManager.getFont(GUIConstants.fontName,GUIConstants.fontSize,0,false,false));
//					proxyBindAddressTextBox.setBounds(171, 63, 125, 25);
//					proxyBindAddressTextBox.setText(Scan.getScanSettings().getProxyIPAddress());
//					proxyBindAddressTextBox.setToolTipText("this is a test");
//				}
//				{
//					proxyBindPortLabel = new GLabel(proxySettingsGroup, SWT.NONE);
//					proxyBindPortLabel.setText("Proxy bind port:");
//					proxyBindPortLabel.setBounds(12, 94, 135, 25);
//					proxyBindPortLabel.setFontf(SWTResourceManager.getFont(GUIConstants.fontName,GUIConstants.fontSize,0,false,false));
//				}
//				{
//					proxyBindPortTextBox = new GText(proxySettingsGroup, SWT.BORDER);
//					proxyBindPortTextBox.setFontf(SWTResourceManager.getFont(GUIConstants.fontName,GUIConstants.fontSize,0,false,false));
//					proxyBindPortTextBox.setBounds(171, 94, 50, 25);
//					proxyBindPortTextBox.setText(String.valueOf(Scan.getScanSettings().getProxyPort()));
//					proxyBindPortTextBox.addVerifyListener(integersOnlyVerifyer);
//				}
//				{
//					maxProxyThreadLabel = new GLabel(proxySettingsGroup, SWT.NONE);
//					maxProxyThreadLabel.setText("Max proxy threads:");
//					maxProxyThreadLabel.setBounds(12, 125, 153, 25);
//					maxProxyThreadLabel.setFontf(SWTResourceManager.getFont(GUIConstants.fontName,GUIConstants.fontSize,0,false,false));
//				}
//				{
//					maxProxyThreadTextBox = new GText(proxySettingsGroup, SWT.BORDER);
//					maxProxyThreadTextBox.setFontf(SWTResourceManager.getFont(GUIConstants.fontName,GUIConstants.fontSize,0,false,false));
//					maxProxyThreadTextBox.setTextLimit(2);
//					maxProxyThreadTextBox.setText(String.valueOf(Scan.getScanSettings().getMaxProxyThreads()));
//					maxProxyThreadTextBox.setBounds(171, 125, 33, 25);
//					maxProxyThreadTextBox.addVerifyListener(integersOnlyVerifyer);
//				}
//				{
//					startProxyButton = new GButton(proxySettingsGroup, SWT.PUSH | SWT.CENTER);
//					startProxyButton.setBounds(12, 25, 60, 26);
//					startProxyButton.setEnabled(false);
//					startProxyButton.setText("Start");
//					startProxyButton.addSelectionListener(new SelectionAdapter() {
//						@Override
//                        public void widgetSelected(SelectionEvent evt) 
//						{
//							commitProxySettings();
//							if ((Boolean) startProxyButton.getData(PROXY_START))
//							{
//								scan.startProxy();
//							}
//							else
//							{
//								// Since this is really a start/stop button
//								scan.stopProxy();
//							}
//							updateProxyStatus();
//						}
//					});
//				}
//				{
//					restartProxyButton = new GButton(proxySettingsGroup, SWT.PUSH | SWT.CENTER);
//					restartProxyButton.setText("Restart");
//					restartProxyButton.setBounds(81, 25, 60, 26);
//					restartProxyButton.setEnabled(false);
//					restartProxyButton.addSelectionListener(new SelectionAdapter() {
//						@Override
//                        public void widgetSelected(SelectionEvent evt) 
//						{
//							commitProxySettings();
//							scan.restartProxy();
//							updateProxyStatus();
//						}
//					});
//				}
			}

			this.layout();
	}

	protected void revealHiddenFieldsButtonWidgetSelected(SelectionEvent evt) 
	{
		Scan.getScanSettings().setRevealHiddenFields(revealHiddenFieldsButton.getSelection());
	}

	protected void allowAllProxyRequestsWidgetSelected(SelectionEvent evt) 
	{
		Scan.getScanSettings().setAllowAllProxyRequests(allowAllProxyRequests.getSelection());
		
	}

	@Override
	public void updateFromSettings()
	{
		proxyBindingsComposite.displayReverseProxyConfigs(Scan.getScanSettings().getReadOnlyReverseProxyConfigs());
		revealHiddenFieldsButton.setSelection(Scan.getScanSettings().isRevealHiddenFields());
		testProxyRequestsCheckbox.setSelection(Scan.getScanSettings().isTestProxyRequests());
		allowAllProxyRequests.setSelection(Scan.getScanSettings().isAllowAllProxyRequests());
		testIntecptedRequestsCheckbox.setSelection(Scan.getScanSettings().isTestInterceptedRequests());
	}

	@Override
	public String updateToSettings()
	{
		// Handled in the control
		return "";
	}

	public ProxyBindingsComposite getProxyBindingsComposite()
	{
		return proxyBindingsComposite;
	}
	
	
}
