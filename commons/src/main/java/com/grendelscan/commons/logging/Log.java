package com.grendelscan.commons.logging;
//package com.grendelscan.logging;
//
//import java.io.File;
//import java.io.IOException;
//
//import org.apache.log4j.Appender;
//import org.apache.log4j.AppenderSkeleton;
//import org.apache.log4j.AsyncAppender;
//import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.ConsoleAppender;
//import org.apache.log4j.FileAppender;
//import org.apache.log4j.Layout;
//import org.apache.log4j.Level;
//import org.apache.log4j.Logger;
//import org.apache.log4j.TTCCLayout;
//import org.apache.log4j.spi.LoggingEvent;
//
//import com.grendelscan.GrendelScan;
//import com.grendelscan.ui.MainWindow;
//import com.grendelscan.scan.ConfigurationManager;
//import com.grendelscan.scan.Scan;
//
//public class Log
//{
//	static class StatusWindowAppender extends AppenderSkeleton
//	{
//
//		@Override
//		public void close()
//		{
//			// Don't care
//		}
//
//		@Override
//		public boolean requiresLayout()
//		{
//			return false;
//		}
//
//		@Override
//		protected void append(LoggingEvent event)
//		{
//			if ((MainWindow.getInstance() != null) && (MainWindow.getInstance().getLogComposite() != null))
//			{
//				MainWindow.getInstance().getLogComposite().appendStatusText(event.getMessage().toString() + "\n");
//			}
//		}
//
//	}
//
//	static private AsyncAppender	appender;
//	static private ConsoleAppender	consoleAppender;
////	static private Logger			grendelLogger;
//	static private Logger			rootLogger;
//
//	static private Layout			verbaseLayout;
//	static final private Object consoleLock = new Object();
//
//	static
//	{
//		Logger.getRootLogger().setLevel(Level.FATAL);
////		grendelLogger = Logger.getLogger("com.grendelscan");
//		rootLogger = Logger.getRootLogger();
//
//		appender = new AsyncAppender();
//
//		verbaseLayout = new TTCCLayout();
//		consoleAppender = new ConsoleAppender(verbaseLayout);
//		rootLogger.addAppender(consoleAppender);
//
//		StatusWindowAppender statusWindowAppender = new StatusWindowAppender();
//		appender.addAppender(statusWindowAppender);
//
//		BasicConfigurator.configure(appender);
//
//	}
//
//
//	public static void debug(Object message)
//	{
//		synchronized (consoleLock)
//		{
//			rootLogger.debug(message);
//		}
//	}
//
//	public static void debug(Object message, Throwable t)
//	{
//		synchronized (consoleLock)
//		{
//			rootLogger.debug(message, t);
//		}
//	}
//
//	public static void error(Object message, Throwable e)
//	{
//		synchronized (consoleLock)
//		{
//			rootLogger.error(message, e);
//		}
//	}
//
//	public static void fatal(Object message)
//	{
//		synchronized (consoleLock)
//		{
//			rootLogger.fatal(message);
//		}
//	}
//
//	public static void fatal(Object message, Throwable t)
//	{
//		synchronized (consoleLock)
//		{
//			rootLogger.fatal(message, t);
//		}
//	}
//
//	public static Level getLevel()
//	{
//		synchronized (consoleLock)
//		{
//			return rootLogger.getLevel();
//		}
//	}
//
//	public static void info(Object message)
//	{
//		synchronized (consoleLock)
//		{
//			rootLogger.info(message);
//		}
//	}
//
//	public static void info(Object message, Throwable t)
//	{
//		synchronized (consoleLock)
//		{
//			rootLogger.info(message, t);
//		}
//	}
//
//	public synchronized static void initializeLibrary()
//	{
//		String logFilename = Scan.getInstance().getOutputDirectory() + File.separator + ConfigurationManager.getString("logging.error_log_file");
//		try
//		{
//			Appender fileAppender = new FileAppender(verbaseLayout, logFilename, false, false, 0);
//
//			rootLogger.addAppender(fileAppender);
//
//			rootLogger.setLevel(Level.WARN);
////			grendelLogger.setLevel(Level.TRACE);
//			String header = "\n" + GrendelScan.versionText + "\n\n";
//			header += "Java vendor: " + System.getProperty("java.vm.vendor") + "\n";
//			header += "Java VM: " + System.getProperty("java.vm.name") + " " + System.getProperty("java.runtime.version") + "\n";
//			header += "OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version") + "\n";
//
//			info(header);
//		}
//		catch (IOException e)
//		{
//			LOGGER.error("Problem setting up logging to file (" + logFilename + "): " + e.toString(), e);
//			System.exit(0);
//		}
//
//	}
//
//	public static void setLevel(Level level)
//	{
//		rootLogger.setLevel(level);
//	}
//
//	public static void trace(Object message)
//	{
////		grendelLogger.trace(message);
//		rootLogger.trace(message);
//	}
//
//	public static void trace(Object message, Throwable t)
//	{
////		grendelLogger.trace(message, t);
//		synchronized (consoleLock)
//		{
//			rootLogger.trace(message, t);
//		}
//	}
//
//	public static void warn(Object message)
//	{
////		grendelLogger.warn(message);
//		synchronized (consoleLock)
//		{
//			rootLogger.warn(message);
//		}
//	}
//
//	public static void warn(Object message, Throwable t)
//	{
////		grendelLogger.warn(message, t);
//		synchronized (consoleLock)
//		{
//			rootLogger.warn(message, t);
//		}
//	}
//}
