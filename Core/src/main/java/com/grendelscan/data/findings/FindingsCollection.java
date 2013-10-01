package com.grendelscan.data.findings;

import com.grendelscan.data.database.collections.DatabaseBackedMap;
import com.grendelscan.scan.Scan;


/**
 * @author dbyrne
 *
 */
public class FindingsCollection extends DatabaseBackedMap<String, Integer>
{

	private String briefDescription;
	private String longDescription;
	private String impact;
	private String recomendations;
	private String source;
	private FindingSeverity severity;
	private String title;
	private String references;
	
	
	public FindingsCollection(String uniqueCollectionName, String briefDescription, 
			String longDescription, String impact, String recomendations, String source,
			FindingSeverity severity, String title, String references)
	{
		super(uniqueCollectionName);
		this.briefDescription = briefDescription;
		this.longDescription = longDescription;
		this.impact = impact;
		this.recomendations = recomendations; 
		this.source = source;
		this.severity = severity;
		this.title = title;
		this.references = references;
	}

	public void addFinding(String host, String newLongDescription)
	{
		if (containsKey(host))
		{
			Finding finding = Scan.getInstance().getFindings().get(get(host));
			finding.setLongDescription(finding.getLongDescription() + "<br/>\n<br/>\n" + newLongDescription);
		}
		else
		{
			Finding finding = new Finding(null, source, severity, host,
					title, briefDescription, longDescription + "<br/>\n<br/>\n" + newLongDescription, 
					impact, recomendations, references);
			Scan.getInstance().getFindings().addFinding(finding);
			put(host, finding.getId());
		}
	}
}
