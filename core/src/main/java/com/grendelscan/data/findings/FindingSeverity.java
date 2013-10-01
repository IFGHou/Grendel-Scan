package com.grendelscan.data.findings;

public enum FindingSeverity
{
	INFO(3, "Informational"), 
	LOW(4, "Low"), 
	MEDIUM(5, "Medium"), 
	HIGH(6, "High");
	
	private int value;
	private String fullName;
	private FindingSeverity(int value, String name)
    {
		this.value = value;
		this.fullName = name;
    }
	
	public static String[] getAllNames()
	{
		return new String[] {INFO.fullName, LOW.fullName, MEDIUM.fullName, HIGH.fullName};
	}
	
	public static FindingSeverity getSeverityByName(String name)
	{
		FindingSeverity severity = null;
		if (name.equalsIgnoreCase(INFO.fullName))
		{
			severity = INFO;
		}
		else if (name.equalsIgnoreCase(LOW.fullName))
		{
			severity = LOW;
		}
		else if (name.equalsIgnoreCase(MEDIUM.fullName))
		{
			severity = MEDIUM;
		}
		else if (name.equalsIgnoreCase(HIGH.fullName))
		{
			severity = HIGH;
		}
		
		return severity;
	}
	
	/**
	 * Returns greater than zero if target is higher severity, zero if 
	 * it is equal, or less than zero if target is less severe.  
	 * @param target
	 * @return
	 */
	public int compare(FindingSeverity target)
	{
		return target.value - value;
	}

	public String getFullName()
    {
    	return fullName;
    }

	public final int getValue()
	{
		return value;
	}
}
