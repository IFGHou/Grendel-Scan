package com.grendelscan.GUI.http.transactionTable;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;

import org.eclipse.swt.widgets.Display;
import com.grendelscan.GUI.customControls.basic.GGroup;
import com.grendelscan.GUI.customControls.basic.GLabel;
import org.eclipse.swt.widgets.MessageBox;
import com.grendelscan.GUI.customControls.basic.GShell;
import com.grendelscan.GUI.customControls.basic.GText;

import com.grendelscan.GrendelScan;
import com.grendelscan.GUI.customControls.basic.GButton;
import com.grendelscan.requester.TransactionSource;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class TransactionTableSettingsDialog extends com.grendelscan.GUI.customControls.basic.GDialog
{

	private GButton					allSourceButton;
	private GButton					authenticationSourceCheckbox;
	private GButton					baseUrlCheckbox;
	private GButton					cancelButton;
	private GButton					categorizerSourceCheckbox;
	private boolean					changed;
	private GButton					cobraSourceCheckbox;
	private GShell					dialogShell;
	private GButton					enumerationSourceCheckbox;
	private TransactionTableFilter	existingSettings;
	private GGroup					filterGroup;
	private GButton					fuzzerSourceCheckbox;
	private GLabel					hostRegexLabel;
	private GText					hostRegexText;
	private GButton					manualRequestCheckbox;
	private GButton					niktoSourceCheckbox;
	private GButton					noneSourceButton;
	private GButton					okButton;
	private GButton					overrideSourceCheckbox;
	private GLabel					pathRegexLabel;
	private GText					pathRegexText;
	private GButton					proxySourceCheckbox;
	private GLabel					queryRegexLabel;
	private GText					queryRegexText;
	private GButton					resetButton;
	private GLabel					responseCodeRegexLabel;
	private GText					responseCodeRegexText;
	private GButton					spiderSourceCheckbox;
	private GButton					testSourceCheckbox;
	private GGroup					transactionSourceGroup;

	public TransactionTableSettingsDialog(GShell parent, int style)
	{
		super(parent, style);
	}

	public boolean open(TransactionTableFilter existingSettings)
	{
		this.existingSettings = existingSettings;
		changed = false;
		createDialog();
		loadDefaults();
		displayFilter(existingSettings);
		dialogShell.setLocation(getParent().toDisplay(100, 100));
		dialogShell.setText(GrendelScan.versionText + " - Transaction GTable Settings");

		dialogShell.open();
		Display display = dialogShell.getDisplay();
		while (!dialogShell.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
		return changed;
	}

	private boolean badPattern(String pattern)
	{
		boolean bad = false;
		try
		{
			Pattern.compile(pattern);
		}
		catch (PatternSyntaxException e)
		{
			bad = true;
		}
		return bad;
	}

	private void changeAllSources(boolean selected)
	{
		manualRequestCheckbox.setSelection(selected);
		proxySourceCheckbox.setSelection(selected);
		authenticationSourceCheckbox.setSelection(selected);
		baseUrlCheckbox.setSelection(selected);
		categorizerSourceCheckbox.setSelection(selected);
		cobraSourceCheckbox.setSelection(selected);
		enumerationSourceCheckbox.setSelection(selected);
		niktoSourceCheckbox.setSelection(selected);
		overrideSourceCheckbox.setSelection(selected);
		spiderSourceCheckbox.setSelection(selected);
		testSourceCheckbox.setSelection(selected);
		fuzzerSourceCheckbox.setSelection(selected);
	}

	private void createDialog()
	{
		GShell parent = getParent();
		dialogShell = new GShell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

		dialogShell.setLayout(new FormLayout());
		dialogShell.layout();
		dialogShell.pack();
		dialogShell.setSize(704, 447);
		{
			okButton = new GButton(dialogShell, SWT.PUSH | SWT.CENTER);
			FormData okButtonLData = new FormData();
			okButtonLData.width = 86;
			okButtonLData.height = 27;
			okButtonLData.left = new FormAttachment(0, 1000, 17);
			okButtonLData.bottom = new FormAttachment(1000, 1000, -5);
			okButton.setLayoutData(okButtonLData);
			okButton.setText("OK");
			okButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent evt)
				{
					okButtonWidgetSelected(evt);
				}
			});
		}
		{
			cancelButton = new GButton(dialogShell, SWT.PUSH | SWT.CENTER);
			FormData cancelButtonLData = new FormData();
			cancelButtonLData.width = 86;
			cancelButtonLData.height = 27;
			cancelButtonLData.left = new FormAttachment(0, 1000, 128);
			cancelButtonLData.bottom = new FormAttachment(1000, 1000, -5);
			cancelButton.setLayoutData(cancelButtonLData);
			cancelButton.setText("Cancel");
			cancelButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent evt)
				{
					dialogShell.close();
				}
			});
		}
		{
			resetButton = new GButton(dialogShell, SWT.PUSH | SWT.CENTER);
			FormData resetButtonLData = new FormData();
			resetButtonLData.width = 86;
			resetButtonLData.height = 27;
			resetButtonLData.left = new FormAttachment(0, 1000, 247);
			resetButtonLData.bottom = new FormAttachment(1000, 1000, -5);
			resetButton.setLayoutData(resetButtonLData);
			resetButton.setText("Reset");
			resetButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent evt)
				{
					loadDefaults();
				}
			});
		}
		{
			filterGroup = new GGroup(dialogShell, SWT.NONE);
			filterGroup.setLayout(new FormLayout());
			FormData filterGroupLData = new FormData();
			filterGroupLData.width = 450;
			filterGroupLData.height = 163;
			filterGroupLData.left = new FormAttachment(0, 1000, 226);
			filterGroupLData.top = new FormAttachment(0, 1000, 12);
			filterGroup.setLayoutData(filterGroupLData);
			filterGroup.setText("Filters");
			{
				hostRegexLabel = new GLabel(filterGroup, SWT.NONE);
				FormData hostRegexLabelLData = new FormData();
				hostRegexLabelLData.width = 100;
				hostRegexLabelLData.height = 17;
				hostRegexLabelLData.left = new FormAttachment(0, 1000, 5);
				hostRegexLabelLData.top = new FormAttachment(0, 1000, 5);
				hostRegexLabel.setLayoutData(hostRegexLabelLData);
				hostRegexLabel.setText("Host regex:");
			}
			{
				FormData hostRegexTextLData = new FormData();
				hostRegexTextLData.width = 298;
				hostRegexTextLData.height = 19;
				hostRegexTextLData.left = new FormAttachment(0, 1000, 127);
				hostRegexTextLData.top = new FormAttachment(0, 1000, 7);
				hostRegexText = new GText(filterGroup, SWT.BORDER);
				hostRegexText.setLayoutData(hostRegexTextLData);
			}
			{
				pathRegexLabel = new GLabel(filterGroup, SWT.NONE);
				FormData pathRegexLabelLData = new FormData();
				pathRegexLabelLData.width = 110;
				pathRegexLabelLData.height = 17;
				pathRegexLabelLData.left = new FormAttachment(0, 1000, 5);
				pathRegexLabelLData.top = new FormAttachment(0, 1000, 45);
				pathRegexLabel.setLayoutData(pathRegexLabelLData);
				pathRegexLabel.setText("Path regex:");
			}
			{
				FormData pathRegexTextLData = new FormData();
				pathRegexTextLData.width = 278;
				pathRegexTextLData.height = 19;
				pathRegexTextLData.left = new FormAttachment(0, 1000, 127);
				pathRegexTextLData.top = new FormAttachment(0, 1000, 47);
				pathRegexText = new GText(filterGroup, SWT.BORDER);
				pathRegexText.setLayoutData(pathRegexTextLData);
			}
			{
				queryRegexLabel = new GLabel(filterGroup, SWT.NONE);
				FormData queryRegexLabelLData = new FormData();
				queryRegexLabelLData.width = 100;
				queryRegexLabelLData.height = 17;
				queryRegexLabelLData.left = new FormAttachment(0, 1000, 5);
				queryRegexLabelLData.top = new FormAttachment(0, 1000, 85);
				queryRegexLabel.setLayoutData(queryRegexLabelLData);
				queryRegexLabel.setText("Query regex:");
			}
			{
				FormData queryRegexTextLData = new FormData();
				queryRegexTextLData.width = 278;
				queryRegexTextLData.height = 19;
				queryRegexTextLData.left = new FormAttachment(0, 1000, 127);
				queryRegexTextLData.top = new FormAttachment(0, 1000, 87);
				queryRegexText = new GText(filterGroup, SWT.BORDER);
				queryRegexText.setLayoutData(queryRegexTextLData);
			}
			{
				responseCodeRegexLabel = new GLabel(filterGroup, SWT.WRAP);
				FormData responseCodeRegexLData = new FormData();
				responseCodeRegexLData.width = 100;
				responseCodeRegexLData.height = 37;
				responseCodeRegexLData.left = new FormAttachment(0, 1000, 5);
				responseCodeRegexLData.top = new FormAttachment(0, 1000, 119);
				responseCodeRegexLabel.setLayoutData(responseCodeRegexLData);
				responseCodeRegexLabel.setText("Response code regex:");
			}
			{
				FormData responseCodeRegexTextLData = new FormData();
				responseCodeRegexTextLData.width = 78;
				responseCodeRegexTextLData.height = 19;
				responseCodeRegexTextLData.left = new FormAttachment(0, 1000, 127);
				responseCodeRegexTextLData.top = new FormAttachment(0, 1000, 127);
				responseCodeRegexText = new GText(filterGroup, SWT.BORDER);
				responseCodeRegexText.setLayoutData(responseCodeRegexTextLData);
			}
		}
		{
			transactionSourceGroup = new GGroup(dialogShell, SWT.NONE);
			FormLayout transactionSourceGroupLayout = new FormLayout();
			transactionSourceGroup.setLayout(transactionSourceGroupLayout);
			FormData transactionSourceGroupLData = new FormData();
			transactionSourceGroupLData.width = 203;
			transactionSourceGroupLData.height = 328;
			transactionSourceGroupLData.left = new FormAttachment(0, 1000, 5);
			transactionSourceGroupLData.top = new FormAttachment(0, 1000, 5);
			transactionSourceGroup.setLayoutData(transactionSourceGroupLData);
			transactionSourceGroup.setText("Transaction Source");
			{
				fuzzerSourceCheckbox = new GButton(transactionSourceGroup, SWT.CHECK | SWT.LEFT);
				FormData fuzzerSourceCheckboxLData = new FormData();
				fuzzerSourceCheckboxLData.width = 114;
				fuzzerSourceCheckboxLData.height = 19;
				fuzzerSourceCheckboxLData.left = new FormAttachment(0, 1000, 5);
				fuzzerSourceCheckboxLData.top = new FormAttachment(0, 1000, 83);
				fuzzerSourceCheckbox.setLayoutData(fuzzerSourceCheckboxLData);
				fuzzerSourceCheckbox.setText("Fuzzer");
			}
			{
				allSourceButton = new GButton(transactionSourceGroup, SWT.PUSH | SWT.CENTER);
				FormData allSourceButtonLData = new FormData();
				allSourceButtonLData.width = 40;
				allSourceButtonLData.height = 20;
				allSourceButtonLData.left = new FormAttachment(0, 1000, 10);
				allSourceButtonLData.top = new FormAttachment(0, 1000, 10);
				allSourceButton.setLayoutData(allSourceButtonLData);
				allSourceButton.setText("All");
				allSourceButton.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent evt)
					{
						changeAllSources(true);
					}
				});
			}
			{
				noneSourceButton = new GButton(transactionSourceGroup, SWT.PUSH | SWT.CENTER);
				noneSourceButton.setText("None");
				FormData noneSourceButtonLData = new FormData();
				noneSourceButtonLData.width = 45;
				noneSourceButtonLData.height = 20;
				noneSourceButtonLData.left = new FormAttachment(0, 1000, 65);
				noneSourceButtonLData.top = new FormAttachment(0, 1000, 10);
				noneSourceButton.setLayoutData(noneSourceButtonLData);
				noneSourceButton.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent evt)
					{
						changeAllSources(false);
					}
				});
			}
			{
				proxySourceCheckbox = new GButton(transactionSourceGroup, SWT.CHECK | SWT.LEFT);
				FormData proxySourceCheckboxLData = new FormData();
				proxySourceCheckboxLData.width = 129;
				proxySourceCheckboxLData.height = 19;
				proxySourceCheckboxLData.left = new FormAttachment(0, 1000, 5);
				proxySourceCheckboxLData.top = new FormAttachment(0, 1000, 37);
				proxySourceCheckbox.setLayoutData(proxySourceCheckboxLData);
				proxySourceCheckbox.setText("Proxy");
			}
			{
				manualRequestCheckbox = new GButton(transactionSourceGroup, SWT.CHECK | SWT.LEFT);
				FormData manualRequestCheckboxLData = new FormData();
				manualRequestCheckboxLData.width = 147;
				manualRequestCheckboxLData.height = 19;
				manualRequestCheckboxLData.left = new FormAttachment(0, 1000, 5);
				manualRequestCheckboxLData.top = new FormAttachment(0, 1000, 61);
				manualRequestCheckbox.setLayoutData(manualRequestCheckboxLData);
				manualRequestCheckbox.setText("Manual request");
			}
			{
				spiderSourceCheckbox = new GButton(transactionSourceGroup, SWT.CHECK | SWT.LEFT);
				FormData spiderSourceCheckboxLData = new FormData();
				spiderSourceCheckboxLData.width = 105;
				spiderSourceCheckboxLData.height = 19;
				spiderSourceCheckboxLData.left = new FormAttachment(0, 1000, 5);
				spiderSourceCheckboxLData.top = new FormAttachment(0, 1000, 107);
				spiderSourceCheckbox.setLayoutData(spiderSourceCheckboxLData);
				spiderSourceCheckbox.setText("Spider");
			}
			{
				testSourceCheckbox = new GButton(transactionSourceGroup, SWT.CHECK | SWT.LEFT);
				FormData testSourceCheckboxLData = new FormData();
				testSourceCheckboxLData.width = 114;
				testSourceCheckboxLData.height = 19;
				testSourceCheckboxLData.left = new FormAttachment(0, 1000, 5);
				testSourceCheckboxLData.top = new FormAttachment(0, 1000, 131);
				testSourceCheckbox.setLayoutData(testSourceCheckboxLData);
				testSourceCheckbox.setText("Scan test");
			}
			{
				enumerationSourceCheckbox = new GButton(transactionSourceGroup, SWT.CHECK | SWT.LEFT);
				FormData enumerationSourceCheckboxLData = new FormData();
				enumerationSourceCheckboxLData.width = 173;
				enumerationSourceCheckboxLData.height = 19;
				enumerationSourceCheckboxLData.left = new FormAttachment(0, 1000, 5);
				enumerationSourceCheckboxLData.top = new FormAttachment(0, 1000, 155);
				enumerationSourceCheckbox.setLayoutData(enumerationSourceCheckboxLData);
				enumerationSourceCheckbox.setText("File enumeration");
			}
			{
				baseUrlCheckbox = new GButton(transactionSourceGroup, SWT.CHECK | SWT.LEFT);
				FormData baseUrlCheckboxLData = new FormData();
				baseUrlCheckboxLData.width = 147;
				baseUrlCheckboxLData.height = 19;
				baseUrlCheckboxLData.left = new FormAttachment(0, 1000, 5);
				baseUrlCheckboxLData.top = new FormAttachment(0, 1000, 179);
				baseUrlCheckbox.setLayoutData(baseUrlCheckboxLData);
				baseUrlCheckbox.setText("Base URL");
			}
			{
				authenticationSourceCheckbox = new GButton(transactionSourceGroup, SWT.CHECK | SWT.LEFT);
				FormData authenticationSourceCheckboxLData = new FormData();
				authenticationSourceCheckboxLData.width = 155;
				authenticationSourceCheckboxLData.height = 19;
				authenticationSourceCheckboxLData.left = new FormAttachment(0, 1000, 5);
				authenticationSourceCheckboxLData.top = new FormAttachment(0, 1000, 203);
				authenticationSourceCheckbox.setLayoutData(authenticationSourceCheckboxLData);
				authenticationSourceCheckbox.setText("Authentication");
			}
			{
				niktoSourceCheckbox = new GButton(transactionSourceGroup, SWT.CHECK | SWT.LEFT);
				FormData niktoSourceCheckboxLData = new FormData();
				niktoSourceCheckboxLData.width = 97;
				niktoSourceCheckboxLData.height = 19;
				niktoSourceCheckboxLData.left = new FormAttachment(0, 1000, 5);
				niktoSourceCheckboxLData.top = new FormAttachment(0, 1000, 227);
				niktoSourceCheckbox.setLayoutData(niktoSourceCheckboxLData);
				niktoSourceCheckbox.setText("Nikto");
			}
			{
				categorizerSourceCheckbox = new GButton(transactionSourceGroup, SWT.CHECK | SWT.LEFT);
				FormData categorizerSourceCheckboxLData = new FormData();
				categorizerSourceCheckboxLData.width = 155;
				categorizerSourceCheckboxLData.height = 19;
				categorizerSourceCheckboxLData.left = new FormAttachment(0, 1000, 5);
				categorizerSourceCheckboxLData.top = new FormAttachment(0, 1000, 251);
				categorizerSourceCheckbox.setLayoutData(categorizerSourceCheckboxLData);
				categorizerSourceCheckbox.setText("Categorizer");
			}
			{
				overrideSourceCheckbox = new GButton(transactionSourceGroup, SWT.CHECK | SWT.LEFT);
				FormData overrideSourceCheckboxLData = new FormData();
				overrideSourceCheckboxLData.width = 155;
				overrideSourceCheckboxLData.height = 19;
				overrideSourceCheckboxLData.left = new FormAttachment(0, 1000, 5);
				overrideSourceCheckboxLData.top = new FormAttachment(0, 1000, 275);
				overrideSourceCheckbox.setLayoutData(overrideSourceCheckboxLData);
				overrideSourceCheckbox.setText("404-detection");
			}
			{
				cobraSourceCheckbox = new GButton(transactionSourceGroup, SWT.CHECK | SWT.LEFT);
				FormData cobraSourceCheckboxLData = new FormData();
				cobraSourceCheckboxLData.width = 105;
				cobraSourceCheckboxLData.height = 19;
				cobraSourceCheckboxLData.left = new FormAttachment(0, 1000, 5);
				cobraSourceCheckboxLData.top = new FormAttachment(0, 1000, 299);
				cobraSourceCheckbox.setLayoutData(cobraSourceCheckboxLData);
				cobraSourceCheckbox.setText("Cobra");
			}
		}

	}

	private void displayFilter(TransactionTableFilter filter)
	{
		if (filter != null)
		{
			for (TransactionSource source : filter.getSources())
			{
				switch (source)
				{
					case AUTHENTICATION:
						authenticationSourceCheckbox.setSelection(true);
						break;
					case AUTOMATIC_RESPONSE_CODE_OVERRIDES:
						overrideSourceCheckbox.setSelection(true);
						break;
					case BASE:
						baseUrlCheckbox.setSelection(true);
						break;
					case CATEGORIZER:
						categorizerSourceCheckbox.setSelection(true);
						break;
					case COBRA:
						cobraSourceCheckbox.setSelection(true);
						break;
					case ENUMERATION:
						enumerationSourceCheckbox.setSelection(true);
						break;
					case FUZZER:
						fuzzerSourceCheckbox.setSelection(true);
						break;
					case MANUAL_REQUEST:
						manualRequestCheckbox.setSelection(true);
						break;
					case NIKTO:
						niktoSourceCheckbox.setSelection(true);
						break;
					case PROXY:
						proxySourceCheckbox.setSelection(true);
						break;
					case SPIDER:
						spiderSourceCheckbox.setSelection(true);
						break;
					case MISC_TEST:
						testSourceCheckbox.setSelection(true);
						break;
				}
			}
			hostRegexText.setText(filter.getHostPattern());
			pathRegexText.setText(filter.getPathPattern());
			queryRegexText.setText(filter.getQueryPattern());
			responseCodeRegexText.setText(filter.getResponseCodePattern());
		}
	}

	private void enableNonTailSettings(boolean enabled)
	{
		authenticationSourceCheckbox.setEnabled(enabled);
		baseUrlCheckbox.setEnabled(enabled);
		categorizerSourceCheckbox.setEnabled(enabled);
		cobraSourceCheckbox.setEnabled(enabled);
		enumerationSourceCheckbox.setEnabled(enabled);
		fuzzerSourceCheckbox.setEnabled(enabled);
		niktoSourceCheckbox.setEnabled(enabled);
		overrideSourceCheckbox.setEnabled(enabled);
		spiderSourceCheckbox.setEnabled(enabled);
		testSourceCheckbox.setEnabled(enabled);

	}

	private void loadDefaults()
	{
		changeAllSources(false);
		hostRegexText.setText("");
		pathRegexText.setText("");
		queryRegexText.setText("");
		responseCodeRegexText.setText("");
		enableNonTailSettings(true);
	}

	private void okButtonWidgetSelected(SelectionEvent evt)
	{
		String message = "";
		if (badPattern(hostRegexText.getText()))
		{
			message += "- Invalid host regular expression\n";
		}
		if (badPattern(pathRegexText.getText()))
		{
			message += "- Invalid path regular expression\n";
		}
		if (badPattern(queryRegexText.getText()))
		{
			message += "- Invalid query regular expression\n";
		}
		if (badPattern(responseCodeRegexText.getText()))
		{
			message += "- Invalid response code regular expression\n";
		}

		if (message != "")
		{
			MessageBox messageBox = new MessageBox(getParent().getShell(), SWT.OK);
			messageBox.setMessage(message);
			messageBox.setText("Error");
			messageBox.open();
		}
		else
		{
			if (!(existingSettings.getHostPattern().equals(hostRegexText.getText())
					&& existingSettings.getPathPattern().equals(pathRegexText.getText())
					&& existingSettings.getQueryPattern().equals(queryRegexText.getText())
					&& existingSettings.getResponseCodePattern().equals(responseCodeRegexText.getText())

					&& (authenticationSourceCheckbox.getSelection() ==
						existingSettings.containsSource(TransactionSource.AUTHENTICATION))
					&& (overrideSourceCheckbox.getSelection() ==
						existingSettings.containsSource(TransactionSource.AUTOMATIC_RESPONSE_CODE_OVERRIDES))
					&& (baseUrlCheckbox.getSelection() ==
						existingSettings.containsSource(TransactionSource.BASE))
					&& (categorizerSourceCheckbox.getSelection() ==
						existingSettings.containsSource(TransactionSource.CATEGORIZER))
					&& (cobraSourceCheckbox.getSelection() ==
						existingSettings.containsSource(TransactionSource.COBRA))
					&& (enumerationSourceCheckbox.getSelection() ==
						existingSettings.containsSource(TransactionSource.ENUMERATION))
					&& (fuzzerSourceCheckbox.getSelection() ==
						existingSettings.containsSource(TransactionSource.FUZZER))
					&& (manualRequestCheckbox.getSelection() ==
						existingSettings.containsSource(TransactionSource.MANUAL_REQUEST))
					&& (niktoSourceCheckbox.getSelection() ==
						existingSettings.containsSource(TransactionSource.NIKTO))
					&& (proxySourceCheckbox.getSelection() ==
						existingSettings.containsSource(TransactionSource.PROXY))
					&& (spiderSourceCheckbox.getSelection() ==
						existingSettings.containsSource(TransactionSource.SPIDER)) 
					&& (testSourceCheckbox.getSelection() == 
						existingSettings.containsSource(TransactionSource.MISC_TEST))))
			{
				changed = true;

				existingSettings.setHostPattern(hostRegexText.getText());
				existingSettings.setPathPattern(pathRegexText.getText());
				existingSettings.setQueryPattern(queryRegexText.getText());
				existingSettings.setResponseCodePattern(responseCodeRegexText.getText());

				existingSettings.clearSources();

				if (proxySourceCheckbox.getSelection())
				{
					existingSettings.addSource(TransactionSource.PROXY);
				}
				if (manualRequestCheckbox.getSelection())
				{
					existingSettings.addSource(TransactionSource.MANUAL_REQUEST);
				}
				if (authenticationSourceCheckbox.getSelection())
				{
					existingSettings.addSource(TransactionSource.AUTHENTICATION);
				}
				if (overrideSourceCheckbox.getSelection())
				{
					existingSettings.addSource(TransactionSource.AUTOMATIC_RESPONSE_CODE_OVERRIDES);
				}
				if (baseUrlCheckbox.getSelection())
				{
					existingSettings.addSource(TransactionSource.BASE);
				}
				if (categorizerSourceCheckbox.getSelection())
				{
					existingSettings.addSource(TransactionSource.CATEGORIZER);
				}
				if (cobraSourceCheckbox.getSelection())
				{
					existingSettings.addSource(TransactionSource.COBRA);
				}
				if (enumerationSourceCheckbox.getSelection())
				{
					existingSettings.addSource(TransactionSource.ENUMERATION);
				}
				if (niktoSourceCheckbox.getSelection())
				{
					existingSettings.addSource(TransactionSource.NIKTO);
				}
				if (spiderSourceCheckbox.getSelection())
				{
					existingSettings.addSource(TransactionSource.SPIDER);
				}
				if (testSourceCheckbox.getSelection())
				{
					existingSettings.addSource(TransactionSource.MISC_TEST);
				}
				if (fuzzerSourceCheckbox.getSelection())
				{
					existingSettings.addSource(TransactionSource.FUZZER);
				}
			}

			dialogShell.close();
		}
	}
}
