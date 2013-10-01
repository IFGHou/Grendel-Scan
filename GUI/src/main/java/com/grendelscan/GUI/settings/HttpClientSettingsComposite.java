package com.grendelscan.GUI.settings;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;

import org.eclipse.swt.widgets.Combo;
import com.grendelscan.GUI.customControls.basic.GGroup;
import com.grendelscan.GUI.customControls.basic.GLabel;
import com.grendelscan.GUI.customControls.basic.GTable;
import com.grendelscan.GUI.customControls.basic.GTableColumn;
import com.grendelscan.GUI.customControls.basic.GTableItem;
import com.grendelscan.GUI.customControls.basic.GText;

import com.grendelscan.GUI.GuiUtils;
import com.grendelscan.GUI.MainWindow;
import com.grendelscan.GUI.Verifiers.EnforceIntegersOnly;
import com.grendelscan.GUI.customControls.basic.GButton;
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
public class HttpClientSettingsComposite extends com.grendelscan.GUI.customControls.basic.GComposite implements GrendelSettingsControl
{

	private GText			httpResponseCodeTextbox;
	private GButton		manualOverrideAddButton;
	private GLabel			manualOverrideCodeLabel;
	private GLabel			manualOverrideRegexLabel;
	private GButton		manualOverrideRemoveButton;
	private GGroup			miscHttpClientGroup;
	private GText			regexOverrideTextbox;
	private GTableColumn	responseCodeCodeColumn;
	private GGroup			responseCodeGroup;
	private GTable			responseCodeOverrideTable;
	private GTableColumn	responseCodeRegexColumn;
	private GButton		testEveryDirectoryCheckBox;
	private GButton applyButton;
	private GButton		useAutomaticOverridesCheckBox;
	private Combo			useragentComboBox;
	private GLabel			useragentLabel;
	{
		// Register as a resource user - SWTResourceManager will
		// handle the obtaining and disposing of resources
		GuiUtils.registerResourceUser(this);
	}

