package com.grendelscan;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ProgramOptions
{
	public static final char	BATCH_OPTION		= 'b';
	public static final char	CONFIG_FILE_OPTION	= 'c';
	public static final char	HELP_OPTION			= 'h';
	public static final char	OUTPUT_DIR_OPTION	= 'o';
	public static final char	VERBOSE_OPTION		= 'v';
	private Option				batchMode;

	private Option				configFile;
	private Option				help;
	private Options				options;
	private Option				outputDirectory;
	private Option				verbose;

	public ProgramOptions()
	{
		options = new Options();
		batchMode = new Option("b", "batch", false, "Runs the program in batch mode, without the GUI");
		options.addOption(batchMode);

		configFile = new Option("c", "config-file", true, "Path to the scan configuration file");
		options.addOption(configFile);

		outputDirectory =
				new Option("o", "output-dir", true, "Output directory for the scan; overrides the config file");
		options.addOption(outputDirectory);

		help = new Option("h", "help", false, "This page");
		options.addOption(help);

		verbose = new Option("v", "verbose", false, "Print status messages to the console");
		options.addOption(verbose);
	}

	public CommandLine parseArguments(String[] args) throws ParseException
	{
		CommandLineParser parser = new GnuParser();
		return parser.parse(options, args);
	}

	public void printHelp()
	{
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp(" ", options);
	}
}
