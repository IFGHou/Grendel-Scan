package com.grendelscan.data.findings;

import java.io.Serializable;




/**
 * A basic event that should show up in a scan report.
 * 
 * @author David Byrne
 */
public class Finding implements Serializable 
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -2671424238558436738L;
	private static int lastId;
	private int id;
	private FindingSeverity severity;
	private String source, title, briefDescription, longDescription, recomendations, references, url, impact, longDescriptionFooter;
	private java.util.Date time;
	

	/**
	 * It's best to use all of these fields. If you don't
	 * any nulls will default to empty strings.
	 * 
	 * @param time
	 *            Probably easiest to send something like
	 *            "new Date()". If null, defaults to the
	 *            current time when the object is
	 *            instantiated.
	 * @param url
	 *            The URL associated with the event.
	 * @param source
	 *            What generated the event. Probably the
	 *            module name, but could be something else,
	 *            especially if the event is an error.
	 * @param severity
	 * @param title
	 * @param briefDescription
	 * @param longDescription
	 * @param recomendations
	 * @param references
	 */
	public Finding(java.util.Date time, String source, FindingSeverity severity, String url, String title, 
						String briefDescription, String longDescription, String impact, String recomendations, String references)
	{
		setId();
		this.source = source;
		this.severity = severity;
		this.title = title;
		this.briefDescription = briefDescription;
		this.longDescription = longDescription;
		this.impact = impact;
		this.recomendations = recomendations;
		this.references = references;
		this.time = time;
		this.url = url;
		initDefaults();
	}

	
	/**
	 * Intentionally default access level
	 * @param lastId
	 */
	static void setLastId(int lastId)
	{
		Finding.lastId = lastId; 
	}

	private synchronized void setId()
	{
		id = ++lastId;
	}
	
	public String getBriefDescription()
	{
		return briefDescription;
	}

	public String getLongDescription()
	{
		return longDescription;
	}

	public String getReferences()
	{
		return references;
	}

	public FindingSeverity getSeverity()
	{
		return severity;
	}

	public String getSource()
	{
		return source;
	}

	public java.util.Date getTime()
	{
		return time;
	}

	public String getTitle()
	{
		return title;
	}

	public String getUrl()
	{
		return url;
	}



	private void initDefaults()
	{
		if (time == null)
		{
			time = new java.util.Date();
		}

		if (source == null)
		{
			source = "Unknown";
		}

		/*
		if (severity < 0)
		{
			severity = ReportableEvent.UNKNOWN_SEVERITY;
		}
*/
		if (title == null)
		{
			title = "";
		}

		if (briefDescription == null)
		{
			briefDescription = "";
		}

		if (longDescription == null)
		{
			longDescription = "";
		}

		if (recomendations == null)
		{
			recomendations = "";
		}

		if (references == null)
		{
			references = "";
		}
	}
	


	public int getId()
    {
    	return id;
    }


	public void setId(int id)
    {
    	this.id = id;
    }


	public String getRecomendations()
    {
    	return recomendations;
    }


	public void setRecomendations(String recomendations)
    {
    	this.recomendations = recomendations;
    }


	public String getImpact()
    {
    	return impact;
    }


	public void setImpact(String impact)
    {
    	this.impact = impact;
    }


	public void setSeverity(FindingSeverity severity)
    {
    	this.severity = severity;
    }


	public void setSource(String source)
    {
    	this.source = source;
    }


	public void setTitle(String title)
    {
    	this.title = title;
    }


	public void setBriefDescription(String briefDescription)
    {
    	this.briefDescription = briefDescription;
    }


	public void setLongDescription(String longDescription)
    {
    	this.longDescription = longDescription;
    }


	public void setReferences(String references)
    {
    	this.references = references;
    }


	public void setUrl(String url)
    {
    	this.url = url;
    }


	public void setTime(java.util.Date time)
    {
    	this.time = time;
    }


	public final String getLongDescriptionFooter()
	{
		return longDescriptionFooter;
	}


	public final void setLongDescriptionFooter(String longDescriptionFooter)
	{
		this.longDescriptionFooter = longDescriptionFooter;
	}
}
