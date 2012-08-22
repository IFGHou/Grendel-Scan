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

import java.security.SecureRandom;
import java.util.Random;

/**
 * Static class that creates GUIDs according to the principles laid out in
 * <a href="http://www.opengroup.org/dce/info/draft-leach-uuids-guids-01.txt">UUIDs and GUIDs</a>.
 * Specifically, these GUIDs have elements specific to the time and place of
 * their creation; a unique TOD value (but the low-level IEEE 802/MAC address
 * of the host system is not determined). The rest of the GUID is a sequence of
 * random hex digits from a cryptographically strong random number generator.
 *
 *<p> This class differs from the Leach specification in a few ways. It does not use a
 * persistent store to track the clock sequence and does not coordinate ids
 * given out by 2 processes on the same machine.  It does not follow the formal
 * clock sequence guidelines but instead uses random numbers for all but the
 * timestamp.</p>
 */
public class UUIDUtils
{
    /**  Cryptographically strong random number generator. */
    private static SecureRandom _rand = new SecureRandom();

    private static Random _weakRand = new Random();

    /**
     * The spec indicates that our time value should be based on 100 nano
     * second increments but our time granularity is in milliseconds.
     * The spec also says we can approximate the time by doing an increment
     * when we dole out new ids in the same millisecond.  We can fit 10,000
     * 100 nanos into a single millisecond.
     */
    private static final int MAX_IDS_PER_MILLI = 10000;

    /**
     *  Any given time-of-day value can only be used once; remember the last used
     *  value so we don't reuse them.
     *  <p>NOTE: this algorithm assumes the clock will not be turned back.
     */
    private static long lastUsedTOD = 0;
    /** Counter to use when we need more than one id in the same millisecond. */
    private static int numIdsThisMilli = 0;

    /**  Hex digits, used for padding UUID strings with random characters. */
    private static final String alphaNum = "0123456789ABCDEF";

    /** 4 bits per hex character. */
    private static final int BITS_PER_DIGIT = 4;

    private static final int BITS_PER_INT = 32;
    private static final int BITS_PER_LONG = 64;
    private static final int DIGITS_PER_INT = BITS_PER_INT / BITS_PER_DIGIT;
    private static final int DIGITS_PER_LONG = BITS_PER_LONG / BITS_PER_DIGIT;

    /**
     *  @private
     */
    private static char[] UPPER_DIGITS = new char[] {
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
    };

    /**
     * Private constructor to prevent instances from being created.
     */
    private UUIDUtils()
    {
    }

    /**
     *
     * Use the createUUID function when you need a unique string that you will
     * use as a persistent identifier in a distributed environment. To a very
     * high degree of certainty, this function returns a unique value; no other
     * invocation on the same or any other system should return the same value.
     *
     * @return a Universally Unique Identifier (UUID)
     * Proper Format: `XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX'
     * where `X' stands for a hexadecimal digit (0-9 or A-F).
     * @author <a href="mailto:mnimer@macromedia.com">Mike Nimer</a>
     * @author <a href="mailto:pfriedman@macromedia.com">Paul Friedman</a>
     */
    public static String createUUID()
    {
        return createUUID(true);
    }

    /**
     * @param secure Boolean indicating whether to create a secure UUID.
     * @see #createUUID()
     */
    public static String createUUID(boolean secure)
    {
        Random rand = secure ? _rand : _weakRand;

        StringBuffer s = new StringBuffer(36);

        appendHexString(uniqueTOD(), false, 11, s);

        //  Just use random padding characters, but ensure that the high bit
        //  is set to eliminate chances of collision with an IEEE 802 address.
        s.append(  alphaNum.charAt( rand.nextInt(16) | 8 ) );

        //  Add random padding characters.
        appendRandomHexChars(32 - s.length(), rand, s);

        //insert dashes in proper position. so the format matches CF
        s.insert(8,"-");
        s.insert(13,"-");
        s.insert(18,"-");
        s.insert(23,"-");

        return s.toString();
    }

    /**
     * Converts a 128-bit UID encoded as a byte[] to a String representation.
     * The format matches that generated by createUID. If a suitable byte[]
     * is not provided, null is returned.
     *
     * @param ba byte[] 16 bytes in length representing a 128-bit UID.
     *
     * @return String representation of the UID, or null if an invalid
     * byte[] is provided.
     */
    public static String fromByteArray(byte[] ba)
    {
        if (ba != null && ba.length == 16)
        {
            StringBuffer result = new StringBuffer(36);
            for (int i = 0; i < 16; i++)
            {
                if (i == 4 || i == 6 || i == 8 || i == 10)
                    result.append("-");

                result.append(UPPER_DIGITS[(ba[i] & 0xF0) >>> 4]);
                result.append(UPPER_DIGITS[(ba[i] & 0x0F)]);
            }
            return result.toString();
        }

        return null;
    }

