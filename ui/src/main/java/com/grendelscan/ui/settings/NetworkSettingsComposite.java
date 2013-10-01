/**
 * 
 */
package com.grendelscan.ui.settings;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;

import com.grendelscan.scan.Scan;
import com.grendelscan.scan.authentication.HttpAuthenticationType;
import com.grendelscan.ui.GuiUtils;
import com.grendelscan.ui.Verifiers.EnforceIntegersOnly;
import com.grendelscan.ui.customControls.basic.GButton;
import com.grendelscan.ui.customControls.basic.GGroup;
import com.grendelscan.ui.customControls.basic.GLabel;
import com.grendelscan.ui.customControls.basic.GText;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is being used commercially (ie, by a corporation, company or
 * business for any purpose whatever) then you should purchase a license for each developer using Jigloo. Please visit www.cloudgarden.com for details. Use of Jigloo implies acceptance of these
 * licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class NetworkSettingsComposite extends com.grendelscan.ui.customControls.basic.GComposite implements GrendelSettingsControl
{
    private GGroup httpClientLimitsGroup;
    private GLabel maxConnectionsPerServerLabel;
    private GText maxConnectionsPerServerTextBox;
    private GLabel maxConsecutiveFailedRequestsLabel;
    private GText maxConsecutiveFailedRequestsTextBox;
    private GLabel maxFailedRequestsPerServerLabel;
    private GText maxFailedRequestsPerServerTextBox;
    private GLabel maxFileSizeLabel;
    private GText maxFileSizeTextBox;
    private GLabel maxRequestsPerSecondLabel;
    private GText maxRequestsPerSecondTextBox;
    private GLabel maxTotalConnectionsLabel;
    private GText maxTotalConnectionsTextBox;
    private GLabel socksProxyAddressLabel;
    private GLabel socksProxyPortLabel;
    // private GText maxTotalRequestsTextBox;
    private GLabel socketReadTimeoutLabel;
    private GText socketReadTimeoutTextBox;
    private GLabel upstreamProxyAddressLabel;
    private GText upstreamProxyAddressTextBox;
    private GText socksProxyAddressTextBox;
    private GButton applyButton;
    private GGroup upstreamProxyAuthenticationTypeGroup;
    private GButton upstreamProxyBasicAuthenticationRadioButton;
    private GButton upstreamProxyDigestAuthenticationRadioButton;
    private GGroup upstreamProxyGroup;
    private GGroup socksProxyGroup;
    private GButton upstreamProxyNTLMAuthenticationRadioButton;
    private GLabel upstreamProxyPasswordLabel;
    private GText upstreamProxyPasswordTextBox;
    private GLabel upstreamProxyPortLabel;
    private GText upstreamProxyPortTextBox;
    private GText socksProxyPortTextBox;
    private GLabel upstreamProxyUsernameLabel;
    private GText upstreamProxyUsernameTextBox;
    private GButton useUpstreamProxyCheckBox;
    private GButton useSocksProxyCheckBox;

    {
        // Register as a resource user - SWTResourceManager will
        // handle the obtaining and disposing of resources
        GuiUtils.registerResourceUser(this);
    }

    public NetworkSettingsComposite(final com.grendelscan.ui.customControls.basic.GComposite parent, final int style)
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

    private void initGUI()
    {
        EnforceIntegersOnly numbersOnlyVerifyer = new EnforceIntegersOnly();
        FormLayout thisLayout = new FormLayout();
        setLayout(thisLayout);
        {
            applyButton = new GButton(this, SWT.PUSH | SWT.CENTER);
            FormData applyButtonLData = new FormData();
            applyButtonLData.width = 118;
            applyButtonLData.height = 33;
            applyButtonLData.top = new FormAttachment(0, 1000, 330);
            applyButtonLData.left = new FormAttachment(0, 1000, 765);
            applyButton.setLayoutData(applyButtonLData);
            applyButton.setText("Apply Settings");
            applyButton.addSelectionListener(new SelectionAdapter()
            {
                @Override
                public void widgetSelected(final SelectionEvent evt)
                {
                    updateToSettings();
                }
            });
        }
        // {
        // socksProxyGroup = new GGroup(this, SWT.NONE);
        // socksProxyGroup.setText("Upstream HTTP Proxy");
        // socksProxyGroup.setFontf(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0,
        // false, false));
        // FormData upstreamProxyGroupLData = new FormData();
        // upstreamProxyGroupLData.width = 448;
        // upstreamProxyGroupLData.height = 183;
        // upstreamProxyGroupLData.left = new FormAttachment(0, 1000, 433);
        // upstreamProxyGroupLData.top = new FormAttachment(0, 1000, 0);
        // socksProxyGroup.setLayoutData(upstreamProxyGroupLData);
        // socksProxyGroup.setLayout(null);
        // {
        // useUpstreamProxyCheckBox = new GButton(socksProxyGroup, SWT.CHECK | SWT.LEFT);
        // useUpstreamProxyCheckBox.setText("Use upstream HTTP proxy");
        // useUpstreamProxyCheckBox.setBounds(12, 19, 200, 30);
        // useUpstreamProxyCheckBox.setFontf(SWTResourceManager.getFont(GUIConstants.fontName,
        // GUIConstants.fontSize, 0, false, false));
        // useUpstreamProxyCheckBox.addSelectionListener(new SelectionAdapter()
        // {
        // @Override
        // public void widgetSelected(SelectionEvent e)
        // {
        // updateUpstreamProxyStatus();
        // }
        // });
        // }
        // {
        // upstreamProxyAddressLabel = new GLabel(socksProxyGroup, SWT.NONE);
        // upstreamProxyAddressLabel.setText("Address:");
        // upstreamProxyAddressLabel.setBounds(10, 53, 66, 23);
        // upstreamProxyAddressLabel.setFontf(SWTResourceManager.getFont(GUIConstants.fontName,
        // GUIConstants.fontSize, 0, false, false));
        // }
        // {
        // upstreamProxyPortLabel = new GLabel(socksProxyGroup, SWT.NONE);
        // upstreamProxyPortLabel.setText("Port:");
        // upstreamProxyPortLabel.setBounds(10, 89, 60, 25);
        // upstreamProxyPortLabel.setFontf(SWTResourceManager.getFont(GUIConstants.fontName,
        // GUIConstants.fontSize, 0, false, false));
        // }
        // {
        // upstreamProxyAddressTextBox = new GText(socksProxyGroup, SWT.BORDER);
        // upstreamProxyAddressTextBox.setFontf(SWTResourceManager.getFont(GUIConstants.fontName,
        // GUIConstants.fontSize, 0, false, false));
        // upstreamProxyAddressTextBox.setBounds(86, 52, 301, 25);
        // }
        // {
        // upstreamProxyPortTextBox = new GText(socksProxyGroup, SWT.BORDER);
        // upstreamProxyPortTextBox.setFontf(SWTResourceManager.getFont(GUIConstants.fontName,
        // GUIConstants.fontSize, 0, false, false));
        // upstreamProxyPortTextBox.setTextLimit(5);
        // upstreamProxyPortTextBox.setBounds(86, 87, 69, 25);
        // upstreamProxyPortTextBox.addVerifyListener(numbersOnlyVerifyer);
        // }
        // {
        // upstreamProxyUsernameTextBox = new GText(socksProxyGroup, SWT.BORDER);
        // upstreamProxyUsernameTextBox.setFontf(SWTResourceManager.getFont(GUIConstants.fontName,
        // GUIConstants.fontSize, 0, false, false));
        // upstreamProxyUsernameTextBox.setEnabled(false);
        // upstreamProxyUsernameTextBox.setBounds(86, 122, 111, 25);
        // }
        // {
        // upstreamProxyUsernameLabel = new GLabel(socksProxyGroup, SWT.NONE);
        // upstreamProxyUsernameLabel.setText("Username:");
        // upstreamProxyUsernameLabel.setEnabled(false);
        // upstreamProxyUsernameLabel.setBounds(10, 127, 74, 25);
        // upstreamProxyUsernameLabel.setFontf(SWTResourceManager.getFont(GUIConstants.fontName,
        // GUIConstants.fontSize, 0, false, false));
        // }
        // {
        // upstreamProxyPasswordLabel = new GLabel(socksProxyGroup, SWT.NONE);
        // upstreamProxyPasswordLabel.setText("Password:");
        // upstreamProxyPasswordLabel.setEnabled(false);
        // upstreamProxyPasswordLabel.setBounds(10, 160, 74, 25);
        // upstreamProxyPasswordLabel.setFontf(SWTResourceManager.getFont(GUIConstants.fontName,
        // GUIConstants.fontSize, 0, false, false));
        // }
        // {
        // upstreamProxyPasswordTextBox = new GText(socksProxyGroup, SWT.BORDER);
        // upstreamProxyPasswordTextBox.setFontf(SWTResourceManager.getFont(GUIConstants.fontName,
        // GUIConstants.fontSize, 0, false, false));
        // upstreamProxyPasswordTextBox.setEnabled(false);
        // upstreamProxyPasswordTextBox.setBounds(86, 158, 113, 25);
        // }
        // {
        // upstreamProxyAuthenticationTypeGroup = new GGroup(socksProxyGroup, SWT.NONE);
        // upstreamProxyAuthenticationTypeGroup.setText("Authentication Type");
        // upstreamProxyAuthenticationTypeGroup.setFontf(SWTResourceManager.getFont(GUIConstants.fontName,
        // GUIConstants.fontSize, 0, false, false));
        // upstreamProxyAuthenticationTypeGroup.setEnabled(false);
        // upstreamProxyAuthenticationTypeGroup.setBounds(240, 87, 202, 107);
        // upstreamProxyAuthenticationTypeGroup.setLayout(null);
        // {
        // upstreamProxyBasicAuthenticationRadioButton =
        // new GButton(upstreamProxyAuthenticationTypeGroup, SWT.RADIO | SWT.LEFT);
        // upstreamProxyBasicAuthenticationRadioButton.setText("Basic");
        // upstreamProxyBasicAuthenticationRadioButton.setEnabled(false);
        // upstreamProxyBasicAuthenticationRadioButton.setBounds(8, 22, 85, 25);
        // upstreamProxyBasicAuthenticationRadioButton.setFontf(SWTResourceManager.getFont(
        // GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
        // }
        // {
        // upstreamProxyDigestAuthenticationRadioButton =
        // new GButton(upstreamProxyAuthenticationTypeGroup, SWT.RADIO | SWT.LEFT);
        // upstreamProxyDigestAuthenticationRadioButton.setText("Digest");
        // upstreamProxyDigestAuthenticationRadioButton.setSelection(true);
        // upstreamProxyDigestAuthenticationRadioButton.setEnabled(false);
        // upstreamProxyDigestAuthenticationRadioButton.setBounds(8, 47, 60, 25);
        // upstreamProxyDigestAuthenticationRadioButton.setFontf(SWTResourceManager.getFont(
        // GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
        // }
        // {
        // upstreamProxyNTLMAuthenticationRadioButton =
        // new GButton(upstreamProxyAuthenticationTypeGroup, SWT.RADIO | SWT.LEFT);
        // upstreamProxyNTLMAuthenticationRadioButton.setText("NTLM");
        // upstreamProxyNTLMAuthenticationRadioButton.setEnabled(false);
        // upstreamProxyNTLMAuthenticationRadioButton.setBounds(8, 72, 60, 25);
        // upstreamProxyNTLMAuthenticationRadioButton.setFontf(SWTResourceManager.getFont(
        // GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
        // }
        // }
        // }
        {
            upstreamProxyGroup = new GGroup(this, SWT.NONE);
            upstreamProxyGroup.setText("Upstream HTTP Proxy");
            upstreamProxyGroup.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
            FormData upstreamProxyGroupLData = new FormData();
            upstreamProxyGroupLData.width = 448;
            upstreamProxyGroupLData.height = 183;
            upstreamProxyGroupLData.left = new FormAttachment(0, 1000, 433);
            upstreamProxyGroupLData.top = new FormAttachment(0, 1000, 0);
            upstreamProxyGroup.setLayoutData(upstreamProxyGroupLData);
            upstreamProxyGroup.setLayout(null);
            {
                useUpstreamProxyCheckBox = new GButton(upstreamProxyGroup, SWT.CHECK | SWT.LEFT);
                useUpstreamProxyCheckBox.setText("Use upstream HTTP proxy");
                useUpstreamProxyCheckBox.setBounds(12, 19, 200, 30);
                useUpstreamProxyCheckBox.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
                useUpstreamProxyCheckBox.addSelectionListener(new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(final SelectionEvent e)
                    {
                        updateUpstreamProxyStatus();
                    }
                });
            }
            {
                upstreamProxyAddressLabel = new GLabel(upstreamProxyGroup, SWT.NONE);
                upstreamProxyAddressLabel.setText("Address:");
                upstreamProxyAddressLabel.setBounds(10, 53, 66, 23);
                upstreamProxyAddressLabel.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
            }
            {
                upstreamProxyPortLabel = new GLabel(upstreamProxyGroup, SWT.NONE);
                upstreamProxyPortLabel.setText("Port:");
                upstreamProxyPortLabel.setBounds(10, 89, 60, 25);
                upstreamProxyPortLabel.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
            }
            {
                upstreamProxyAddressTextBox = new GText(upstreamProxyGroup, SWT.BORDER);
                upstreamProxyAddressTextBox.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
                upstreamProxyAddressTextBox.setBounds(86, 52, 301, 25);
            }
            {
                upstreamProxyPortTextBox = new GText(upstreamProxyGroup, SWT.BORDER);
                upstreamProxyPortTextBox.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
                upstreamProxyPortTextBox.setTextLimit(5);
                upstreamProxyPortTextBox.setBounds(86, 87, 69, 25);
                upstreamProxyPortTextBox.addVerifyListener(numbersOnlyVerifyer);
            }
            {
                upstreamProxyUsernameTextBox = new GText(upstreamProxyGroup, SWT.BORDER);
                upstreamProxyUsernameTextBox.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
                upstreamProxyUsernameTextBox.setEnabled(false);
                upstreamProxyUsernameTextBox.setBounds(86, 122, 111, 25);
            }
            {
                upstreamProxyUsernameLabel = new GLabel(upstreamProxyGroup, SWT.NONE);
                upstreamProxyUsernameLabel.setText("Username:");
                upstreamProxyUsernameLabel.setEnabled(false);
                upstreamProxyUsernameLabel.setBounds(10, 127, 74, 25);
                upstreamProxyUsernameLabel.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
            }
            {
                upstreamProxyPasswordLabel = new GLabel(upstreamProxyGroup, SWT.NONE);
                upstreamProxyPasswordLabel.setText("Password:");
                upstreamProxyPasswordLabel.setEnabled(false);
                upstreamProxyPasswordLabel.setBounds(10, 160, 74, 25);
                upstreamProxyPasswordLabel.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
            }
            {
                upstreamProxyPasswordTextBox = new GText(upstreamProxyGroup, SWT.BORDER);
                upstreamProxyPasswordTextBox.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
                upstreamProxyPasswordTextBox.setEnabled(false);
                upstreamProxyPasswordTextBox.setBounds(86, 158, 113, 25);
            }
            {
                upstreamProxyAuthenticationTypeGroup = new GGroup(upstreamProxyGroup, SWT.NONE);
                upstreamProxyAuthenticationTypeGroup.setText("Authentication Type");
                upstreamProxyAuthenticationTypeGroup.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
                upstreamProxyAuthenticationTypeGroup.setEnabled(false);
                upstreamProxyAuthenticationTypeGroup.setBounds(240, 87, 202, 107);
                upstreamProxyAuthenticationTypeGroup.setLayout(null);
                {
                    upstreamProxyBasicAuthenticationRadioButton = new GButton(upstreamProxyAuthenticationTypeGroup, SWT.RADIO | SWT.LEFT);
                    upstreamProxyBasicAuthenticationRadioButton.setText("Basic");
                    upstreamProxyBasicAuthenticationRadioButton.setEnabled(false);
                    upstreamProxyBasicAuthenticationRadioButton.setBounds(8, 22, 85, 25);
                    upstreamProxyBasicAuthenticationRadioButton.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
                }
                {
                    upstreamProxyDigestAuthenticationRadioButton = new GButton(upstreamProxyAuthenticationTypeGroup, SWT.RADIO | SWT.LEFT);
                    upstreamProxyDigestAuthenticationRadioButton.setText("Digest");
                    upstreamProxyDigestAuthenticationRadioButton.setSelection(true);
                    upstreamProxyDigestAuthenticationRadioButton.setEnabled(false);
                    upstreamProxyDigestAuthenticationRadioButton.setBounds(8, 47, 60, 25);
                    upstreamProxyDigestAuthenticationRadioButton.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
                }
                {
                    upstreamProxyNTLMAuthenticationRadioButton = new GButton(upstreamProxyAuthenticationTypeGroup, SWT.RADIO | SWT.LEFT);
                    upstreamProxyNTLMAuthenticationRadioButton.setText("NTLM");
                    upstreamProxyNTLMAuthenticationRadioButton.setEnabled(false);
                    upstreamProxyNTLMAuthenticationRadioButton.setBounds(8, 72, 60, 25);
                    upstreamProxyNTLMAuthenticationRadioButton.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
                }
            }
        }

        {
            socksProxyGroup = new GGroup(this, SWT.NONE);
            socksProxyGroup.setText("Upstream HTTP Proxy");
            socksProxyGroup.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
            FormData upstreamProxyGroupLData = new FormData();
            upstreamProxyGroupLData.width = 448;
            upstreamProxyGroupLData.height = 107;
            upstreamProxyGroupLData.left = new FormAttachment(0, 1000, 433);
            upstreamProxyGroupLData.top = new FormAttachment(0, 1000, 200);
            socksProxyGroup.setLayoutData(upstreamProxyGroupLData);
            socksProxyGroup.setLayout(null);
            {
                useSocksProxyCheckBox = new GButton(socksProxyGroup, SWT.CHECK | SWT.LEFT);
                useSocksProxyCheckBox.setText("Use SOCKS proxy");
                useSocksProxyCheckBox.setBounds(12, 19, 200, 30);
                useSocksProxyCheckBox.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
                useSocksProxyCheckBox.addSelectionListener(new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(final SelectionEvent e)
                    {
                        updateSocksProxyStatus();
                    }
                });
            }
            {
                socksProxyAddressLabel = new GLabel(socksProxyGroup, SWT.NONE);
                socksProxyAddressLabel.setText("Address:");
                socksProxyAddressLabel.setBounds(10, 53, 66, 23);
                socksProxyAddressLabel.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
            }
            {
                socksProxyPortLabel = new GLabel(socksProxyGroup, SWT.NONE);
                socksProxyPortLabel.setText("Port:");
                socksProxyPortLabel.setBounds(10, 89, 60, 25);
                socksProxyPortLabel.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
            }
            {
                socksProxyAddressTextBox = new GText(socksProxyGroup, SWT.BORDER);
                socksProxyAddressTextBox.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
                socksProxyAddressTextBox.setBounds(86, 52, 301, 25);
            }
            {
                socksProxyPortTextBox = new GText(socksProxyGroup, SWT.BORDER);
                socksProxyPortTextBox.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
                socksProxyPortTextBox.setTextLimit(5);
                socksProxyPortTextBox.setBounds(86, 87, 69, 25);
                socksProxyPortTextBox.addVerifyListener(numbersOnlyVerifyer);
            }
        }
        {
            httpClientLimitsGroup = new GGroup(this, SWT.NONE);
            httpClientLimitsGroup.setText("Limits");
            httpClientLimitsGroup.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
            FormData httpClientLimitsGroupLData = new FormData();
            httpClientLimitsGroupLData.width = 403;
            httpClientLimitsGroupLData.height = 235;
            httpClientLimitsGroupLData.left = new FormAttachment(0, 1000, 12);
            httpClientLimitsGroupLData.top = new FormAttachment(0, 1000, 0);
            httpClientLimitsGroup.setLayoutData(httpClientLimitsGroupLData);
            httpClientLimitsGroup.setLayout(null);
            {
                maxRequestsPerSecondLabel = new GLabel(httpClientLimitsGroup, SWT.NONE);
                maxRequestsPerSecondLabel.setText("Max requests per second:");
                maxRequestsPerSecondLabel.setEnabled(true);
                maxRequestsPerSecondLabel.setBounds(12, 25, 239, 17);
                maxRequestsPerSecondLabel.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
            }
            {
                maxRequestsPerSecondTextBox = new GText(httpClientLimitsGroup, SWT.BORDER);
                maxRequestsPerSecondTextBox.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
                maxRequestsPerSecondTextBox.setTextLimit(4);
                maxRequestsPerSecondTextBox.setEnabled(true);
                maxRequestsPerSecondTextBox.setBounds(263, 25, 45, 25);
                maxRequestsPerSecondTextBox.addVerifyListener(numbersOnlyVerifyer);
            }
            {
                maxConnectionsPerServerLabel = new GLabel(httpClientLimitsGroup, SWT.NONE);
                maxConnectionsPerServerLabel.setText("Max connections per server:");
                maxConnectionsPerServerLabel.setEnabled(true);
                maxConnectionsPerServerLabel.setBounds(12, 57, 232, 17);
                maxConnectionsPerServerLabel.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
            }
            {
                maxTotalConnectionsLabel = new GLabel(httpClientLimitsGroup, SWT.NONE);
                maxTotalConnectionsLabel.setText("Max total connections:");
                maxTotalConnectionsLabel.setEnabled(true);
                maxTotalConnectionsLabel.setBounds(12, 90, 239, 25);
                maxTotalConnectionsLabel.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
            }
            {
                maxConnectionsPerServerTextBox = new GText(httpClientLimitsGroup, SWT.BORDER);
                maxConnectionsPerServerTextBox.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
                maxConnectionsPerServerTextBox.setTextLimit(3);
                maxConnectionsPerServerTextBox.setEnabled(true);
                maxConnectionsPerServerTextBox.setBounds(263, 57, 45, 25);
                maxConnectionsPerServerTextBox.addVerifyListener(numbersOnlyVerifyer);
            }
            {
                maxTotalConnectionsTextBox = new GText(httpClientLimitsGroup, SWT.BORDER);
                maxTotalConnectionsTextBox.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
                maxTotalConnectionsTextBox.setTextLimit(3);
                maxTotalConnectionsTextBox.setEnabled(true);
                maxTotalConnectionsTextBox.setBounds(263, 90, 45, 25);
                maxTotalConnectionsTextBox.addVerifyListener(numbersOnlyVerifyer);
            }
            {
                maxConsecutiveFailedRequestsLabel = new GLabel(httpClientLimitsGroup, SWT.NONE);
                maxConsecutiveFailedRequestsLabel.setText("Max consecutive failed requests:");
                maxConsecutiveFailedRequestsLabel.setBounds(12, 122, 232, 30);
                maxConsecutiveFailedRequestsLabel.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
            }
            {
                maxConsecutiveFailedRequestsTextBox = new GText(httpClientLimitsGroup, SWT.BORDER);
                maxConsecutiveFailedRequestsTextBox.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
                maxConsecutiveFailedRequestsTextBox.setBounds(263, 122, 45, 25);
            }
            {
                maxFailedRequestsPerServerLabel = new GLabel(httpClientLimitsGroup, SWT.NONE);
                maxFailedRequestsPerServerLabel.setText("Max failed requests per server:");
                maxFailedRequestsPerServerLabel.setBounds(12, 153, 239, 30);
                maxFailedRequestsPerServerLabel.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
            }
            {
                maxFailedRequestsPerServerTextBox = new GText(httpClientLimitsGroup, SWT.BORDER);
                maxFailedRequestsPerServerTextBox.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0, false, false));
                maxFailedRequestsPerServerTextBox.setBounds(263, 155, 45, 25);
            }
            {
                socketReadTimeoutLabel = new GLabel(httpClientLimitsGroup, SWT.NONE);
                socketReadTimeoutLabel.setText("Socket read timeout (seconds):");
                socketReadTimeoutLabel.setBounds(12, 185, 232, 30);
            }
            {
                socketReadTimeoutTextBox = new GText(httpClientLimitsGroup, SWT.BORDER);
                socketReadTimeoutTextBox.setBounds(263, 187, 45, 25);
            }
            // {
            // maxTotalRequestsLabel = new GLabel(httpClientLimitsGroup,
            // SWT.NONE);
            // maxTotalRequestsLabel.setText("Max total requests:");
            // maxTotalRequestsLabel.setBounds(12, 217, 132, 30);
            // maxTotalRequestsLabel.setFontf(SWTResourceManager.getFont(GUIConstants.fontName,GUIConstants.fontSize,0,false,false));
            // }
            // {
            // maxTotalRequestsTextBox = new GText(httpClientLimitsGroup,
            // SWT.BORDER);
            // maxTotalRequestsTextBox.setFontf(SWTResourceManager.getFont(GUIConstants.fontName,GUIConstants.fontSize,0,false,false));
            // maxTotalRequestsTextBox.setBounds(219, 218, 80, 25);
            // }
            {
                maxFileSizeLabel = new GLabel(httpClientLimitsGroup, SWT.NONE);
                maxFileSizeLabel.setText("Max response size (kilobytes):");
                maxFileSizeLabel.setBounds(12, 218, 232, 30);
            }
            {
                maxFileSizeTextBox = new GText(httpClientLimitsGroup, SWT.BORDER);
                maxFileSizeTextBox.setBounds(263, 218, 80, 30);
                maxFileSizeTextBox.addVerifyListener(numbersOnlyVerifyer);
            }
        }
        this.layout();
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
        maxConsecutiveFailedRequestsTextBox.setText(String.valueOf(Scan.getScanSettings().getMaxConsecutiveFailedRequests()));
        maxFailedRequestsPerServerTextBox.setText(String.valueOf(Scan.getScanSettings().getMaxFailedRequestsPerHost()));
        socketReadTimeoutTextBox.setText(String.valueOf(Scan.getScanSettings().getSocketReadTimeout()));
        maxFileSizeTextBox.setText(String.valueOf(Scan.getScanSettings().getMaxFileSizeKiloBytes()));
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

    void updateUpstreamProxyStatus()
    {
        upstreamProxyAddressLabel.setEnabled(useUpstreamProxyCheckBox.getSelection());
        upstreamProxyPortLabel.setEnabled(useUpstreamProxyCheckBox.getSelection());
        upstreamProxyAddressTextBox.setEnabled(useUpstreamProxyCheckBox.getSelection());
        upstreamProxyPortTextBox.setEnabled(useUpstreamProxyCheckBox.getSelection());
    }
}
