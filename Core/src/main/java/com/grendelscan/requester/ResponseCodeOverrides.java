package com.grendelscan.requester;

import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.grendelscan.data.database.collections.DatabaseBackedList;
import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.requester.http.transactions.UnrequestableTransaction;
import com.grendelscan.scan.ConfigurationManager;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.utils.StringUtils;
import com.grendelscan.utils.URIStringUtils;
import com.grendelscan.utils.ResponseCompare.HttpResponseScoreUtils;
import com.grendelscan.utils.ResponseCompare.HttpTransactionMatchCriteriaWeights;

public class ResponseCodeOverrides
{
	private final Map<Pattern, Integer> manualOverrides;
	private final DatabaseBackedList<String> testedDirectories;
	private final DatabaseBackedList<StandardHttpTransaction> automaticOverrideSamples;
	private boolean useAutomaticOverrides;
	
	private int acceptableAutomaticThreshold;
	private int acceptableSamePatternThreshold;
	private HttpTransactionMatchCriteriaWeights weights;
	private final RequestOptions requestOptions;
	
	public ResponseCodeOverrides(Map<String, Integer> rawManualOverrides, 
						boolean useAutomaticOverrides, int acceptableAutomaticThreshold)
	{
		testedDirectories = new DatabaseBackedList<String>("response-code-overrides-tested-directories");
		requestOptions = new RequestOptions();
		requestOptions.reason = "Response code overrides";
		requestOptions.testTransaction = false;
		requestOptions.followRedirects = false;
		
		weights = new HttpTransactionMatchCriteriaWeights();
		this.useAutomaticOverrides = useAutomaticOverrides;
		this.acceptableAutomaticThreshold = acceptableAutomaticThreshold;
		acceptableSamePatternThreshold = ConfigurationManager.getInt("response_code_overrides.same_pattern_threshold", 95);
		
		manualOverrides = Collections.synchronizedMap(new HashMap<Pattern, Integer>(1));
		for (String pattern: rawManualOverrides.keySet())
		{
			manualOverrides.put(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.DOTALL), rawManualOverrides.get(pattern));
		}
		
