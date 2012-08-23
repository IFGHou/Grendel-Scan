package com.grendelscan.utils;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.grendelscan.logging.Log;

public class StringUtils
{
	public static final int FORMAT_ALL_ASCII = 3;
	public static final int FORMAT_ALL_CASE_ALPHA = 8;
	public static final int FORMAT_ALL_CASE_ALPHANUMERIC = 9;
	public static final int FORMAT_BASE64 = 2;
	public static final int FORMAT_DECIMAL = 4;
	public static final int FORMAT_HEXADECIMAL = 0;
	public static final int FORMAT_HTML_DECIMAL_ENCODED = 12;
	public static final int FORMAT_HTML_HEX_ENCODED = 11;
	public static final int FORMAT_LOW_ASCII = 1;
	public static final int FORMAT_LOWER_CASE_ALPHA = 7;
	public static final int FORMAT_PLAIN_TEXT = 5;
	public static final int FORMAT_UPPER_CASE_ALPHA = 6;
	public static final int FORMAT_URL_ENCODED = 10;
	public static final int FORMAT_DECIMAL_COMMA_SEPERATED = 13;
	public static final int FORMAT_HTML_DOUBLE_PADDED_HEX_ENCODED = 14;
	private static Random random = new Random();
	
	private static final Charset defaultCharset = Charset.forName("ISO-8859-1"); // aka ISO-LATIN-1
	public static Charset getDefaultCharset()
    {
    	return defaultCharset;
    }

	/**
	 * Only supports ASCII right now. Don't try unicode. Converts all
	 * characters, even ones that don't need to be
	 * 
	 * Supports FORMAT_HTML_HEX_ENCODED
	 * 
	 * @param string
	 * @param format
	 * @return
	 */
	public static String completeEncode(String string, int format)
	{
		Formatter formatter = new Formatter();
		String result = "";
		for (char character: string.toCharArray())
		{
			int numericValue = Character.valueOf(character);
			switch (format)
			{
				case FORMAT_HTML_HEX_ENCODED:
					formatter.format("&%02X;", numericValue);
					break;
				case FORMAT_HTML_DOUBLE_PADDED_HEX_ENCODED:
					formatter.format("&0000%02X;", numericValue);
					break;
				case FORMAT_HTML_DECIMAL_ENCODED:
					formatter.format("&%02d", numericValue);
					break;
				case FORMAT_URL_ENCODED:
					formatter.format("%%%02X", numericValue);
					break;
				case FORMAT_HEXADECIMAL:
					formatter.format("%02X", numericValue);
					break;
				case FORMAT_DECIMAL_COMMA_SEPERATED:
					formatter.format("%02d,", numericValue);
					break;
				default:
					result += character;
			}
		}
		if (result.equals(""))
		{
			result = formatter.toString();
		}
		if (format == FORMAT_DECIMAL_COMMA_SEPERATED)
		{
			result = result.replaceFirst(",$", "");
		}
		return result;
	}

	/**
	 * Only supports ASCII right now. Don't try unicode. Only converts
	 * non-alphanumeric characters
	 * 
	 * Supports FORMAT_HTML_HEX_ENCODED
	 * 
	 * @param string
	 * @param format
	 * @return
	 */
	public static String nonAlphanumericEncode(String string, int format)
	{
		Formatter formatter = new Formatter();
		String result = "";
		for (char character: string.toCharArray())
		{
			if (((character >= 'a') && (character <= 'z')) ||
					((character >= 'A') && (character <= 'Z')) ||
					((character >= '0') && (character <= '9')))
			{
				int numericValue = Character.getNumericValue(character);
				switch (format)
				{
					case FORMAT_HTML_HEX_ENCODED:
						result += formatter.format("&%02x;", numericValue);
						break;
					case FORMAT_HTML_DECIMAL_ENCODED:
						result += formatter.format("&%02d", numericValue);
						break;
					case FORMAT_URL_ENCODED:
						result += formatter.format("%%%02x", numericValue);
						break;
					case FORMAT_HEXADECIMAL:
						result += formatter.format("%02x", numericValue);
						break;
					
					default:
						result += character;
				}
			}
			else
			{
				result += character;
			}
			
		}
		return result;
	}

