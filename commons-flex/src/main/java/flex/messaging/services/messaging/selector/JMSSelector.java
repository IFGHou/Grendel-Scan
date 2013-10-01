/*
 * @(#)JMSSelector.java 1.28 00/11/16
 * 
 * Copyright 1999-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc. Use is subject to license terms.
 */
package flex.messaging.services.messaging.selector;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

import flex.messaging.MessageException;
import flex.messaging.log.LogCategories;
import flex.messaging.messages.Message;

/**
 * Provides JMS selector capability to a MessageConsumer.
 * 
 * Uses a SQL parser written in SQLParser.jj
 * 
 * @author Farrukh Najmi
 * @exclude
 */
public class JMSSelector
{
    public static final String LOG_CATEGORY = LogCategories.MESSAGE_SELECTOR; // Because we're not always JMS-specific.
    private static final int PARSE_FAILURE = 10600;
    private static final int BAD_TYPE_COMPARISON = 10601;
    private static final int PARSER_ERROR = 10602;
    static final boolean debug = false;

    String pattern;
    Message msg;

    /**
     * Class Constructor.
     * 
     * @param pattern
     */
    public JMSSelector(String pattern)
    {
        if (pattern == null)
        {
            pattern = "";
        }

        // reason for passing the msg is to prevent dependency on com.sun.jms.*
        this.msg = null;
        this.pattern = pattern;
        InputStream stream = new ByteArrayInputStream(pattern.getBytes());

    }

    /**
     * Returns the pattern used by this selector.
     * 
     */
    public String getPattern()
    {
        return pattern;
    }

    /**
     * Sets the pattern that will be used by this selector to perform matches Must conform to SQL-92 specification for a SQL pattern.
     */
    public void setPattern(String p)
    {
        pattern = p;
        InputStream stream = new ByteArrayInputStream(pattern.getBytes());

        try
        {
            match(msg);
        }
        catch (MessageException me)
        {
            throw me;
        }
        catch (Throwable t)
        {
            throw new MessageException(t);
        }
    }

    /**
     * Matches the message against the selector expression.
     * 
     * @param msg
     *            The message to match against.
     * @return true if the message headers match the selector; otherwise false.
     * @exception JMSSelectorException
     */
    public boolean match(Message msg)
    {
        boolean matched = false;
        if (pattern.equals(""))
        {
            matched = true; // No selector
        }
        else
        {
        }
        return matched;
    }