    /**
     * A utility method to check whether a String value represents a
     * correctly formatted UID value. UID values are expected to be
     * in the format generated by createUID(), implying that only
     * capitalized A-F characters in addition to 0-9 digits are
     * supported.
     *
     * @param uid The value to test whether it is formatted as a UID.
     *
     * @return Returns true if the value is formatted as a UID.
     */
    public static boolean isUID(String uid)
    {
        if (uid != null && uid.length() == 36)
        {
            char[] chars = uid.toCharArray();
            for (int i = 0; i < 36; i++)
            {
                char c = chars[i];

                // Check for correctly placed hyphens
                if (i == 8 || i == 13 || i == 18 || i == 23)
                {
                    if (c != '-')
                    {
                        return false;
                    }
                }
                // We allow capital alpha-numeric hex digits only
                else if (c < 48 || c > 70 || (c > 57 && c < 65))
                {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    /**
     * Converts a UID formatted String to a byte[]. The UID must be in the
     * format generated by createUID, otherwise null is returned.
     *
     * @param uid String representing a 128-bit UID.
     *
     * @return byte[] 16 bytes in length representing the 128-bits of the
     * UID or null if the uid could not be converted.
     */
    public static byte[] toByteArray(String uid)
    {
        if (isUID(uid))
        {
            byte[] result = new byte[16];
            char[] chars = uid.toCharArray();
            int r = 0;

            for (int i = 0; i < chars.length; i++)
            {
                if (chars[i] == '-')
                    continue;
                int h1 = Character.digit(chars[i], 16);
                i++;
                int h2 = Character.digit(chars[i], 16);
                result[r++] = (byte)(((h1 << 4) | h2) & 0xFF);
            }
            return result;
        }

        return null;
    }

    private static void appendRandomHexChars(int n, Random rand, StringBuffer result)
    {
        int digitsPerInt = DIGITS_PER_INT;
        while (n > 0)
        {
            int digitsToUse = Math.min(n, digitsPerInt);
            n -= digitsToUse;
            appendHexString(rand.nextInt(), true, digitsToUse, result);
        }
    }

    private static void appendHexString
        (long value, boolean prependZeroes, int nLeastSignificantDigits,
         StringBuffer result)
    {
        int bitsPerDigit = BITS_PER_DIGIT;

        long mask = (1L << bitsPerDigit) - 1;

        if (nLeastSignificantDigits < DIGITS_PER_LONG)
        {
            // Clear the bits that we don't care about.
            value &= (1L << (bitsPerDigit * nLeastSignificantDigits)) - 1;
        }

        // Reorder the sequence so that the first set of bits will become the
        // last set of bits.
        int i = 0;
        long reorderedValue = 0;
        if (value == 0)
        {
            // One zero is dumped.
            i++;
        }
        else
        {
            do
            {
                reorderedValue = (reorderedValue << bitsPerDigit) | (value & mask);
                value >>>= bitsPerDigit;
                i++;
            } while (value != 0);
        }

        if (prependZeroes)
        {
            for (int j = nLeastSignificantDigits - i; j > 0; j--)
            {
                result.append('0');
            }
        }


        // Dump the reordered sequence, with the most significant character
        // first.
        for (; i > 0; i--)
        {
            result.append(alphaNum.charAt((int) (reorderedValue & mask)));
            reorderedValue >>>= bitsPerDigit;
        }
    }


    /**
     *  @return a time value, unique for calls to this method loaded by the same classloader.
     */
    private static synchronized long uniqueTOD()
    {
        long currentTOD = System.currentTimeMillis();

        // Clock was set back... do not hang in this case waiting to catch up.
        // Instead, rely on the random number part to differentiate the ids.
        if (currentTOD < lastUsedTOD)
            lastUsedTOD = currentTOD;

        if (currentTOD == lastUsedTOD)
        {
            numIdsThisMilli++;
            /*
             * Fall back to the old technique of sleeping if we allocate
             * too many ids in one time interval.
             */
            if (numIdsThisMilli >= MAX_IDS_PER_MILLI)
            {
                while ( currentTOD == lastUsedTOD )
                {
                    try { Thread.sleep(1); } catch ( Exception interrupt ) { /* swallow, wake up */ }
                    currentTOD = System.currentTimeMillis();
                }
                lastUsedTOD = currentTOD;
                numIdsThisMilli = 0;
            }
        }
        else
        {
            //  We have a new TOD, reset the counter
            lastUsedTOD = currentTOD;
            numIdsThisMilli = 0;
        }

        return lastUsedTOD * MAX_IDS_PER_MILLI + numIdsThisMilli;
    }
}