	/*
	 * public static final int FORMAT_HEXADECIMAL = 0; public static final int
	 * FORMAT_LOW_ASCII = 1; public static final int FORMAT_BASE64 = 2; public
	 * static final int FORMAT_ALL_ASCII = 3; public static final int
	 * FORMAT_DECIMAL = 4; public static final int FORMAT_PLAIN_TEXT = 5; public
	 * static final int FORMAT_UPPER_CASE_ALPHA = 6; public static final int
	 * FORMAT_LOWER_CASE_ALPHA = 7; public static final int
	 * FORMAT_ALL_CASE_ALPHA = 8; public static final int
	 * FORMAT_ALL_CASE_ALPHANUMERIC = 9; public static final int
	 * FORMAT_URL_ENCODED = 10; public static final int FORMAT_HTML_ENCODED =
	 * 11;
	 * 
	 * 
	 */
	public static byte generateRandomByte(int stringFormat)
	{

		switch (stringFormat)
		{
			case FORMAT_DECIMAL:
			{
				int ranges[][] = { { '0', '9' } };
				return (byte) rand(ranges);
			}
			case FORMAT_UPPER_CASE_ALPHA:
			{
				int ranges[][] = { { 'A', 'Z' } };
				return (byte) rand(ranges);
			}
			case FORMAT_LOWER_CASE_ALPHA:
			{
				int ranges[][] = { { 'a', 'z' } };
				return (byte) rand(ranges);
			}
			case FORMAT_ALL_CASE_ALPHA:
			{
				int ranges[][] = { { 'a', 'z' }, { 'A', 'Z' } };
				return (byte) rand(ranges);
			}
			default:
				int ranges[][] = { { 0, 255 } };
				return (byte) rand(ranges);
		}
	}

	public static String generateRandomString(int stringFormat, int length)
	{
		byte rawString[] = new byte[length];
		for (int index = 0; index < length; index++)
		{
			rawString[index] = generateRandomByte(stringFormat);
		}

		return new String(rawString, StringUtils.getDefaultCharset());
	}

	public static double getBitsPerByte(int format)
	{
		double bits;
		switch (format)
		{
			case FORMAT_HEXADECIMAL:
				bits = 4;
				break;
			case FORMAT_LOW_ASCII:
				bits = 7;
				break;
			case FORMAT_BASE64:
				bits = 6;
				break;
			case FORMAT_ALL_ASCII:
				bits = 8;
				break;
			case FORMAT_DECIMAL:
				bits = 3.3219;
				break;
			case FORMAT_PLAIN_TEXT:
				bits = 6.5545;
				break;
			default:
				bits = 0;
		}
		return bits;
	}

	/**
	 * Note that it can be difficult to tell the difference between a plain text
	 * word and a base64 string. If there is a possibility of confusion, check
	 * the result. This will guess base64 before alphanumeric. Also note that
	 * this doesn't check for validity of base64 format. This is because padding
	 * may be omitted by some applications when the value is sent to the client.
	 * 
	 * Only FORMAT_ALL_ASCII, FORMAT_DECIMAL, FORMAT_HEXADECIMAL, FORMAT_BASE64,
	 * FORMAT_PLAIN_TEXT, FORMAT_LOW_ASCII, AND FORMAT_ALL_ASCII are supported.
	 * 
	 * @param target
	 * @return
	 */
	public static int getStringFormat(String target)
	{
		int format = FORMAT_ALL_ASCII;

		Pattern pattern = Pattern.compile("^[0-9]+$");
		if (target.matches("^[0-9]+$"))
		{
			format = FORMAT_DECIMAL;
		}
		// This isn't a mistake I want the cases to be mutually exclusive. I.e.
		// all upper or all lower
		else if (target.matches("^[0-9a-f]+$") || target.matches("^[0-9A-F]+$"))
		{
			format = FORMAT_HEXADECIMAL;
		}
		else if (target.matches("^[0-9a-zA-Z=+/]+$"))
		{
			format = FORMAT_BASE64;
		}
		else if (target.matches("^[\\x20-\\x7d]+$"))
		{
			format = FORMAT_PLAIN_TEXT;
		}
		else if (target.matches("^[\\x00-\\x7f]+$"))
		{
			format = FORMAT_LOW_ASCII;
		}
		else
		{
			format = FORMAT_ALL_ASCII;
		}

		return format;
	}

