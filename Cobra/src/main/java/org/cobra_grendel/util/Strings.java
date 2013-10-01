/*
 * GNU LESSER GENERAL PUBLIC LICENSE Copyright (C) 2006 The Lobo Project
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 * 
 * Contact info: xamjadmin@users.sourceforge.net
 */
package org.cobra_grendel.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Summary description for Strings.
 */
public class Strings
{
	public static final String[] EMPTY_ARRAY = new String[0];
	private static final String HEX_CHARS = "0123456789ABCDEF";
	
	private static final MessageDigest MESSAGE_DIGEST;
	
	static
	{
		MessageDigest md;
		try
		{
			md = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException err)
		{
			throw new IllegalStateException();
		}
		MESSAGE_DIGEST = md;
	}
	
	private Strings()
	{
	}
	
	public static int compareVersions(String version1, String version2)
	{
		return version1.compareTo(version2);
	}
	
	public static int countChars(String text, char ch)
	{
		int len = text.length();
		int count = 0;
		for (int i = 0; i < len; i++)
		{
			if (ch == text.charAt(i))
			{
				count++;
			}
		}
		return count;
	}
	
	public static int countLines(String text)
	{
		int startIdx = 0;
		int lineCount = 1;
		for (;;)
		{
			int lbIdx = text.indexOf('\n', startIdx);
			if (lbIdx == -1)
			{
				break;
			}
			lineCount++;
			startIdx = lbIdx + 1;
		}
		return lineCount;
	}
	
	public static String getHash32(String source) throws UnsupportedEncodingException
	{
		String md5 = getMD5(source);
		return md5.substring(0, 8);
	}
	
	public static String getHash64(String source) throws UnsupportedEncodingException
	{
		String md5 = getMD5(source);
		return md5.substring(0, 16);
	}
	
