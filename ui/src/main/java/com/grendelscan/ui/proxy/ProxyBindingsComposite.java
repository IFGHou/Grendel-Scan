package com.grendelscan.ui.proxy;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;

import com.grendelscan.proxy.ForwardProxy;
import com.grendelscan.proxy.ReverseProxyConfig;
import com.grendelscan.scan.Scan;
import com.grendelscan.ui.Verifiers.EnforceIntegersOnly;
import com.grendelscan.ui.Verifiers.EnforceLooseIPAddress;
import com.grendelscan.ui.customControls.basic.GButton;
import com.grendelscan.ui.customControls.basic.GGroup;
import com.grendelscan.ui.customControls.basic.GLabel;
import com.grendelscan.ui.customControls.basic.GText;
import com.grendelscan.ui.settings.GrendelSettingsControl;

public class ProxyBindingsComposite extends com.grendelscan.ui.customControls.basic.GComposite implements GrendelSettingsControl
{

    protected GButton startForwardProxyButton;

    protected GButton enableProxyCheckBox;
    protected GLabel proxyBindAddressLabel;
    protected GText proxyBindAddressTextBox;
    protected GLabel proxyBindPortLabel;
    protected GText proxyBindPortTextBox;
    // protected GLabel maxProxyThreadLabel;
    // protected GText maxProxyThreadTextBox;
    protected GGroup reverseProxySettingsGroup;

    protected ReverseProxySettingsComposite reverseProxySettingsComposite;

    public ProxyBindingsComposite(final GGroup parent, final int style)
    {
        super(parent, style);
        initGUI();
    }

    public void displayReverseProxyConfigs(final List<ReverseProxyConfig> configs)
    {
        reverseProxySettingsComposite.displayReverseProxyConfigs(configs);
    }

