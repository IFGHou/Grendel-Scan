package com.grendelscan.GUI.settings.scanSettings;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.cloudgarden.resource.SWTResourceManager;
import com.grendelscan.GUI.GUIConstants;
import com.grendelscan.GUI.MainWindow;
import com.grendelscan.GUI.AuthWizard.AuthWizardDialog;
import com.grendelscan.GUI.settings.GrendelSettingsControl;
import com.grendelscan.requester.authentication.AuthenticationPackage;
import com.grendelscan.requester.authentication.FormBasedAuthentication;
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
public class AuthenticationSettingsComposite extends org.eclipse.swt.widgets.Composite implements
		GrendelSettingsControl
{

	protected Button		accountAddButton;
	protected Button		accountRemoveButton;
	protected Group			accountsGroup;
	protected Table			accountsTable;
	protected Label			authenticationUrlLabel;
	protected Text			authenticationUrlTextBox;
	protected TableColumn	domainColumn;
	protected Label			domainFieldLabel;
	protected Text			domainFieldTextbox;
	protected Label			domainLabel;
	protected Text			domainTextBox;
	protected Group			formAuthenticationGroup;
	protected Button		httpGetRadio;
	protected Group			httpMethodGroup;
	protected Button		httpPostRadio;
	protected TableColumn	passwordColumn;
	protected Label			passwordFieldLabel;
	protected Text			passwordFieldTextbox;
	protected Label			passwordLabel;
	protected Text			passwordTextBox;
//	protected FormData 		passwordTextBoxLData;
	protected Label			postQueryLabel;
	protected Text			postQueryTextbox;
	protected Button		runAuthWizardButton;
	protected Button		showPasswordsCheckbox;
	private Button removeButton;
	private Button addButton;
	private Table htmlFormTable;
	private Button applyButton;
	protected Button		useAuthenticationCheckBox;
	protected Button		automaticAuthenticationDetectionCheckBox;
	protected Button		useDomainCheckbox;
	protected TableColumn	usernameColumn;
	protected Label			usernameFieldLabel;
	protected Text			usernameFieldTextbox;
	protected Label			usernameLabel;
	protected Text			usernameTextBox;
	{
		// Register as a resource user - SWTResourceManager will
		// handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}

	public AuthenticationSettingsComposite(org.eclipse.swt.widgets.Composite parent, int style)
	{
		super(parent, style);
		initGUI();
	}

	public void setHttpMethod(final String httpMethod)
	{

		getDisplay().asyncExec
				(
						new Runnable()
				{
					@Override
					public void run()
				{
					boolean post = false;
					if (httpMethod.equalsIgnoreCase("POST"))
					{
						post = true;
					}
					httpPostRadio.setSelection(post);
					httpGetRadio.setSelection(!post);
					httpPostButtonSelectAction();
				}
				});

	}

	public void setPasswordField(final String passwordField)
	{
		getDisplay().asyncExec
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
		getDisplay().asyncExec
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

	public void setUri(final String uri)
	{
		getDisplay().asyncExec
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

	public void setUsernameField(final String usernameField)
	{
		getDisplay().asyncExec
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

	@Override
	public void updateFromSettings()
	{
		accountsTable.removeAll();
		useAuthenticationCheckBox.setSelection(Scan.getScanSettings().isUseAuthentication());
		automaticAuthenticationDetectionCheckBox.setSelection(Scan.getScanSettings().isAutomaticAuthentication());
		AuthenticationPackage auth = Scan.getScanSettings().getAuthenticationPackage();
		authenticationUrlTextBox.setText("");
		usernameFieldTextbox.setText("");
		passwordFieldTextbox.setText("");
		postQueryTextbox.setText("");
		for (String user : Scan.getScanSettings().getReadOnlyAuthenticationCredentials().keySet())
		{
			displayCredentials(user, Scan.getScanSettings().getReadOnlyAuthenticationCredentials().get(user));
		}

		if (auth != null)
		{
			// httpAuthenticationRadio.setSelection(auth instanceof
			// HttpBasedAuthentication ? true: false);

			if (auth instanceof FormBasedAuthentication)
			{
				FormBasedAuthentication h = (FormBasedAuthentication) auth;
				authenticationUrlTextBox.setText(h.getUri());
				httpPostRadio.setSelection(h.getMethod().equalsIgnoreCase("POST") ? true : false);
				httpGetRadio.setSelection(h.getMethod().equalsIgnoreCase("GET") ? true : false);
				postQueryTextbox.setText(h.getPostQuery());
				passwordFieldTextbox.setText(h.getPasswordParameterName());
				usernameFieldTextbox.setText(h.getUserParameterName());
			}
		}

		updateAuthenticationTab();
	}

	private void initGUI()
	{
		{
			FormLayout thisLayout = new FormLayout();
			this.setLayout(thisLayout);
			this.setSize(1094, 738);
			{
				applyButton = new Button(this, SWT.PUSH | SWT.CENTER);
				FormData applyButtonLData = new FormData();
				applyButtonLData.width = 118;
				applyButtonLData.height = 31;
				applyButtonLData.bottom =  new FormAttachment(1000, 1000, -12);
				applyButtonLData.right =  new FormAttachment(1000, 1000, -12);
				applyButton.setLayoutData(applyButtonLData);
				applyButton.setText("Apply Settings");
				applyButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(@SuppressWarnings("unused") SelectionEvent evt) {
						updateToSettings();
					}
				});
			}
			{
				useAuthenticationCheckBox = new Button(this, SWT.CHECK | SWT.LEFT);
				useAuthenticationCheckBox.setText("Use authentication");
				FormData useAuthenticationCheckBoxLData = new FormData();
				useAuthenticationCheckBoxLData.width = 158;
				useAuthenticationCheckBoxLData.height = 21;
				useAuthenticationCheckBoxLData.top =  new FormAttachment(0, 1000, 12);
				useAuthenticationCheckBoxLData.left =  new FormAttachment(0, 1000, 12);
				useAuthenticationCheckBox.setLayoutData(useAuthenticationCheckBoxLData);
				useAuthenticationCheckBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
						GUIConstants.fontSize, 0, false, false));
				useAuthenticationCheckBox.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(@SuppressWarnings("unused") SelectionEvent e)
					{
						updateAuthenticationTab();
						updateToSettings();
					}
				});
			}
			{
				automaticAuthenticationDetectionCheckBox = new Button(this, SWT.CHECK | SWT.LEFT);
				automaticAuthenticationDetectionCheckBox.setText("Automatically submit authentication");
				FormData useAuthenticationCheckBoxLData = new FormData();
				useAuthenticationCheckBoxLData.width = 207;
				useAuthenticationCheckBoxLData.height = 25;
				useAuthenticationCheckBoxLData.top =  new FormAttachment(0, 1000, 12);
				useAuthenticationCheckBoxLData.left =  new FormAttachment(0, 1000, 308);
				automaticAuthenticationDetectionCheckBox.setLayoutData(useAuthenticationCheckBoxLData);
				automaticAuthenticationDetectionCheckBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
						GUIConstants.fontSize, 0, false, false));
				automaticAuthenticationDetectionCheckBox.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(@SuppressWarnings("unused") SelectionEvent e)
					{
						updateToSettings();
					}
				});
			}
			
			{
				useDomainCheckbox = new Button(this, SWT.CHECK | SWT.LEFT);
				useDomainCheckbox.setText("Use domain");
				useDomainCheckbox.setEnabled(false);
				FormData useDomainCheckboxLData = new FormData();
				useDomainCheckboxLData.width = 113;
				useDomainCheckboxLData.height = 21;
				useDomainCheckboxLData.top =  new FormAttachment(0, 1000, 12);
				useDomainCheckboxLData.left =  new FormAttachment(0, 1000, 197);
				useDomainCheckbox.setLayoutData(useDomainCheckboxLData);
				useDomainCheckbox.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0,
						false, false));
				useDomainCheckbox.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(@SuppressWarnings("unused") SelectionEvent event)
					{

						if (!useDomainCheckbox.getSelection())
						{
							domainColumn.setResizable(false);
							domainColumn.setWidth(0);
							domainTextBox.setText("");
						}
						else
						{
							domainColumn.setResizable(true);
							domainColumn.setWidth(120);
						}
					}
				});
			}
			{
				accountsGroup = new Group(this, SWT.NONE);
				FormLayout accountsGroupLayout = new FormLayout();
				accountsGroup.setText("User Accounts");
				accountsGroup.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0,
						false, false));
				FormData accountsGroupLData = new FormData();
				accountsGroupLData.width = 508;
				accountsGroupLData.height = 257;
				accountsGroupLData.top =  new FormAttachment(0, 1000, 446);
				accountsGroupLData.left =  new FormAttachment(0, 1000, 12);
				accountsGroupLData.bottom =  new FormAttachment(1000, 1000, -12);
				accountsGroup.setLayoutData(accountsGroupLData);
				accountsGroup.setLayout(accountsGroupLayout);
				{
					showPasswordsCheckbox = new Button(accountsGroup, SWT.CHECK | SWT.LEFT);
					showPasswordsCheckbox.setText("Show passwords");
					FormData showPasswordsCheckboxLData = new FormData();
					showPasswordsCheckboxLData.width = 144;
					showPasswordsCheckboxLData.height = 22;
					showPasswordsCheckboxLData.left =  new FormAttachment(0, 1000, 218);
					showPasswordsCheckboxLData.top =  new FormAttachment(0, 1000, 108);
					showPasswordsCheckbox.setLayoutData(showPasswordsCheckboxLData);
					showPasswordsCheckbox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
					showPasswordsCheckbox.addSelectionListener(new SelectionAdapter()
					{
						@Override
						public void widgetSelected(@SuppressWarnings("unused") SelectionEvent event)
						{
							createPasswordTextBox();
							for (TableItem item : accountsTable.getItems())
								{
									String pass;
									if (showPasswordsCheckbox.getSelection())
									{
										pass = (String) item.getData("password");
									}
									else
									{
										pass = "********";
									}
									item.setText(1, pass);
								}
							}
					});
				}
				{
					usernameLabel = new Label(accountsGroup, SWT.NONE);
					usernameLabel.setText("Username:");
					FormData usernameLabelLData = new FormData();
					usernameLabelLData.width = 76;
					usernameLabelLData.height = 19;
					usernameLabelLData.left =  new FormAttachment(0, 1000, 8);
					usernameLabelLData.top =  new FormAttachment(0, 1000, 9);
					usernameLabel.setLayoutData(usernameLabelLData);
					usernameLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0,
							false, false));
				}
				{
					usernameTextBox = new Text(accountsGroup, SWT.BORDER);
					FormData usernameTextBoxLData = new FormData();
					usernameTextBoxLData.width = 177;
					usernameTextBoxLData.height = 19;
					usernameTextBoxLData.left =  new FormAttachment(0, 1000, 95);
					usernameTextBoxLData.top =  new FormAttachment(0, 1000, 9);
					usernameTextBox.setLayoutData(usernameTextBoxLData);
					usernameTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0,
							false, false));
				}
				{
					passwordLabel = new Label(accountsGroup, SWT.NONE);
					passwordLabel.setText("Password:");
					FormData passwordLabelLData = new FormData();
					passwordLabelLData.width = 74;
					passwordLabelLData.height = 19;
					passwordLabelLData.left =  new FormAttachment(0, 1000, 8);
					passwordLabelLData.top =  new FormAttachment(0, 1000, 40);
					passwordLabel.setLayoutData(passwordLabelLData);
					passwordLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0,
							false, false));
				}
