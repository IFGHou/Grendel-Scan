package com.grendelscan.GUI2.settings;

import java.net.URISyntaxException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;

import com.grendelscan.GUI.customControls.basic.GComposite;
import com.grendelscan.GUI.customControls.basic.GGroup;
import com.grendelscan.GUI.customControls.basic.GLabel;
import com.grendelscan.GUI.customControls.basic.GText;

import com.cloudgarden.resource.SWTResourceManager;
import com.grendelscan.GUI.GUIConstants;
import com.grendelscan.GUI.MainWindow;
import com.grendelscan.GUI.Verifiers.EnforceIntegersOnly;
import com.grendelscan.GUI.customControls.AddRemoveHandler;
import com.grendelscan.GUI.customControls.AddRemoveListComposite;
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
public class GeneralSettingsComposite extends GComposite implements GrendelSettingsControl
{


	private GLabel		parseDomLabel;
	private GGroup		responseCompareSettingsGroup;
	protected GLabel		maxCategorizerThreadsLabel;
	protected GText		maxCategorizerThreadTextBox;
	protected GLabel		maxRequesterThreadsLabel;
	protected GText		maxRequesterThreadTextbox;
	protected GLabel		maxTesterThreadsLabel;
	protected GText		maxTesterThreadTextBox;
	private GButton applyButton;
	protected AddRemoveListComposite baseUrlsList;
	protected GButton	parseDomCheckbox;

	protected GGroup		threadSettingsGroup;