    public void externalUpdateForwardProxyGUIStatus()
    {
        if (!isDisposed())
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

    public String getLocalIP()
    {
        return proxyBindAddressTextBox.getText();
    }

    public int getLocalPort()
    {
        return Integer.valueOf(proxyBindPortTextBox.getText());
    }

    public ReverseProxyConfig[] getReverseProxyConfigs()
    {
        return reverseProxySettingsComposite.getReverseProxyConfigs();
    }

    private void initGUI()
    {
        setLayout(new FormLayout());
        {
            EnforceIntegersOnly numbersOnlyVerifyer = new EnforceIntegersOnly();
            EnforceLooseIPAddress ipAddressOnlyVerifyer = new EnforceLooseIPAddress();

            GGroup mainProxySettingsGroup = new GGroup(this, SWT.NONE);
            mainProxySettingsGroup.setLayout(new FormLayout());
            mainProxySettingsGroup.setText("Main proxy settings");
            FormData groupLayoutData = new FormData();
            groupLayoutData.width = 450;
            groupLayoutData.height = 120;
            groupLayoutData.left = new FormAttachment(0, 1000, 5);
            groupLayoutData.top = new FormAttachment(0, 1000, 5);
            mainProxySettingsGroup.setLayoutData(groupLayoutData);
            {
                int currentLeft = 5;
                int currentTop = 5;
                {
                    startForwardProxyButton = new GButton(mainProxySettingsGroup, SWT.PUSH | SWT.CENTER);
                    FormData layoutData = new FormData();
                    layoutData.width = 60;
                    layoutData.height = 27;
                    layoutData.left = new FormAttachment(0, 1000, currentLeft);
                    layoutData.top = new FormAttachment(0, 1000, currentTop);
                    startForwardProxyButton.setLayoutData(layoutData);
                    // startForwardProxyButton.setText("Start");
                    currentTop += 8;
                    startForwardProxyButton.addSelectionListener(new SelectionAdapter()
                    {
                        @Override
                        public void widgetSelected(final SelectionEvent evt)
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
                // else
                // {
                // enableProxyCheckBox = new GButton(mainProxySettingsGroup, SWT.CHECK | SWT.LEFT);
                // enableProxyCheckBox.setText("Enable internal proxy");
                // enableProxyCheckBox.setFontf(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
                // FormData layoutData = new FormData();
                // layoutData.width = 150;
                // layoutData.height = 19;
                // layoutData.left = new FormAttachment(0, 1000, currentLeft);
                // layoutData.top = new FormAttachment(0, 1000, currentTop);
                // enableProxyCheckBox.setLayoutData(layoutData);
                // enableProxyCheckBox.addSelectionListener
                // (
                // new SelectionAdapter()
                // {
                // @Override
                // public void widgetSelected(SelectionEvent event)
                // {
                // updateForwardProxyGUIStatus();
                // }
                // }
                // );
                // }

                currentTop += 30;
                currentLeft = 5;
                {
                    proxyBindAddressLabel = new GLabel(mainProxySettingsGroup, SWT.NONE);
                    proxyBindAddressLabel.setText("Proxy bind address:");
                    FormData layoutData = new FormData();
                    layoutData.width = 115;
                    layoutData.height = 19;
                    layoutData.left = new FormAttachment(0, 1000, currentLeft);
                    layoutData.top = new FormAttachment(0, 1000, currentTop);
                    proxyBindAddressLabel.setLayoutData(layoutData);
                    currentLeft += layoutData.width + 5;
                }
                {
                    proxyBindAddressTextBox = new GText(mainProxySettingsGroup, SWT.BORDER);
                    FormData layoutData = new FormData();
                    layoutData.width = 100;
                    layoutData.height = 19;
                    layoutData.left = new FormAttachment(0, 1000, currentLeft);
                    layoutData.top = new FormAttachment(0, 1000, currentTop);
                    proxyBindAddressTextBox.setLayoutData(layoutData);
                    proxyBindAddressTextBox.addVerifyListener(ipAddressOnlyVerifyer);
                    currentLeft += layoutData.width + 25;
                }
                {
                    proxyBindPortLabel = new GLabel(mainProxySettingsGroup, SWT.NONE);
                    proxyBindPortLabel.setText("Proxy bind port:");
                    FormData layoutData = new FormData();
                    layoutData.width = 90;
                    layoutData.height = 19;
                    layoutData.left = new FormAttachment(0, 1000, currentLeft);
                    layoutData.top = new FormAttachment(0, 1000, currentTop);
                    proxyBindPortLabel.setLayoutData(layoutData);
                    currentLeft += layoutData.width + 5;
                }
                {
                    proxyBindPortTextBox = new GText(mainProxySettingsGroup, SWT.BORDER);
                    proxyBindPortTextBox.setTextLimit(5);
                    FormData layoutData = new FormData();
                    layoutData.width = 40;
                    layoutData.height = 19;
                    layoutData.left = new FormAttachment(0, 1000, currentLeft);
                    layoutData.top = new FormAttachment(0, 1000, currentTop);
                    proxyBindPortTextBox.setLayoutData(layoutData);
                    proxyBindPortTextBox.addVerifyListener(numbersOnlyVerifyer);
                    currentLeft += layoutData.width + 15;
                }

                currentTop += 35;
                currentLeft = 5;
                // {
                // maxProxyThreadLabel = new GLabel(mainProxySettingsGroup, SWT.NONE);
                // maxProxyThreadLabel.setText("Max proxy threads:");
                // FormData layoutData = new FormData();
                // layoutData.width = 115;
                // layoutData.height = 19;
                // layoutData.left = new FormAttachment(0, 1000, currentLeft);
                // layoutData.top = new FormAttachment(0, 1000, currentTop);
                // maxProxyThreadLabel.setLayoutData(layoutData);
                // maxProxyThreadLabel.setFontf(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
                // currentLeft += layoutData.width + 5;
                // }
                // {
                // maxProxyThreadTextBox = new GText(mainProxySettingsGroup, SWT.BORDER);
                // FormData layoutData = new FormData();
                // layoutData.width = 30;
                // layoutData.height = 19;
                // layoutData.left = new FormAttachment(0, 1000, currentLeft);
                // layoutData.top = new FormAttachment(0, 1000, currentTop);
                // maxProxyThreadTextBox.setLayoutData(layoutData);
                // maxProxyThreadTextBox.setTextLimit(2);
                // maxProxyThreadTextBox.setFontf(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
                // maxProxyThreadTextBox.addVerifyListener(numbersOnlyVerifyer);
                // currentLeft += layoutData.width + 15;
                // }
            }
        }

        {
            reverseProxySettingsGroup = new GGroup(this, SWT.NONE);
            reverseProxySettingsGroup.setLayout(new FormLayout());
            reverseProxySettingsGroup.setText("Reverse Proxy Settings");
            FormData groupLayoutData = new FormData();
            groupLayoutData.left = new FormAttachment(0, 1000, 5);
            groupLayoutData.top = new FormAttachment(0, 1000, 160);
            groupLayoutData.right = new FormAttachment(1000, 1000, -5);
            groupLayoutData.bottom = new FormAttachment(1000, 1000, -5);
            reverseProxySettingsGroup.setLayoutData(groupLayoutData);
            {
                reverseProxySettingsComposite = new ReverseProxySettingsComposite(reverseProxySettingsGroup, SWT.NONE);
                reverseProxySettingsComposite.setLayout(new FormLayout());
                FormData layoutData = new FormData();
                layoutData.left = new FormAttachment(0, 1000, 5);
                layoutData.top = new FormAttachment(0, 1000, 5);
                layoutData.right = new FormAttachment(1000, 1000, -5);
                layoutData.bottom = new FormAttachment(1000, 1000, -5);
                reverseProxySettingsComposite.setLayoutData(layoutData);
            }
        }
        this.layout();
    }

    public boolean isProxyEnabled()
    {
        return enableProxyCheckBox.getSelection();
    }

    public void setLocalIP(final String localIP)
    {
        proxyBindAddressTextBox.setText(localIP);
    }

    public void setLocalPort(final int port)
    {
        proxyBindPortTextBox.setText(String.valueOf(port));
    }

    // public int getMaxProxyTreads()
    // {
    // return Integer.valueOf(maxProxyThreadTextBox.getText());
    // }
    // public void setMaxProxyTreads(int threadCount)
    // {
    // maxProxyThreadTextBox.setText(String.valueOf(threadCount));
    // }

    public void setProxyEnabled(final boolean enabled)
    {
        enableProxyCheckBox.setSelection(enabled);
    }

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
        // maxProxyThreadLabel.setEnabled(enabled);
        // maxProxyThreadTextBox.setEnabled(enabled);
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
