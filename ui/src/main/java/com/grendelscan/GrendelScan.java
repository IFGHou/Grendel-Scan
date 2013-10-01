/*
 * GrendelScan.java
 */

package com.grendelscan;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.ConfigurationManager;
import com.grendelscan.scan.Scan;
import com.grendelscan.ui.MainWindow;

/**
 * 
 * This is the main class for the program.
 * 
 * @author David Byrne
 * 
 */
public class GrendelScan
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GrendelScan.class);
    public static final String defaultConfigDirectory = "conf";
    public static final String version = "v2.0-alpha";
    public static final String versionHttpText = "Grendel-Scan/" + version;
    public static final String versionText = "Grendel-Scan " + version;
    private static CommandLine commands;
    private static ProgramOptions options;

    private static void checkHelp()
    {
        if (commands.hasOption(ProgramOptions.HELP_OPTION))
        {
            options.printHelp();
            System.exit(0);
        }
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

    private static String getOutputDir()
    {
        if (commands.hasOption(ProgramOptions.OUTPUT_DIR_OPTION))
        {
            return commands.getOptionValue(ProgramOptions.OUTPUT_DIR_OPTION);
        }
        return "";
    }

    public static void main(final String[] args)
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
                LOGGER.error("Error loading scan settings, defaults will be used: " + e.toString(), e);
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
                outputDirectory = MainWindow.getInstance().getOutputDir();
            }
            else
            {
                LOGGER.error("No output directory provided");
                options.printHelp();
                System.exit(0);
            }
        }

        Scan.instantiate(useGUI, outputDirectory);

        if (useGUI)
        {
            MainWindow.getInstance().showFullGUI();
        }

    }

}
