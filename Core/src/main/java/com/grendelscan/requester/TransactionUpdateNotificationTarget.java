package com.grendelscan.requester;

import com.grendelscan.requester.http.transactions.StandardHttpTransaction;

public interface TransactionUpdateNotificationTarget
{
	public void updatedTransaction(StandardHttpTransaction transaction);
}
