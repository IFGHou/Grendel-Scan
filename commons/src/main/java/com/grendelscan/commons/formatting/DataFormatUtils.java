/**
 * 
 */
package com.grendelscan.commons.formatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.NotImplementedException;

import com.grendelscan.commons.MimeUtils;
import com.grendelscan.commons.formatting.encoding.HexEncodingUtils;
import com.grendelscan.commons.formatting.encoding.HtmlEncodingUtils;
import com.grendelscan.commons.formatting.encoding.UrlEncodingUtils;

/**
 * @author david
 * 
 */
public class DataFormatUtils
{

    private final static Pattern HEX_UPPER_PATTERN = Pattern.compile("^(?:[A-F0-9]{2}){2,}$");
    private final static Pattern HEX_LOWER_PATTERN = Pattern.compile("^(?:[a-f0-9]{2}){2,}$");
    private final static Pattern HEX_LOWER_0X_PREFIX_PATTERN = Pattern.compile("^0[xX](?:[a-f0-9]{2})+$");
    private final static Pattern HEX_UPPER_0X_PREFIX_PATTERN = Pattern.compile("^0[xX](?:[A-F0-9]{2})+$");
    private final static Pattern HEX_LOWER_SLASH_X_PATTERN = Pattern.compile("^(?:\\\\[xX][a-f0-9]{2})+$");
    private final static Pattern HEX_UPPER_SLASH_X_PATTERN = Pattern.compile("^(?:\\\\[xX][A-F0-9]{2})+$");

    private final static Pattern URL_ENCODED_QUERY_PATTERN = Pattern.compile("^[^\\x00-\\x20\\x7f-\\xff\\{\\}\"'`^#\\|\\[\\]\\(\\)<>=&]+=[^\\x00-\\x20\\x7f-\\xff\\{\\}\"'`^#\\|\\[\\]\\(\\)<>]*$");

    private final static Pattern URL_ENCODED_BASE_PATTERN = Pattern.compile("^[\\x21-\\x7e]+$");
    private final static Pattern URL_ENCODED_ESCAPE_PATTERN = Pattern.compile("%[a-f0-9]{2}", Pattern.CASE_INSENSITIVE);

    private final static Pattern BASE64_PATTERN = Pattern.compile("^[a-zA-Z0-9+/]+={0,2}$");
    private final static Pattern BASE64_PRETTY_PATTERN = Pattern.compile("^(?:[a-zA-Z0-9+/]{64,76}={0,2}\\x0d?\\x0a)" + // has to be at least one full line
                    "[a-zA-Z0-9+/]{0,76}+={0,2}" + "\\x0d?\\x0a?$" // last line doesn't have to end with CRLF
    );
    private final static Pattern BASE64_PRETTY_LINE_PATTERN = Pattern.compile("^([a-zA-Z0-9+/]+={0,2})(\\x0d?\\x0a)");
    private final static Pattern BASE64_WEB_PATTERN = Pattern.compile("^[a-zA-Z0-90\\-_]+={0,2}$");

    private final static Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[0-9A-Za-z]+$");
    private final static Pattern NUMERIC_PATTERN = Pattern.compile("^[0-9]+$");

    private final static Pattern ASCII_TEXT_PATTERN = Pattern.compile("^[\\x09\\x0d\\x0a\\x20-\\x7e]+$");

    private static final float WORD_PERCENTAGE_THRESHOLD = 0.30F;

    private final static Pattern CONTAINS_NUMBERS_PATTERN = Pattern.compile("[0-9]");
    private final static Pattern CONTAINS_UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private final static Pattern CONTAINS_LOWERCASE_PATTERN = Pattern.compile("[a-z]");

