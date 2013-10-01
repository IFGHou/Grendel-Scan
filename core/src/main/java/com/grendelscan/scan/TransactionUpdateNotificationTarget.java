package com.grendelscan.scan;

import com.grendelscan.commons.http.transactions.StandardHttpTransaction;

public interface TransactionUpdateNotificationTarget
{
	public void updatedTransaction(StandardHttpTransaction transaction);
}
