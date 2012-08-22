package com.grendelscan.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.grendelscan.logging.Log;

public class FileUtils
{
	/**
	 * Sets the path to have the correct direction of slashes
	 * 
	 * @param filePath
	 * @return
	 */
	public static String correctFilePathFormat(String filePath)
	{
		String newPath;
		if (isWindows())
		{
			newPath = filePath.replace('/', '\\');
		}
		else
		{
			newPath = filePath.replace('\\', '/');
		}
		return newPath;
	}
	
	/**
	 * Recursively deletes a directory. BE CAREFUL!
	 * @param path
	 * @return
	 */
	static public boolean deleteDirectory(File path)
	{
		if (path.exists())
		{
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++)
			{
				if (files[i].isDirectory())
				{
					deleteDirectory(files[i]);
				}
				else
				{
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}
	
	/**
	 * 
	 * @param filePath
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static List<String> getFileAsLines(String filePath) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		String line;
		ArrayList<String> lines = new ArrayList<String>();
		while ((line = reader.readLine()) != null)
		{
			lines.add(line);
		}
		reader.close();
		
		return lines;
	}
	
	private static boolean isWindows()
	{
		if (System.getProperty("os.name").toUpperCase().contains("WINDOWS"))
		{
			return true;
		}
		return false;
	}
	
	public static boolean writeToFile(String fileName, String string)
	{
		return writeToFile(fileName, string.getBytes(StringUtils.getDefaultCharset()));
	}
	
	public static boolean writeToFile(String fileName, byte[] bytes)
	{
		boolean good = false;
		try
		{
			OutputStream outfile = new FileOutputStream(fileName);
			outfile.write(bytes);
			outfile.close();
			good = true;
		}
		catch (FileNotFoundException e)
		{
			Log.error("Problem opening file for writing in SimpleDebugReport.writeReport: " + e.toString(), e);
		}
		catch (IOException e)
		{
			Log.error("IOException in SimpleDebugReportGenerator.generateReport: " + e.toString(), e);
		}
		return good;
	}
	
	public static boolean createDirectories(String directoryName)
	{
		return (new File(directoryName)).mkdirs();		
	}
	
	public static boolean fileExists(String directoryName)
	{
		return (new File(directoryName)).exists();
	}
	
	public static String readFile(String fileName)
	{
		String contents = "";
		
		try
		{
			FileInputStream infile = new FileInputStream(fileName);
			byte buffer[] = new byte[5000];
			int readBytes;
			while ((readBytes = infile.read(buffer)) > 0)
			{
				contents += new String(buffer, 0, readBytes);
			}
			infile.close();
		}
		catch (FileNotFoundException e)
		{
			Log.error("Problem opening file for writing in FileUtils.readFile: " + e.toString(), e);
		}
		catch (IOException e)
		{
			Log.error("IOException in FileUtils.readFile: " + e.toString(), e);
		}

		return contents;
	}
}
