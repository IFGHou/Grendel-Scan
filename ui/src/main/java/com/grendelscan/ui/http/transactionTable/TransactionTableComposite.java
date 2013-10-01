package com.grendelscan.ui.http.transactionTable;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.SWT;

import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.Scan;
import com.grendelscan.ui.customControls.basic.GComposite;
import com.grendelscan.ui.customControls.basic.GSashForm;
import com.grendelscan.ui.customControls.basic.GTableItem;
import com.grendelscan.ui.http.transactionDisplay.TransactionComposite;


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
public class TransactionTableComposite extends GSashForm {

	AbstractTransactionTable transactionTable;
	TransactionComposite transactionComposite;
	GTableItem lastSelectedItem;
	
	public TransactionTableComposite(GComposite parent, int style, AbstractTransactionTable transactionTable) 
	{
		super(parent, style | SWT.VERTICAL);
		this.transactionTable = transactionTable;
		initGUI();
	}

	private void initGUI() 
	{
		transactionTable.setParent(this);
		SelectionListener sl = new SelectionListener()
		{
			
			@Override
			public void widgetSelected(SelectionEvent event)
			{
				selected(event);
			}
			
			private void selected(SelectionEvent event)
			{
				TableItem[] item = transactionTable.getTableViewer().getTable().getSelection();
				if (item.length > 0 && item[0] != lastSelectedItem)
				{
					int id = Integer.valueOf(item[0].getText());
					StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(id);
					transactionComposite.setShowResponse(transaction.isSuccessfullExecution());
					transactionComposite.displayTransactionData(transaction);
					lastSelectedItem = (GTableItem) item[0];
				}

			}
			@Override
			public void widgetDefaultSelected(SelectionEvent event)
			{
				selected(event);
			}
		};
		transactionTable.getTableViewer().getTable().addSelectionListener(sl);
		transactionComposite = new TransactionComposite(this, SWT.NONE, false, false, true);
		this.setWeights(new int[] {40, 60});
		this.layout();
	}

}
