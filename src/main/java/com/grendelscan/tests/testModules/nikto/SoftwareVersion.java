package com.grendelscan.tests.testModules.nikto;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;

import com.grendelscan.data.database.collections.DatabaseBackedList;
import com.grendelscan.data.database.collections.DatabaseBackedMap;
import com.grendelscan.data.findings.Finding;
import com.grendelscan.data.findings.FindingSeverity;
import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.tests.libraries.nikto.CurrentSoftwareVersion;
import com.grendelscan.tests.libraries.nikto.Nikto;
import com.grendelscan.tests.testModuleUtils.TestModuleGUIPath;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByResponseHeaderTest;
import com.grendelscan.utils.HtmlUtils;
import com.grendelscan.utils.URIStringUtils;

/**
 * Based on the logic in nikto_outdated.plugin
 * 
 * @author David Byrne
 * 
 */
public class SoftwareVersion extends TestModule implements ByResponseHeaderTest
{
//	private class OutdatedSoftware
//	{
//		String					header;
//		String					host;
//		String					softwareString;
//		StandardHttpTransaction	transaction;
//		CurrentSoftwareVersion	version;
//
//		public OutdatedSoftware(String host, String header, String softwareString, CurrentSoftwareVersion version,
//				StandardHttpTransaction transaction)
//		{
//			this.transaction = transaction;
//			this.host = host;
//			this.header = header;
//			this.softwareString = softwareString;
//			this.version = version;
//		}
//	}

	private DatabaseBackedList<String>	testedSoftware;

	private DatabaseBackedMap<String, Map<String, Integer>>	serverHeaders;

	public SoftwareVersion()
	{
		Nikto.initialize();
		serverHeaders = new DatabaseBackedMap<String, Map<String, Integer>>("nikto_server_header_locations");
		testedSoftware = new DatabaseBackedList<String>("nikto-tested-software-versions");
	}
	
	@Override
	public String getDescription()
	{
		return "Checks the software versions reported by the web server against " +
				"the current release defined in Nikto (the db_outdated database). " +
				"Some of the logic in retrieving and processing the version " +
				"number is based on logic in nikto_outdated.plugin. " +
				"\n" +
				Nikto.getInstance().getNiktoCredits();
	}

	@Override
	public TestModuleGUIPath getGUIDisplayPath()
	{
		return TestModuleGUIPath.NIKTO;
	}

	@Override
	public String getName()
	{
		return "Nikto version check";
	}



	@Override
	public boolean isExperimental()
	{
		return false;
	}


	private void checkSoftwareVersion(String hostUri, String headerValue) throws InterruptedScanException
	{
		for (String softwareString : splitComponents(headerValue))
		{
			if (testedSoftware.contains(softwareString))
			{
				continue;
			}
			for (CurrentSoftwareVersion version : Nikto.getInstance().getCurrentSoftwareVersions())
			{
				handlePause_isRunning();
				if (version.doesSoftwareNameMatch(softwareString))
				{
					if (version.isOutdated(softwareString))
					{
						StandardHttpTransaction transaction =
								Scan.getInstance().getTransactionRecord().getTransaction(serverHeaders.get(hostUri).get(headerValue));
						logFinding(hostUri, headerValue, softwareString, version, transaction);
					}
					break;
				}
			}
			testedSoftware.add(softwareString);
		}
	}

	private void logFinding(String host, String header, String softwareString, CurrentSoftwareVersion version,
			StandardHttpTransaction transaction)
	{
		String shortDescription = "Outdated server software was detected";
		String longDescription = null;
		try
		{
			longDescription = "Using the Nikto database, an outdated version of software was detected. The details can be found below.<br>\n<br>\n "
					+
						"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Software</b>: "
								+ version.getFriendlySoftwareName()
								+ "<br>\n"
								+
								"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Server</b>: "
								+ URIStringUtils.getHostUri(transaction.getRequestWrapper().getAbsoluteUriString())
								+ "<br>\n"
								+
								"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Nikto message</b>: "
								+ version.getWarningMessage(version
										.extractVersionComponent(softwareString))
								+ "<br>\n"
								+
								"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Transaction</b>: "
								+ HtmlUtils.makeLink(transaction.getSavedUrl(),
										String.valueOf(transaction.getId())) + "<br>\n<br>\n<br>\n" +
					Nikto.getInstance().getNiktoCredits();
		}
		catch (URISyntaxException e)
		{
			IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing", e);
			Log.error(e.toString(), e);
			throw ise;
		}
		String impact = "Outdated software often has security flaws.";
		String recomendations = "Consult the software vendor or project website for the latest version.";
		String references = "";

		Finding event =
				new Finding(null, getName(), FindingSeverity.LOW, "See description", "Outdated software detected",
						shortDescription, longDescription, impact, recomendations, references);
		Scan.getInstance().getFindings().addFinding(event);
	}

	private String[] splitComponents(String header)
	{
		String components[] = {};

		if (header.toLowerCase().contains("apache"))
		{
			components = header.split(" ");
		}
		else if (header.toLowerCase().contains("weblogic"))
		{
			String temp[] = header.split(" ");
			if (temp.length > 1)
			{
				components = new String[1];
				components[0] = temp[0] + "/" + temp[1];
			}
		}
		else if (header.toLowerCase().contains("sitescope"))
		{
			components = new String[1];
			String temp[] = header.split(" ");
			components[0] = temp[0];
		}
		else
		{
			String match;
			// if there are no space
			if (header.indexOf(" ") == -1)
			{
				match = header;
			}
			// if there are spaces and a forward slash
			else if (header.indexOf("/") >= 0)
			{
				match = header.replaceAll("\\s+", "");
			}
			// use the last non (alphanumeric, period and parens) as a delimiter
			else
			{
				String delim = header.replaceAll("[a-zA-Z0-9\\.\\(\\)]+", "");
				// get the last character in the string
				delim = delim.substring(delim.length() - 1);

				// replace the delimiters with spaces and take off the last
				// section
				match = header.replace(delim, " ").replaceFirst(" [^ ]*$", "");
			}
			match = match.replaceFirst("\\s+$", "");
			components = new String[1];
			components[0] = match;
		}
		return components;
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.tests.testTypes.ByResponseHeaderTest#getResponseHeaders()
	 */
	@Override
	public String[] getResponseHeaders()
	{
		String[] headers = { "Server", "X-Powered-By" };
		return headers;
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.tests.testTypes.ByResponseHeaderTest#testByResponseHeader(int, java.lang.String)
	 */
	@Override
	public void testByResponseHeader(int transactionID, String responseHeaderName, int testJobId) throws InterruptedScanException
	{
		StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);

		String hostUri = null;
		try
		{
			hostUri = URIStringUtils.getHostUri(transaction.getRequestWrapper().getAbsoluteUriString());
		}
		catch (URISyntaxException e)
		{
			IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing in Nikto SoftwareVersion", e);
			Log.error(e.toString(), e);
			throw ise;
		}
		Map<String, Integer> headers;
		synchronized(serverHeaders)
		{
			if (serverHeaders.containsKey(hostUri))
			{
				headers = serverHeaders.get(hostUri);
			}
			else
			{
				headers = new HashMap<String, Integer>(1);
			}
	
			for (Header header : transaction.getResponseWrapper().getHeaders().getHeaders(responseHeaderName))
			{
				handlePause_isRunning();
				if (!headers.containsKey(header.getValue()))
				{
					checkSoftwareVersion(hostUri, header.getValue());
					headers.put(header.getValue(), transaction.getId());
				}
			}
	
			serverHeaders.put(hostUri, headers);
		}

	}
}