//				{
//					passwordTextBox = new Text(accountsGroup, SWT.BORDER);
//					passwordTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0,
//							false, false));
//				}
				{
					domainLabel = new Label(accountsGroup, SWT.NONE);
					domainLabel.setText("Domain:");
					domainLabel.setEnabled(false);
					FormData domainLabelLData = new FormData();
					domainLabelLData.width = 60;
					domainLabelLData.height = 19;
					domainLabelLData.left =  new FormAttachment(0, 1000, 8);
					domainLabelLData.top =  new FormAttachment(0, 1000, 71);
					domainLabel.setLayoutData(domainLabelLData);
					domainLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0,
							false, false));
				}
				{
					domainTextBox = new Text(accountsGroup, SWT.BORDER);
					domainTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0,
							false, false));
					FormData domainTextBoxLData = new FormData();
					domainTextBoxLData.width = 178;
					domainTextBoxLData.height = 19;
					domainTextBoxLData.left =  new FormAttachment(0, 1000, 94);
					domainTextBoxLData.top =  new FormAttachment(0, 1000, 71);
					domainTextBox.setLayoutData(domainTextBoxLData);
					domainTextBox.setEnabled(false);
				}
				{
					accountAddButton = new Button(accountsGroup, SWT.PUSH | SWT.CENTER);
					accountAddButton.setText("Add");
					FormData accountAddButtonLData = new FormData();
					accountAddButtonLData.width = 42;
					accountAddButtonLData.height = 29;
					accountAddButtonLData.left =  new FormAttachment(0, 1000, 8);
					accountAddButtonLData.top =  new FormAttachment(0, 1000, 103);
					accountAddButton.setLayoutData(accountAddButtonLData);
					accountAddButton.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize,
							0, false, false));
					accountAddButton.addSelectionListener(new SelectionAdapter()
					{
						@Override
						public void widgetSelected(@SuppressWarnings("unused") SelectionEvent event)
						{
							if (!usernameTextBox.getText().equals(""))
								{
									displayCredentials(usernameTextBox.getText(), passwordTextBox.getText());
									passwordTextBox.setText("");
									usernameTextBox.setText("");
									updateToSettings();
								}
							}
					});
				}
				{
					accountRemoveButton = new Button(accountsGroup, SWT.PUSH | SWT.CENTER);
					accountRemoveButton.setText("Remove");
					FormData accountRemoveButtonLData = new FormData();
					accountRemoveButtonLData.width = 69;
					accountRemoveButtonLData.height = 29;
					accountRemoveButtonLData.left =  new FormAttachment(0, 1000, 82);
					accountRemoveButtonLData.top =  new FormAttachment(0, 1000, 103);
					accountRemoveButton.setLayoutData(accountRemoveButtonLData);
					accountRemoveButton.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
					accountRemoveButton.addSelectionListener(new SelectionAdapter()
					{
						@Override
						public void widgetSelected(@SuppressWarnings("unused") SelectionEvent event)
						{
							for (TableItem item : accountsTable.getSelection())
								{
									usernameTextBox.setText(item.getText(0));
									passwordTextBox.setText((String) item.getData("password"));
									if (useDomainCheckbox.getSelection())
									{
										domainTextBox.setText(item.getText(2));
									}
									item.dispose();
									break;
								}
							}
					});
				}
				{
					accountsTable = new Table(accountsGroup, SWT.BORDER);
					accountsTable.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0,
							false, false));
					FormData accountsTableLData = new FormData();
					accountsTableLData.width = 423;
					accountsTableLData.height = 76;
					accountsTableLData.left =  new FormAttachment(0, 1000, 7);
					accountsTableLData.top =  new FormAttachment(0, 1000, 144);
					accountsTableLData.bottom =  new FormAttachment(1000, 1000, -9);
					accountsTableLData.right =  new FormAttachment(1000, 1000, -8);
					accountsTable.setLayoutData(accountsTableLData);
					accountsTable.setHeaderVisible(true);
					{
						usernameColumn = new TableColumn(accountsTable, SWT.NONE);
						usernameColumn.setText("Username");
						usernameColumn.setWidth(120);
					}
					{
						passwordColumn = new TableColumn(accountsTable, SWT.NONE);
						passwordColumn.setText("Password");
						passwordColumn.setWidth(120);
					}
					{
						domainColumn = new TableColumn(accountsTable, SWT.NONE);
						domainColumn.setText("Domain");
						domainColumn.setResizable(false);
					}
				}
			}
			{
				formAuthenticationGroup = new Group(this, SWT.NONE);
				FormLayout formAuthenticationGroupLayout = new FormLayout();
				formAuthenticationGroup.setLayout(formAuthenticationGroupLayout);
				formAuthenticationGroup.setText("HTML Form Authentication Settings");
				formAuthenticationGroup.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
						GUIConstants.fontSize, 0, false, false));
				FormData formAuthenticationGroupLData = new FormData();
				formAuthenticationGroupLData.top =  new FormAttachment(0, 1000, 39);
				formAuthenticationGroupLData.left =  new FormAttachment(0, 1000, 12);
				formAuthenticationGroupLData.width = 1064;
				formAuthenticationGroupLData.height = 372;
				formAuthenticationGroupLData.right =  new FormAttachment(1000, 1000, -12);
				formAuthenticationGroup.setLayoutData(formAuthenticationGroupLData);
				{
					removeButton = new Button(formAuthenticationGroup, SWT.PUSH | SWT.CENTER);
					removeButton.setEnabled(false);
					FormData removeButtonLData = new FormData();
					removeButtonLData.left =  new FormAttachment(0, 1000, 76);
					removeButtonLData.width = 50;
					removeButtonLData.height = 23;
					removeButtonLData.bottom =  new FormAttachment(1000, 1000, -219);
					removeButton.setLayoutData(removeButtonLData);
					removeButton.setText("Remove");
				}
				{
					addButton = new Button(formAuthenticationGroup, SWT.PUSH | SWT.CENTER);
					addButton.setEnabled(false);
					FormData addButtonLData = new FormData();
					addButtonLData.left =  new FormAttachment(0, 1000, 9);
					addButtonLData.width = 61;
					addButtonLData.height = 23;
					addButtonLData.bottom =  new FormAttachment(1000, 1000, -219);
					addButton.setLayoutData(addButtonLData);
					addButton.setText("Add");
					addButton.addSelectionListener(new SelectionAdapter()
					{
						@Override
						public void widgetSelected(@SuppressWarnings("unused") SelectionEvent event)
						{
							addHtmlFormData();
						}
					});
				}
				{
					FormData htmlFormTableLData = new FormData();
					htmlFormTableLData.left =  new FormAttachment(0, 1000, 3);
					htmlFormTableLData.top =  new FormAttachment(0, 1000, 6);
					htmlFormTableLData.width = 1032;
					htmlFormTableLData.height = 98;
					htmlFormTableLData.right =  new FormAttachment(1000, 1000, -9);
					htmlFormTable = new Table(formAuthenticationGroup, SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
					htmlFormTable.setEnabled(false);
					htmlFormTable.setLayoutData(htmlFormTableLData);
					htmlFormTable.setHeaderVisible(true);
					{
						TableColumn column = new TableColumn(htmlFormTable, SWT.NONE);
						column.setText("URL");
						column.setWidth(300);
					}
					{
						TableColumn column = new TableColumn(htmlFormTable, SWT.NONE);
						column.setText("Method");
						column.setWidth(60);
					}
					{
						TableColumn column = new TableColumn(htmlFormTable, SWT.NONE);
						column.setText("User");
						column.setWidth(120);
					}
					{
						TableColumn column = new TableColumn(htmlFormTable, SWT.NONE);
						column.setText("Password");
						column.setWidth(120);
					}
					{
						TableColumn column = new TableColumn(htmlFormTable, SWT.NONE);
						column.setText("Domain");
						column.setWidth(120);
					}
					{
						TableColumn column = new TableColumn(htmlFormTable, SWT.NONE);
						column.setText("POST Query");
						column.setWidth(200);
					}

				}
				{
					runAuthWizardButton = new Button(formAuthenticationGroup, SWT.PUSH | SWT.CENTER);
					FormData runAuthWizardButtonLData = new FormData();
					runAuthWizardButtonLData.width = 96;
					runAuthWizardButtonLData.height = 31;
					runAuthWizardButtonLData.bottom =  new FormAttachment(1000, 1000, -174);
					runAuthWizardButtonLData.left =  new FormAttachment(0, 1000, 174);
					runAuthWizardButton.setLayoutData(runAuthWizardButtonLData);
					runAuthWizardButton.setText("Run Wizard");
					runAuthWizardButton.addSelectionListener(new SelectionAdapter()
					{
						@Override
						public void widgetSelected(@SuppressWarnings("unused") SelectionEvent e)
						{
							runAuthWizard();
						}
					});
				}
				{
					authenticationUrlTextBox = new Text(formAuthenticationGroup, SWT.BORDER);
					authenticationUrlTextBox.setText("");
					FormData authenticationUrlTextBoxLData = new FormData();
					authenticationUrlTextBoxLData.width = 945;
					authenticationUrlTextBoxLData.height = 19;
					authenticationUrlTextBoxLData.bottom =  new FormAttachment(1000, 1000, -137);
					authenticationUrlTextBoxLData.left =  new FormAttachment(0, 1000, 91);
					authenticationUrlTextBoxLData.right =  new FormAttachment(1000, 1000, -16);
					authenticationUrlTextBox.setLayoutData(authenticationUrlTextBoxLData);
					authenticationUrlTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
				}
				{
					httpMethodGroup = new Group(formAuthenticationGroup, SWT.NONE);
					FormLayout httpMethodGroupLayout = new FormLayout();
					httpMethodGroup.setLayout(httpMethodGroupLayout);
					httpMethodGroup.setText("HTTP Method");
					FormData httpMethodGroupLData = new FormData();
					httpMethodGroupLData.width = 140;
					httpMethodGroupLData.height = 28;
					httpMethodGroupLData.bottom =  new FormAttachment(1000, 1000, -174);
					httpMethodGroupLData.left =  new FormAttachment(0, 1000, 9);
					httpMethodGroup.setLayoutData(httpMethodGroupLData);
					httpMethodGroup.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0,
							false, false));
					{
						httpGetRadio = new Button(httpMethodGroup, SWT.RADIO | SWT.LEFT);
						httpGetRadio.setText("GET");
						FormData httpGetRadioLData = new FormData();
						httpGetRadioLData.width = 40;
						httpGetRadioLData.height = 16;
						httpGetRadioLData.left =  new FormAttachment(0, 1000, 5);
						httpGetRadioLData.top =  new FormAttachment(0, 1000, 5);
						httpGetRadio.setLayoutData(httpGetRadioLData);
						httpGetRadio.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize,
								0, false, false));
					}
					{
						httpPostRadio = new Button(httpMethodGroup, SWT.RADIO | SWT.LEFT);
						httpPostRadio.setText("POST");
						FormData httpPostRadioLData = new FormData();
						httpPostRadioLData.width = 47;
						httpPostRadioLData.height = 19;
						httpPostRadioLData.top =  new FormAttachment(0, 1000, 5);
						httpPostRadioLData.left =  new FormAttachment(0, 1000, 64);
						httpPostRadio.setLayoutData(httpPostRadioLData);
						httpPostRadio.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize,
								0, false, false));
						httpPostRadio.addSelectionListener(new SelectionAdapter()
						{
							@Override
							public void widgetSelected(@SuppressWarnings("unused") SelectionEvent event)
							{
								httpPostButtonSelectAction();
							}
						});
					}
				}
				{
					usernameFieldTextbox = new Text(formAuthenticationGroup, SWT.BORDER);
					usernameFieldTextbox.setText("");
					FormData usernameFieldTextboxLData = new FormData();
					usernameFieldTextboxLData.width = 942;
					usernameFieldTextboxLData.height = 19;
					usernameFieldTextboxLData.bottom =  new FormAttachment(1000, 1000, -106);
					usernameFieldTextboxLData.left =  new FormAttachment(0, 1000, 93);
					usernameFieldTextboxLData.right =  new FormAttachment(1000, 1000, -17);
					usernameFieldTextbox.setLayoutData(usernameFieldTextboxLData);
					usernameFieldTextbox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
				}
				{
					usernameFieldLabel = new Label(formAuthenticationGroup, SWT.NONE);
					usernameFieldLabel.setText("Username field:");
					FormData usernameFieldLabelLData = new FormData();
					usernameFieldLabelLData.width = 94;
					usernameFieldLabelLData.height = 25;
					usernameFieldLabelLData.bottom =  new FormAttachment(1000, 1000, -106);
					usernameFieldLabelLData.left =  new FormAttachment(0, 1000, 8);
					usernameFieldLabel.setLayoutData(usernameFieldLabelLData);
					usernameFieldLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize,
							0, false, false));
				}
				{
					passwordFieldLabel = new Label(formAuthenticationGroup, SWT.NONE);
					passwordFieldLabel.setText("Password field:");
					FormData passwordFieldLabelLData = new FormData();
					passwordFieldLabelLData.width = 83;
					passwordFieldLabelLData.height = 25;
					passwordFieldLabelLData.bottom =  new FormAttachment(1000, 1000, -75);
					passwordFieldLabelLData.left =  new FormAttachment(0, 1000, 8);
					passwordFieldLabel.setLayoutData(passwordFieldLabelLData);
					passwordFieldLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize,
							0, false, false));
				}
				{
					passwordFieldTextbox = new Text(formAuthenticationGroup, SWT.BORDER);
					passwordFieldTextbox.setText("");
					FormData passwordFieldTextboxLData = new FormData();
					passwordFieldTextboxLData.width = 945;
					passwordFieldTextboxLData.height = 19;
					passwordFieldTextboxLData.bottom =  new FormAttachment(1000, 1000, -75);
					passwordFieldTextboxLData.left =  new FormAttachment(0, 1000, 91);
					passwordFieldTextboxLData.right =  new FormAttachment(1000, 1000, -16);
					passwordFieldTextbox.setLayoutData(passwordFieldTextboxLData);
					passwordFieldTextbox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
				}
				{
					domainFieldLabel = new Label(formAuthenticationGroup, SWT.NONE);
					domainFieldLabel.setText("Domain field:");
					domainFieldLabel.setEnabled(false);
					FormData domainFieldLabelLData = new FormData();
					domainFieldLabelLData.width = 70;
					domainFieldLabelLData.height = 25;
					domainFieldLabelLData.bottom =  new FormAttachment(1000, 1000, -40);
					domainFieldLabelLData.left =  new FormAttachment(0, 1000, 8);
					domainFieldLabel.setLayoutData(domainFieldLabelLData);
					domainFieldLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize,
							0, false, false));
				}
				{
					domainFieldTextbox = new Text(formAuthenticationGroup, SWT.BORDER);
					domainFieldTextbox.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize,
							0, false, false));
					FormData domainFieldTextboxLData = new FormData();
					domainFieldTextboxLData.width = 946;
					domainFieldTextboxLData.height = 19;
					domainFieldTextboxLData.bottom =  new FormAttachment(1000, 1000, -44);
					domainFieldTextboxLData.left =  new FormAttachment(0, 1000, 90);
					domainFieldTextboxLData.right =  new FormAttachment(1000, 1000, -16);
					domainFieldTextbox.setLayoutData(domainFieldTextboxLData);
					domainFieldTextbox.setEnabled(false);
				}
				{
					postQueryLabel = new Label(formAuthenticationGroup, SWT.NONE);
					postQueryLabel.setText("POST Query");
					postQueryLabel.setEnabled(false);
					FormData postQueryLabelLData = new FormData();
					postQueryLabelLData.width = 78;
					postQueryLabelLData.height = 25;
					postQueryLabelLData.bottom =  new FormAttachment(1000, 1000, -9);
					postQueryLabelLData.left =  new FormAttachment(0, 1000, 8);
					postQueryLabel.setLayoutData(postQueryLabelLData);
					postQueryLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0,
							false, false));
				}
				{
					postQueryTextbox = new Text(formAuthenticationGroup, SWT.BORDER);
					postQueryTextbox.setText("");
					postQueryTextbox.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize,
							0, false, false));
					FormData postQueryTextboxLData = new FormData();
					postQueryTextboxLData.width = 944;
					postQueryTextboxLData.height = 19;
					postQueryTextboxLData.bottom =  new FormAttachment(1000, 1000, -9);
					postQueryTextboxLData.left =  new FormAttachment(0, 1000, 92);
					postQueryTextboxLData.right =  new FormAttachment(1000, 1000, -16);
					postQueryTextbox.setLayoutData(postQueryTextboxLData);
					postQueryTextbox.setEnabled(false);
				}
				{
					authenticationUrlLabel = new Label(formAuthenticationGroup, SWT.NONE);
					authenticationUrlLabel.setText("URL:");
					FormData authenticationUrlLabelLData = new FormData();
					authenticationUrlLabelLData.width = 55;
					authenticationUrlLabelLData.height = 25;
					authenticationUrlLabelLData.bottom =  new FormAttachment(1000, 1000, -137);
					authenticationUrlLabelLData.left =  new FormAttachment(0, 1000, 9);
					authenticationUrlLabel.setLayoutData(authenticationUrlLabelLData);
					authenticationUrlLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
				}
				createPasswordTextBox();
			}
		}
		this.layout();
	}

	protected void displayCredentials(String userName, String password)
	{
		TableItem item = new TableItem(accountsTable, SWT.NONE);
		item.setData("password", password);
		item.setText(0, userName);
		String pass;
		if (showPasswordsCheckbox.getSelection())
		{
			pass = password;
		}
		else
		{
			pass = "********";
		}

		// if (useDomainCheckbox.getSelection())
		// {
		// item.setText(2, domainTextBox.getText());
		// domainTextBox.setText("");
		// }
		item.setText(1, pass);
	}

	protected void createPasswordTextBox()
	{
		int style = SWT.BORDER;
		if (!showPasswordsCheckbox.getSelection())
		{
			style |= SWT.PASSWORD;
		}
		String value = "";
		if (passwordTextBox != null)
		{
			value = passwordTextBox.getText();
			passwordTextBox.dispose();
		}
		passwordTextBox = new Text(accountsGroup, style);
		passwordTextBox.setText(value);
		FormData passwordTextBoxLData = new FormData();
		passwordTextBoxLData.width = 176;
		passwordTextBoxLData.height = 19;
		passwordTextBoxLData.left =  new FormAttachment(0, 1000, 96);
		passwordTextBoxLData.top =  new FormAttachment(0, 1000, 40);
		passwordTextBox.setLayoutData(passwordTextBoxLData);
		passwordTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false,
				false));
	}


	protected void httpPostButtonSelectAction()
	{
		postQueryTextbox.setEnabled(httpPostRadio.getSelection() && useAuthenticationCheckBox.getSelection());
		postQueryLabel.setEnabled(httpPostRadio.getSelection() && useAuthenticationCheckBox.getSelection());
	}

	protected void runAuthWizard()
	{
		AuthWizardDialog wizardDialog = new AuthWizardDialog(getShell(), this);
		wizardDialog.open();
	}

	protected void updateAuthenticationTab()
	{
		httpPostButtonSelectAction();
		automaticAuthenticationDetectionCheckBox.setEnabled(useAuthenticationCheckBox.getSelection());
		accountsGroup.setEnabled(useAuthenticationCheckBox.getSelection());
		usernameLabel.setEnabled(useAuthenticationCheckBox.getSelection());
		usernameTextBox.setEnabled(useAuthenticationCheckBox.getSelection());
		passwordLabel.setEnabled(useAuthenticationCheckBox.getSelection());
		domainLabel.setEnabled(useDomainCheckbox.getSelection() && useAuthenticationCheckBox.getSelection());
		domainTextBox.setEnabled(useDomainCheckbox.getSelection() && useAuthenticationCheckBox.getSelection());
		accountsTable.setEnabled(useAuthenticationCheckBox.getSelection());
		showPasswordsCheckbox.setEnabled(useAuthenticationCheckBox.getSelection());
		accountAddButton.setEnabled(useAuthenticationCheckBox.getSelection());
		accountRemoveButton.setEnabled(useAuthenticationCheckBox.getSelection());
		passwordTextBox.setEnabled(useAuthenticationCheckBox.getSelection());
		
	}
	
	
	@Override
	public String updateToSettings()
	{
		String message = "";
		Scan.getScanSettings().setUseAuthentication(useAuthenticationCheckBox.getSelection());
		Scan.getScanSettings().setAutomaticAuthentication(automaticAuthenticationDetectionCheckBox.getSelection());
		if (!authenticationUrlTextBox.getText().isEmpty())
		{
			Scan.getScanSettings().setAuthenticationPackage(new FormBasedAuthentication(authenticationUrlTextBox.getText(), 
					httpPostRadio.getSelection() ? "POST" : "GET", postQueryTextbox.getText(), 
					usernameFieldTextbox.getText(), passwordFieldTextbox.getText()));
		}
		
		for (TableItem tempItem:  accountsTable.getItems())
		{
			Scan.getScanSettings().getAuthenticationCredentials().put(tempItem.getText(0), (String) tempItem.getData("password")); 
			// It's a hashmap, so duplicate's aren't possible
		}
		return message;
	}

	void addHtmlFormData()
	{
		String message = "";
		String userField;
		String passwordField;
		String domainField;
		String postBody;
		String method;
		
		try
		{
			URI uri = new URI(authenticationUrlTextBox.getText());
			if (!uri.isAbsolute())
			{
				message += "- The URL must be absolute\n";
			}
		}
		catch (URISyntaxException e)
		{
			message += "- The URL is invalid (" + e.getMessage() + ")\n";
		}
		
		userField = usernameFieldTextbox.getText();
		if (userField.isEmpty())
		{
			message += "- The username field cannot be blank\n";
		}
		
		passwordField = passwordFieldTextbox.getText();
		if (userField.isEmpty())
		{
			message += "- The password field cannot be blank\n";
		}
		
		domainField = domainFieldTextbox.getText();
		method = httpGetRadio.getSelection() ? "GET" : "POST";
		postBody = postQueryTextbox.getText();
		
		if (message.isEmpty())
		{
			FormBasedAuthentication auth = new FormBasedAuthentication(authenticationUrlTextBox.getText(), method, postBody, userField, passwordField);
			TableItem item = new TableItem(htmlFormTable, SWT.None);
			item.setText(new String[]{authenticationUrlTextBox.getText(), method, userField, passwordField, domainField, postBody});
			authenticationUrlTextBox.clearSelection();
			usernameFieldTextbox.clearSelection();
			passwordFieldTextbox.clearSelection();
			domainFieldTextbox.clearSelection();
			postQueryTextbox.clearSelection();
			
		}
		else
		{
			MainWindow.getInstance().displayMessage("Error:", "The HTML form data is invalid:\n\n" + message, true);
		}

	}
}
