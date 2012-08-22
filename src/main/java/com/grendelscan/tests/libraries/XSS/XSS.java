package com.grendelscan.tests.libraries.XSS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cobra_grendel.html.domimpl.HTMLDocumentImpl;
import org.w3c.dom.Node;

import com.grendelscan.html.EventExecutorTerminator;
import com.grendelscan.html.EventHandlerExecutor;
import com.grendelscan.logging.Log;
import com.grendelscan.requester.TransactionSource;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.requester.http.transactions.UnrequestableTransaction;
import com.grendelscan.scan.ConfigurationManager;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.tests.libraries.TokenTesting.HtmlContext;
import com.grendelscan.tests.libraries.TokenTesting.TokenContext;
import com.grendelscan.tests.libraries.TokenTesting.TokenContextType;
import com.grendelscan.tests.libraries.TokenTesting.TokenTesting;
import com.grendelscan.tests.testModuleUtils.settings.SelectableOption;
import com.grendelscan.tests.testModuleUtils.settings.SingleSelectOptionGroup;
import com.grendelscan.utils.StringUtils;

public class XSS
{

	static private List<String>	advancedBaseAttacks;
	static private String		fakeHostname;
	static private String		fakeIPAddress;
	static private boolean		initialized	= false;
	static private String		javascriptMethod;
	static private List<String>	mediumBaseAttacks;
	static private List<String>	simpleBaseAttacks;
	static private SingleSelectOptionGroup agressionOptionsGroup;
	static private SelectableOption lowOption;
	static private SelectableOption mediumOption;
	static private SelectableOption highOption;
	
	
	public static synchronized SingleSelectOptionGroup getAgressionOptions()
	{
		if (agressionOptionsGroup == null)
		{
			agressionOptionsGroup =
					new SingleSelectOptionGroup("XSS testing aggression", "Select the aggression levels for testing XSS.", null);
	
			lowOption = new SelectableOption("Low", false, "", null);
			agressionOptionsGroup.addOption(lowOption);
	
			mediumOption = new SelectableOption("Medium", true, "", null);
			agressionOptionsGroup.addOption(mediumOption);
	
			highOption = new SelectableOption("High", false, "", null);
			agressionOptionsGroup.addOption(highOption);
		}
		return agressionOptionsGroup;
	}
	
	public static XSSAggression getAggression()
	{
		if (lowOption.isSelected())
			return XSSAggression.LOW;

		if (mediumOption.isSelected())
			return XSSAggression.MEDIUM;

		return XSSAggression.HIGH;
	}

	public static void checkForBadChars(List<String> attackStrings, Character badChars[])
	{
		List<String> tmpAttacks = new ArrayList<String>(attackStrings);
		for (String attackString : tmpAttacks)
		{
			for (char badChar : badChars)
			{
				if (attackString.indexOf(badChar) >= 0)
				{
					attackStrings.remove(attackString);
					break;
				}
			}
		}
	}


	public static boolean checkSuccessfulXSSExecution(StandardHttpTransaction transaction)
	{
		boolean success = false;
		HTMLDocumentImpl document = (HTMLDocumentImpl) transaction.runDOM();
		if (document != null)
		{
			if (!(success = checkDOMXSS(document)))
			{
				EventExecutorTerminator test = new EventExecutorTerminator()
				{
					@Override
					public boolean stopExecution(Node node)
					{
						return checkDOMXSS((HTMLDocumentImpl) node.getOwnerDocument());
					}
				};

				try
				{
					EventHandlerExecutor.executeEvents(document, test);
				}
				catch (Exception e) // Needed to handle JavaScript exceptions
				{
					Log.error("Problem executing JavaScript for XSS detection: " + e.toString(), e);
				}
				success = checkDOMXSS(document);
			}
		}
		return success;
	}

