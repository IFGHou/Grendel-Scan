/**
 * 
 */
package com.grendelscan.GUI.http.transactionTable;

import org.eclipse.jface.viewers.Viewer;

import com.grendelscan.GUI.customControls.dataTable.ColumnComparator;

/**
 * @author david
 * 
 */
public class TransactionTableSorter extends ColumnComparator<TransactionTableColumns>
{


	@Override
	public int compare(@SuppressWarnings("unused") Viewer viewer, Object e1, Object e2)
	{
		TransactionSummary t1 = (TransactionSummary) e1;
		TransactionSummary t2 = (TransactionSummary) e2;
		int rc = 0;
		switch (propertyIndex)
		{
			case ID:
				rc = Integer.valueOf(t1.getTransactionID()).compareTo(t2.getTransactionID());
				break;
			case HOST:
				rc = t1.getHost().compareTo(t2.getHost());
				break;
			case METHOD:
				rc = t1.getMethod().compareTo(t2.getMethod());
				break;
			case PATH:
				rc = t1.getPath().compareTo(t2.getMethod());
				break;
			case QUERY:
				rc = t1.getQuery().compareTo(t2.getQuery());
				break;
			case RESPONSE_CODE:
				rc = Integer.valueOf(t1.getResponseCode()).compareTo(t2.getResponseCode());
				break;
			case SOURCE:
				rc = t1.getSource().compareTo(t2.getSource());
				break;
			case TIME:
				rc = Long.valueOf(t1.getTime()).compareTo(t2.getTime());
				//$FALL-THROUGH$
			default:
				rc = 0;
		}
		// If descending order, flip the direction
		if (direction == DESCENDING)
		{
			rc = -rc;
		}
		return rc;
	}


	/* (non-Javadoc)
	 * @see com.grendelscan.GUI.customControls.dataTable.ColumnComparator#getDefaultColumn()
	 */
	@Override
	public TransactionTableColumns getDefaultColumn()
	{
		return TransactionTableColumns.ID;
	}

}
