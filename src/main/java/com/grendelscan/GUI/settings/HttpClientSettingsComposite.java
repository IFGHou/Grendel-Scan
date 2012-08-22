package com.grendelscan.GUI.settings;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.cloudgarden.resource.SWTResourceManager;
import com.grendelscan.GUI.GUIConstants;
import com.grendelscan.GUI.MainWindow;
import com.grendelscan.GUI.Verifiers.EnforceIntegersOnly;
import com.grendelscan.scan.Scan;
import com.grendelscan.utils.RegexUtils;

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
public class HttpClientSettingsComposite extends org.eclipse.swt.widgets.Composite implements GrendelSettingsControl
{

	private Text			httpResponseCodeTextbox;
	private Button		manualOverrideAddButton;
	private Label			manualOverrideCodeLabel;
	private Label			manualOverrideRegexLabel;
	private Button		manualOverrideRemoveButton;
	private Group			miscHttpClientGroup;
	private Text			regexOverrideTextbox;
	private TableColumn	responseCodeCodeColumn;
	private Group			responseCodeGroup;
	private Table			responseCodeOverrideTable;
	private TableColumn	responseCodeRegexColumn;
	private Button		testEveryDirectoryCheckBox;
	private Button applyButton;
	private Button		useAutomaticOverridesCheckBox;
	private Combo			useragentComboBox;
	private Label			useragentLabel;
	{
		// Register as a resource user - SWTResourceManager will
		// handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}

	public HttpClientSettingsComposite(org.eclipse.swt.widgets.Composite parent, int style)
	{
		super(parent, style);
		initGUI();
	}

	@Override
	public void updateFromSettings()
	{
		useragentComboBox.setText(Scan.getScanSettings().getUserAgentString());
		useAutomaticOverridesCheckBox.setSelection(Scan.getScanSettings().getUseAutomaticResponseCodeOverrides());
		testEveryDirectoryCheckBox.setSelection(Scan.getScanSettings().getTestAllDirectoriesForResponseCodeOverrides());
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
			applyButtonLData.right =  new FormAttachment(1000, 1000, -12);
			applyButtonLData.top =  new FormAttachment(0, 1000, 280);
			applyButton.setLayoutData(applyButtonLData);
			applyButton.setText("Apply Settings");
			applyButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent evt) {
					updateToSettings();
				}
			});
		}
		{
			miscHttpClientGroup = new Group(this, SWT.NONE);
			FormLayout miscHttpClientGroupLayout = new FormLayout();
			miscHttpClientGroup.setText("Miscellaneous Settings");
			miscHttpClientGroup.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0,
						false, false));
			FormData miscHttpClientGroupLData = new FormData();
			miscHttpClientGroupLData.width = 899;
			miscHttpClientGroupLData.height = 37;
			miscHttpClientGroupLData.left =  new FormAttachment(0, 1000, 12);
			miscHttpClientGroupLData.right =  new FormAttachment(1000, 1000, 0);
			miscHttpClientGroupLData.top =  new FormAttachment(0, 1000, 214);
			miscHttpClientGroup.setLayoutData(miscHttpClientGroupLData);
			miscHttpClientGroup.setLayout(miscHttpClientGroupLayout);
			{
				useragentComboBox = new Combo(miscHttpClientGroup, SWT.NONE);
				useragentComboBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize,
							0, false, false));
				FormData useragentComboBoxLData = new FormData();
				useragentComboBoxLData.width = 695;
				useragentComboBoxLData.height = 27;
				useragentComboBoxLData.left =  new FormAttachment(0, 1000, 157);
				useragentComboBoxLData.top =  new FormAttachment(0, 1000, 2);
				useragentComboBoxLData.right =  new FormAttachment(1000, 1000, -9);
				useragentComboBox.setLayoutData(useragentComboBoxLData);
			}
			{
				useragentLabel = new Label(miscHttpClientGroup, SWT.NONE);
				useragentLabel.setText("User agent string:");
				FormData useragentLabelLData = new FormData();
				useragentLabelLData.width = 142;
				useragentLabelLData.height = 30;
				useragentLabelLData.left =  new FormAttachment(10, 1000, 0);
				useragentLabelLData.right =  new FormAttachment(168, 1000, 0);
				useragentLabelLData.top =  new FormAttachment(47, 1000, 0);
				useragentLabelLData.bottom =  new FormAttachment(613, 1000, 0);
				useragentLabel.setLayoutData(useragentLabelLData);
				useragentLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0,
							false, false));
			}
		}
		{
			responseCodeGroup = new Group(this, SWT.NONE);
			FormLayout responseCodeGroupLayout = new FormLayout();
			responseCodeGroup.setText("Response Code Overrides");
			responseCodeGroup.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0,
						false, false));
			FormData responseCodeGroupLData = new FormData();
			responseCodeGroupLData.width = 825;
			responseCodeGroupLData.height = 168;
			responseCodeGroupLData.left =  new FormAttachment(0, 1000, 12);
			responseCodeGroupLData.top =  new FormAttachment(0, 1000, 12);
			responseCodeGroupLData.right =  new FormAttachment(1000, 1000, 0);
			responseCodeGroup.setLayoutData(responseCodeGroupLData);
			responseCodeGroup.setLayout(responseCodeGroupLayout);
			{
				useAutomaticOverridesCheckBox = new Button(responseCodeGroup, SWT.CHECK | SWT.LEFT);
				useAutomaticOverridesCheckBox.setText("Use automatic response code overrides");
				useAutomaticOverridesCheckBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
				FormData useAutomaticOverridesCheckBoxLData = new FormData();
				useAutomaticOverridesCheckBoxLData.width = 306;
				useAutomaticOverridesCheckBoxLData.height = 30;
				useAutomaticOverridesCheckBoxLData.left =  new FormAttachment(0, 1000, 8);
				useAutomaticOverridesCheckBoxLData.top =  new FormAttachment(0, 1000, 9);
				useAutomaticOverridesCheckBox.setLayoutData(useAutomaticOverridesCheckBoxLData);
				useAutomaticOverridesCheckBox
							.setToolTipText("Some websites will respond to a bad file name \nwith a \"200 OK\" or a redirect. Enabling this \noption causes Grendel-Scan to build a profile of \nlogical file-not-found error messages, \nsignificantly improving accuracy. If you know \nthat the website will always respond with a \nproper 404, disabling this option will speed up \nthe scan.");
				useAutomaticOverridesCheckBox.addSelectionListener(new SelectionAdapter()
					{
						@Override
						public void widgetSelected(SelectionEvent event)
						{
							updateAutomaticResponseCodes();
						}
					});
			}
			{
				testEveryDirectoryCheckBox = new Button(responseCodeGroup, SWT.CHECK | SWT.LEFT);
				testEveryDirectoryCheckBox.setText("Test every directory");
				testEveryDirectoryCheckBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
				FormData testEveryDirectoryCheckBoxLData = new FormData();
				testEveryDirectoryCheckBoxLData.width = 248;
				testEveryDirectoryCheckBoxLData.height = 30;
				testEveryDirectoryCheckBoxLData.left =  new FormAttachment(0, 1000, 8);
				testEveryDirectoryCheckBoxLData.top =  new FormAttachment(0, 1000, 39);
				testEveryDirectoryCheckBox.setLayoutData(testEveryDirectoryCheckBoxLData);
				testEveryDirectoryCheckBox
							.setToolTipText("Some websites have different error messages\nfrom directory to directory. This will cause the \nautomatic file-not-found profile to be updated\nfor every directory that Grendel-Scan \nencounters. If you know that the file-not-found\nresponse is consistent across directories, \ndisabling this option will improve performance.");
			}
			{
				manualOverrideCodeLabel = new Label(responseCodeGroup, SWT.NONE);
				manualOverrideCodeLabel.setText("HTTP response code:");
				FormData manualOverrideCodeLabelLData = new FormData();
				manualOverrideCodeLabelLData.width = 135;
				manualOverrideCodeLabelLData.height = 25;
				manualOverrideCodeLabelLData.left =  new FormAttachment(0, 1000, 8);
				manualOverrideCodeLabelLData.top =  new FormAttachment(0, 1000, 81);
				manualOverrideCodeLabel.setLayoutData(manualOverrideCodeLabelLData);
				manualOverrideCodeLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
			}
			{
				manualOverrideRegexLabel = new Label(responseCodeGroup, SWT.NONE);
				manualOverrideRegexLabel.setText("Regex override:");
				FormData manualOverrideRegexLabelLData = new FormData();
				manualOverrideRegexLabelLData.width = 127;
				manualOverrideRegexLabelLData.height = 25;
				manualOverrideRegexLabelLData.left =  new FormAttachment(0, 1000, 8);
				manualOverrideRegexLabelLData.top =  new FormAttachment(0, 1000, 120);
				manualOverrideRegexLabel.setLayoutData(manualOverrideRegexLabelLData);
				manualOverrideRegexLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
			}
			{
				httpResponseCodeTextbox = new Text(responseCodeGroup, SWT.BORDER);
				httpResponseCodeTextbox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
				httpResponseCodeTextbox.setTextLimit(3);
				FormData httpResponseCodeTextboxLData = new FormData();
				httpResponseCodeTextboxLData.width = 27;
				httpResponseCodeTextboxLData.height = 19;
				httpResponseCodeTextboxLData.left =  new FormAttachment(0, 1000, 153);
				httpResponseCodeTextboxLData.top =  new FormAttachment(0, 1000, 85);
				httpResponseCodeTextbox.setLayoutData(httpResponseCodeTextboxLData);
				httpResponseCodeTextbox.addVerifyListener(numbersOnlyVerifyer);
			}
			{
				regexOverrideTextbox = new Text(responseCodeGroup, SWT.BORDER);
				regexOverrideTextbox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
				FormData regexOverrideTextboxLData = new FormData();
				regexOverrideTextboxLData.width = 167;
				regexOverrideTextboxLData.height = 19;
				regexOverrideTextboxLData.left =  new FormAttachment(0, 1000, 156);
				regexOverrideTextboxLData.top =  new FormAttachment(0, 1000, 126);
				regexOverrideTextbox.setLayoutData(regexOverrideTextboxLData);
			}
			{
				manualOverrideAddButton = new Button(responseCodeGroup, SWT.PUSH | SWT.CENTER);
				manualOverrideAddButton.setText("Add");
				FormData manualOverrideAddButtonLData = new FormData();
				manualOverrideAddButtonLData.width = 70;
				manualOverrideAddButtonLData.height = 30;
				manualOverrideAddButtonLData.left =  new FormAttachment(0, 1000, 347);
				manualOverrideAddButtonLData.top =  new FormAttachment(0, 1000, 83);
				manualOverrideAddButton.setLayoutData(manualOverrideAddButtonLData);
				manualOverrideAddButton.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
				manualOverrideAddButton.addSelectionListener(new SelectionAdapter()
					{
						@Override
						public void widgetSelected(SelectionEvent event)
						{
							if (!httpResponseCodeTextbox.getText().equals("")
									&& !regexOverrideTextbox.getText().equals("") &&
										httpResponseCodeTextbox.getText().matches("[1-5]\\d{2}"))
								{
									if (!RegexUtils.validateRegex(regexOverrideTextbox.getText()))
									{
										MainWindow.getInstance().displayMessage("Error",
												"Invalid response code regular expression.", true);
									}
									else
									{
										addResponseCodeOverride(httpResponseCodeTextbox.getText(),
												regexOverrideTextbox.getText());
										httpResponseCodeTextbox.setText("");
										regexOverrideTextbox.setText("");
									}
								}

							}
					});
			}
			{
				manualOverrideRemoveButton = new Button(responseCodeGroup, SWT.PUSH | SWT.CENTER);
				manualOverrideRemoveButton.setText("Remove");
				FormData manualOverrideRemoveButtonLData = new FormData();
				manualOverrideRemoveButtonLData.width = 70;
				manualOverrideRemoveButtonLData.height = 30;
				manualOverrideRemoveButtonLData.left =  new FormAttachment(0, 1000, 347);
				manualOverrideRemoveButtonLData.top =  new FormAttachment(0, 1000, 125);
				manualOverrideRemoveButton.setLayoutData(manualOverrideRemoveButtonLData);
				manualOverrideRemoveButton.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
				manualOverrideRemoveButton.addSelectionListener(new SelectionAdapter()
					{
						@Override
						public void widgetSelected(SelectionEvent event)
						{
							for (TableItem tempItem : responseCodeOverrideTable.getSelection())
								{
									httpResponseCodeTextbox.setText(tempItem.getText(0));
									regexOverrideTextbox.setText(tempItem.getText(1));
									tempItem.dispose();
									break;
								}
							}
					});
			}
			{
				responseCodeOverrideTable = new Table(responseCodeGroup, SWT.SINGLE | SWT.BORDER);
				responseCodeOverrideTable.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
							GUIConstants.fontSize, 0, false, false));
				responseCodeOverrideTable.setHeaderVisible(true);
				FormData responseCodeOverrideTableLData = new FormData();
				responseCodeOverrideTableLData.width = 417;
				responseCodeOverrideTableLData.height = 119;
				responseCodeOverrideTableLData.top =  new FormAttachment(0, 1000, 11);
				responseCodeOverrideTableLData.left =  new FormAttachment(0, 1000, 445);
				responseCodeOverrideTableLData.bottom =  new FormAttachment(1000, 1000, -10);
				responseCodeOverrideTableLData.right =  new FormAttachment(1000, 1000, -9);
				responseCodeOverrideTable.setLayoutData(responseCodeOverrideTableLData);
				{
					responseCodeCodeColumn = new TableColumn(responseCodeOverrideTable, SWT.NONE);
					responseCodeCodeColumn.setText("HTTP Code");
					responseCodeCodeColumn.setWidth(100);
				}
				{
					responseCodeRegexColumn = new TableColumn(responseCodeOverrideTable, SWT.NONE);
					responseCodeRegexColumn.setText("Regex");
					responseCodeRegexColumn.setWidth(250);
				}
			}
		}
		this.layout();
	}

	private void addResponseCodeOverride(String code, String pattern)
	{
		TableItem item = new TableItem(responseCodeOverrideTable, SWT.NONE);
		item.setText(new String[] { code, pattern });
	}

	/**
	 * Overriding checkSubclass allows this class to extend
	 * org.eclipse.swt.widgets.Composite
	 */
	@Override
	protected void checkSubclass()
	{
	}

	private void updateAutomaticResponseCodes()
	{
		testEveryDirectoryCheckBox.setEnabled(useAutomaticOverridesCheckBox.getSelection());
	}


	@Override
	public String updateToSettings()
	{
		Scan.getScanSettings().setUserAgentString(useragentComboBox.getText());
		Scan.getScanSettings().setUseAutomaticResponseCodeOverrides(useAutomaticOverridesCheckBox.getSelection());
		Scan.getScanSettings().setTestAllDirectoriesForResponseCodeOverrides(testEveryDirectoryCheckBox.getSelection());

		return "";
	}
}