	public static ArrayList<String> craftAdvancedXSSAttacks(TokenContextType htmlContext)
	{
		initializeLibrary();
		ArrayList<String> attacks = new ArrayList<String>();
		// We add in all of the simple & medium ones because we will try some
		// evasion with them
		attacks.addAll(craftSimpleEscapeAttacks(htmlContext, advancedBaseAttacks));
		attacks.addAll(craftMediumEscapeAttacks(htmlContext, advancedBaseAttacks));
		attacks.addAll(advancedBaseAttacks);

		ArrayList<String> tmpAttacks = new ArrayList<String>(attacks);
		for (String attack : tmpAttacks)
		{
			if (attack.contains(" "))
			{
				attacks.add(attack.replace(' ', '\t'));
				attacks.add(attack.replace(' ', '\b'));
				attacks.add(attack.replace(' ', '\n'));
				attacks.add(attack.replace(' ', '\r'));
			}
		}
		return attacks;
	}

	public static ArrayList<String> craftMediumXSSAttacks(TokenContextType htmlContext)
	{
		initializeLibrary();
		return craftMediumEscapeAttacks(htmlContext, mediumBaseAttacks);
	}


	public static SuccessfullXSS testHtmlContexts(Collection<TokenContext> contexts, String sourceName, int testJobId) throws InterruptedScanException
	{
		HtmlContext firstHtmlContext = null;
		Set<TokenContextType> types = new HashSet<TokenContextType>();
		Set<String> quotes = new HashSet<String>(1);
		for(TokenContext context: contexts)
		{
			HtmlContext hc = (HtmlContext) context;
			if (firstHtmlContext == null)
			{
				firstHtmlContext = hc;
			}
			types.add(hc.getContextType());
			quotes.addAll(Arrays.asList(hc.getPossibleQuoteCharacters()));
		}
		
		Character badChars[] = XSS.getBlockedCharacters(firstHtmlContext, sourceName, testJobId);
		for(TokenContextType type: types)
		{
			SuccessfullXSS result = XSS.testHtmlContext(firstHtmlContext, sourceName, XSS.getAggression(), type, badChars, quotes, testJobId);
			if (result != null)
			{
				return result;
			}
		}
		return null;
	}

	public static SuccessfullXSS testHtmlContext(HtmlContext context, String sourceName, 
			XSSAggression aggression, TokenContextType type, Character badChars[],
			Set<String> quotes, int testJobId) throws InterruptedScanException
	{
		ArrayList<String> tests = new ArrayList<String>();
		tests.addAll(XSS.craftSimpleXSSAttacks(type));

		
		if (aggression.equals(XSSAggression.HIGH) || aggression.equals(XSSAggression.MEDIUM))
		{
			tests.addAll(XSS.craftMediumXSSAttacks(type));
		}
		if (aggression.equals(XSSAggression.HIGH) )
		{
			tests.addAll(XSS.craftAdvancedXSSAttacks(type));
		}
		
		return runTests(tests, context, badChars, quotes, sourceName, testJobId);
	}
	
	private static SuccessfullXSS runTests(List<String> attackStrings, HtmlContext htmlContext,
			Character badChars[], Collection<String> quotes, String sourceName, int testJobId) throws InterruptedScanException
	{
	
		for (String rawAttackString : attackStrings)
		{
			String newToken = TokenTesting.getInstance().generateToken();
			for (String processedAttackString : XSS.updateAttackStringTokens(rawAttackString, newToken, quotes, badChars))
			{
				Scan.getInstance().getTesterQueue().handlePause_isRunning();
				try
				{
					StandardHttpTransaction transactions[] =
							TokenTesting.getInstance().duplicateTokenTest(htmlContext, processedAttackString,
									sourceName, TransactionSource.MISC_TEST, testJobId);
					StandardHttpTransaction testOutputTransaction = transactions[1];
	
					Scan.getInstance().getTesterQueue().handlePause_isRunning();
					if (XSS.checkSuccessfulXSSExecution(testOutputTransaction))
					{
						SuccessfullXSS result = new SuccessfullXSS();
						result.attackString = processedAttackString;
						result.context = htmlContext;
						result.token = newToken;
						result.transaction = testOutputTransaction;
						return result;
					}
				}
				catch (UnrequestableTransaction e)
				{
					Log.error(sourceName + " duplicateTokenTest transaction unrequestable: " + e.toString(), e);
				}
			}
		}
	
		return null;
	}


	public static ArrayList<String> craftSimpleXSSAttacks(TokenContextType htmlContext)
	{
		initializeLibrary();
		return craftSimpleEscapeAttacks(htmlContext, simpleBaseAttacks);
	}

