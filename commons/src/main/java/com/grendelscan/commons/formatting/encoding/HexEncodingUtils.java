/**
 * 
 */
package com.grendelscan.commons.formatting.encoding;

import java.util.Arrays;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.output.ByteArrayOutputStream;

import com.grendelscan.commons.formatting.DataFormatException;

/**
 * @author david
 * 
 */
public class HexEncodingUtils
{
    private final static Hex hex = new Hex();

    public static final byte[] HEX_DIGITS_LOWER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    public static final byte[] HEX_DIGITS_UPPER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    public static byte[] decodeHex(final byte[] data) throws DataFormatException
    {
        try
        {
            return hex.decode(data);
        }
        catch (DecoderException e)
        {
            throw new DataFormatException(e);
        }
    }

    public static byte[] encodeHex(final byte[] data, final byte[] toDigits, final boolean zero_prefix, final boolean slash_x)
    {
        int originalLength = data.length;
        byte[] out;
        int newPosition = 0;
        if (zero_prefix)
        {
            out = new byte[originalLength * 2 + 2];
            out[0] = '0';
            out[1] = 'x';
            newPosition = 2;
        }
        else if (slash_x)
        {
            out = new byte[originalLength * 4];
        }
        else
        {
            out = new byte[originalLength * 2];
        }

        for (int oldPosition = 0; oldPosition < originalLength; oldPosition++)
        {
            if (slash_x)
            {
                out[newPosition++] = '\\';
                out[newPosition++] = 'x';
            }
            out[newPosition++] = toDigits[(0xF0 & data[oldPosition]) >>> 4];
            out[newPosition++] = toDigits[0x0F & data[oldPosition]];
        }
        return out;
    }

    public static byte[] simplifyHexFormat(final byte[] data)
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] _data = data;
        if (_data[0] == '0' && (_data[1] == 'x' || _data[1] == 'X'))
        {
            _data = Arrays.copyOfRange(data, 2, data.length);
        }
        for (byte b : _data)
        {
            if (b >= 'A' && b <= 'F' || b >= 'a' && b <= 'f' || b >= '0' && b <= '9')
            {
                output.write(b);
            }
        }
        return output.toByteArray();
    }

}
