package com.grendelscan.tests.libraries.platformErrorMessages;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.grendelscan.data.database.collections.DatabaseBackedMap;
import com.grendelscan.html.HtmlNodeUtilities;
import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.ConfigurationManager;
import com.grendelscan.tests.testModuleUtils.settings.TextOption;

public class PlatformErrorMessages
{
	private class FuzzPatternXMLParser extends DefaultHandler
	{
		String									currentElement;
		PlatformErrorMessageMatchPattern		currentPattern;
		String									currentString;
		List<PlatformErrorMessageMatchPattern>	patterns;

		public FuzzPatternXMLParser(List<PlatformErrorMessageMatchPattern> patterns)
		{
			this.patterns = patterns;
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException
		{
			String s = new String(ch, start, length);
			currentString += s;

		}

		@Override
		public void endElement(String uri, String localName, String name) throws SAXException
		{
			if (currentElement.equals("textsearchstring"))
			{
				currentPattern.addTextPattern(currentString);
			}
			else if (currentElement.equals("titlesearchstring"))
			{
				currentPattern.addTitlePattern(currentString);
			}
			currentElement = "";
		}

		@Override
		public void startElement(String uri, String localName, String name, Attributes attributes)
		{
			if (name.equalsIgnoreCase("pattern"))
			{
				currentPattern = new PlatformErrorMessageMatchPattern();
				patterns.add(currentPattern);
			}
			currentElement = name.toLowerCase();
			currentString = "";
		}
	}

	private static PlatformErrorMessages	instance;

	public static PlatformErrorMessages getInstance()
	{
		return instance;
	}

	public static synchronized void initialize()
	{
		if (instance == null)
		{
			instance = new PlatformErrorMessages();
		}
	}

	private List<PlatformErrorMessageMatchPattern>	htmlErrorMatchPatterns;
	private DatabaseBackedMap<Integer, String>		messageHits;
	private static TextOption								regexErrorPatternsOption;
	private List<Pattern>							regexPatterns;

	private PlatformErrorMessages()
	{
		regexPatterns = new ArrayList<Pattern>();
		messageHits = new DatabaseBackedMap<Integer, String>("platform_message_hits");

		regexErrorPatternsOption =
				new TextOption("Manual regex error patterns", "",
						"Each line is a seperate regular express to match error messages", true, null);

		createRegexPatterns();
		htmlErrorMatchPatterns = new ArrayList<PlatformErrorMessageMatchPattern>();
		loadFuzzPatterns();
	}

	public DatabaseBackedMap<Integer, String> getReadOnlyMessageHits()
	{
		return messageHits;
	}

	public static TextOption getRegexErrorPatternsOption()
	{
		return regexErrorPatternsOption;
	}

	/**
	 * Will return true if a fuzz pattern or SQL injection error pattern is
	 * matched
	 * 
	 * @param transaction
	 * @return
	 */
	public String isErrorMatch(StandardHttpTransaction transaction, boolean recordHit)
	{
		String fuzzPattern = "";
		boolean match = false;
		if (!(fuzzPattern = isTextErrorMatchPattern(new String(transaction.getResponseWrapper().getBody()))).equals("")
				|| !(fuzzPattern = isTextErrorMatchPattern(transaction.getResponseWrapper().getStrippedResponseText())).equals("")
				|| !(fuzzPattern =
						isTitleErrorMatchPattern(HtmlNodeUtilities.getTitleText(transaction.getResponseWrapper().getResponseDOM())))
						.equals("")

		)
		{
			match = true;
		}
		// if (!match)
		// {
		// String sqlType;
		// if ((sqlType =
		// SQLInjection.findSQLErrorMessages(transaction.getStrippedResponseText()))
		// != null)
		// {
		// fuzzPattern = "SQL error (" + sqlType + ")";
		// match = true;
		// }
		// }
		if (!match && !(fuzzPattern = testRegexPatterns(transaction)).equals(""))
		{
			match = true;
		}

		if (match && recordHit)
		{
			messageHits.put(transaction.getId(), fuzzPattern);
		}
		return fuzzPattern;
	}

	private void createRegexPatterns()
	{
		synchronized(regexPatterns)
		{
			regexPatterns.clear();
			for (String rawPattern : regexErrorPatternsOption.getValue().split("[\r\n]+"))
			{
				// skip any "pattern" that is empty, or only white space
				if (rawPattern.matches("\\s*"))
				{
					continue;
				}
				try
				{
					regexPatterns.add(Pattern.compile(rawPattern, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE));
				}
				catch (PatternSyntaxException e)
				{
					Log.error("Invalid regex (" + rawPattern + ")in platform error messages: " + e.toString(), e);
				}
			}
		}
	}

	private String isTextErrorMatchPattern(String text)
	{
		String fuzzString = "";

		String lText = normalizeText(text);

		for (PlatformErrorMessageMatchPattern fuzzPattern : htmlErrorMatchPatterns)
		{
			if (!(fuzzString = fuzzPattern.isTextMatch(lText)).equals(""))
			{
				break;
			}
		}

		return fuzzString;
	}

	private String isTitleErrorMatchPattern(String text)
	{
		String fuzzString = "";

		String lText = normalizeText(text);

		for (PlatformErrorMessageMatchPattern fuzzPattern : htmlErrorMatchPatterns)
		{
			if (!(fuzzString = fuzzPattern.isTitleMatch(lText)).equals(""))
			{
				break;
			}
		}

		return fuzzString;
	}

	private void loadFuzzPatterns()
	{
		XMLReader xmlReader;
		try
		{
			xmlReader = XMLReaderFactory.createXMLReader();
			FuzzPatternXMLParser handler = new FuzzPatternXMLParser(htmlErrorMatchPatterns);
			xmlReader.setContentHandler(handler);
			xmlReader.setErrorHandler(handler);
			FileReader reader =
					new FileReader("conf" + File.separator
							+ ConfigurationManager.getString("fuzzer.html_error_match_pattern_file"));
			xmlReader.parse(new InputSource(reader));
		}
		catch (SAXException e)
		{
			Log.error("Problem loading fuzz match patterns: " + e.toString(), e);
		}
		catch (FileNotFoundException e)
		{
			Log.error("Problem loading fuzz match patterns: " + e.toString(), e);
		}
		catch (IOException e)
		{
			Log.error("Problem loading fuzz match patterns: " + e.toString(), e);
		}
	}

	private String normalizeText(String text)
	{
		if (text == null)
		{
			return "";
		}
		return text.toLowerCase().replaceAll("\\s+", " ");
	}

	private String testRegexPatterns(StandardHttpTransaction transaction)
	{
		String patternText = "";
		String rawBody = normalizeText(new String(transaction.getResponseWrapper().getBody()));
		String textResponse = normalizeText(transaction.getResponseWrapper().getStrippedResponseText());

		for (Pattern pattern : regexPatterns)
		{
			if (pattern.matcher(textResponse).find() || pattern.matcher(rawBody).find())
			{
				patternText = pattern.pattern();
				break;
			}
		}
		return patternText;
	}
}