	public static Character[] getBlockedCharacters(HtmlContext htmlContext, String source, int testJobId) throws InterruptedScanException
	{
		List<Character> badChars = new ArrayList<Character>(1);
		char testChars[][] = new char[][] {
									new char[] { ' ' },
									new char[] { '&' },
									new char[] { ';' },
									new char[] { '<' },
									new char[] { '(' },
									new char[] { '"' },
									new char[] { '\'' },
									new char[] { ',' },
									new char[] { '-' },
									new char[] { '>' },
									new char[] { ')' },
		// new char[]{'<', '(', '"', '\'', ',', '-'},
		// new char[]{'>', ')'
				};
		for (char[] test : testChars)
		{
			String startToken = TokenTesting.getInstance().generateToken();
			String endToken = TokenTesting.getInstance().generateToken();
			StandardHttpTransaction outputTransaction;
			try
			{
				outputTransaction =
						TokenTesting.getInstance().duplicateTokenTest(
								htmlContext,
								startToken + String.copyValueOf(test) + endToken, 
								source, TransactionSource.MISC_TEST, testJobId)[1];
				Pattern blockedCharactersPattern =
						Pattern.compile(startToken + "(.*?)" + endToken, Pattern.CASE_INSENSITIVE);
				Matcher m =
						blockedCharactersPattern.matcher(new String(outputTransaction.getResponseWrapper().getBody()));
				Set<Character> goodChars = new HashSet<Character>(1);
				while (m.find())
				{
					String middle = m.group(1);
					if (!middle.equals(""))
					{
						if (test.length == 1)
						{
							if ((middle.length() == 1) && (middle.charAt(0) == test[0]))
							{
								goodChars.add(test[0]);
							}
						}
						else
						{
							for (char character : test)
							{
								if (middle.indexOf(character) >= 0)
								{
									goodChars.add(character);
								}
							}
						}
					}
				}
				for (char c : test)
				{
					if (!goodChars.contains(c))
					{
						badChars.add(c);
					}
				}
			}
			catch (UnrequestableTransaction e)
			{
				Log.warn("XSS.getBlockedCharacters request unrequestable: " + e.toString());
			}
		}

		return badChars.toArray(new Character[0]);
	}

	public static String getFakeHostname()
	{
		initializeLibrary();
		return fakeHostname;
	}

	public static String getFakeIPAddress()
	{
		initializeLibrary();
		return fakeIPAddress;
	}

	public static String getJavascriptMethod()
	{
		initializeLibrary();
		return javascriptMethod;
	}


	public static String getXSSImpact()
	{
		return "Cross site scripting can (XSS) is a vulnerability that allows an attacker to insert arbitrary web content "
				+ "(HTML, JavaScript, etc) into an otherwise legitimate page. Because the URL is still on the targeted website, a user "
				+ "may consider it to be a trusted link. This can be leveraged to perform many attacks, such as: <br>"
				+ "<ul><li>Reformatting the page to appear as a login page, but with the credentials sent to the attacker</li>"
				+ "<li>Sending the user’s session key to the attacker, allowing for session hijacking</li>"
				+ "<li>Forcing the user’s browser to send attacks to other websites</li>"
				+ "<li>Logging the user’s keystrokes</li></ul>";
	}


	public static String getXSSRecomendations()
	{
		return "Before using any data (stored or user-supplied) to generate web page content intended to be "
				+ "simple text, escape all HTML meta characters. Usually, the characters listed below are the only "
				+ "ones that need to be escaped. This can be done by converting them to “&amp;#nn;” (ignore the quotes), "
				+ "where “nn” is the hexadecimal ASCII character number. <br>" + "<center><table border=0>" + "<tr>"
				+ "<td><b>Character</b></td>" + "<td><b>Encoding</b></td>" + "<td> </td>" + "<td><b>Character</b></td>"
				+ "<td><b>Encoding</b></td>" + "</tr>" + "<tr>" + "<td>&lt;</td>" + "<td>&amp;lt; or &amp;#60;</td>"
				+ "<td> </td>" + "<td>)</td>" + "<td>&amp;#41;</td>" + "</tr>" +

				"<tr>" + "<td>&gt;</td>" + "<td>&amp;gt; or &amp;#60;</td>" + "<td> </td>" + "<td>#</td>"
				+ "<td>&amp;#35;</td>" + "</tr>" +

				"<tr>" + "<td>&amp;</td>" + "<td>&amp;amp; or &amp;#38;</td>" + "<td> </td>" + "<td>%</td>"
				+ "<td>&amp;#37;</td>" + "</tr>" +

				"<tr>" + "<td>&quot;</td>" + "<td>&amp;quot; or &amp;#34;</td>" + "<td> </td>" + "<td>;</td>"
				+ "<td>&amp;#59;</td>" + "</tr>" +

				"<tr>" + "<td>'</td>" + "<td>&amp;#39;</td>" + "<td> </td>" + "<td>+</td>" + "<td>&amp;#43;</td>"
				+ "</tr>" +

				"<tr>" + "<td>(</td>" + "<td>&amp;#40;</td>" + "<td> </td>" + "<td>-</td>" + "<td>&amp;#45;</td>"
				+ "</tr></table>";
	}

