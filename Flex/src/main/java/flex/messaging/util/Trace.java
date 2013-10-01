/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2002 - 2007 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/
package flex.messaging.util;

import java.util.Date;


/**
 * Primitive run-time tracing class
 *
 * Code as follows:
 * if (Trace.foo)
 *     Trace.trace("trace msg"...);
 *
 * Enable as follows:
 * java -Dtrace.foo -Dtrace.foo2 -Dtrace.foo3 or -Dtrace.all
 *
 * Special flags:
 * -Dtrace.flex                  -- enables all tracing
 * -Dtrace.foo                   -- enables tracing on foo subsystem
 * -Dtrace.timeStamp             -- timeStamp all output lines
 * -Dtrace.caller                -- print the Class:method caller
 * -Dtrace.stackLines=10         -- print 10 stack lines
 * -Dtrace.stackPrefix=java.lang -- print the stack up to java.lang
 *
 * Add new xxx members as desired.
 *
 * @exclude
 */

public class Trace 
{
    public static final boolean config = (System.getProperty("trace.config") != null);

    public static final boolean amf = (System.getProperty("trace.amf") != null);
    public static final boolean remote = amf || (System.getProperty("trace.remote") != null);
    public static final boolean ssl = (System.getProperty("trace.ssl") != null);

    public static final boolean rtmp = (System.getProperty("trace.rtmp") != null);
    public static final boolean command = rtmp || (System.getProperty("trace.command") != null);
    public static final boolean error = rtmp || (System.getProperty("trace.error") != null);
    public static final boolean message = rtmp || (System.getProperty("trace.message") != null);
    public static final boolean resolve = rtmp || (System.getProperty("trace.resolve") != null);
    public static final boolean transport = rtmp || (System.getProperty("trace.transport") != null);
    public static final boolean ack = rtmp || (System.getProperty("trace.ack") != null);
    public static final boolean io = rtmp || (System.getProperty("trace.io") != null);
    public static final boolean threadpool = rtmp || (System.getProperty("trace.threadpool") != null);

    // print just the stack caller
    public static final boolean caller = (System.getProperty("trace.caller") != null);
    // print stack up to the prefix
    public static final String stackPrefix = System.getProperty("trace.stackPrefix");

    // print this number of stack lines
    public static int stackLines = 0;
    static {
        try 
        {
            stackLines = Integer.parseInt(System.getProperty("trace.stackLines"));
        } 
        catch (NumberFormatException e) 
        {
        }
    }
    // print a timestamp with each line
    public static final boolean timeStamp = (System.getProperty("trace.timeStamp") != null);

    /**
     * Write the string as a line to the trace stream. If the
     * "stack" property is enabled, then the caller's stack call
     * is also shown in the date.
     * 
     * @param str string to write to the trace stream
     */
    public static void trace(String str) 
    {
        if (timeStamp)
            System.err.print(new Date());

        if(caller)
            System.err.print(ExceptionUtil.getCallAt(new Throwable(), 1) + " ");

        System.err.println(str);

        if (stackLines > 0)
            System.err.println(ExceptionUtil.getStackTraceLines(new Throwable(), stackLines));
        else if (stackPrefix != null)
            System.err.println(ExceptionUtil.getStackTraceUpTo(new Throwable(), stackPrefix));
    }
}

