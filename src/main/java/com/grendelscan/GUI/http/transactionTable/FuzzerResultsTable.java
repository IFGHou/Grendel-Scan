/**
 * 
 */
package com.grendelscan.GUI.http.transactionTable;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

/**
 * @author david
 * 
 */
public class FuzzerResultsTable extends AbstractTransactionTable
{

	/**
	 * @param parent
	 * @param style
	 */
	public FuzzerResultsTable(Composite parent, int style, TransactionSummaryProvider transactionSummaryProvider)
	{
		super(parent, style, transactionSummaryProvider, false);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.GUI.http.transactionTable.AbstractTransactionTable#addContextMenuItems(org.eclipse.swt.widgets.Menu, int)
	 */
	@Override
	protected void addContextMenuItems(Menu menu, int transactionID)
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.GUI.customControls.dataTable.AbstractDataTable#initGUIDone(int)
	 */
	@Override
	protected int initGUIBottomControls(int top)
	{
		// TODO Auto-generated method stub
		return 0;
	}



}
