/**
 * 
 */
package com.grendelscan.GUI.settings;


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
import com.grendelscan.requester.authentication.HttpAuthenticationType;
import com.grendelscan.scan.Scan;

/**
 * This code was edited or generated using CloudGarden's Jigloo
 * SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation,
 * company or business for any purpose whatever) then you
 * should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details.
 * Use of Jigloo implies acceptance of these licensing terms.
 * A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
 * THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
 * LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class NetworkSettingsComposite extends org.eclipse.swt.widgets.Composite implements
		GrendelSettingsControl
{
	private Group			httpClientLimitsGroup;
	private Label			maxConnectionsPerServerLabel;
	private Text			maxConnectionsPerServerTextBox;
	private Label			maxConsecutiveFailedRequestsLabel;
	private Text			maxConsecutiveFailedRequestsTextBox;
	private Label			maxFailedRequestsPerServerLabel;
	private Text			maxFailedRequestsPerServerTextBox;
	private Label			maxFileSizeLabel;
	private Text			maxFileSizeTextBox;
	private Label			maxRequestsPerSecondLabel;
	private Text			maxRequestsPerSecondTextBox;
	private Label			maxTotalConnectionsLabel;
	private Text			maxTotalConnectionsTextBox;
	private Label			socksProxyAddressLabel;
	private Label socksProxyPortLabel;
	// private Text maxTotalRequestsTextBox;
	private Label			socketReadTimeoutLabel;
	private Text			socketReadTimeoutTextBox;
	private Label			upstreamProxyAddressLabel;
	private Text			upstreamProxyAddressTextBox;
	private Text			socksProxyAddressTextBox;
	private Button applyButton;
	private Group			upstreamProxyAuthenticationTypeGroup;
	private Button		upstreamProxyBasicAuthenticationRadioButton;
	private Button		upstreamProxyDigestAuthenticationRadioButton;
	private Group			upstreamProxyGroup;
	private Group			socksProxyGroup;
	private Button		upstreamProxyNTLMAuthenticationRadioButton;
	private Label			upstreamProxyPasswordLabel;
	private Text			upstreamProxyPasswordTextBox;
	private Label			upstreamProxyPortLabel;
	private Text			upstreamProxyPortTextBox;
	private Text			socksProxyPortTextBox;
	private Label			upstreamProxyUsernameLabel;
	private Text			upstreamProxyUsernameTextBox;
	private Button		useUpstreamProxyCheckBox;
	private Button		useSocksProxyCheckBox;
	
	{
		// Register as a resource user - SWTResourceManager will
		// handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}

	public NetworkSettingsComposite(org.eclipse.swt.widgets.Composite parent, int style)
	{
		super(parent, style);
		initGUI();
	}


	@Override
	public void updateFromSettings()
	{
		upstreamProxyAddressTextBox.setText(Scan.getScanSettings().getUpstreamProxyAddress());
		useUpstreamProxyCheckBox.setSelection(Scan.getScanSettings().getUseUpstreamProxy());
		upstreamProxyPortTextBox.setText(Scan.getScanSettings().getUpstreamProxyPort() + "");
		upstreamProxyUsernameTextBox.setText(Scan.getScanSettings().getUpstreamProxyUsername());
		upstreamProxyPasswordTextBox.setText(Scan.getScanSettings().getUpstreamProxyPassword());

		useSocksProxyCheckBox.setSelection(Scan.getScanSettings().isUseSocksProxy());
		socksProxyAddressTextBox.setText(Scan.getScanSettings().getSocksHost());
		socksProxyPortTextBox.setText(Integer.toString(Scan.getScanSettings().getSocksPort()));

		
		if (Scan.getScanSettings().getUpstreamProxyAuthenticationType() == null)
		{
			upstreamProxyDigestAuthenticationRadioButton.setSelection(true);
		}
		else
		{
			switch (Scan.getScanSettings().getUpstreamProxyAuthenticationType())
			{
				case DIGEST:
					upstreamProxyDigestAuthenticationRadioButton.setSelection(true);
					break;
				case BASIC:
					upstreamProxyBasicAuthenticationRadioButton.setSelection(true);
					break;
				case NTLM:
					upstreamProxyNTLMAuthenticationRadioButton.setSelection(true);
					break;
			}
		}
		updateUpstreamProxyStatus();

		maxRequestsPerSecondTextBox.setText(String.valueOf(Scan.getScanSettings().getMaxRequestsPerSecond()));
		maxConnectionsPerServerTextBox.setText(String.valueOf(Scan.getScanSettings().getMaxConnectionsPerServer()));
		maxTotalConnectionsTextBox.setText(String.valueOf(Scan.getScanSettings().getMaxTotalConnections()));
		maxConsecutiveFailedRequestsTextBox.setText(String.valueOf(Scan.getScanSettings()
				.getMaxConsecutiveFailedRequests()));
		maxFailedRequestsPerServerTextBox.setText(String.valueOf(Scan.getScanSettings().getMaxFailedRequestsPerHost()));
		socketReadTimeoutTextBox.setText(String.valueOf(Scan.getScanSettings().getSocketReadTimeout()));
		maxFileSizeTextBox.setText(String.valueOf(Scan.getScanSettings().getMaxFileSizeKiloBytes()));
	}

	private void initGUI()
	{
		EnforceIntegersOnly numbersOnlyVerifyer = new EnforceIntegersOnly();
		FormLayout thisLayout = new FormLayout();
		setLayout(thisLayout);
		{
			applyButton = new Button(this, SWT.PUSH | SWT.CENTER);
			FormData applyButtonLData = new FormData();
			applyButtonLData.width = 118;
			applyButtonLData.height = 33;
			applyButtonLData.top =  new FormAttachment(0, 1000, 330);
			applyButtonLData.left =  new FormAttachment(0, 1000, 765);
			applyButton.setLayoutData(applyButtonLData);
			applyButton.setText("Apply Settings");
			applyButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent evt) {
					updateToSettings();
				}
			});
		}
//		{
//			socksProxyGroup = new Group(this, SWT.NONE);
//			socksProxyGroup.setText("Upstream HTTP Proxy");
//			socksProxyGroup.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0,
//						false, false));
//			FormData upstreamProxyGroupLData = new FormData();
//			upstreamProxyGroupLData.width = 448;
//			upstreamProxyGroupLData.height = 183;
//			upstreamProxyGroupLData.left =  new FormAttachment(0, 1000, 433);
//			upstreamProxyGroupLData.top =  new FormAttachment(0, 1000, 0);
//			socksProxyGroup.setLayoutData(upstreamProxyGroupLData);
//			socksProxyGroup.setLayout(null);
//			{
//				useUpstreamProxyCheckBox = new Button(socksProxyGroup, SWT.CHECK | SWT.LEFT);
//				useUpstreamProxyCheckBox.setText("Use upstream HTTP proxy");
//				useUpstreamProxyCheckBox.setBounds(12, 19, 200, 30);
//				useUpstreamProxyCheckBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
//							GUIConstants.fontSize, 0, false, false));
//				useUpstreamProxyCheckBox.addSelectionListener(new SelectionAdapter()
//					{
//						@Override
//						public void widgetSelected(SelectionEvent e)
//						{
//							updateUpstreamProxyStatus();
//						}
//					});
//			}
//			{
//				upstreamProxyAddressLabel = new Label(socksProxyGroup, SWT.NONE);
//				upstreamProxyAddressLabel.setText("Address:");
//				upstreamProxyAddressLabel.setBounds(10, 53, 66, 23);
//				upstreamProxyAddressLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
//							GUIConstants.fontSize, 0, false, false));
//			}
//			{
//				upstreamProxyPortLabel = new Label(socksProxyGroup, SWT.NONE);
//				upstreamProxyPortLabel.setText("Port:");
//				upstreamProxyPortLabel.setBounds(10, 89, 60, 25);
//				upstreamProxyPortLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
//							GUIConstants.fontSize, 0, false, false));
//			}
//			{
//				upstreamProxyAddressTextBox = new Text(socksProxyGroup, SWT.BORDER);
//				upstreamProxyAddressTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
//							GUIConstants.fontSize, 0, false, false));
//				upstreamProxyAddressTextBox.setBounds(86, 52, 301, 25);
//			}
//			{
//				upstreamProxyPortTextBox = new Text(socksProxyGroup, SWT.BORDER);
//				upstreamProxyPortTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
//							GUIConstants.fontSize, 0, false, false));
//				upstreamProxyPortTextBox.setTextLimit(5);
//				upstreamProxyPortTextBox.setBounds(86, 87, 69, 25);
//				upstreamProxyPortTextBox.addVerifyListener(numbersOnlyVerifyer);
//			}
//			{
//				upstreamProxyUsernameTextBox = new Text(socksProxyGroup, SWT.BORDER);
//				upstreamProxyUsernameTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
//							GUIConstants.fontSize, 0, false, false));
//				upstreamProxyUsernameTextBox.setEnabled(false);
//				upstreamProxyUsernameTextBox.setBounds(86, 122, 111, 25);
//			}
//			{
//				upstreamProxyUsernameLabel = new Label(socksProxyGroup, SWT.NONE);
//				upstreamProxyUsernameLabel.setText("Username:");
//				upstreamProxyUsernameLabel.setEnabled(false);
//				upstreamProxyUsernameLabel.setBounds(10, 127, 74, 25);
//				upstreamProxyUsernameLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
//							GUIConstants.fontSize, 0, false, false));
//			}
//			{
//				upstreamProxyPasswordLabel = new Label(socksProxyGroup, SWT.NONE);
//				upstreamProxyPasswordLabel.setText("Password:");
//				upstreamProxyPasswordLabel.setEnabled(false);
//				upstreamProxyPasswordLabel.setBounds(10, 160, 74, 25);
//				upstreamProxyPasswordLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
//							GUIConstants.fontSize, 0, false, false));
//			}
//			{
//				upstreamProxyPasswordTextBox = new Text(socksProxyGroup, SWT.BORDER);
//				upstreamProxyPasswordTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
//							GUIConstants.fontSize, 0, false, false));
//				upstreamProxyPasswordTextBox.setEnabled(false);
//				upstreamProxyPasswordTextBox.setBounds(86, 158, 113, 25);
//			}
//			{
//				upstreamProxyAuthenticationTypeGroup = new Group(socksProxyGroup, SWT.NONE);
//				upstreamProxyAuthenticationTypeGroup.setText("Authentication Type");
//				upstreamProxyAuthenticationTypeGroup.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
//							GUIConstants.fontSize, 0, false, false));
//				upstreamProxyAuthenticationTypeGroup.setEnabled(false);
//				upstreamProxyAuthenticationTypeGroup.setBounds(240, 87, 202, 107);
//				upstreamProxyAuthenticationTypeGroup.setLayout(null);
//				{
//					upstreamProxyBasicAuthenticationRadioButton =
//								new Button(upstreamProxyAuthenticationTypeGroup, SWT.RADIO | SWT.LEFT);
//					upstreamProxyBasicAuthenticationRadioButton.setText("Basic");
//					upstreamProxyBasicAuthenticationRadioButton.setEnabled(false);
//					upstreamProxyBasicAuthenticationRadioButton.setBounds(8, 22, 85, 25);
//					upstreamProxyBasicAuthenticationRadioButton.setFont(SWTResourceManager.getFont(
//								GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
//				}
//				{
//					upstreamProxyDigestAuthenticationRadioButton =
//								new Button(upstreamProxyAuthenticationTypeGroup, SWT.RADIO | SWT.LEFT);
//					upstreamProxyDigestAuthenticationRadioButton.setText("Digest");
//					upstreamProxyDigestAuthenticationRadioButton.setSelection(true);
//					upstreamProxyDigestAuthenticationRadioButton.setEnabled(false);
//					upstreamProxyDigestAuthenticationRadioButton.setBounds(8, 47, 60, 25);
//					upstreamProxyDigestAuthenticationRadioButton.setFont(SWTResourceManager.getFont(
//								GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
//				}
//				{
//					upstreamProxyNTLMAuthenticationRadioButton =
//								new Button(upstreamProxyAuthenticationTypeGroup, SWT.RADIO | SWT.LEFT);
//					upstreamProxyNTLMAuthenticationRadioButton.setText("NTLM");
//					upstreamProxyNTLMAuthenticationRadioButton.setEnabled(false);
//					upstreamProxyNTLMAuthenticationRadioButton.setBounds(8, 72, 60, 25);
//					upstreamProxyNTLMAuthenticationRadioButton.setFont(SWTResourceManager.getFont(
//								GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
//				}
//			}
//		}
		{
			upstreamProxyGroup = new Group(this, SWT.NONE);
			upstreamProxyGroup.setText("Upstream HTTP Proxy");
			upstreamProxyGroup.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0,
						false, false));
			FormData upstreamProxyGroupLData = new FormData();
			upstreamProxyGroupLData.width = 448;
			upstreamProxyGroupLData.height = 183;
			upstreamProxyGroupLData.left =  new FormAttachment(0, 1000, 433);
			upstreamProxyGroupLData.top =  new FormAttachment(0, 1000, 0);
			upstreamProxyGroup.setLayoutData(upstreamProxyGroupLData);
			upstreamProxyGroup.setLayout(null);
			{
				useUpstreamProxyCheckBox = new Button(upstreamProxyGroup, SWT.CHECK | SWT.LEFT);
				useUpstreamProxyCheckBox.setText("Use upstream HTTP proxy");
				useUpstreamProxyCheckBox.setBounds(12, 19, 200, 30);
				useUpstreamProxyCheckBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
				useUpstreamProxyCheckBox.addSelectionListener(new SelectionAdapter()
					{
						@Override
						public void widgetSelected(SelectionEvent e)
						{
							updateUpstreamProxyStatus();
						}
					});
			}
			{
				upstreamProxyAddressLabel = new Label(upstreamProxyGroup, SWT.NONE);
				upstreamProxyAddressLabel.setText("Address:");
				upstreamProxyAddressLabel.setBounds(10, 53, 66, 23);
				upstreamProxyAddressLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
			}
			{
				upstreamProxyPortLabel = new Label(upstreamProxyGroup, SWT.NONE);
				upstreamProxyPortLabel.setText("Port:");
				upstreamProxyPortLabel.setBounds(10, 89, 60, 25);
				upstreamProxyPortLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
			}
			{
				upstreamProxyAddressTextBox = new Text(upstreamProxyGroup, SWT.BORDER);
				upstreamProxyAddressTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
				upstreamProxyAddressTextBox.setBounds(86, 52, 301, 25);
			}
			{
				upstreamProxyPortTextBox = new Text(upstreamProxyGroup, SWT.BORDER);
				upstreamProxyPortTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
				upstreamProxyPortTextBox.setTextLimit(5);
				upstreamProxyPortTextBox.setBounds(86, 87, 69, 25);
				upstreamProxyPortTextBox.addVerifyListener(numbersOnlyVerifyer);
			}
			{
				upstreamProxyUsernameTextBox = new Text(upstreamProxyGroup, SWT.BORDER);
				upstreamProxyUsernameTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
				upstreamProxyUsernameTextBox.setEnabled(false);
				upstreamProxyUsernameTextBox.setBounds(86, 122, 111, 25);
			}
			{
				upstreamProxyUsernameLabel = new Label(upstreamProxyGroup, SWT.NONE);
				upstreamProxyUsernameLabel.setText("Username:");
				upstreamProxyUsernameLabel.setEnabled(false);
				upstreamProxyUsernameLabel.setBounds(10, 127, 74, 25);
				upstreamProxyUsernameLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
			}
			{
				upstreamProxyPasswordLabel = new Label(upstreamProxyGroup, SWT.NONE);
				upstreamProxyPasswordLabel.setText("Password:");
				upstreamProxyPasswordLabel.setEnabled(false);
				upstreamProxyPasswordLabel.setBounds(10, 160, 74, 25);
				upstreamProxyPasswordLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
			}
			{
				upstreamProxyPasswordTextBox = new Text(upstreamProxyGroup, SWT.BORDER);
				upstreamProxyPasswordTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
				upstreamProxyPasswordTextBox.setEnabled(false);
				upstreamProxyPasswordTextBox.setBounds(86, 158, 113, 25);
			}
			{
				upstreamProxyAuthenticationTypeGroup = new Group(upstreamProxyGroup, SWT.NONE);
				upstreamProxyAuthenticationTypeGroup.setText("Authentication Type");
				upstreamProxyAuthenticationTypeGroup.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
				upstreamProxyAuthenticationTypeGroup.setEnabled(false);
				upstreamProxyAuthenticationTypeGroup.setBounds(240, 87, 202, 107);
				upstreamProxyAuthenticationTypeGroup.setLayout(null);
				{
					upstreamProxyBasicAuthenticationRadioButton =
								new Button(upstreamProxyAuthenticationTypeGroup, SWT.RADIO | SWT.LEFT);
					upstreamProxyBasicAuthenticationRadioButton.setText("Basic");
					upstreamProxyBasicAuthenticationRadioButton.setEnabled(false);
					upstreamProxyBasicAuthenticationRadioButton.setBounds(8, 22, 85, 25);
					upstreamProxyBasicAuthenticationRadioButton.setFont(SWTResourceManager.getFont(
								GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
				}
				{
					upstreamProxyDigestAuthenticationRadioButton =
								new Button(upstreamProxyAuthenticationTypeGroup, SWT.RADIO | SWT.LEFT);
					upstreamProxyDigestAuthenticationRadioButton.setText("Digest");
					upstreamProxyDigestAuthenticationRadioButton.setSelection(true);
					upstreamProxyDigestAuthenticationRadioButton.setEnabled(false);
					upstreamProxyDigestAuthenticationRadioButton.setBounds(8, 47, 60, 25);
					upstreamProxyDigestAuthenticationRadioButton.setFont(SWTResourceManager.getFont(
								GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
				}
				{
					upstreamProxyNTLMAuthenticationRadioButton =
								new Button(upstreamProxyAuthenticationTypeGroup, SWT.RADIO | SWT.LEFT);
					upstreamProxyNTLMAuthenticationRadioButton.setText("NTLM");
					upstreamProxyNTLMAuthenticationRadioButton.setEnabled(false);
					upstreamProxyNTLMAuthenticationRadioButton.setBounds(8, 72, 60, 25);
					upstreamProxyNTLMAuthenticationRadioButton.setFont(SWTResourceManager.getFont(
								GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
				}
			}
		}

		{
			socksProxyGroup = new Group(this, SWT.NONE);
			socksProxyGroup.setText("Upstream HTTP Proxy");
			socksProxyGroup.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0,
						false, false));
			FormData upstreamProxyGroupLData = new FormData();
			upstreamProxyGroupLData.width = 448;
			upstreamProxyGroupLData.height = 107;
			upstreamProxyGroupLData.left =  new FormAttachment(0, 1000, 433);
			upstreamProxyGroupLData.top =  new FormAttachment(0, 1000, 200);
			socksProxyGroup.setLayoutData(upstreamProxyGroupLData);
			socksProxyGroup.setLayout(null);
			{
				useSocksProxyCheckBox = new Button(socksProxyGroup, SWT.CHECK | SWT.LEFT);
				useSocksProxyCheckBox.setText("Use SOCKS proxy");
				useSocksProxyCheckBox.setBounds(12, 19, 200, 30);
				useSocksProxyCheckBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
				useSocksProxyCheckBox.addSelectionListener(new SelectionAdapter()
					{
						@Override
						public void widgetSelected(SelectionEvent e)
						{
							updateSocksProxyStatus();
						}
					});
			}
			{
				socksProxyAddressLabel = new Label(socksProxyGroup, SWT.NONE);
				socksProxyAddressLabel.setText("Address:");
				socksProxyAddressLabel.setBounds(10, 53, 66, 23);
				socksProxyAddressLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
			}
			{
				socksProxyPortLabel = new Label(socksProxyGroup, SWT.NONE);
				socksProxyPortLabel.setText("Port:");
				socksProxyPortLabel.setBounds(10, 89, 60, 25);
				socksProxyPortLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
			}
			{
				socksProxyAddressTextBox = new Text(socksProxyGroup, SWT.BORDER);
				socksProxyAddressTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
				socksProxyAddressTextBox.setBounds(86, 52, 301, 25);
			}
			{
				socksProxyPortTextBox = new Text(socksProxyGroup, SWT.BORDER);
				socksProxyPortTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
				socksProxyPortTextBox.setTextLimit(5);
				socksProxyPortTextBox.setBounds(86, 87, 69, 25);
				socksProxyPortTextBox.addVerifyListener(numbersOnlyVerifyer);
			}
		}		
		{
			httpClientLimitsGroup = new Group(this, SWT.NONE);
			httpClientLimitsGroup.setText("Limits");
			httpClientLimitsGroup.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize,
						0, false, false));
			FormData httpClientLimitsGroupLData = new FormData();
			httpClientLimitsGroupLData.width = 403;
			httpClientLimitsGroupLData.height = 235;
			httpClientLimitsGroupLData.left =  new FormAttachment(0, 1000, 12);
			httpClientLimitsGroupLData.top =  new FormAttachment(0, 1000, 0);
			httpClientLimitsGroup.setLayoutData(httpClientLimitsGroupLData);
			httpClientLimitsGroup.setLayout(null);
			{
				maxRequestsPerSecondLabel = new Label(httpClientLimitsGroup, SWT.NONE);
				maxRequestsPerSecondLabel.setText("Max requests per second:");
				maxRequestsPerSecondLabel.setEnabled(true);
				maxRequestsPerSecondLabel.setBounds(12, 25, 239, 17);
				maxRequestsPerSecondLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
			}
			{
				maxRequestsPerSecondTextBox = new Text(httpClientLimitsGroup, SWT.BORDER);
				maxRequestsPerSecondTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
				maxRequestsPerSecondTextBox.setTextLimit(4);
				maxRequestsPerSecondTextBox.setEnabled(true);
				maxRequestsPerSecondTextBox.setBounds(263, 25, 45, 25);
				maxRequestsPerSecondTextBox.addVerifyListener(numbersOnlyVerifyer);
			}
			{
				maxConnectionsPerServerLabel = new Label(httpClientLimitsGroup, SWT.NONE);
				maxConnectionsPerServerLabel.setText("Max connections per server:");
				maxConnectionsPerServerLabel.setEnabled(true);
				maxConnectionsPerServerLabel.setBounds(12, 57, 232, 17);
				maxConnectionsPerServerLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
			}
			{
				maxTotalConnectionsLabel = new Label(httpClientLimitsGroup, SWT.NONE);
				maxTotalConnectionsLabel.setText("Max total connections:");
				maxTotalConnectionsLabel.setEnabled(true);
				maxTotalConnectionsLabel.setBounds(12, 90, 239, 25);
				maxTotalConnectionsLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
			}
			{
				maxConnectionsPerServerTextBox = new Text(httpClientLimitsGroup, SWT.BORDER);
				maxConnectionsPerServerTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
				maxConnectionsPerServerTextBox.setTextLimit(3);
				maxConnectionsPerServerTextBox.setEnabled(true);
				maxConnectionsPerServerTextBox.setBounds(263, 57, 45, 25);
				maxConnectionsPerServerTextBox.addVerifyListener(numbersOnlyVerifyer);
			}
			{
				maxTotalConnectionsTextBox = new Text(httpClientLimitsGroup, SWT.BORDER);
				maxTotalConnectionsTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
				maxTotalConnectionsTextBox.setTextLimit(3);
				maxTotalConnectionsTextBox.setEnabled(true);
				maxTotalConnectionsTextBox.setBounds(263, 90, 45, 25);
				maxTotalConnectionsTextBox.addVerifyListener(numbersOnlyVerifyer);
			}
			{
				maxConsecutiveFailedRequestsLabel = new Label(httpClientLimitsGroup, SWT.NONE);
				maxConsecutiveFailedRequestsLabel.setText("Max consecutive failed requests:");
				maxConsecutiveFailedRequestsLabel.setBounds(12, 122, 232, 30);
				maxConsecutiveFailedRequestsLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
			}
			{
				maxConsecutiveFailedRequestsTextBox = new Text(httpClientLimitsGroup, SWT.BORDER);
				maxConsecutiveFailedRequestsTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
				maxConsecutiveFailedRequestsTextBox.setBounds(263, 122, 45, 25);
			}
			{
				maxFailedRequestsPerServerLabel = new Label(httpClientLimitsGroup, SWT.NONE);
				maxFailedRequestsPerServerLabel.setText("Max failed requests per server:");
				maxFailedRequestsPerServerLabel.setBounds(12, 153, 239, 30);
				maxFailedRequestsPerServerLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
			}
			{
				maxFailedRequestsPerServerTextBox = new Text(httpClientLimitsGroup, SWT.BORDER);
				maxFailedRequestsPerServerTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
				maxFailedRequestsPerServerTextBox.setBounds(263, 155, 45, 25);
			}
			{
				socketReadTimeoutLabel = new Label(httpClientLimitsGroup, SWT.NONE);
				socketReadTimeoutLabel.setText("Socket read timeout (seconds):");
				socketReadTimeoutLabel.setBounds(12, 185, 232, 30);
				socketReadTimeoutLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
			}
			{
				socketReadTimeoutTextBox = new Text(httpClientLimitsGroup, SWT.BORDER);
				socketReadTimeoutTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
				socketReadTimeoutTextBox.setBounds(263, 187, 45, 25);
			}
			// {
			// maxTotalRequestsLabel = new Label(httpClientLimitsGroup,
			// SWT.NONE);
			// maxTotalRequestsLabel.setText("Max total requests:");
			// maxTotalRequestsLabel.setBounds(12, 217, 132, 30);
			// maxTotalRequestsLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName,GUIConstants.fontSize,0,false,false));
			// }
			// {
			// maxTotalRequestsTextBox = new Text(httpClientLimitsGroup,
			// SWT.BORDER);
			// maxTotalRequestsTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,GUIConstants.fontSize,0,false,false));
			// maxTotalRequestsTextBox.setBounds(219, 218, 80, 25);
			// }
			{
				maxFileSizeLabel = new Label(httpClientLimitsGroup, SWT.NONE);
				maxFileSizeLabel.setText("Max response size (kilobytes):");
				maxFileSizeLabel.setBounds(12, 218, 232, 30);
				maxFileSizeLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize,
							0, false, false));
			}
			{
				maxFileSizeTextBox = new Text(httpClientLimitsGroup, SWT.BORDER);
				maxFileSizeTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize,
							0, false, false));
				maxFileSizeTextBox.setBounds(263, 218, 80, 30);
				maxFileSizeTextBox.addVerifyListener(numbersOnlyVerifyer);
			}
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


	void updateUpstreamProxyStatus()
	{
		upstreamProxyAddressLabel.setEnabled(useUpstreamProxyCheckBox.getSelection());
		upstreamProxyPortLabel.setEnabled(useUpstreamProxyCheckBox.getSelection());
		upstreamProxyAddressTextBox.setEnabled(useUpstreamProxyCheckBox.getSelection());
		upstreamProxyPortTextBox.setEnabled(useUpstreamProxyCheckBox.getSelection());
	}

	void updateSocksProxyStatus()
	{
		socksProxyAddressLabel.setEnabled(useSocksProxyCheckBox.getSelection());
		socksProxyPortLabel.setEnabled(useSocksProxyCheckBox.getSelection());
		socksProxyAddressTextBox.setEnabled(useSocksProxyCheckBox.getSelection());
		socksProxyPortTextBox.setEnabled(useSocksProxyCheckBox.getSelection());
	}

	@Override
	public String updateToSettings()
	{

		Scan.getScanSettings().setUpstreamProxyAddress(upstreamProxyAddressTextBox.getText());
		Scan.getScanSettings().setUseUpstreamProxy(useUpstreamProxyCheckBox.getSelection());
		Scan.getScanSettings().setUpstreamProxyPort(Integer.valueOf(upstreamProxyPortTextBox.getText()));
		Scan.getScanSettings().setUpstreamProxyUsername(upstreamProxyUsernameTextBox.getText());
		Scan.getScanSettings().setUpstreamProxyPassword(upstreamProxyPasswordTextBox.getText());
		
		Scan.getScanSettings().setUseSocksProxy(useSocksProxyCheckBox.getSelection());
		Scan.getScanSettings().setSocksHost(socksProxyAddressTextBox.getText());
		Scan.getScanSettings().setSocksPort(Integer.valueOf(socksProxyPortTextBox.getText()));

		if (upstreamProxyDigestAuthenticationRadioButton.getSelection())
		{
			Scan.getScanSettings().setUpstreamProxyAuthenticationType(HttpAuthenticationType.DIGEST);
		}
		else if (upstreamProxyBasicAuthenticationRadioButton.getSelection())
		{
			Scan.getScanSettings().setUpstreamProxyAuthenticationType(HttpAuthenticationType.BASIC);
		}
		else if (upstreamProxyNTLMAuthenticationRadioButton.getSelection())
		{
			Scan.getScanSettings().setUpstreamProxyAuthenticationType(HttpAuthenticationType.NTLM);
		}

		Scan.getScanSettings().setMaxRequestsPerSecond(Integer.valueOf(maxRequestsPerSecondTextBox.getText()));
		Scan.getScanSettings().setMaxConnectionsPerServer(Integer.valueOf(maxConnectionsPerServerTextBox.getText()));
		Scan.getScanSettings().setMaxTotalConnections(Integer.valueOf(maxTotalConnectionsTextBox.getText()));
		Scan.getScanSettings().setMaxConsecutiveFailedRequests(Integer.valueOf(maxConsecutiveFailedRequestsTextBox.getText()));
		Scan.getScanSettings().setMaxFailedRequestsPerHost(Integer.valueOf(maxFailedRequestsPerServerTextBox.getText()));
		Scan.getScanSettings().setSocketReadTimeout(Integer.valueOf(socketReadTimeoutTextBox.getText()));
		Scan.getScanSettings().setMaxFileSizeKiloBytes(Integer.valueOf(maxFileSizeTextBox.getText()));

		return "";
	}
}
