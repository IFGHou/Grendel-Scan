package com.grendelscan.GUI.http.transactionTable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;

import com.grendelscan.GUI.customControls.basic.GComposite;
import com.grendelscan.GUI.customControls.basic.GLabel;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import com.grendelscan.GUI.customControls.basic.GText;

import com.grendelscan.GUI.GuiUtils;
import com.grendelscan.GUI.MainWindow;
import com.grendelscan.GUI.customControls.basic.GButton;
import com.grendelscan.GUI.fuzzing.FuzzTemplateComposite;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.Scan;

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
public class AllTransactionTable extends AbstractTransactionTable
{
	protected TransactionTableFilter	filter;

	public AllTransactionTable(GComposite parent, int style)
	{
		super(parent, style, Scan.getInstance().getTransactionRecord().getSummaryProvider(), true);
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.grendelscan.GUI.http.transactionTable.AbstractTransactionTable#
	 * addContextMenuItems()
	 */
	@Override
	protected void addContextMenuItems(Menu menu, final int transactionID)
	{
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("Use as fuzz template");
		item.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent evt)
			{
				StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
				FuzzTemplateComposite.fuzzTemplateText = transaction.getRequestWrapper().toString();
				MainWindow.getInstance().setSelection(MainWindow.getInstance().getFuzzerTab());
				FuzzTemplateComposite.getFuzzTemplate(getShell());
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.grendelscan.GUI.http.transactionTable.AbstractTransactionTable#
	 * customInitGUI()
	 */
	@Override
	protected int initGUIStart(int top)
	{
		settingsDialog = new TransactionTableSettingsDialog(getShell(), 0);
		top += 35;
		{
			settingsButton = new GButton(this, SWT.PUSH | SWT.CENTER);
			FormData settingsButtonLData = new FormData();
			settingsButtonLData.width = 75;
			settingsButtonLData.height = 25;
			settingsButtonLData.left = new FormAttachment(0, 1000, 5);
			settingsButtonLData.top = new FormAttachment(0, 1000, 5);
			settingsButton.setLayoutData(settingsButtonLData);
			settingsButton.setText("Settings");
			settingsButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent evt)
				{
					if (filter == null)
					{
						filter = new TransactionTableFilter();
					}
					if (settingsDialog.open(filter))
					{
						if (tableViewer.getFilters().length == 0)
						{
							tableViewer.addFilter(filter);
						}
						refreshData();
					}
				}
			});
		}
		{
			exportDisplayedButton = new GButton(this, SWT.PUSH | SWT.CENTER);
			FormData exportDisplayedButtonLData = new FormData();
			exportDisplayedButtonLData.width = 120;
			exportDisplayedButtonLData.height = 25;
			exportDisplayedButtonLData.left = new FormAttachment(0, 1000, 90);
			exportDisplayedButtonLData.top = new FormAttachment(0, 1000, 5);
			exportDisplayedButton.setLayoutData(exportDisplayedButtonLData);
			exportDisplayedButton.setText("Export Displayed");
			exportDisplayedButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent evt)
				{
					exportDisplayedButtonWidgetSelected(evt);
				}
			});
		}
		{
			exportAllButton = new GButton(this, SWT.PUSH | SWT.CENTER);
			FormData exportAllButtonLData = new FormData();
			exportAllButtonLData.width = 75;
			exportAllButtonLData.height = 25;
			exportAllButtonLData.left = new FormAttachment(0, 1000, 220);
			exportAllButtonLData.top = new FormAttachment(0, 1000, 5);
			exportAllButton.setLayoutData(exportAllButtonLData);
			exportAllButton.setText("Export All");
			exportAllButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent evt)
				{
					exportAllButtonWidgetSelected(evt);
				}
			});
		}
		{
			GLabel transactionCountLabel = new GLabel(this, SWT.NONE);
			transactionCountLabel.setText("Total request count:");
			FormData transactionCountLabelLData = new FormData();
			transactionCountLabelLData.width = 125;
			transactionCountLabelLData.height = 25;
			transactionCountLabelLData.top = new FormAttachment(0, 1000, 5);
			transactionCountLabelLData.right = new FormAttachment(1000, 1000, -70);
			transactionCountLabel.setLayoutData(transactionCountLabelLData);
		}
		{
			transactionCountTextBox = new GText(this, SWT.READ_ONLY | SWT.BORDER);
			transactionCountTextBox.setBackground(GuiUtils.getColor(255, 255, 255));
			FormData transactionCountTextBoxLData = new FormData();
			transactionCountTextBoxLData.width = 50;
			transactionCountTextBoxLData.height = 20;
			transactionCountTextBoxLData.top = new FormAttachment(0, 1000, 5);
			transactionCountTextBoxLData.right = new FormAttachment(1000, 1000, -5);
			transactionCountTextBox.setLayoutData(transactionCountTextBoxLData);
		}
		return top;
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.GUI.customControls.dataTable.AbstractDataTable#initGUIDone(int)
	 */
	@Override
	protected int initGUIBottomControls(int top)
	{
		return top;
	}



}
