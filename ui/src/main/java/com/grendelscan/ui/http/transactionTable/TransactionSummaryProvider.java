/**
 * 
 */
package com.grendelscan.ui.http.transactionTable;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.Viewer;

import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.ui.customControls.dataTable.TableDataRepository;

/**
 * @author david
 * 
 */
public class TransactionSummaryProvider implements TableDataRepository<TransactionSummary>
{
	protected Map<Integer, TransactionSummary>	transactions;
	private long lastUpdatedTime; 
	
	public TransactionSummaryProvider(Map<Integer, TransactionSummary> transactions)
	{
		this.transactions = Collections.synchronizedMap(new HashMap<Integer, TransactionSummary>(500));
		if (transactions != null)
		{
			this.transactions.putAll(transactions);
		}
	}

	public void addOrUpdateTransaction(StandardHttpTransaction transaction)
	{
		if (transactions.containsKey(transaction.getId()))
		{
			transactions.get(transaction.getId()).updateSummary(transaction);
		}
		else
		{
			transactions.put(transaction.getId(), new TransactionSummary(transaction));
		}
		lastUpdatedTime = (new Date()).getTime();
	}

	public void clear()
	{
		transactions.clear();
	}

	public final Collection<TransactionSummary> getTransactionList()
	{
		return transactions.values();
	}

//	/* (non-Javadoc)
//	 * @see com.grendelscan.ui.customControls.dataTable.DataProvider#getData()
//	 */
//	@Override
//	public Collection<TransactionSummary> getData()
//	{
//		return getTransactionList();
//	}

	/* (non-Javadoc)
	 * @see com.grendelscan.ui.UpdateService.UpdateServiceDataProvider#getLastModified()
	 */
	@Override
	public long getLastModified()
	{
		return lastUpdatedTime;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement)
	{
		return transactions.values().toArray();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		// TODO Auto-generated method stub
		
	}


}
