package com.grendelscan.fuzzing;

import java.net.URISyntaxException;
import java.util.List;

import com.grendelscan.GUI.MainWindow;
import com.grendelscan.GUI.fuzzing.FuzzerComposite;
import com.grendelscan.GUI.http.transactionDisplay.HttpFormatException;
import com.grendelscan.GUI.http.transactionTable.TransactionSummaryProvider;
import com.grendelscan.GUI.proxy.interception.InterceptFilter;
import com.grendelscan.GUI.proxy.interception.StandardInterceptFilter;
import com.grendelscan.logging.Log;
import com.grendelscan.requester.RequestOptions;
import com.grendelscan.requester.TransactionSource;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.requester.http.transactions.UnrequestableTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.libraries.platformErrorMessages.PlatformErrorMessages;
import com.grendelscan.utils.HttpUtils;
import com.grendelscan.utils.StringUtils;

public class Fuzzer
{
	private String template;
	FuzzVector fuzzVector;
	int maxRequests;
	int requestCount;
	String unusedFuzzValue;
	public static final String FUZZ_TOKEN = "%%FUZZ%%";
	private static Thread fuzzThread;
	boolean running;
	List<InterceptFilter> filters;
	private boolean paused;
	final RequestOptions fuzzRequestOptions;
	final FuzzerComposite fuzzerComposite;
	TransactionSummaryProvider fuzzResults;
	
	public void setPaused(boolean paused)
    {
    	this.paused = paused;
    }

	public boolean isPaused()
    {
    	return paused;
    }

	boolean matchPlatformMessages;
	
	public Fuzzer(FuzzVector fuzzVector, int maxRequests, String template, List<InterceptFilter> filters, 
			boolean matchPlatformMessages, FuzzerComposite fuzzerComposite)
    {
	    super();
	    fuzzResults = new TransactionSummaryProvider(null);
	    this.fuzzerComposite = fuzzerComposite;
	    this.filters = filters;
	    this.fuzzVector = fuzzVector;
	    this.maxRequests = maxRequests;
	    if (this.maxRequests == 0)
	    {
	    	this.maxRequests = Integer.MAX_VALUE;
	    }
	    this.maxRequests = maxRequests;
	    this.template = template;
	    this.matchPlatformMessages = matchPlatformMessages;
	    fuzzRequestOptions = new RequestOptions();
	    fuzzRequestOptions.testTransaction = false;
	    fuzzRequestOptions.reason = "Fuzzer";
//	    fuzzRequestOptions.onlyUriIfNew = false;
    }

	public boolean isRunning()
    {
    	return running;
    }

	public void stop()
	{
		running = false;
		if (fuzzThread != null)
		{
//			synchronized(this)
			{
				try
	            {
					Thread.sleep(1500);
	            }
	            catch (InterruptedException e)
	            {
	            	//Stop waiting, just kill it
	            }
			}
			fuzzThread.interrupt();
		}
	}
	
	
	public void reset()
	{
		requestCount = 0;
		fuzzVector.reset();
	}
	
	public void start()
	{
		if (fuzzThread != null && fuzzThread.isAlive())
		{
			MainWindow.getInstance().displayMessage("Error", "Fuzzing already underway", false);
		}
		else
		{
			running = true;
			fuzzThread = new Thread(new fuzzRunable());
			fuzzThread.setName("Fuzzer thread");
			fuzzThread.start();
		}
	}

	class fuzzRunable implements Runnable
	{
		@Override
		public void run()
        {
			fuzzVector.reset();
	        while(!fuzzVector.done() && running && requestCount < maxRequests)
	        {
	        	checkPause();	
	        	StandardHttpTransaction transaction;
				try
				{
					transaction = getNextTransaction();
				}
				catch (URISyntaxException e)
				{
					IllegalStateException ise = new IllegalStateException("Invalid template URI", e);
					Log.error(e.toString(), e);
					throw ise;
				}
	        	// Could be null if the request is unparsable
	        	if (transaction != null)
	        	{
		        	try
					{
		        		transaction.setRequestOptions(fuzzRequestOptions);
		        		transaction.execute();
			        	checkPause();	
			        	unusedFuzzValue = "";
			        	requestCount++;
			        	if (StandardInterceptFilter.matchesFilters(filters, transaction) || 
			        		(
			        			matchPlatformMessages
			        			&& PlatformErrorMessages.getInstance().isErrorMatch(transaction, true).length() > 0)
			        		)
			        	{
			        		fuzzResults.addOrUpdateTransaction(transaction);
			        	}
					}
					catch (UnrequestableTransaction e)
					{
						Log.warn("Fuzz request not sendable: " + e.toString(), e);
					}
					catch (InterruptedScanException e)
					{
						Log.info("Scan aborted: " + e.toString(), e);
						return;
					}
	        	}
	        }
	        fuzzerComposite.fuzzDone();
        }
	}
	
	void checkPause()
	{
//		synchronized(fuzzThread)
		{
			try
            {
				while (paused && running)
				{
					Thread.sleep(250);
				}
            }
            catch (InterruptedException e)
            {
            	//Done checking for pause. Handle the probably stop elsewhere
            }
		}
	}
	
	StandardHttpTransaction getNextTransaction() throws URISyntaxException
	{
		StandardHttpTransaction transaction = null;
		if (!fuzzVector.done())
		{
			String value; 
			if (unusedFuzzValue == null || unusedFuzzValue.equals(""))
			{
				value = fuzzVector.getNextValue();
				unusedFuzzValue = value;
			}
			else
			{
				value = unusedFuzzValue;
			}
			if (value != null)
			{
				String rawRequest = template.replace(FUZZ_TOKEN, value);
                try
                {
                	transaction = HttpUtils.parseIntoHttpRequest(TransactionSource.FUZZER, rawRequest.getBytes(StringUtils.getDefaultCharset()), -1);
                }
                catch (HttpFormatException e)
                {
                	Log.error("Invalid request format in fuzzer: " + e.toString(), e);
                }
			}
		}
		return transaction;
	}

	public final TransactionSummaryProvider getFuzzResults()
	{
		return fuzzResults;
	}
}