	public static String getXSSReferences()
	{
		return "http://www.owasp.org/index.php/Cross_Site_Scripting";
	}

	/**
	 * Replaces various tokens in an XSS attack string with actual data
	 * 
	 * @param attack
	 * @param token
	 * @param context
	 * @return
	 */
	public static ArrayList<String> updateAttackStringTokens(String attack, String token, Collection<String> quotes,
			Character badChars[])
	{
		initializeLibrary();
		boolean added = false;
		ArrayList<String> attacks = new ArrayList<String>();

		String newAttack = attack.replaceAll("%%token%%", token);
		newAttack =
				newAttack.replaceAll("%%decimal_comma_token%%", StringUtils.completeEncode(token,
						StringUtils.FORMAT_DECIMAL_COMMA_SEPERATED));

		if (newAttack.contains("%%html_encoded_method_token%%"))
		{
			String htmlEncodedBaseAttack =
					newAttack.replaceAll("%%html_encoded_method_token%%", StringUtils.completeEncode(
							javascriptMethod + "(" + token + ")", StringUtils.FORMAT_HTML_HEX_ENCODED));
			attacks.add(htmlEncodedBaseAttack);

			htmlEncodedBaseAttack =
					newAttack.replaceAll("%%html_encoded_method_token%%", StringUtils.completeEncode(
							javascriptMethod + "(" + token + ")", StringUtils.FORMAT_HTML_DECIMAL_ENCODED));
			attacks.add(htmlEncodedBaseAttack);

			htmlEncodedBaseAttack =
					newAttack.replaceAll("%%html_encoded_method_token%%", StringUtils
							.completeEncode(javascriptMethod + "(" + token + ")",
									StringUtils.FORMAT_HTML_DOUBLE_PADDED_HEX_ENCODED));
			attacks.add(htmlEncodedBaseAttack);
			added = true;
		}

		if (newAttack.contains("%%html_encoded_script_path_token%%"))
		{
			String htmlEncodedBaseAttack =
					newAttack.replaceAll("%%html_encoded_script_path_token%%", StringUtils.completeEncode("http://"
							+ fakeHostname + "/" + token, StringUtils.FORMAT_HTML_HEX_ENCODED));
			attacks.add(htmlEncodedBaseAttack);

			htmlEncodedBaseAttack =
					newAttack.replaceAll("%%html_encoded_script_path_token%%", StringUtils.completeEncode("http://"
							+ fakeHostname + "/" + token, StringUtils.FORMAT_HTML_DECIMAL_ENCODED));
			attacks.add(htmlEncodedBaseAttack);

			htmlEncodedBaseAttack =
					newAttack.replaceAll("%%html_encoded_script_path_token%%", StringUtils.completeEncode("http://"
							+ fakeHostname + "/" + token, StringUtils.FORMAT_HTML_DOUBLE_PADDED_HEX_ENCODED));
			attacks.add(htmlEncodedBaseAttack);
			added = true;
		}

		if (!added)
		{
			attacks.add(newAttack);
		}

		ArrayList<String> tempAttacks = new ArrayList<String>(attacks);
		for (String tempAttack : tempAttacks)
		{
			if (tempAttack.contains("%%quote%%"))
			{
				attacks.remove(tempAttack);
				for (String quote : quotes)
				{
					attacks.add(tempAttack.replace("%%quote%%", quote));
				}
			}
		}

		checkForBadChars(attacks, badChars);

		return attacks;
	}


