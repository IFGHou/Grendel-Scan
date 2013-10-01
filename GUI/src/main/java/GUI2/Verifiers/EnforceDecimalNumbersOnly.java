package com.grendelscan.GUI2.Verifiers;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

public class EnforceDecimalNumbersOnly implements VerifyListener 
{
	@Override
	public void verifyText(VerifyEvent e)
    {
		if (e.character == '\b')
			return;
		String string = e.text;
		char [] chars = new char [string.length ()];
		string.getChars (0, chars.length, chars, 0);
		boolean decimal = false;
		for (int i=0; i<chars.length; i++) 
		{
			if (chars[i] == '.')
			{
				if (decimal)
				{
					e.doit = false;
					return;
				}
				decimal = true;
			}
			else if (!('0' <= chars [i] && chars [i] <= '9')) 
			{
				e.doit = false;
				return;
			}
		}
    }
}
