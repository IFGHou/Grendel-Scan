package com.grendelscan.GUI.AuthWizard;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.cloudgarden.resource.SWTResourceManager;
import com.grendelscan.GUI.GUIConstants;
import com.grendelscan.GUI.Verifiers.EnforceIntegersOnly;
import com.grendelscan.GUI.settings.scanSettings.AuthenticationSettingsComposite;
import com.grendelscan.logging.Log;
import com.grendelscan.proxy.ProxyConfig;
import com.grendelscan.proxy.authWizardProxy.AuthWizardProxy;
import com.grendelscan.scan.Scan;
import com.grendelscan.utils.URIStringUtils;

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
public class AuthWizardDialog extends org.eclipse.swt.widgets.Dialog
{
	Shell dialogShell;
	private Composite wizardComposite;
	private Group proxySettingsGroup;
	Text httpMethodTextBox;
	private Label httpMethodLabel;
	private Button cancelButton;
	private Button completeButton;
	private Label directionsLabel;
	private Label authenticationUrlLabel;
	Text authenticationUrlTextBox;
	Text postQueryTextbox;
	private Label postQueryLabel;
	Text passwordFieldTextbox;
	private Label passwordFieldLabel;
	private Label usernameFieldLabel;
	Text usernameFieldTextbox;
	private Group formAuthenticationGroup;
	Button startProxyButton;
	private Text proxyBindPortTextBox;
	private Text proxyBindAddressTextBox;
	private Label proxyBindPortLabel;
	private Label proxyBindAddressLabel;
	
	private AuthWizardProxy wizardProxy;
	AuthenticationSettingsComposite authenticationSettingsComposite;
	String method;
	
	public AuthWizardDialog(Shell parent, AuthenticationSettingsComposite authenticationSettingsComposite) 
	{
		super(parent, SWT.NONE);
		this.authenticationSettingsComposite = authenticationSettingsComposite;
	}