    public static byte[] decodeData(final byte[] data, final DataFormatType format) throws DataFormatException
    {
        if (!format.isEncodeable())
        {
            throw new NotImplementedException(format.toString() + " is not implemented for decoding; it is a basic data format.");
        }
        switch (format)
        {
            case HEX_LOWER_SIMPLE:
            case HEX_UPPER_SIMPLE:
                return HexEncodingUtils.decodeHex(data);

            case HEX_LOWER_WITH_0X_PREFIX:
            case HEX_UPPER_WITH_0X_PREFIX:
            case HEX_LOWER_SLASH_X:
            case HEX_UPPER_SLASH_X:
                return HexEncodingUtils.decodeHex(HexEncodingUtils.simplifyHexFormat(data));

            case BASE64:
            case BASE64_PRETTY:
            case BASE64_WEB:
                return Base64.decodeBase64(data);

            case HTML_BASIC_ENTITIES:
            case HTML_FULL_ENTITIES:
                return HtmlEncodingUtils.decodeHtml(data);

            case URL_BASIC_ENCODED:
            case URL_FULL_ENCODED:
                return UrlEncodingUtils.decodeUrl(data);

            default:
                throw new NotImplementedException(format.toString() + " is not implemented for decoding.");
        }
    }

    public static byte[] encodeData(final byte[] data, final DataFormatType format, DataFormatOptions options) throws DataFormatException
    {
        if (!format.isEncodeable())
        {
            throw new NotImplementedException(format.toString() + " is not implemented for decoding; it is a basic data format.");
        }
        if (options == null)
        {
            options = new DataFormatOptions();
        }
        switch (format)
        {
            case HEX_LOWER_SIMPLE:
                return HexEncodingUtils.encodeHex(data, HexEncodingUtils.HEX_DIGITS_LOWER, false, false);
            case HEX_UPPER_SIMPLE:
                return HexEncodingUtils.encodeHex(data, HexEncodingUtils.HEX_DIGITS_UPPER, false, false);

            case HEX_LOWER_WITH_0X_PREFIX:
                return HexEncodingUtils.encodeHex(data, HexEncodingUtils.HEX_DIGITS_LOWER, true, false);
            case HEX_UPPER_WITH_0X_PREFIX:
                return HexEncodingUtils.encodeHex(data, HexEncodingUtils.HEX_DIGITS_UPPER, true, false);
            case HEX_LOWER_SLASH_X:
                return HexEncodingUtils.encodeHex(data, HexEncodingUtils.HEX_DIGITS_LOWER, false, true);
            case HEX_UPPER_SLASH_X:
                return HexEncodingUtils.encodeHex(data, HexEncodingUtils.HEX_DIGITS_UPPER, false, true);

            case BASE64:
                return Base64.encodeBase64(data);
            case BASE64_PRETTY:
                return new Base64(options.BASE64_LINE_LENGTH, options.BASE64_LINE_DELIMITER, false).encode(data);
            case BASE64_WEB:
                return Base64.encodeBase64(data, false, true);

            case HTML_BASIC_ENTITIES:
                return HtmlEncodingUtils.encodeHtmlDefault(data);
            case HTML_FULL_ENTITIES:
                return HtmlEncodingUtils.encodeFullHtml(data);

            case URL_BASIC_ENCODED:
                return UrlEncodingUtils.encodeDefaultChars(data);
            case URL_FULL_ENCODED:
                return UrlEncodingUtils.encodeAllChars(data);

            default:
                throw new NotImplementedException(format.toString() + " is not implemented for decoding.");
        }
    }

