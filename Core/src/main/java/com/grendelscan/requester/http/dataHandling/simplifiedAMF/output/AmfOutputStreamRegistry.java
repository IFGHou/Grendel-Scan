/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.simplifiedAMF.output;

import java.io.*;
import java.util.*;

import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AmfOutputStream;

/**
 * @author david
 *
 */
public class AmfOutputStreamRegistry
{
	private static AmfOutputStreamRegistry instance;
	private final Map<OutputStream, AmfOutputStream> registry;
	
	public static void initialize()
	{
		instance = new AmfOutputStreamRegistry();
	}
	
	private AmfOutputStreamRegistry()
	{
		registry = new HashMap<OutputStream, AmfOutputStream>();
	}


	/**
	 * @return the registry
	 */
	public static AmfOutputStream getStream(OutputStream out)
	{
		if (out instanceof AmfOutputStream)
			return (AmfOutputStream) out;
		if (instance.registry.containsKey(out))
			return instance.registry.get(out);
		AmfOutputStream amfOut = new AmfOutputStream(out);;
		instance.registry.put(out, amfOut);
		return amfOut;
	}
	
	public static void register(OutputStream out, AmfOutputStream amf)
	{
		instance.registry.put(out, amf);
	}
	
	public static void unregister(OutputStream out)
	{
		instance.registry.remove(out);
	}
}