	static boolean checkDOMXSS(HTMLDocumentImpl document)
	{
		boolean success = false;
		if ((document.getXssToken() != null) && !document.getXssToken().equals(""))
		{
			success = true;
		}
		return success;
	}

	private static ArrayList<String> craftMediumEscapeAttacks(TokenContextType htmlContext,
			List<String> attacksTemplates)
	{
		ArrayList<String> attacks = new ArrayList<String>();
		for (String baseAttack : attacksTemplates)
		{
			switch (htmlContext)
			{
				case HTML_COMMENT:
					attacks.add("-->" + baseAttack);
					break;
				case HTML_TITLE:
					attacks.add("</title>" + baseAttack);
					break;
				case HTML_SCRIPT:
					attacks.add("</script>" + baseAttack);
					// There is intentionally no "break" here
				case HTML_EVENT_HANDLER:
					attacks.add(";" + javascriptMethod + "(%%token%%);");
					attacks.add("%%quote%%);" + javascriptMethod + "(%%token%%);");
					attacks.add("%%quote%%;" + javascriptMethod + "(%%token%%);");
					attacks.add("%%quote%%onclick=" + javascriptMethod + "(%%token%%) ");
					attacks.add(" onclick=" + javascriptMethod + "(%%token%%) ");
					// There is intentionally no "break" here
					if (htmlContext == TokenContextType.HTML_SCRIPT)
					{
						break;
					}
				case HTML_TAG_ATTRIBUTE_VALUE:
					attacks.add("%%quote%%>" + baseAttack);
					break;
				case HTML_STYLE:
					attacks.add("</style>" + baseAttack);
					break;
				case HTML_TAG_ATTRIBUTE_NAME:
					attacks.add(" onclick=" + javascriptMethod + "(%%token%%) ");
					// There is intentionally no "break" here
				case HTML_TAG_NAME:
					attacks.add(">" + baseAttack);
					break;
				case HTML_TEXTAREA:
					attacks.add("</textarea>" + baseAttack);
					break;
				case HTML_PRE:
					attacks.add("</pre>" + baseAttack);
					break;
				default:
					attacks.add(baseAttack);
			}
		}
		return attacks;
	}

	private static ArrayList<String> craftSimpleEscapeAttacks(TokenContextType htmlContext,
			List<String> attacksTemplates)
	{
		ArrayList<String> attacks = new ArrayList<String>();
		for (String baseAttack : attacksTemplates)
		{
			switch (htmlContext)
			{
				case HTML_COMMENT:
					attacks.add("-->" + baseAttack);
					break;
				case HTML_TITLE:
					attacks.add("</title>" + baseAttack);
					break;
				case HTML_TAG_ATTRIBUTE_VALUE:
				case HTML_EVENT_HANDLER:
					attacks.add("%%quote%%>" + baseAttack);
					break;
				case HTML_SCRIPT:
					attacks.add("</script>" + baseAttack);
					break;
				case HTML_STYLE:
					attacks.add("</style>" + baseAttack);
					break;
				case HTML_TAG_ATTRIBUTE_NAME:
				case HTML_TAG_NAME:
					attacks.add(">" + baseAttack);
					break;
				case HTML_TEXTAREA:
					attacks.add("</textarea>" + baseAttack);
					break;
				case HTML_PRE:
					attacks.add("</pre>" + baseAttack);
					break;
				default:
					attacks.add(baseAttack);
			}
		}
		return attacks;
	}

	@SuppressWarnings("unchecked")
	private synchronized static void initializeLibrary()
	{
		if (!initialized)
		{
			initialized = true;
			simpleBaseAttacks = ConfigurationManager.getList("xss.simple_base_attacks");
			mediumBaseAttacks = ConfigurationManager.getList("xss.medium_base_attacks");
			advancedBaseAttacks = ConfigurationManager.getList("xss.advanced_base_attacks");
			fakeIPAddress = ConfigurationManager.getString("xss.fake_ip_address");
			fakeHostname = ConfigurationManager.getString("xss.fake_hostname");
			javascriptMethod = ConfigurationManager.getString("xss.javascript_method");
		}
	}
}