    public static DataFormat getDataFormat(final byte[] data, final String mimeType) throws DataFormatException
    {
        DataFormat format = new DataFormat();
        if (mimeType != null && !mimeType.isEmpty())
        {
            String mime = mimeType.toLowerCase();
            if (MimeUtils.isAmf(mime))
            {
                format.formatType = DataFormatType.AMF;
                return format;
            }
            if (MimeUtils.isUrlEncoded(mime))
            {
                format.formatType = DataFormatType.URL_ENCODED_QUERY_STRING;
                return format;
            }
        }
        if (data == null || data.length == 0)
        {
            throw new DataFormatException("Null data and can't figure out the MIME type");
        }
        String dataString = new String(data);
        try
        {
            Long.valueOf(dataString);
            format.formatType = DataFormatType.INTEGER;
            return format;
        }
        catch (NumberFormatException e)
        {
            // No problem, just not an integer
        }

        try
        {
            Double.valueOf(dataString);
            format.formatType = DataFormatType.FLOAT;
            return format;
        }
        catch (NumberFormatException e)
        {
            // No problem, just not a double
        }

        float wordPercentage = getWordPercentage(dataString);

        if (NUMERIC_PATTERN.matcher(dataString).matches())
        {
            format.formatType = DataFormatType.NUMERIC;
        }

        else if (HEX_UPPER_PATTERN.matcher(dataString).matches())
        {
            format.formatType = DataFormatType.HEX_UPPER_SIMPLE;
        }

        else if (HEX_LOWER_PATTERN.matcher(dataString).matches())
        {
            format.formatType = DataFormatType.HEX_LOWER_SIMPLE;
        }

        else if (HEX_LOWER_0X_PREFIX_PATTERN.matcher(dataString).matches())
        {
            format.formatType = DataFormatType.HEX_LOWER_WITH_0X_PREFIX;
        }

        else if (HEX_UPPER_0X_PREFIX_PATTERN.matcher(dataString).matches())
        {
            format.formatType = DataFormatType.HEX_UPPER_WITH_0X_PREFIX;
        }

        else if (HEX_LOWER_SLASH_X_PATTERN.matcher(dataString).matches())
        {
            format.formatType = DataFormatType.HEX_LOWER_SLASH_X;
        }

        else if (HEX_UPPER_SLASH_X_PATTERN.matcher(dataString).matches())
        {
            format.formatType = DataFormatType.HEX_UPPER_SLASH_X;
        }

        else if (potentialBase64(dataString, wordPercentage))
        {
            if (BASE64_PATTERN.matcher(dataString).matches())
            {
                format.formatType = DataFormatType.BASE64;
            }

            else if (BASE64_PRETTY_PATTERN.matcher(dataString).matches())
            {
                Matcher m = BASE64_PRETTY_LINE_PATTERN.matcher(dataString);
                if (m.find())
                {
                    format.options.BASE64_LINE_LENGTH = m.group(1).length();
                    format.options.BASE64_LINE_DELIMITER = m.group(2).getBytes();
                }
                else
                {
                    throw new IllegalStateException("Apparent bug in parsing Base64 pretty");
                }
                format.formatType = DataFormatType.BASE64_PRETTY;
            }

            else if (BASE64_WEB_PATTERN.matcher(dataString).matches())
            {
                format.formatType = DataFormatType.BASE64_WEB;
            }
        }

        else if (URL_ENCODED_BASE_PATTERN.matcher(dataString).matches() && URL_ENCODED_ESCAPE_PATTERN.matcher(dataString).matches())
        {
            format.formatType = DataFormatType.URL_BASIC_ENCODED;
        }

        else if (URL_ENCODED_QUERY_PATTERN.matcher(dataString).matches())
        {
            format.formatType = DataFormatType.URL_ENCODED_QUERY_STRING;
        }

        // keep these at the bottom
        else if (ASCII_TEXT_PATTERN.matcher(dataString).matches())
        {
            format.formatType = DataFormatType.ASCII_TEXT;
        }

        else
        {
            format.formatType = DataFormatType.ASCII_BINARY;
        }

        return format;
    }

    private static float getWordPercentage(final String data)
    {
        if (data == null || data.isEmpty())
        {
            return 0;
        }
        return 1 - (float) stripWords(data).length() / (float) data.length();
    }

    public static boolean potentialBase64(final String dataString, final float wordPercentage)
    {
        return dataString.length() > 2 && wordPercentage < WORD_PERCENTAGE_THRESHOLD && CONTAINS_LOWERCASE_PATTERN.matcher(dataString).matches() && CONTAINS_NUMBERS_PATTERN.matcher(dataString).matches()
                        && CONTAINS_UPPERCASE_PATTERN.matcher(dataString).matches();
    }

    private static String stripWords(final String data)
    {
        String d = data.toLowerCase();
        // FIXME: The line below is the only dependency to GrendelScan. Is there a way to pass that data in rather than retrieve it from Scan? - Jonathan Byrne 10/01/2013
        // for (String word : Scan.getInstance().getWordList().getReadOnlyWordsSortedBySize())
        // {
        // if (d.length() < 3) // We have no words less than three chars
        // {
        // break;
        // }
        // if (word.length() > d.length())
        // {
        // continue;
        // }
        // if (d.contains(word))
        // {
        // d = d.replace(word, "");
        // }
        // }
        return d;
    }

}