		if (useAutomaticOverrides)
		{
			automaticOverrideSamples = new DatabaseBackedList<StandardHttpTransaction>("response-code-overrides-samples");
		}
		else
		{
			automaticOverrideSamples = null;
		}
	}
	
	private long totalGenTime = 0;
	private final Object timeLock = new Object();

	private Set<String> generateUris(String baseUri)
	{
		Set <String> uris = Collections.synchronizedSet(new HashSet<String>());
		
		// Make the URI for a directory
		uris.add(baseUri + "random" + StringUtils.generateRandomString(StringUtils.FORMAT_LOWER_CASE_ALPHA, 6) + "/");

		for (String ext: ConfigurationManager.getStringArray("file_enumeration.common_framework_extensions"))
		{
			uris.add(baseUri + "random" + StringUtils.generateRandomString(StringUtils.FORMAT_LOWER_CASE_ALPHA, 6) + "." + ext);
		}
		return uris;
	}
	
	private void testAutomaticOverridesForDirectory(StandardHttpTransaction originalTransaction, String directoryUri, int testJobId) throws InterruptedScanException
	{
		long start = Calendar.getInstance().getTimeInMillis();
		for (String uri: generateUris(directoryUri))
		{
			Scan.getInstance().getTesterQueue().handlePause_isRunning();
			if (Scan.getScanSettings().getUrlFilters().isUriAllowed(uri))
			{
				Log.debug("Testing for logical 404 responses: " + uri);
	            try
	            {
					StandardHttpTransaction transaction = originalTransaction.cloneForSessionReuse(
							TransactionSource.AUTOMATIC_RESPONSE_CODE_OVERRIDES, testJobId);
					transaction.getRequestWrapper().setURI(uri, true);
					long estart = Calendar.getInstance().getTimeInMillis();
					transaction.setRequestOptions(requestOptions);
					transaction.execute();
					long eend = Calendar.getInstance().getTimeInMillis();
					synchronized(timeLock) 
						{totalExecuteTime += eend - estart;}
					if (transaction.isSuccessfullExecution() && transaction.getResponseWrapper().getStatusLine().getStatusCode() != 404)
					{
						long cstart = Calendar.getInstance().getTimeInMillis();
						addSignatureIfNew(transaction);
						long cend = Calendar.getInstance().getTimeInMillis();
						synchronized(timeLock) 
							{totalCompareTime += cend - cstart;}
					}
	            }
				catch (UnrequestableTransaction e)
				{
					Log.warn("Response code override request is not legal: " + e.toString(), e);
				}
            }
		}
		long end = Calendar.getInstance().getTimeInMillis();
		synchronized(timeLock) 
			{totalGenTime += end - start;}
	}

	private long totalCompareTime = 0;
	private long totalExecuteTime = 0;
	
	private synchronized void addSignatureIfNew(StandardHttpTransaction transaction)
	{
		synchronized(automaticOverrideSamples)
		{
			for (StandardHttpTransaction oldTransaction: automaticOverrideSamples)
			{
	//				int score = StringUtils.scoreStringDifferenceIgnoreCase(response, oldResponse, 100);
				int score = HttpResponseScoreUtils.scoreResponseMatch(transaction, oldTransaction, weights, 
						100, Scan.getScanSettings().isParseHtmlDom(), false);
				if (score >= acceptableSamePatternThreshold)
				{
					return;
				}
			}
			automaticOverrideSamples.add(transaction);
		}
	}
	
	
	public int getLogicalResponseCode(StandardHttpTransaction transaction, boolean generateNewProfiles, int testJobId) throws InterruptedScanException
	{
		if (transaction.getResponseWrapper().getStatusLine().getStatusCode() == 404 ||
			! Scan.getScanSettings().getUseAutomaticResponseCodeOverrides())
		{
			return transaction.getResponseWrapper().getStatusLine().getStatusCode();
		}
		
		if (generateNewProfiles)
		{
			String directoryUri;
			try
			{
				directoryUri = URIStringUtils.getDirectoryUri(transaction.getRequestWrapper().getAbsoluteUriString()) + "";
			}
			catch (URISyntaxException e)
			{
				Log.error("Very weird problem parsing a url: " + e.toString(), e);
				throw new IllegalArgumentException(e);
			}
			
			synchronized(testedDirectories)
			{
				if (!testedDirectories.contains(directoryUri))
				{
					testAutomaticOverridesForDirectory(transaction, directoryUri, testJobId);
					testedDirectories.add(directoryUri);
				}
			}
		}
		
		for (Pattern override: manualOverrides.keySet())
		{
			if (override.matcher(new String(transaction.getResponseWrapper().getBody())).find())
			{
				return manualOverrides.get(override);
			}
		}
		
		Set<StandardHttpTransaction> tempSet;
		// Minimize the lock time
		synchronized (automaticOverrideSamples)
		{
			tempSet = new HashSet<StandardHttpTransaction>(automaticOverrideSamples);
		}
		
		for (StandardHttpTransaction sampleTransaction: tempSet)
		{
			int score = HttpResponseScoreUtils.scoreResponseMatch(transaction, sampleTransaction, weights, 
					100, Scan.getScanSettings().isParseHtmlDom(), false);
			if (score >= acceptableAutomaticThreshold)
			{
				return 404;
			}
		}
		
		return transaction.getResponseWrapper().getStatusLine().getStatusCode();
	}


	public long getTotalCompareTime()
	{
		return totalCompareTime;
	}


	public long getTotalExecuteTime()
	{
		return totalExecuteTime;
	}

	public long getTotalGenTime()
	{
		return totalGenTime;
	}
	
}