	public static int rand(int[][] ranges)
	{
		int returnValue = 0;
		int totalValues = 0;
		for (int range[]: ranges)
		{
			int high = range[1];
			int low = range[0];
			totalValues += (high - low) + 1;
		}

		int randomValue = random.nextInt(totalValues);

		totalValues = 0;
		int previousTotalValues;
		for (int range[]: ranges)
		{
			int high = range[1];
			int low = range[0];
			previousTotalValues = totalValues;
			totalValues += (high - low) + 1;
			if (randomValue <= totalValues)
			{
				int offset = randomValue - previousTotalValues;
				returnValue = low + offset;
				break;
			}
		}
		return returnValue;
	}

	/**
	 * This really needs to be improved. Way too slow.
	 */ 
	public static int scoreStringDifference(String stringA, String stringB, int perfectScore)
	{
		int score = 0;
		double maxLength;
		if (stringA.equals(stringB))
		{
			return perfectScore;
		}
		
		int levenshteinDistance = org.apache.commons.lang.StringUtils.getLevenshteinDistance(stringA, stringB);

		if (stringA.length() > stringB.length())
		{
			maxLength = stringA.length();
		}
		else
		{
			maxLength = stringB.length();
		}

		if (maxLength > 0)
		{
			score = (int) Math.round(((maxLength - levenshteinDistance) / maxLength) * perfectScore);
		}

		return score;
	}

	public static int scoreStringDifferenceIgnoreCase(String stringA, String stringB, int perfectScore)
	{
		if (stringA == null)
		{
			stringA = "";
		}
		if (stringB == null)
		{
			stringB = "";
		}
		return scoreStringDifference(stringA.toLowerCase(), stringB.toLowerCase(), perfectScore);
	}

	public static String normalizeWhiteSpace(String input)
	{
		return input.replaceAll("[\\x00-\\x20]++", " ");
	}

	/**
	 * Case insensitive
	 * 
	 * @param text
	 * @param regexs
	 * @return
	 */
	public static boolean testMultipleRegexs(String text, Iterable<String> regexs)
	{
		boolean found = false;
		for(String regex: regexs)
		{
			Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			if (pattern.matcher(text).find())
			{
				found = true;
				break;
			}
		}
		return found;
	}
	
	public static String join(String[] strings, String joinString)
	{
		String joined = "";
		for (String string: strings)
		{
			joined += string.concat(joinString);
		}
		return joined;
	}
	
	

	public static String unquote(String string)
	{
		String unquoted = string;
		if (string != null && !string.equals(""))
		{
			char first = string.charAt(0);
			char last = string.charAt(string.length() - 1);
			
			if (first == last && (first == '"' || first == '\''))
			{
				unquoted = string.substring(1, string.length() - 1);
			}
			else
			{
				unquoted = string;
			}
		}
		return unquoted;
	}
	
