package com.grendelscan.scan.reporting;


import java.io.File;

import com.grendelscan.data.findings.Finding;
import com.grendelscan.data.findings.FindingSeverity;

public class TextReportGenerator extends ReportGenerator
{
	private String newLine;
	public TextReportGenerator(String reportFilename, FindingSeverity minSeverity)
    {
	    super(reportFilename, minSeverity);
		if (File.separator.equals("/"))
		{
			newLine = "\n";
		}
		else
		{
			newLine = "\r\n";
		}
    }

	int lineLength = 60;
	@Override
	public void generateReport()
	{
		Finding event;
		String reportString = "";
		while (isMoreEvents())
		{
			event = getNextEvent();
			reportString += 
				"============================================================" + newLine + 
				toMultiLine(event.getTitle(), lineLength) + newLine +
				newLine +
				toMultiLine(event.getUrl(), lineLength) + newLine +
//				toMultiLine(event.getBriefDescription(), lineLength) + newLine +
				"============================================================" + newLine +
				newLine +
				"SEVERITY: " + event.getSeverity().getFullName() + newLine +
				"SOURCE: " + event.getSource() + newLine +
				newLine +
				"DESCRIPTION: " + newLine +
				toMultiLine(event.getLongDescription(), lineLength) + newLine +
				newLine +
				newLine +
				"IMPACT: " + newLine +
				toMultiLine(event.getImpact(), lineLength) + newLine +
				newLine +
				newLine +
				"RECOMMENDATIONS: " + newLine +
				toMultiLine(event.getRecomendations(), lineLength) + newLine +
				newLine +
				"REFERENCES: " + newLine +
				toMultiLine(event.getReferences(), lineLength) + newLine +
				newLine +
				newLine;
		}
		reportString = reportString.replaceAll("</?br>", newLine);
		writeReport(reportString.trim());
	}
	
	private String toMultiLine(String string, int maxLineLength)
	{
		string = string.replaceAll("</?br>", newLine);
		String text = "";
		int currentLineLength = 0;
		if (string != null)
		{
			String tokens[] = string.split(" ");
			for (String token: tokens) 
			{
				if (currentLineLength + token.length() > maxLineLength)
				{
					currentLineLength = 0;
					text += newLine;
				}
				if (token.length() > maxLineLength)
				{
					int position = 0;
					while (position <= token.length())
					{
						int endChar = position + maxLineLength;
						if (endChar > token.length())
						{
							endChar = token.length();
						}
						text += token.substring(position, endChar) + newLine;
						position += maxLineLength;
					}
				}
				else
				{	
					text += token + " ";
					if (token.contains(newLine))
					{
						currentLineLength = 0;
					}
					else
					{
						currentLineLength += token.length() + 1;
					}
				}
			}
		}
		
		return text.trim();
	}
	

}
