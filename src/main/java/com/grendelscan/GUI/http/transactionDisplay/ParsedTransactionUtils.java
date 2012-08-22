package com.grendelscan.GUI.http.transactionDisplay;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

import com.grendelscan.GUI.MainWindow;
import com.grendelscan.utils.MimeUtils;

public class ParsedTransactionUtils
{
	/**
	 * 
	 * @param shell
	 * @param _string
	 * @param _mimeType Optional. If a null or blank string is passed, the mimeType will be guessed at
	 * using a simple regex
	 * @param editable
	 * @return Returns null if the string shouldn't be displayed
	 */
	public static String PrepareRawString(String string, String mimeType, boolean editable) throws UpdateCanceledException
	{
		String _string = string;
		String _mimeType = mimeType;
		if (_mimeType == null || _mimeType.equals(""))
		{
			_mimeType = getMimeType(_string);
		}
		if (MimeUtils.isWebTextMimeType(_mimeType))
		{
			_string.replaceAll("\r?\n", Text.DELIMITER);
		}
		if (editable)
		{
			if (_string.contains("\0"))
			{
				int result = MainWindow.getInstance().displayPrompt("Warning:", "The body contains null characters. If\n" +
						"you change the view to \"Raw\", they will\n" +
						"be deleted and the body may become\n" +
						"corrupted. Do you want to continue?", SWT.YES | SWT.NO, true);
				if (result == SWT.NO)
				{
					throw new UpdateCanceledException("User canceled the update because of null characters.");
				}
				_string = stripNulls(_string);
			}
		}
		else
		{
			_string = stripNulls(_string);
		}
		return _string;
	}
	
	private static String stripNulls(String string)
	{
		return string.replaceAll("\0", " ");
	}
	
	static final Pattern mimeTypePattern = Pattern.compile(".*?Content-type:\\s+([^;\\s]+.*)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	public static String getMimeType(String httpMessage)
	{
		Matcher m = mimeTypePattern.matcher(httpMessage);
		if (m.find())
			return m.group(1);

		return "";
	}

}
