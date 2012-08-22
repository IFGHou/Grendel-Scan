package com.grendelscan.scan.reporting;


import java.io.File;
import java.text.DateFormat;
import java.util.Date;

import com.grendelscan.data.findings.Finding;
import com.grendelscan.data.findings.FindingSeverity;

public class HtmlReportGenerator extends ReportGenerator
{
	private String newLine;
	public HtmlReportGenerator(String reportFilename, FindingSeverity minSeverity)
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
		Date date = new Date();
		String dateString = DateFormat.getDateInstance(DateFormat.FULL).format(date) + " ";
		dateString += DateFormat.getTimeInstance(DateFormat.FULL).format(date);

		String reportString = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" + newLine +
				"<html xmlns=\"http://www.w3.org/1999/xhtml\" >" + newLine +
				"<head><title>Grendel-Scan Report: " + dateString + "</title>" + newLine +
				"<style type=\"text/css\"> " + newLine +
				"BODY { word-wrap: break-word; font-family: Arial; font-size: 10pt; } " + newLine +
				".vulnerabilityTitle { text-align: center; font-size: 12pt; font-weight: bold; border-right: gray 1px solid; border-top: gray 1px solid; border-left: gray 1px solid; border-bottom: gray 1px solid; } " + newLine +
				".heading { vertical-align: top; border-right: gray 1px solid; border-top: gray 1px solid; border-left: gray 1px solid; border-bottom: gray 1px solid; font-size: 11pt; font-weight: bold; background-color: lightgrey; } " + newLine +
				".vulnerabilityText { border-right: gray 1px solid; border-top: gray 1px solid; border-left: gray 1px solid; border-bottom: gray 1px solid; } " + newLine +
				".findingTable { border-collapse:collapse; border-right: gray 2px solid; border-top: gray 2px solid; vertical-align: top; border-left: gray 2px solid; border-bottom: gray 2px solid; text-align: left; } </style></head>" + newLine;

		
		reportString += "<body><table style=\"width: 800px\"><tr><td style=\"text-align: center; font-size: 16pt;\">Grendel-Scan Report<br />" +
				"<div style=\"font-size: 14pt\">" + dateString + "</div><br /></td></tr>";
		while (isMoreEvents())
		{
			event = getNextEvent();
			reportString += "<tr><td style=\"width: 800px\"><table class=\"findingTable\">" + newLine;
			String leftHtml = "<tr><td style=\"width: 200px\" class=\"heading\">";
			String middleHtml = "</td><td style=\"width: 600px\" class=\"vulnerabilityText\">";
			String rightHtml = "</td></tr>" + newLine;
			
			reportString += "<tr><td class=\"vulnerabilityTitle\" colspan=\"2\">";
			reportString += wrapText(event.getTitle());
			reportString += rightHtml;
			
			reportString += leftHtml;
			reportString += "Severity:";
			reportString += middleHtml;
			reportString += wrapText(event.getSeverity().getFullName());
			reportString += rightHtml;
			
			if (!event.getUrl().equals(""))
			{
				reportString += leftHtml;
				reportString += "URL:";
				reportString += middleHtml;
				reportString += wrapText(event.getUrl());
				reportString += rightHtml;
			}
			
			reportString += leftHtml;
			reportString += "Description:";
			reportString += middleHtml;
			reportString += wrapText(event.getLongDescription());
			reportString += rightHtml;
			
			
			if (!event.getImpact().equals(""))
			{
				reportString += leftHtml;
				reportString += "Impact:";
				reportString += middleHtml;
				reportString += wrapText(event.getImpact());
				reportString += rightHtml;
			}

			if (!event.getRecomendations().equals(""))
			{
				reportString += leftHtml;
				reportString += "Recommendations:";
				reportString += middleHtml;
				reportString += wrapText(event.getRecomendations());
				reportString += rightHtml;
			}
			
			if (!event.getReferences().equals(""))
			{
				reportString += leftHtml;
				reportString += "References:";
				reportString += middleHtml;
				reportString += wrapText(event.getReferences());
				reportString += rightHtml;
			}
			
			reportString += "</table></td></tr>" + newLine;
			reportString += "<tr><td>&nbsp;</td></tr>" + newLine;
		}
		reportString += "</table></body></html>";
		
		writeReport(reportString.trim());
	}
//	private Pattern htmlWrapPattern = Pattern.compile("(.*?)(\\S{30,}|$)");
//	private Pattern longStringPattern = Pattern.compile("(\\S{1,10})");
	
//	private String makeLinks(String text)
//	{
//		
//	}
	
	private String wrapText(String text)
	{
		return text;
//		String result = "";
//		Matcher htmlWrapMatcher = htmlWrapPattern.matcher(text);
//		while (htmlWrapMatcher.find())
//		{
//			result += htmlWrapMatcher.group(1);
//			String longString = htmlWrapMatcher.group(2);
//			Matcher longMatcher = longStringPattern.matcher(longString);
//			while (longMatcher.find())
//			{
//				result += longMatcher.group(1) + "&#8203;";
//			}
//		}
//		
//		return result;
	}
}
