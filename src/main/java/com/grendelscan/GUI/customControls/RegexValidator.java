package com.grendelscan.GUI.customControls;

import com.grendelscan.utils.RegexUtils;

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
