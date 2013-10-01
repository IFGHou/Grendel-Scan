package com.grendelscan.scan.reporting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import com.grendelscan.commons.StringUtils;
import com.grendelscan.data.findings.Finding;
import com.grendelscan.data.findings.FindingSeverity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.scan.Scan;
public abstract class ReportGenerator
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportGenerator.class);
	protected String reportFilename;

	protected FindingSeverity minSeverity;
	private Iterator<Finding> iterator;
	
	
	public ReportGenerator(String reportFilename, FindingSeverity minSeverity)
    {
	    this.reportFilename = reportFilename;
	    this.minSeverity = minSeverity;
    }

	protected boolean isMoreEvents()
	{
		return getIterator().hasNext();
	}
	
	private Iterator<Finding> getIterator()
	{
		if (iterator == null)
		{
//			iterator = Scan.getInstance().getFindings().iterator();
		}
		return iterator;
	}
	/**
	 * Grabs the next event 
	 * @return Null if no more records
	 */
	protected Finding getNextEvent()
	{
		Finding event = null;

		if (getIterator().hasNext())
		{
			event = getIterator().next();
		}
		
		return event;
	}
	 
	
	public abstract void generateReport();
	
	protected void writeReport(String reportString)
	{
		OutputStream outfile;

		try
		{
			String filename;
			if (reportFilename.contains(File.separator))
			{
				filename = reportFilename;
			}
			else
			{
				filename = Scan.getInstance().getOutputDirectory() + File.separator + reportFilename;
			}
			outfile = new FileOutputStream(filename);
			outfile.write(reportString.getBytes(StringUtils.getDefaultCharset()));
			outfile.close();
		}
		catch (FileNotFoundException e)
		{
			LOGGER.error("Problem opening file for writing in SimpleDebugReport.writeReport: " + e.toString(), e);
		}
		catch (IOException e)
		{
			LOGGER.error("IOException in SimpleDebugReportGenerator.generateReport: " + e.toString(), e);

		}
	}
	public String getReportFilename()
    {
    	return reportFilename;
    }

	public FindingSeverity getMinSeverity()
    {
    	return minSeverity;
    }

}