	public HttpClientSettingsComposite(com.grendelscan.GUI.customControls.basic.GComposite parent, int style)
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
			applyButton = new GButton(this, SWT.PUSH | SWT.CENTER);
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
			miscHttpClientGroup = new GGroup(this, SWT.NONE);
			FormLayout miscHttpClientGroupLayout = new FormLayout();
			miscHttpClientGroup.setText("Miscellaneous Settings");
			miscHttpClientGroup.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0,
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
				useragentComboBox.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize,
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
				useragentLabel = new GLabel(miscHttpClientGroup, SWT.NONE);
				useragentLabel.setText("User agent string:");
				FormData useragentLabelLData = new FormData();
				useragentLabelLData.width = 142;
				useragentLabelLData.height = 30;
				useragentLabelLData.left =  new FormAttachment(10, 1000, 0);
				useragentLabelLData.right =  new FormAttachment(168, 1000, 0);
				useragentLabelLData.top =  new FormAttachment(47, 1000, 0);
				useragentLabelLData.bottom =  new FormAttachment(613, 1000, 0);
				useragentLabel.setLayoutData(useragentLabelLData);
				useragentLabel.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0,
							false, false));
			}
		}
		{
			responseCodeGroup = new GGroup(this, SWT.NONE);
			FormLayout responseCodeGroupLayout = new FormLayout();
			responseCodeGroup.setText("Response Code Overrides");
			responseCodeGroup.setFontf(GuiUtils.getFont(GuiUtils.fontName, GuiUtils.fontSize, 0,
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
				useAutomaticOverridesCheckBox = new GButton(responseCodeGroup, SWT.CHECK | SWT.LEFT);
				useAutomaticOverridesCheckBox.setText("Use automatic response code overrides");
				useAutomaticOverridesCheckBox.setFontf(GuiUtils.getFont(GuiUtils.fontName,
							GuiUtils.fontSize, 0, false, false));
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
				testEveryDirectoryCheckBox = new GButton(responseCodeGroup, SWT.CHECK | SWT.LEFT);
				testEveryDirectoryCheckBox.setText("Test every directory");
				testEveryDirectoryCheckBox.setFontf(GuiUtils.getFont(GuiUtils.fontName,
							GuiUtils.fontSize, 0, false, false));
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
				manualOverrideCodeLabel = new GLabel(responseCodeGroup, SWT.NONE);
				manualOverrideCodeLabel.setText("HTTP response code:");
				FormData manualOverrideCodeLabelLData = new FormData();
				manualOverrideCodeLabelLData.width = 135;
				manualOverrideCodeLabelLData.height = 25;
				manualOverrideCodeLabelLData.left =  new FormAttachment(0, 1000, 8);
				manualOverrideCodeLabelLData.top =  new FormAttachment(0, 1000, 81);
				manualOverrideCodeLabel.setLayoutData(manualOverrideCodeLabelLData);
				manualOverrideCodeLabel.setFontf(GuiUtils.getFont(GuiUtils.fontName,
							GuiUtils.fontSize, 0, false, false));
			}
			{
				manualOverrideRegexLabel = new GLabel(responseCodeGroup, SWT.NONE);
				manualOverrideRegexLabel.setText("Regex override:");
				FormData manualOverrideRegexLabelLData = new FormData();
				manualOverrideRegexLabelLData.width = 127;
				manualOverrideRegexLabelLData.height = 25;
				manualOverrideRegexLabelLData.left =  new FormAttachment(0, 1000, 8);
				manualOverrideRegexLabelLData.top =  new FormAttachment(0, 1000, 120);
				manualOverrideRegexLabel.setLayoutData(manualOverrideRegexLabelLData);
				manualOverrideRegexLabel.setFontf(GuiUtils.getFont(GuiUtils.fontName,
							GuiUtils.fontSize, 0, false, false));
			}
			{
				httpResponseCodeTextbox = new GText(responseCodeGroup, SWT.BORDER);
				httpResponseCodeTextbox.setFontf(GuiUtils.getFont(GuiUtils.fontName,
							GuiUtils.fontSize, 0, false, false));
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
				regexOverrideTextbox = new GText(responseCodeGroup, SWT.BORDER);
				FormData regexOverrideTextboxLData = new FormData();
				regexOverrideTextboxLData.width = 167;
				regexOverrideTextboxLData.height = 19;
				regexOverrideTextboxLData.left =  new FormAttachment(0, 1000, 156);
				regexOverrideTextboxLData.top =  new FormAttachment(0, 1000, 126);
				regexOverrideTextbox.setLayoutData(regexOverrideTextboxLData);
			}
			{
				manualOverrideAddButton = new GButton(responseCodeGroup, SWT.PUSH | SWT.CENTER);
				manualOverrideAddButton.setText("Add");
				FormData manualOverrideAddButtonLData = new FormData();
				manualOverrideAddButtonLData.width = 70;
				manualOverrideAddButtonLData.height = 30;
				manualOverrideAddButtonLData.left =  new FormAttachment(0, 1000, 347);
				manualOverrideAddButtonLData.top =  new FormAttachment(0, 1000, 83);
				manualOverrideAddButton.setLayoutData(manualOverrideAddButtonLData);
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
				manualOverrideRemoveButton = new GButton(responseCodeGroup, SWT.PUSH | SWT.CENTER);
				manualOverrideRemoveButton.setText("Remove");
				FormData manualOverrideRemoveButtonLData = new FormData();
				manualOverrideRemoveButtonLData.width = 70;
				manualOverrideRemoveButtonLData.height = 30;
				manualOverrideRemoveButtonLData.left =  new FormAttachment(0, 1000, 347);
				manualOverrideRemoveButtonLData.top =  new FormAttachment(0, 1000, 125);
				manualOverrideRemoveButton.setLayoutData(manualOverrideRemoveButtonLData);
				manualOverrideRemoveButton.setFontf(GuiUtils.getFont(GuiUtils.fontName,
							GuiUtils.fontSize, 0, false, false));
				manualOverrideRemoveButton.addSelectionListener(new SelectionAdapter()
					{
						@Override
						public void widgetSelected(SelectionEvent event)
						{
							for (GTableItem tempItem : responseCodeOverrideTable.getSelection())
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
				responseCodeOverrideTable = new GTable(responseCodeGroup, SWT.SINGLE | SWT.BORDER);
				responseCodeOverrideTable.setFontf(GuiUtils.getFont(GuiUtils.fontName,
							GuiUtils.fontSize, 0, false, false));
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
					responseCodeCodeColumn = new GTableColumn(responseCodeOverrideTable, SWT.NONE);
					responseCodeCodeColumn.setText("HTTP Code");
					responseCodeCodeColumn.setWidth(100);
				}
				{
					responseCodeRegexColumn = new GTableColumn(responseCodeOverrideTable, SWT.NONE);
					responseCodeRegexColumn.setText("Regex");
					responseCodeRegexColumn.setWidth(250);
				}
			}
		}
		this.layout();
	}

	private void addResponseCodeOverride(String code, String pattern)
	{
		GTableItem item = new GTableItem(responseCodeOverrideTable, SWT.NONE);
		item.setText(new String[] { code, pattern });
	}

	/**
	 * Overriding checkSubclass allows this class to extend
	 * com.grendelscan.GUI.customControls.basic.GComposite
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
