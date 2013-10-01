package com.grendelscan.smashers.utils.platformErrorMessages;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.grendelscan.commons.ConfigurationManager;
import com.grendelscan.commons.html.HtmlNodeUtilities;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.data.database.collections.DatabaseBackedMap;
import com.grendelscan.smashers.settings.TextOption;

public class PlatformErrorMessages
{
    private class FuzzPatternXMLParser extends DefaultHandler
    {
        String currentElement;
        PlatformErrorMessageMatchPattern currentPattern;
        String currentString;
        List<PlatformErrorMessageMatchPattern> patterns;

        public FuzzPatternXMLParser(final List<PlatformErrorMessageMatchPattern> patterns)
        {
            this.patterns = patterns;
        }

        @Override
        public void characters(final char[] ch, final int start, final int length) throws SAXException
        {
            String s = new String(ch, start, length);
            currentString += s;

        }

        @Override
        public void endElement(final String uri, final String localName, final String name) throws SAXException
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
        public void startElement(final String uri, final String localName, final String name, final Attributes attributes)
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

    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformErrorMessages.class);

    private static PlatformErrorMessages instance;

    private static TextOption regexErrorPatternsOption;

    public static PlatformErrorMessages getInstance()
    {
        return instance;
    }

    public static TextOption getRegexErrorPatternsOption()
    {
        return regexErrorPatternsOption;
    }

    public static synchronized void initialize()
    {
        if (instance == null)
        {
            instance = new PlatformErrorMessages();
        }
    }

    private final List<PlatformErrorMessageMatchPattern> htmlErrorMatchPatterns;
    private final DatabaseBackedMap<Integer, String> messageHits;

    private final List<Pattern> regexPatterns;

    private PlatformErrorMessages()
    {
        regexPatterns = new ArrayList<Pattern>();
        messageHits = new DatabaseBackedMap<Integer, String>("platform_message_hits");

        regexErrorPatternsOption = new TextOption("Manual regex error patterns", "", "Each line is a seperate regular express to match error messages", true, null);

        createRegexPatterns();
        htmlErrorMatchPatterns = new ArrayList<PlatformErrorMessageMatchPattern>();
        loadFuzzPatterns();
    }

    private void createRegexPatterns()
    {
        synchronized (regexPatterns)
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
                    LOGGER.error("Invalid regex (" + rawPattern + ")in platform error messages: " + e.toString(), e);
                }
            }
        }
    }

    public DatabaseBackedMap<Integer, String> getReadOnlyMessageHits()
    {
        return messageHits;
    }

    /**
     * Will return true if a fuzz pattern or SQL injection error pattern is matched
     * 
     * @param transaction
     * @return
     */
    public String isErrorMatch(final StandardHttpTransaction transaction, final boolean recordHit)
    {
        String fuzzPattern = "";
        boolean match = false;
        if (!(fuzzPattern = isTextErrorMatchPattern(new String(transaction.getResponseWrapper().getBody()))).equals("") || !(fuzzPattern = isTextErrorMatchPattern(transaction.getResponseWrapper().getStrippedResponseText())).equals("")
                        || !(fuzzPattern = isTitleErrorMatchPattern(HtmlNodeUtilities.getTitleText(transaction.getResponseWrapper().getResponseDOM()))).equals("")

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

    private String isTextErrorMatchPattern(final String text)
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

    private String isTitleErrorMatchPattern(final String text)
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
            FileReader reader = new FileReader("conf" + File.separator + ConfigurationManager.getString("fuzzer.html_error_match_pattern_file"));
            xmlReader.parse(new InputSource(reader));
        }
        catch (SAXException e)
        {
            LOGGER.error("Problem loading fuzz match patterns: " + e.toString(), e);
        }
        catch (FileNotFoundException e)
        {
            LOGGER.error("Problem loading fuzz match patterns: " + e.toString(), e);
        }
        catch (IOException e)
        {
            LOGGER.error("Problem loading fuzz match patterns: " + e.toString(), e);
        }
    }

    private String normalizeText(final String text)
    {
        if (text == null)
        {
            return "";
        }
        return text.toLowerCase().replaceAll("\\s+", " ");
    }

    private String testRegexPatterns(final StandardHttpTransaction transaction)
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
