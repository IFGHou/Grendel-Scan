package com.grendelscan.ui.http.transactionDisplay;

import com.grendelscan.commons.http.transactions.StandardHttpTransaction;

public interface TransactionUpdateNotificationTarget
{
	public void updatedTransaction(StandardHttpTransaction transaction);
}
