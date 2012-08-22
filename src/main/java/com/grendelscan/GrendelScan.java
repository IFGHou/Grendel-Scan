/*
 * GrendelScan.java
 */

package com.grendelscan;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.ConfigurationException;

import com.grendelscan.GUI.MainWindow;
import com.grendelscan.logging.Log;
import com.grendelscan.scan.ConfigurationManager;
import com.grendelscan.scan.Scan;

/**
 * 
 * This is the main class for the program.
 * 
 * @author David Byrne
 * 
 */
public class GrendelScan
{
	public static final String	defaultConfigDirectory	= "conf";
	public static final String	version					= "v2.0-alpha";
	public static final String	versionHttpText			= "Grendel-Scan/" + version;
	public static final String	versionText				= "Grendel-Scan " + version;
	private static CommandLine commands;
	private static ProgramOptions options;
	
	public static void main(String[] args)
	{
		Thread.currentThread().setName("Main thread");
		ConfigurationManager.initializeConfiguration(defaultConfigDirectory + File.separator + "scanner.conf");

		boolean useGUI = true;
		String outputDirectory = "";
		String configFile = "";
		
		if (args.length > 0)
		{
			options = new ProgramOptions();
			try
			{
				commands = options.parseArguments(args);
			}
			catch (ParseException e)
			{
				options.printHelp();
				System.exit(0);
			}
			checkHelp();

			useGUI = !commandLineOnly();
			outputDirectory = getOutputDir();
			configFile = getConfigFile();
		}
		
		
		if (!configFile.isEmpty())
		{
			try
			{
				Scan.getScanSettings().loadScanSettings(configFile);
			}
			catch (ConfigurationException e)
			{
				Log.error("Error loading scan settings, defaults will be used: " + e.toString(), e);
			}
		}

		
		if (useGUI)
		{
			MainWindow.startBareGUI();
		}
		
		if (outputDirectory.isEmpty())
		{
			if (useGUI)
			{
				MainWindow.startBareGUI();
				outputDirectory = MainWindow.getInstance().getOutputDir();
			}
			else 
			{
				Log.fatal("No output directory provided");
				options.printHelp();
				System.exit(0);
			}
		}
		
		Scan.instantiate(useGUI, outputDirectory);
		
		if(useGUI)
		{
			MainWindow.getInstance().showFullGUI();
		}
		
	}

	private static void checkHelp()
	{
		if (commands.hasOption(ProgramOptions.HELP_OPTION))
		{
			options.printHelp();
			System.exit(0);
		}
	}		
	
	private static String getOutputDir()
	{
		if (commands.hasOption(ProgramOptions.OUTPUT_DIR_OPTION))
		{
			return commands.getOptionValue(ProgramOptions.OUTPUT_DIR_OPTION);
		}
		return "";
	}

	
	private static boolean commandLineOnly()
	{
		return commands.hasOption(ProgramOptions.BATCH_OPTION);
	}
	
	private static String getConfigFile()
	{
		if (commands.hasOption(ProgramOptions.CONFIG_FILE_OPTION))
		{
			return commands.getOptionValue(ProgramOptions.CONFIG_FILE_OPTION);
		}
		return "";
	}
	
}
