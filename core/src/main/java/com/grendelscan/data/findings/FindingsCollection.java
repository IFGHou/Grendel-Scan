package com.grendelscan.data.findings;

import com.grendelscan.data.database.collections.DatabaseBackedMap;
import com.grendelscan.scan.Scan;

/**
 * @author dbyrne
 * 
 */
public class FindingsCollection extends DatabaseBackedMap<String, Integer>
{

    private final String briefDescription;
    private final String longDescription;
    private final String impact;
    private final String recomendations;
    private final String source;
    private final FindingSeverity severity;
    private final String title;
    private final String references;

    public FindingsCollection(final String uniqueCollectionName, final String briefDescription, final String longDescription, final String impact, final String recomendations, final String source, final FindingSeverity severity, final String title,
                    final String references)
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

    public void addFinding(final String host, final String newLongDescription)
    {
        if (containsKey(host))
        {
            Finding finding = Scan.getInstance().getFindings().get(get(host));
            finding.setLongDescription(finding.getLongDescription() + "<br/>\n<br/>\n" + newLongDescription);
        }
        else
        {
            Finding finding = new Finding(null, source, severity, host, title, briefDescription, longDescription + "<br/>\n<br/>\n" + newLongDescription, impact, recomendations, references);
            Scan.getInstance().getFindings().addFinding(finding);
            put(host, finding.getId());
        }
    }
}