	{
		// Register as a resource user - SWTResourceManager will
		// handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}

	public GeneralSettingsComposite(com.grendelscan.GUI.customControls.basic.GComposite parent, int style)
	{
		super(parent, style);
		initGUI();
	}

	@Override
	public void updateFromSettings()
	{
		maxRequesterThreadTextbox.setText(Scan.getScanSettings().getMaxRequesterThreads() + "");
		maxTesterThreadTextBox.setText(Scan.getScanSettings().getMaxTesterThreads() + "");
		maxCategorizerThreadTextBox.setText(Scan.getScanSettings().getMaxCategorizerThreads() + "");
		parseDomCheckbox.setSelection(Scan.getScanSettings().isParseHtmlDom());

		baseUrlsList.removeAll();
		for (String baseUri : Scan.getScanSettings().getReadOnlyBaseURIs())
		{
			baseUrlsList.addItem(baseUri);
		}
	}

	@Override
	public String updateToSettings()
	{
		Scan.getScanSettings().setMaxRequesterThreads(Integer.valueOf(maxRequesterThreadTextbox.getText()));
		Scan.getScanSettings().setMaxTesterThreads(Integer.valueOf(maxTesterThreadTextBox.getText()));
		Scan.getScanSettings().setMaxCategorizerThreads(Integer.valueOf(maxCategorizerThreadTextBox.getText()));
		Scan.getScanSettings().setParseHtmlDom(Boolean.valueOf(parseDomCheckbox.getSelection()));

		return "";
	}

	
	private void initGUI()
	{
		EnforceIntegersOnly numbersOnlyVerifyer = new EnforceIntegersOnly();
		setFontf(GUIConstants.getFont(0));
		FormLayout thisLayout = new FormLayout();
		setLayout(thisLayout);
		this.setSize(772, 396);
		{
			applyButton = new GButton(this, SWT.PUSH | SWT.CENTER);
			FormData applyButtonLData = new FormData();
			applyButtonLData.width = 121;
			applyButtonLData.height = 31;
			applyButtonLData.right =  new FormAttachment(1000, 1000, -12);
			applyButtonLData.bottom =  new FormAttachment(1000, 1000, -8);
			applyButton.setLayoutData(applyButtonLData);
			applyButton.setText("Apply Changes");
			applyButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent evt) {
					updateToSettings();
				}
			});
		}

		{
			threadSettingsGroup = new GGroup(this, SWT.NONE);
			threadSettingsGroup.setText("Thread Settings");
			threadSettingsGroup.setSize(threadSettingsGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			FormData threadSettingsGroupLData = new FormData();
			threadSettingsGroupLData.top =  new FormAttachment(0, 1000, 12);
			threadSettingsGroupLData.left =  new FormAttachment(0, 1000, 13);
			threadSettingsGroup.setLayoutData(threadSettingsGroupLData);
			threadSettingsGroup.setLayout(null);
			{
				maxRequesterThreadsLabel = new GLabel(threadSettingsGroup, SWT.NONE);
				maxRequesterThreadsLabel.setText("Max requester threads:");
				maxRequesterThreadsLabel.setSize(maxRequesterThreadsLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				maxRequesterThreadsLabel.setBounds(12, 25, 200, 25);
			}
			{
				maxCategorizerThreadsLabel = new GLabel(threadSettingsGroup, SWT.NONE);
				maxCategorizerThreadsLabel.setText("Max categorizer threads:");
				maxCategorizerThreadsLabel.setBounds(12, 86, 199, 25);
			}
			{
				maxTesterThreadsLabel = new GLabel(threadSettingsGroup, SWT.NONE);
				maxTesterThreadsLabel.setText("Max tester threads:");
				maxTesterThreadsLabel.setBounds(12, 56, 199, 18);
			}
			{
				maxRequesterThreadTextbox = new GText(threadSettingsGroup, SWT.BORDER);
				maxRequesterThreadTextbox.setTextLimit(2);
				maxRequesterThreadTextbox.setBounds(223, 25, 33, 25);
				maxRequesterThreadTextbox.addVerifyListener(numbersOnlyVerifyer);
			}
			{
				maxTesterThreadTextBox = new GText(threadSettingsGroup, SWT.BORDER);
				maxTesterThreadTextBox.setTextLimit(2);
				maxTesterThreadTextBox.setBounds(223, 56, 33, 25);
				maxTesterThreadTextBox.addVerifyListener(numbersOnlyVerifyer);
			}
			{
				maxCategorizerThreadTextBox = new GText(threadSettingsGroup, SWT.BORDER);
				maxCategorizerThreadTextBox.setTextLimit(2);
				maxCategorizerThreadTextBox.setBounds(223, 86, 33, 25);
				maxCategorizerThreadTextBox.addVerifyListener(numbersOnlyVerifyer);
			}
		}
		{
			responseCompareSettingsGroup = new GGroup(this, SWT.NONE);
			responseCompareSettingsGroup.setText("Response comparisons:");
			FormData responseCompareSettingsGroupLData = new FormData();
			responseCompareSettingsGroupLData.width = 415;
			responseCompareSettingsGroupLData.height = 105;
			responseCompareSettingsGroupLData.left =  new FormAttachment(0, 1000, 304);
			responseCompareSettingsGroupLData.top =  new FormAttachment(0, 1000, 12);
			responseCompareSettingsGroup.setLayoutData(responseCompareSettingsGroupLData);
			responseCompareSettingsGroup.setLayout(null);
			{
				parseDomLabel = new GLabel(responseCompareSettingsGroup, SWT.NONE);
				parseDomLabel.setText("Parse HTML DOM\n" + "More accurate, but takes more CPU cycles.\n"
						+ "Affects a number of test modules, and\n" + "automatic File-Not-Found detection.");
				parseDomLabel.setBounds(53, 25, 323, 93);
			}
			{
				parseDomCheckbox = new GButton(responseCompareSettingsGroup, SWT.CHECK | SWT.LEFT);
				parseDomCheckbox.setEnabled(true);
				parseDomCheckbox.setBounds(7, 57, 34, 19);
			}
		}
		{
			AddRemoveHandler handler = new AddRemoveHandler()
			{

				@Override
				public void addItem(String item) throws Throwable
				{
					try
					{
						Scan.getScanSettings().addBaseURI(item);// ScanSettings will only add it if it's new
						Scan.getInstance().addBaseURIToScan(item);
					}
					catch(IllegalArgumentException e)
					{
						MainWindow.getInstance().displayMessage("Error", "Duplicate base URI (" + item + ")", true);
						throw e;
					}
					catch (URISyntaxException e)
					{
						MainWindow.getInstance().displayMessage("Error", "Problem with base URI (" + item + "): " + e.toString(), true);
						throw e;
					} 
				}

				@Override
				public void removeItem(String item) throws Throwable
				{
					Scan.getScanSettings().removeBaseURI(item);
				}

				@Override
				public void clear() throws Throwable
				{
					Scan.getScanSettings().clearBaseURIs();
				}
				
			};
			baseUrlsList = new AddRemoveListComposite(this, SWT.NONE, "Base URLs", handler, null);
			FormData baseUrlsGroupLData = new FormData();
			baseUrlsGroupLData.top =  new FormAttachment(0, 1000, 160);
			baseUrlsGroupLData.left =  new FormAttachment(0, 1000, 12);
			baseUrlsGroupLData.right =  new FormAttachment(1000, 1000, -12);
			baseUrlsGroupLData.bottom =  new FormAttachment(1000, 1000, -51);
			baseUrlsGroupLData.width = 748;
			baseUrlsGroupLData.height = 185;
			baseUrlsList.setLayoutData(baseUrlsGroupLData);
		}
		this.layout();
	}
}
