/**
 * 
 */
package com.grendelscan.commons.formatting;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.NotImplementedException;

import com.grendelscan.commons.formatting.encoding.HexEncodingUtils;
import com.grendelscan.commons.formatting.encoding.HtmlEncodingUtils;
import com.grendelscan.commons.formatting.encoding.UrlEncodingUtils;

/**
 * @author david
 * 
 */
public class DataEncodingStream extends FilterOutputStream
{

    private final DataFormat format;
    private Base64 base64;

    /**
     * @param out
     */
    public DataEncodingStream(final OutputStream out, final DataFormat format)
    {
        super(out);
        switch (format.formatType)
        {
            case ALPHANUMERIC:
            case AMF:
            case ASCII_BINARY:
            case ASCII_TEXT:
            case FLOAT:
            case INTEGER:
            case NUMERIC:
                throw new NotImplementedException(format.formatType.toString() + " does not make sense for an encoding stream");
            case BASE64:
                base64 = new Base64();
                break;
            case BASE64_PRETTY:
                base64 = new Base64(format.options.BASE64_LINE_LENGTH, format.options.BASE64_LINE_DELIMITER, false);
                break;
            case BASE64_WEB:
                base64 = new Base64(true);
                break;

            case HEX_LOWER_SIMPLE:
            case HEX_LOWER_SLASH_X:
            case HEX_LOWER_WITH_0X_PREFIX:
            case HEX_UPPER_SIMPLE:
            case HEX_UPPER_SLASH_X:
            case HEX_UPPER_WITH_0X_PREFIX:
            case HTML_BASIC_ENTITIES:
            case HTML_FULL_ENTITIES:
            case URL_BASIC_ENCODED:
            case URL_ENCODED_QUERY_STRING:
            case URL_FULL_ENCODED:
                // Nothing required
        }
        this.format = format;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.FilterOutputStream#write(byte[])
     */
    @Override
    public void write(final byte[] b) throws IOException
    {
        write(b, 0, b.length);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.FilterOutputStream#write(byte[], int, int)
     */
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException
    {
        byte[] bytes;
        if (off > 0 || len < b.length)
        {
            bytes = Arrays.copyOfRange(b, off, off + len);
        }
        else
        {
            bytes = b;
        }

        switch (format.formatType)
        {
            case BASE64:
            case BASE64_PRETTY:
            case BASE64_WEB:
                out.write(base64.encode(bytes));
                break;

            case HEX_LOWER_SIMPLE:
                out.write(HexEncodingUtils.encodeHex(bytes, HexEncodingUtils.HEX_DIGITS_LOWER, false, false));
                break;
            case HEX_UPPER_SIMPLE:
                out.write(HexEncodingUtils.encodeHex(bytes, HexEncodingUtils.HEX_DIGITS_UPPER, false, false));
                break;
            case HEX_LOWER_WITH_0X_PREFIX:
                out.write(HexEncodingUtils.encodeHex(bytes, HexEncodingUtils.HEX_DIGITS_LOWER, true, false));
                break;
            case HEX_UPPER_WITH_0X_PREFIX:
                out.write(HexEncodingUtils.encodeHex(bytes, HexEncodingUtils.HEX_DIGITS_UPPER, true, false));
                break;
            case HEX_LOWER_SLASH_X:
                out.write(HexEncodingUtils.encodeHex(bytes, HexEncodingUtils.HEX_DIGITS_LOWER, false, true));
                break;
            case HEX_UPPER_SLASH_X:
                out.write(HexEncodingUtils.encodeHex(bytes, HexEncodingUtils.HEX_DIGITS_UPPER, false, true));
                break;

            case HTML_BASIC_ENTITIES:
                HtmlEncodingUtils.encodeHtmlDefault(out, bytes);
                break;
            case HTML_FULL_ENTITIES:
                out.write(HtmlEncodingUtils.encodeFullHtml(bytes));
                break;

            case URL_BASIC_ENCODED:
            case URL_ENCODED_QUERY_STRING:
            case URL_FULL_ENCODED:
                out.write(UrlEncodingUtils.encodeForParam(bytes));
                break;

            default:
                throw new NotImplementedException("This should not be reachable");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.FilterOutputStream#write(int)
     */
    @Override
    public void write(final int b) throws IOException
    {
        write(new byte[] { (byte) b }, 0, 1);
    }

}
