package com.grendelscan.scan;


import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.grendelscan.logging.Log;
import com.grendelscan.utils.StringUtils;

public class ConfigurationManager 
{
	private static Configuration scannerConfiguration;

	public static void initializeConfiguration(String path)
	{
		try
		{
			scannerConfiguration = new PropertiesConfiguration(path);
		}
		catch (ConfigurationException e)
		{
			Log.fatal("Failed to open scanner.conf. Program will now exit: " + e.toString());
			System.exit(1);
		}

	}

/* TODO UCdetector: Remove unused code: 
	public static boolean containsKey(String key)
    {
	    return scannerConfiguration.containsKey(key);
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static BigDecimal getBigDecimal(String key, BigDecimal defaultValue)
    {
	    return scannerConfiguration.getBigDecimal(key, defaultValue);
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static BigDecimal getBigDecimal(String key)
    {
	    return scannerConfiguration.getBigDecimal(key);
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static BigInteger getBigInteger(String key, BigInteger defaultValue)
    {
	    return scannerConfiguration.getBigInteger(key, defaultValue);
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static BigInteger getBigInteger(String key)
    {
	    return scannerConfiguration.getBigInteger(key);
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static boolean getBoolean(String key, boolean defaultValue)
    {
	    return scannerConfiguration.getBoolean(key, defaultValue);
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static Boolean getBoolean(String key, Boolean defaultValue)
    {
	    return scannerConfiguration.getBoolean(key, defaultValue);
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static boolean getBoolean(String key)
    {
	    return scannerConfiguration.getBoolean(key);
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static byte getByte(String key, byte defaultValue)
    {
	    return scannerConfiguration.getByte(key, defaultValue);
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static Byte getByte(String key, Byte defaultValue)
    {
	    return scannerConfiguration.getByte(key, defaultValue);
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static byte getByte(String key)
    {
	    return scannerConfiguration.getByte(key);
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static double getDouble(String key, double defaultValue)
    {
	    return scannerConfiguration.getDouble(key, defaultValue);
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static Double getDouble(String key, Double defaultValue)
    {
	    return scannerConfiguration.getDouble(key, defaultValue);
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static double getDouble(String key)
    {
	    return scannerConfiguration.getDouble(key);
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static float getFloat(String key, float defaultValue)
    {
	    return scannerConfiguration.getFloat(key, defaultValue);
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static Float getFloat(String key, Float defaultValue)
    {
	    return scannerConfiguration.getFloat(key, defaultValue);
    }
*/

	public static float getFloat(String key)
    {
	    return scannerConfiguration.getFloat(key);
    }

	public static int getInt(String key, int defaultValue)
    {
	    return scannerConfiguration.getInt(key, defaultValue);
    }

	public static int getInt(String key)
    {
	    return scannerConfiguration.getInt(key);
    }

/* TODO UCdetector: Remove unused code: 
	public static Integer getInteger(String key, Integer defaultValue)
    {
	    return scannerConfiguration.getInteger(key, defaultValue);
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static Iterator getKeys()
    {
	    return scannerConfiguration.getKeys();
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static Iterator getKeys(String prefix)
    {
	    return scannerConfiguration.getKeys(prefix);
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static List getList(String key, List defaultValue)
    {
	    return scannerConfiguration.getList(key, defaultValue);
    }
*/

	public static List getList(String key)
    {
	    return scannerConfiguration.getList(key);
    }

/* TODO UCdetector: Remove unused code: 
	public static long getLong(String key, long defaultValue)
    {
	    return scannerConfiguration.getLong(key, defaultValue);
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static Long getLong(String key, Long defaultValue)
    {
	    return scannerConfiguration.getLong(key, defaultValue);
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static long getLong(String key)
    {
	    return scannerConfiguration.getLong(key);
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static Properties getProperties(String key)
    {
	    return scannerConfiguration.getProperties(key);
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static Object getProperty(String key)
    {
	    return scannerConfiguration.getProperty(key);
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static short getShort(String key, short defaultValue)
    {
	    return scannerConfiguration.getShort(key, defaultValue);
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static Short getShort(String key, Short defaultValue)
    {
	    return scannerConfiguration.getShort(key, defaultValue);
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static short getShort(String key)
    {
	    return scannerConfiguration.getShort(key);
    }
*/

	public static String getString(String key, String defaultValue)
    {
	    return StringUtils.unquote(scannerConfiguration.getString(key, defaultValue));
    }

	public static String getString(String key)
    {
	    return StringUtils.unquote(scannerConfiguration.getString(key));
    }

	public static String[] getStringArray(String key)
    {
		String a[] = scannerConfiguration.getStringArray(key);
		for (int index = 0; index < a.length; index++)
		{
			a[index] = StringUtils.unquote(a[index]);
		}
	    return a;
    }

/* TODO UCdetector: Remove unused code: 
	public static Configuration subset(String prefix)
    {
	    return scannerConfiguration.subset(prefix);
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static void addProperty(String key, Object value)
    {
	    scannerConfiguration.addProperty(key, value);
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static void clear()
    {
	    scannerConfiguration.clear();
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static void clearProperty(String key)
    {
	    scannerConfiguration.clearProperty(key);
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static boolean isEmpty()
    {
	    return scannerConfiguration.equals("");
    }
*/

/* TODO UCdetector: Remove unused code: 
	public static void setProperty(String key, Object value)
    {
	    scannerConfiguration.setProperty(key, value);
    }
*/
}