    /**
     * Used to determine selector match in a SQL LIKE expression. For example:
     * 
     * <pre>
     * str LIKE patternStr [ESCAPE escapeChar]
     * </pre>
     * 
     * @param patternStr
     *            The pattern used in SQL LIKE statement
     * @param str
     *            The string being compared with patternStr in SQL LIKE statement
     * @param escapeChar
     *            The escape character used to treat wildcards '_' and '%' as normal
     * 
     * @return <code>true</code> if the string matches the pattern; otherwise false.
     */
    boolean matchPattern(String patternStr, String str, char escapeChar)
    {
        boolean matched = false;
        String escapeCharStr = String.valueOf(escapeChar);
        String wildCards = "_%";
        String delims = wildCards + escapeCharStr;
        boolean escaped = false;
        int index = 0;
        String tok = null;

        try
        {
            if (debug)
            {
                System.err.println("JMSSelector:matchPattern patternStr = \'" + patternStr + "\' str = \'" + str + "\'");
            }
            if (str != null)
            {
                StringTokenizer st = new StringTokenizer(patternStr, delims, true);

                // Parse string into a Collection of tokens since we will need
                // to peek forward as we
                // scan tokens
                ArrayList tokens = new ArrayList();
                int k = 1;
                while (st.hasMoreTokens())
                {
                    tok = st.nextToken();
                    if (debug)
                    {
                        System.err.println(k++ + " matchPattern Token=" + tok);
                    }
                    tokens.add(tok);
                }

                matched = true;

                // Iterate over tokens list and match each token with str
                int numTokens = tokens.size();
                for (int i = 0; i < numTokens; i++)
                {
                    tok = (String) tokens.get(i);

                    // Token can be a delimeter or actual token
                    if (tok.equals(escapeCharStr) && (!escaped))
                    {
                        // Remember that the next character in patterStr must be
                        // treated literally
                        escaped = true;
                    }
                    else if (tok.equals("%") && (!escaped))
                    {
                        if (i == (numTokens - 1))
                        {
                            // wildcard is last character in pattern,
                            // match entire string.
                            index = str.length();
                        }
                        else if (i != numTokens - 1)
                        { // There are more tokens. If not then we have a
                          // match

                            // Now scan forward
                            int _cnt = 0; // count of '_' delimeters
                                          // encountered
                            ++i;
                            for (; i < numTokens; i++)
                            {
                                tok = (String) tokens.get(i);

                                if (tok.equals(escapeCharStr) && (!escaped))
                                {
                                    // Remember that the next character in
                                    // patterStr must be treated literally
                                    escaped = true;
                                }
                                else if (tok.equals("%") && (!escaped))
                                {
                                    // % followed by % is same as %
                                }
                                else if (tok.equals("_") && (!escaped))
                                {
                                    ++_cnt;
                                }
                                else
                                {
                                    // This is the nextNonDelimTok
                                    int oldIndex = index;

                                    if (i == (numTokens - 1))
                                    {
                                        // Not a general purpose fix for
                                        // wildcard matching bug.
                                        // At least handle case when
                                        // only one wildcard in pattern
                                        // that has a group of characters
                                        // trailing it.
                                        if (str.endsWith(tok))
                                        {
                                            index = str.length() - tok.length();
                                        }
                                        else
                                        {
                                            matched = false;
                                            if (debug)
                                            {
                                                System.err.println("no matched5 for token: '" + tok + "'");
                                            }
                                        }
                                    }
                                    else
                                    {
                                        index = str.indexOf(tok, index);
                                    }

                                    if (index < 0)
                                    {
                                        matched = false;
                                        if (debug)
                                        {
                                            System.err.println("no matched1 for token: '" + tok + "'");
                                        }
                                    }
                                    else
                                    {
                                        // Make sure that we have _cnt
                                        // charecters between old index and new
                                        // index
                                        if (index - oldIndex >= _cnt)
                                        {
                                            index += tok.length();
                                            if (debug)
                                            {
                                                System.err.println("matched1: " + str.substring(0, index));
                                            }
                                        }
                                        else
                                        {
                                            matched = false;
                                            if (debug)
                                            {
                                                System.err.println("no matched 2 for token: '" + tok + "'");
                                            }
                                        }
                                    }
                                    escaped = false;
                                    break;
                                }
                            }
                        }
                    }
                    else if (tok.equals("_") && (!escaped))
                    {
                        index++;
                        if (debug)
                        {
                            System.err.println("matched2: " + str.substring(0, index));
                        }
                    }
                    else
                    {
                        // Compare token read with corresponding string
                        int tokLen = tok.length();

                        if (debug)
                        {
                            System.err.println(index + " " + tokLen);
                        }
                        if (index + tokLen <= str.length())
                        {
                            String subStr = null;

                            try
                            {
                                subStr = str.substring(index, index + tokLen);
                            }
                            catch (StringIndexOutOfBoundsException e)
                            {
                                matched = false;
                                break;
                            }

                            if (!subStr.equalsIgnoreCase(tok))
                            {
                                matched = false;
                                if (debug)
                                {
                                    System.err.println("no matched3 for token: '" + tok + "'");
                                }

                                break;
                            }
                            else
                            {
                                index = index + tok.length();
                                if (debug)
                                {
                                    System.err.println("matched3: " + str.substring(0, index));
                                }
                            }
                        }
                        else
                        {
                            matched = false;
                            if (debug)
                            {
                                System.err.println("no matched4 for token: '" + tok + "'");
                            }

                            break;
                        }
                        escaped = false;
                    }
                }
            }
            if (matched && index != str.length())
            {
                if (debug)
                {
                    System.err.println("no match5(remainder): " + str.substring(index, str.length()));
                }
                matched = false;
            }
            if (debug)
            {
                System.err.println("JMSSelector:matchPattern patternStr = \'" + patternStr + "\' str = \'" + str + "\' matched = " + matched);
            }
        }
        catch (StringIndexOutOfBoundsException e)
        {
            matched = false;
            if (debug)
            {
                e.printStackTrace();
                System.err.println("HANDLED OUTOFBOUNDS JMSSelector:matchPattern patternStr = \'" + patternStr + "\' str = \'" + str + "\' matched = " + matched);

            }
        }

        return matched;
    }

    /**
     * Strip leading and trailing quotes from a String Literal. Also, the nested quote character is represented as 2 consecutive quotes, so replace all occurrances of double quotes with single quotes.
     */
    String processStringLiteral(String strLiteral)
    {

        // Strip leading and trailing quotes
        strLiteral = strLiteral.substring(1, strLiteral.length() - 1);

        // Replace all occurances of consecutive quotes as single quote.
        int index = strLiteral.indexOf("''");
        if (index > -1)
        {
            StringBuffer sb = new StringBuffer(strLiteral);
            while (index != -1)
            {
                sb.deleteCharAt(index);
                index = sb.toString().indexOf("''");
            }
            strLiteral = sb.toString();
        }
        return strLiteral;
    }
}
