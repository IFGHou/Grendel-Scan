package com.grendelscan.testing.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.scan.Scan;
import com.grendelscan.testing.modules.AbstractTestModule;

public abstract class TransactionTestJob extends TestJob
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionTestJob.class);
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	protected int	transactionID;

	public TransactionTestJob(Class<? extends AbstractTestModule> moduleClass, int transactionID)
	{
		super(moduleClass);
		this.transactionID = transactionID;
		if (!Scan.getInstance().getTransactionRecord().getTransaction(transactionID).isSuccessfullExecution())
		{
			LOGGER.warn("What???");
		}
	}

	public final int getTransactionID()
	{
		return transactionID;
	}

	public final void setTransactionID(int transactionID)
	{
		this.transactionID = transactionID;
	}

}