	public static String getJavaIdentifier(String candidateID)
	{
		int len = candidateID.length();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < len; i++)
		{
			char ch = candidateID.charAt(i);
			boolean good = i == 0 ? Character.isJavaIdentifierStart(ch) : Character.isJavaIdentifierPart(ch);
			if (good)
			{
				buf.append(ch);
			}
			else
			{
				buf.append('_');
			}
		}
		return buf.toString();
	}
	
	public static String getJavaStringLiteral(String text)
	{
		StringBuffer buf = new StringBuffer();
		buf.append('"');
		int len = text.length();
		for (int i = 0; i < len; i++)
		{
			char ch = text.charAt(i);
			switch (ch)
			{
				case '\\':
					buf.append("\\\\");
					break;
				case '\n':
					buf.append("\\n");
					break;
				case '\r':
					buf.append("\\r");
					break;
				case '\t':
					buf.append("\\t");
					break;
				case '"':
					buf.append("\\\"");
					break;
				default:
					buf.append(ch);
					break;
			}
		}
		buf.append('"');
		return buf.toString();
	}
	
	public static String getMD5(String source) throws UnsupportedEncodingException
	{
		byte[] bytes = source.getBytes("UTF8");
		byte[] result;
		synchronized (MESSAGE_DIGEST)
		{
			MESSAGE_DIGEST.update(bytes);
			result = MESSAGE_DIGEST.digest();
		}
		char[] resChars = new char[32];
		int len = result.length;
		for (int i = 0; i < len; i++)
		{
			byte b = result[i];
			int lo4 = b & 0x0F;
			int hi4 = (b & 0xF0) >> 4;
			resChars[i * 2] = HEX_CHARS.charAt(hi4);
			resChars[i * 2 + 1] = HEX_CHARS.charAt(lo4);
		}
		return new String(resChars);
	}
	
	public static String getTextFromStream(InputStream in) throws IOException
	{
		Reader reader = new InputStreamReader(in, "UTF-8");
		char[] buffer = new char[256];
		int numRead;
		int offset = 0;
		while ((numRead = reader.read(buffer, offset, buffer.length - offset)) != -1)
		{
			offset += numRead;
			if (offset >= (buffer.length * 2) / 3)
			{
				char[] newBuffer = new char[buffer.length * 2];
				System.arraycopy(buffer, 0, newBuffer, 0, offset);
				buffer = newBuffer;
			}
		}
		return new String(buffer, 0, offset);
	}
	
	public static boolean isBlank(String text)
	{
		return (text == null) || "".equals(text);
	}
	
	public static boolean isJavaIdentifier(String id)
	{
		if (id == null)
		{
			return false;
		}
		int len = id.length();
		if (len == 0)
		{
			return false;
		}
		if (!Character.isJavaIdentifierStart(id.charAt(0)))
		{
			return false;
		}
		for (int i = 1; i < len; i++)
		{
			if (!Character.isJavaIdentifierPart(id.charAt(i)))
			{
				return false;
			}
		}
		return true;
	}
	
	// public static boolean isTrimmable(char ch) {
	// switch(ch) {
	// case ' ':
	// case '\t':
	// case '\r':
	// case '\n':
	// return true;
	// }
	// return false;
	// }
	//
	// /**
	// * Trims blanks, line breaks and tabs.
	// * @param text
	// * @return
	// */
	// public static String trim(String text) {
	// int len = text.length();
	// int startIdx;
	// for(startIdx = 0; startIdx < len; startIdx++) {
	// char ch = text.charAt(startIdx);
	// if(!isTrimmable(ch)) {
	// break;
	// }
	// }
	// int endIdx;
	// for(endIdx = len; --endIdx > startIdx; ) {
	// char ch = text.charAt(endIdx);
	// if(!isTrimmable(ch)) {
	// break;
	// }
	// }
	// return text.substring(startIdx, endIdx + 1);
	// }
	
	public static String[] split(String phrase)
	{
		int length = phrase.length();
		ArrayList wordList = new ArrayList();
		StringBuffer word = null;
		for (int i = 0; i < length; i++)
		{
			char ch = phrase.charAt(i);
			switch (ch)
			{
				case ' ':
				case '\t':
				case '\r':
				case '\n':
					if (word != null)
					{
						wordList.add(word.toString());
						word = null;
					}
					break;
				default:
					if (word == null)
					{
						word = new StringBuffer();
					}
					word.append(ch);
			}
		}
		if (word != null)
		{
			wordList.add(word.toString());
		}
		return (String[]) wordList.toArray(EMPTY_ARRAY);
	}
	
	public static String strictHtmlEncode(String rawText)
	{
		StringBuffer output = new StringBuffer();
		int length = rawText.length();
		for (int i = 0; i < length; i++)
		{
			char ch = rawText.charAt(i);
			switch (ch)
			{
				case '&':
					output.append("&amp;");
					break;
				case '"':
					output.append("&quot;");
					break;
				case '<':
					output.append("&lt;");
					break;
				case '>':
					output.append("&gt;");
					break;
				default:
					output.append(ch);
			}
		}
		return output.toString();
	}
	
	public static String trimForAlphaNumDash(String rawText)
	{
		int length = rawText.length();
		for (int i = 0; i < length; i++)
		{
			char ch = rawText.charAt(i);
			if (((ch >= 'a') && (ch <= 'z')) || ((ch >= 'A') && (ch <= 'Z')) || ((ch >= '0') && (ch <= '9'))
			        || (ch == '-'))
			{
				continue;
			}
			return rawText.substring(0, i);
		}
		return rawText;
	}
	
	public static String truncate(String text, int maxLength)
	{
		if (text == null)
		{
			return null;
		}
		if (text.length() <= maxLength)
		{
			return text;
		}
		return text.substring(0, Math.max(maxLength - 3, 0)) + "...";
	}
	
	public static String unquote(String text)
	{
		if (text.startsWith("\"") && text.endsWith("\""))
		{
			return text.substring(1, text.length() - 2);
		}
		return text;
	}
}
