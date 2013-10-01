/**
 * 
 */
package com.grendelscan.commons.flex.output;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.grendelscan.commons.flex.AmfOutputStream;

/**
 * @author david
 * 
 */
public class AmfOutputStreamRegistry
{
    private static AmfOutputStreamRegistry instance;

    /**
     * @return the registry
     */
    public static AmfOutputStream getStream(final OutputStream out)
    {
        if (out instanceof AmfOutputStream)
        {
            return (AmfOutputStream) out;
        }
        if (instance.registry.containsKey(out))
        {
            return instance.registry.get(out);
        }
        AmfOutputStream amfOut = new AmfOutputStream(out);
        ;
        instance.registry.put(out, amfOut);
        return amfOut;
    }

    public static void initialize()
    {
        instance = new AmfOutputStreamRegistry();
    }

    public static void register(final OutputStream out, final AmfOutputStream amf)
    {
        instance.registry.put(out, amf);
    }

    public static void unregister(final OutputStream out)
    {
        instance.registry.remove(out);
    }

    private final Map<OutputStream, AmfOutputStream> registry;

    private AmfOutputStreamRegistry()
    {
        registry = new HashMap<OutputStream, AmfOutputStream>();
    }
}
