package com.grendelscan.scan.data.findings;

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
    private static final long serialVersionUID = -2671424238558436738L;
    private static int lastId;

    /**
     * Intentionally default access level
     * 
     * @param lastId
     */
    static void setLastId(final int lastId)
    {
        Finding.lastId = lastId;
    }

    private int id;
    private FindingSeverity severity;
    private String source, title, briefDescription, longDescription, recomendations, references, url, impact, longDescriptionFooter;

    private java.util.Date time;

    /**
     * It's best to use all of these fields. If you don't any nulls will default to empty strings.
     * 
     * @param time
     *            Probably easiest to send something like "new Date()". If null, defaults to the current time when the object is instantiated.
     * @param url
     *            The URL associated with the event.
     * @param source
     *            What generated the event. Probably the module name, but could be something else, especially if the event is an error.
     * @param severity
     * @param title
     * @param briefDescription
     * @param longDescription
     * @param recomendations
     * @param references
     */
    public Finding(final java.util.Date time, final String source, final FindingSeverity severity, final String url, final String title, final String briefDescription, final String longDescription, final String impact, final String recomendations,
                    final String references)
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

    public String getBriefDescription()
    {
        return briefDescription;
    }

    public int getId()
    {
        return id;
    }

    public String getImpact()
    {
        return impact;
    }

    public String getLongDescription()
    {
        return longDescription;
    }

    public final String getLongDescriptionFooter()
    {
        return longDescriptionFooter;
    }

    public String getRecomendations()
    {
        return recomendations;
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
         * if (severity < 0) { severity = ReportableEvent.UNKNOWN_SEVERITY; }
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

    public void setBriefDescription(final String briefDescription)
    {
        this.briefDescription = briefDescription;
    }

    private synchronized void setId()
    {
        id = ++lastId;
    }

    public void setId(final int id)
    {
        this.id = id;
    }

    public void setImpact(final String impact)
    {
        this.impact = impact;
    }

    public void setLongDescription(final String longDescription)
    {
        this.longDescription = longDescription;
    }

    public final void setLongDescriptionFooter(final String longDescriptionFooter)
    {
        this.longDescriptionFooter = longDescriptionFooter;
    }

    public void setRecomendations(final String recomendations)
    {
        this.recomendations = recomendations;
    }

    public void setReferences(final String references)
    {
        this.references = references;
    }

    public void setSeverity(final FindingSeverity severity)
    {
        this.severity = severity;
    }

    public void setSource(final String source)
    {
        this.source = source;
    }

    public void setTime(final java.util.Date time)
    {
        this.time = time;
    }

    public void setTitle(final String title)
    {
        this.title = title;
    }

    public void setUrl(final String url)
    {
        this.url = url;
    }
}