	public static boolean notEmpty(String string)
	{
		if (string != null && !string.equals(""))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * This will return the actual value if the string is a
	 * number, otherwise it will return the base 256 value 
	 * @param tempString
	 * @return
	 */
	public static double getNumericValue(String string)
	{
		double value = 0;
		String tempString = string.toLowerCase();
		if (tempString.matches("[0-9\\.]+"))
		{
			value = Double.valueOf(tempString);
		}
		else
		{
			int power = 0;
			// loop over the string starting at the last character
			for (int index = tempString.length() - 1; index >= 0; index--, power++)
			{
				value += Math.pow(256, power) * tempString.charAt(index); 
			}
		}
		
		return value;
	}

	static Pattern perlMetaCharacterPattern = Pattern.compile("\\\\([\\$@\"&%/])");

	/**
	 * This will apply Perl rules to unescape strings in that format. For example
	 * \$ is only found in Perl, or related languages. This is mostly for Nikto
	 * databases. Note that it will also unescape some things that are usually
	 * specific to regexes, such as \/, but not all regex metacharacters.
	 * @param perlString
	 * @return
	 */
	public static String unescapePerlStrings(String perlString)
	{
		Matcher m = perlMetaCharacterPattern.matcher(perlString);
		return m.replaceAll("$1");
	}
	
	public static String md5Hash(String source)
	{
		String result = null;
		MessageDigest md = null;
		try
		{
			md = MessageDigest.getInstance("MD5");
			result = new String(md.digest(source.getBytes(StringUtils.getDefaultCharset())), StringUtils.getDefaultCharset());
		}
		catch (NoSuchAlgorithmException e)
		{
			Log.error("Something weird with the MD5 hash call in md5Hash: " + e.toString(), e);
		}
		
		return result;
	}

    public static String rot13(String string) 
    {
    	StringBuilder out = new StringBuilder(string.length());
        for (int i = 0; i < string.length(); i++) 
        {
            char c = string.charAt(i);
            if       (c >= 'a' && c <= 'm') c += 13;
            else if  (c >= 'n' && c <= 'z') c -= 13;
            else if  (c >= 'A' && c <= 'M') c += 13;
            else if  (c >= 'N' && c <= 'Z') c -= 13;
            out.append(c);
        }
        return out.toString();
    }

    /**
     * Rot13, plus it increments numbers by one
     * @param string
     * @return
     */
    public static String rot13_1(String string) 
    {
    	StringBuilder out = new StringBuilder(string.length());
        for (int i = 0; i < string.length(); i++) 
        {
            char c = string.charAt(i);
            if       (c >= 'a' && c <= 'm') c += 13;
            else if  (c >= 'n' && c <= 'z') c -= 13;
            else if  (c >= 'A' && c <= 'M') c += 13;
            else if  (c >= 'N' && c <= 'Z') c -= 13;
            else if  (c >= '0' && c <= '8') c += 1;
            else if  (c == '9') c = '0';
            out.append(c);
        }
        return out.toString();
    }

    public static String rotX(String string, int inc) 
    {
    	StringBuilder out = new StringBuilder(string.length());
        for (int i = 0; i < string.length(); i++) 
        {
            char c = string.charAt(i);
            if       (c >= 'a' && c <= 'a' + inc - 1) c += inc;
            else if  (c >= 'a' + inc && c <= 'z') c -= inc;
            else if  (c >= 'A' && c <= 'A' + inc - 1) c += inc;
            else if  (c >= 'A' + inc && c <= 'Z') c -= inc;
            else if  (c >= '0' && c <= '0' + (inc % 10) - 1) c += (inc % 10);
            else if  (c >= '0' + (inc % 10) && c <= '9') c -= (inc % 10);
            out.append(c);
        }
        return out.toString();
    }

    public static String rotXWithHexSafety(String string, int inc) 
    {
    	StringBuilder out = new StringBuilder(string.length());
        for (int i = 0; i < string.length(); i++) 
        {
            char c = string.charAt(i);
            if       (c >= 'a' && c <= 'a' + (inc % 6) - 1) c += (inc % 6);
            else if  (c >= 'a' + (inc % 6) && c <= 'f') c -= (inc % 6);
            else if  (c >= 'A' && c <= 'A' + (inc % 6) - 1) c += (inc % 6);
            else if  (c >= 'A' + (inc % 6) && c <= 'F') c -= (inc % 6);
            else if  (c >= 'g' && c <= 'g' + inc - 1) c += inc;
            else if  (c >= 'g' + inc && c <= 'z') c -= inc;
            else if  (c >= 'g' && c <= 'G' + inc - 1) c += inc;
            else if  (c >= 'g' + inc && c <= 'Z') c -= inc;
            else if  (c >= '0' && c <= '0' + (inc % 10) - 1) c += (inc % 10);
            else if  (c >= '0' + (inc % 10) && c <= '9') c -= (inc % 10);
            out.append(c);
        }
        return out.toString();
    }
    
    public static String indentLines(String lines, int tabs)
    {
    	StringBuilder sb = new StringBuilder();
    	boolean first = true;
    	for (String line: lines.split("\n"))
    	{
    		if (first)
    		{
    			first = false;
    		}
    		else
    		{
    			sb.append("\n");
    		}
    		for (int i = 0; i < tabs; i++)
			{
				sb.append("\t");
			}
    		sb.append(line);
    	}
    	return sb.toString();
    }
}