	public void open() 
	{
			Shell parent = getParent();
			dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

			{
				//Register as a resource user - SWTResourceManager will
				//handle the obtaining and disposing of resources
				SWTResourceManager.registerResourceUser(dialogShell);
			}

			
			dialogShell.setText("HTML Form Authentication Wizard");
			dialogShell.setLayout(null);
			dialogShell.layout();
			dialogShell.pack();			
			dialogShell.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
			{
				wizardComposite = new Composite(dialogShell, SWT.NONE);
				wizardComposite.setLayout(null);
				wizardComposite.setBounds(5, 5, 865, 504);
				{
					proxySettingsGroup = new Group(wizardComposite, SWT.NONE);
					proxySettingsGroup.setText("Wizard Proxy");
					proxySettingsGroup.setFont(SWTResourceManager.getFont(GUIConstants.fontName,GUIConstants.fontSize,0,false,false));
					proxySettingsGroup.setBounds(503, 12, 316, 152);
					proxySettingsGroup.setToolTipText("The built-in proxy server allows you to guide \nthe scan by navigating the website from your\nbrowser, while it's pointed at the proxy. This is\nan alternative to the spidering modules.");
					proxySettingsGroup.setLayout(null);
					{
						proxyBindAddressLabel = new Label(proxySettingsGroup, SWT.NONE);
						proxyBindAddressLabel.setText("Proxy bind address:");
						proxyBindAddressLabel.setBounds(12, 33, 153, 25);
						proxyBindAddressLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName,GUIConstants.fontSize,0,false,false));
					}
					{
						proxyBindPortLabel = new Label(proxySettingsGroup, SWT.NONE);
						proxyBindPortLabel.setText("Proxy bind port:");
						proxyBindPortLabel.setBounds(12, 64, 135, 25);
						proxyBindPortLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName,GUIConstants.fontSize,0,false,false));
					}
					{
						proxyBindAddressTextBox = new Text(proxySettingsGroup, SWT.BORDER);
						proxyBindAddressTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,GUIConstants.fontSize,0,false,false));
						proxyBindAddressTextBox.setBounds(175, 37, 125, 25);
						proxyBindAddressTextBox.setToolTipText("this is a test");
						proxyBindAddressTextBox.setText("127.0.0.1");
					}
					{
						proxyBindPortTextBox = new Text(proxySettingsGroup, SWT.BORDER);
						proxyBindPortTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,GUIConstants.fontSize,0,false,false));
						proxyBindPortTextBox.setBounds(175, 68, 50, 25);
						proxyBindPortTextBox.setText("8008");
						proxyBindPortTextBox.addVerifyListener(new EnforceIntegersOnly());
					}
					{
						startProxyButton = new Button(proxySettingsGroup, SWT.PUSH | SWT.CENTER);
						startProxyButton.setText("Start Proxy");
						startProxyButton.setBounds(12, 101, 87, 30);
						startProxyButton.addSelectionListener(new SelectionAdapter() 
						{
							@Override
							public void widgetSelected(@SuppressWarnings("unused") SelectionEvent event) 
							{
								startWizardProxy();
							}
						});
					}
				}
				{
					formAuthenticationGroup = new Group(wizardComposite, SWT.NONE);
					formAuthenticationGroup.setText("HTML Form Authentication Settings");
					formAuthenticationGroup.setFont(SWTResourceManager.getFont(GUIConstants.fontName,GUIConstants.fontSize,0,false,false));
					formAuthenticationGroup.setBounds(17, 251, 820, 197);
					formAuthenticationGroup.setLayout(null);
					{
						usernameFieldTextbox = new Text(formAuthenticationGroup, SWT.READ_ONLY | SWT.BORDER);
						usernameFieldTextbox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,GUIConstants.fontSize,0,false,false));
						usernameFieldTextbox.setBounds(337, 66, 125, 25);
					}
					{
						usernameFieldLabel = new Label(formAuthenticationGroup, SWT.NONE);
						usernameFieldLabel.setText("Username field:");
						usernameFieldLabel.setBounds(236, 72, 95, 25);
						usernameFieldLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName,GUIConstants.fontSize,0,false,false));
					}
					{
						passwordFieldLabel = new Label(formAuthenticationGroup, SWT.NONE);
						passwordFieldLabel.setText("Password field:");
						passwordFieldLabel.setBounds(236, 103, 89, 25);
						passwordFieldLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName,GUIConstants.fontSize,0,false,false));
					}
					{
						passwordFieldTextbox = new Text(formAuthenticationGroup, SWT.READ_ONLY | SWT.BORDER);
						passwordFieldTextbox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,GUIConstants.fontSize,0,false,false));
						passwordFieldTextbox.setBounds(337, 103, 125, 25);
					}
					{
						postQueryLabel = new Label(formAuthenticationGroup, SWT.NONE);
						postQueryLabel.setText("POST Query");
						postQueryLabel.setBounds(12, 156, 80, 28);
						postQueryLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName,GUIConstants.fontSize,0,false,false));
					}
					{
						postQueryTextbox = new Text(formAuthenticationGroup, SWT.READ_ONLY | SWT.BORDER);
						postQueryTextbox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,GUIConstants.fontSize,0,false,false));
						postQueryTextbox.setBounds(113, 160, 667, 25);
					}
					{
						authenticationUrlTextBox = new Text(formAuthenticationGroup, SWT.READ_ONLY | SWT.BORDER);
						authenticationUrlTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,GUIConstants.fontSize,0,false,false));
						authenticationUrlTextBox.setBounds(53, 33, 747, 25);
					}
					{
						authenticationUrlLabel = new Label(formAuthenticationGroup, SWT.NONE);
						authenticationUrlLabel.setText("URL:");
						authenticationUrlLabel.setBounds(12, 33, 35, 25);
						authenticationUrlLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName,GUIConstants.fontSize,0,false,false));
					}
					{
						httpMethodLabel = new Label(formAuthenticationGroup, SWT.NONE);
						httpMethodLabel.setText("HTTP method:");
						httpMethodLabel.setBounds(14, 70, 97, 30);
					}
					{
						httpMethodTextBox = new Text(formAuthenticationGroup, SWT.READ_ONLY | SWT.BORDER);
						httpMethodTextBox.setBounds(115, 68, 60, 25);
					}
				}
				{
					directionsLabel = new Label(wizardComposite, SWT.WRAP);
					directionsLabel.setText("Follow the steps below to capture a login template:\n1. Direct your browser to the application's login page.\n2. Make sure that no other pages are open, or use a tool like FoxyProxy to direct only login requests to the proxy.\n2. Start the wizard proxy, modifying the address and port as needed.\n3. Configure your browser to use the wizard proxy.\n4. Submit credentials (they don't need to be valid) to the app's login page. The credentials will not be submitted to the web server.\n5. Identify the username and password fields in the dialog.\n6. Confirm the wizard's settings are correct and click on \"Complete\".\n\nThe proxy server will only accept a single request. It will need to be restarted to repeat the process.");
					directionsLabel.setBounds(12, 6, 458, 233);
				}
				{
					completeButton = new Button(wizardComposite, SWT.PUSH | SWT.CENTER);
					completeButton.setText("Complete");
					completeButton.setBounds(17, 460, 70, 30);
					completeButton.addSelectionListener(new SelectionAdapter() 
					{
						@Override
						public void widgetSelected(@SuppressWarnings("unused") SelectionEvent event) 
						{
							authenticationSettingsComposite.setHttpMethod(method);
							authenticationSettingsComposite.setUri(authenticationUrlTextBox.getText());
							authenticationSettingsComposite.setUsernameField(usernameFieldTextbox.getText());
							authenticationSettingsComposite.setPasswordField(passwordFieldTextbox.getText());
							authenticationSettingsComposite.setPostQuery(postQueryTextbox.getText());
							dialogShell.close();
						}
					});
				}
				{
					cancelButton = new Button(wizardComposite, SWT.PUSH | SWT.CENTER);
					cancelButton.setText("Cancel");
					cancelButton.setBounds(104, 460, 70, 30);
					cancelButton.addSelectionListener(new SelectionAdapter() 
					{
						@Override
						public void widgetSelected(@SuppressWarnings("unused") SelectionEvent event) 
						{
							dialogShell.close();
						}
					});
				}
			}
			dialogShell.setLocation(getParent().toDisplay(100, 100));
			dialogShell.open();
			Display display = dialogShell.getDisplay();
			while (!dialogShell.isDisposed()) 
			{
				if (!display.readAndDispatch())
					display.sleep();
			}
		stopWizzardProxy();
	}
	
	
	private void stopWizzardProxy()
	{
		if (wizardProxy != null)
		{
			wizardProxy.stopProxy();
		}
	}

	
	void startWizardProxy()
	{
		try
		{
			ProxyConfig proxyConfig = new ProxyConfig();
			proxyConfig.setBindPort(Integer.valueOf(proxyBindPortTextBox.getText()));
			proxyConfig.setBindIP(proxyBindAddressTextBox.getText());
			wizardProxy = new AuthWizardProxy(this, proxyConfig);
			if (wizardProxy.isRunning())
			{
				Scan.getInstance().displayMessage("Authentication Wizard", "Proxy started.");
				startProxyButton.setEnabled(false);
			}
		}
		catch (IllegalStateException e)
		{
			Log.error("Problem with auth wizard: " + e.toString(), e);
		}
	}
	
	public void requestComplete()
	{
		final AuthWizardDialog authWizardDialog = this;
		this.dialogShell.getDisplay().syncExec
		(
			new Runnable()
			{
				@Override
				public void run()
				{
					AuthWizardFieldDialog fields = new AuthWizardFieldDialog(authWizardDialog, dialogShell);
					String query;
					if (method.equalsIgnoreCase("POST"))
					{
						query = postQueryTextbox.getText();
					}
					else
					{
						try
						{
							query = URIStringUtils.getQuery(authenticationUrlTextBox.getText());
						}
						catch (URISyntaxException e)
						{
							IllegalArgumentException ise = new IllegalArgumentException("Illegal uri format: " + e.toString(), e);
							Log.error(e.toString(), e);
							throw ise;
						}
					}
					Set<String> fieldSet = new HashSet<String>();
					for(String param: query.split("&"))
					{
						fieldSet.add(param.split("=")[0]);
					}
					fields.open(fieldSet.toArray(new String[0]));
					startProxyButton.setEnabled(true);
				}
			}
			);
	}
	
	public void setHttpMethod(String httpMethod)
	{
		this.method = httpMethod;
		
		this.dialogShell.getDisplay().asyncExec
		(
			new Runnable()
			{
				@Override
				public void run()
				{
					httpMethodTextBox.setText(method);
				}
		});
	}

	public void setUri(final String uri)
	{
		this.dialogShell.getDisplay().asyncExec
		(
			new Runnable()
			{
				@Override
				public void run()
				{
					authenticationUrlTextBox.setText(uri);
				}
		});		
	}


	public void setPasswordField(final String passwordField)
	{
		this.dialogShell.getDisplay().asyncExec
		(
			new Runnable()
			{
				@Override
				public void run()
				{
					passwordFieldTextbox.setText(passwordField);
				}
		});		
	}

	public void setPostQuery(final String postQuery)
	{
		this.dialogShell.getDisplay().asyncExec
		(
			new Runnable()
			{
				@Override
				public void run()
				{
					postQueryTextbox.setText(postQuery);
				}
		});		
	}

	public void setUsernameField(final String usernameField)
	{
		this.dialogShell.getDisplay().asyncExec
		(
			new Runnable()
			{
				@Override
				public void run()
				{
					usernameFieldTextbox.setText(usernameField);
				}
		});		
	}


}
