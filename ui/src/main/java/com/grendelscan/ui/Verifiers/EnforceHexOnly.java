package com.grendelscan.ui.Verifiers;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

public class EnforceHexOnly implements VerifyListener 
{
	@Override
	public void verifyText(VerifyEvent e)
    {
		String string = e.text;
		
		char [] chars = new char [string.length ()];
		string.getChars (0, chars.length, chars, 0);
		
		for (int i=0; i<chars.length; i++) 
		{
			char ch = chars[i];
			if (!(
					(ch >= '0' && ch <= '9')
					||(ch >= 'a' && ch <= 'f')
					||(ch >= 'A' && ch <= 'F')
				)) 
			{
				e.doit = false;
				return;
			}
		}
		e.doit = true;
    }
}
