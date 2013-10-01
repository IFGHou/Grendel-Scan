package com.grendelscan.ui.customControls;

import com.grendelscan.commons.RegexUtils;

public class RegexValidator implements StringDataValidator
{
	
	@Override
	public boolean validData(String data)
	{
		boolean valid = true;
		if (data.equals("") || !RegexUtils.validateRegex(data))
		{
			valid = false;
		}
		return valid;
	}
	
}
