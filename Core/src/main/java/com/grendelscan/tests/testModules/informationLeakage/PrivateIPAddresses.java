package com.grendelscan.tests.testModules.informationLeakage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;

import com.grendelscan.data.database.DataNotFoundException;
import com.grendelscan.data.findings.Finding;
import com.grendelscan.data.findings.FindingSeverity;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.tests.testModuleUtils.TestModuleGUIPath;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByHttpResponseCodeTest;
import com.grendelscan.utils.HtmlUtils;
import com.grendelscan.utils.MimeUtils;
import com.grendelscan.utils.URIStringUtils;

public class PrivateIPAddresses extends TestModule implements ByHttpResponseCodeTest
{
	private Finding finding;
	private static final String FINDING_NAME = "private_ip_address_finding_number";
	private Pattern			privateIPRegEx;

	public PrivateIPAddresses()
	{
		privateIPRegEx =
			Pattern.compile("\\b((?:10(:?\\.\\d{1,3}){3})|(?:192\\.168(?:\\.\\d{1,3}){2})|(?:172\\.[1-3]\\d(?:\\.\\d{1,3}){2}))\\b");
	}
	
	@Override
	public String getDescription()
	{
		return "Looks for private IP addresses (RFC 1918) leaked in web server responses.";
	}

	@Override
	public TestModuleGUIPath getGUIDisplayPath()
	{
		return TestModuleGUIPath.INFORMATION_LEAKAGE;
	}

	@Override
	public String getName()
	{
		return "Private address leakage";
	}

	@Override
	public String[] getResponseCodes()
	{
		return new String[] { "200", "301", "302", "303", "307" };
	}


	@Override
	public boolean isExperimental()
	{
		return false;
	}


	@Override
	public void testByHttpResponseCode(int transactionID, int testJobId) throws InterruptedScanException
	{
		StandardHttpTransaction transaction =
				Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
		Matcher matcher;
		for (Header header : transaction.getResponseWrapper().getHeaders().getReadOnlyHeaders())
		{
			handlePause_isRunning();
			matcher = privateIPRegEx.matcher(header.getValue());
			if (matcher.find() && isPrivateAddress(matcher.group(1)))
			{
				transaction.writeToDisk();
				logFinding(HtmlUtils.makeLink(transaction.getSavedUrl(), transaction
						.getRequestWrapper().getAbsoluteUriString())
						+ ": "
						+ matcher.group(1) + " was found in the header named \"" + header.getName() + "\"", 
						URIStringUtils.getFileUri(transaction.getRequestWrapper().getAbsoluteUriString()));
				break;
			}
		}

		if (MimeUtils.isWebTextMimeType(transaction.getResponseWrapper().getHeaders().getMimeType()))
		{
			String body = new String(transaction.getResponseWrapper().getBody());
			matcher = privateIPRegEx.matcher(body);
			while (matcher.find())
			{
				handlePause_isRunning();
				if (isPrivateAddress(matcher.group(1)))
				{
					transaction.writeToDisk();
					logFinding(HtmlUtils.makeLink(transaction.getSavedUrl(), transaction
							.getRequestWrapper().getAbsoluteUriString())
							+ ": "
							+ matcher.group(1) + " was found in the response body", 
							URIStringUtils.getFileUri(transaction.getRequestWrapper().getAbsoluteUriString()));
					break;
				}
			}
		}
	}


	private boolean isPrivateAddress(String address)
	{
		String sOctets[] = address.split("\\.");
		if (sOctets.length != 4)
		{
			return false;
		}

		int bOctets[] = new int[4];
		for (int index = 0; index < 4; index++)
		{
			bOctets[index] = Integer.valueOf(sOctets[index]);
			if ((bOctets[index] > 255) || (bOctets[index] < 0))
			{
				return false;
			}
		}

		switch (bOctets[0])
		{
			case 10:
				return true;
			case 192:
				if (bOctets[1] == 168)
				{
					return true;
				}
			case 172:
				if ((bOctets[1] >= 16) && (bOctets[1] <= 31))
				{
					return true;
				}
			default:
				return false;
		}
	}

	private synchronized void logFinding(String description, String url)
	{
		try
		{
			if (finding == null)
			{
				finding = Scan.getInstance().getFindings().get(Scan.getInstance().getTestData().getInt(FINDING_NAME));
			}
			finding.setLongDescription(finding.getLongDescription() + description + "<br>\n");
		}
		catch (DataNotFoundException e)
		{
			String title = "Private IP address leakage";
			String location = url;
			String shortDescription = "Possible leakage of an internal IP address has been identified in " + location + ".";
			String longDescription =
					"Private IP addreses defined by RFC 1918 are not routable on the Internet, but are " +
							"frequently used on Internet-exposed devices and then changed by Network Address Translation (NAT).<br><br>" +
							"Private IP addresses were found in the following location(s):<br>\n" +
							description + "<br>\n";

			String impact =
				"Knowning the private IP address of a server can help an attacker to craft better attacks against internal assets.";
			String recomendation = "For HTTP headers, consult the web or application server's " +
							"documentation to disable this behavior. For IP addresses in the " +
							"response body, modify the application's behavior to surpress the " +
							"inclusion of private IP addresses.";

			finding = new Finding(null, "Module00008", FindingSeverity.INFO, url,
					title, shortDescription, longDescription, impact, recomendation,
					HtmlUtils.makeLink("http://www.ietf.org/rfc/rfc1918.txt"));
			
			Scan.getInstance().getTestData().setInt(FINDING_NAME, finding.getId());
			Scan.getInstance().getFindings().addFinding(finding);
		}

	}

}
