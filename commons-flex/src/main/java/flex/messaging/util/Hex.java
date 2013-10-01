/*************************************************************************
 * 
 * ADOBE CONFIDENTIAL __________________
 * 
 * Copyright 2002 - 2007 Adobe Systems Incorporated All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains the property of Adobe Systems Incorporated and its suppliers, if any. The intellectual and technical concepts contained herein are
 * proprietary to Adobe Systems Incorporated and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of
 * this information or reproduction of this material is strictly forbidden unless prior written permission is obtained from Adobe Systems Incorporated.
 **************************************************************************/
package flex.messaging.util;

/**
 * Code to read and write Base64-encoded text. Fairly special-purpose, not quite ready for general streaming as they don't let you drain less than everything that is currently available.
 * 
 * @exclude
 */
public class Hex
{
    private static final char digits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    public static class Decoder
    {
        private int filled = 0;
        private byte data[];
        private int work[] = { 0, 0 };

        public Decoder()
        {
            data = new byte[256];
        }

        public void decode(String encoded)
        {

            int estimate = 1 + encoded.length() / 2;

            if (filled + estimate > data.length)
            {
                int length = data.length * 2;
                while (length < filled + estimate)
                {
                    length *= 2;
                }
                byte[] newdata = new byte[length];

                System.arraycopy(data, 0, newdata, 0, filled);
                data = newdata;
            }

            for (int i = 0; i < encoded.length(); ++i)
            {
                work[0] = Character.digit(encoded.charAt(i), 16);
                i++;
                work[1] = Character.digit(encoded.charAt(i), 16);
                data[filled++] = (byte) (((work[0] << 4) | (work[1])) & 0xff);
            }
        }

        public byte[] drain()
        {
            byte[] r = new byte[filled];
            System.arraycopy(data, 0, r, 0, filled);
            filled = 0;
            return r;
        }

        public byte[] flush() throws IllegalStateException
        {
            return drain();
        }

        public void reset()
        {
            filled = 0;
        }

    }

    public static class Encoder
    {
        private StringBuffer output;

        public Encoder(int size)
        {
            output = new StringBuffer(size * 2);
        }

        private void encodeBlock(byte work)
        {
            output.append(digits[(work & 0xF0) >>> 4]);
            output.append(digits[(work & 0x0F)]);
        }

        public void encode(byte[] data)
        {
            encode(data, 0, data.length);
        }

        public void encode(byte[] data, int offset, int length)
        {
            int plainIndex = offset;

            while (plainIndex < (offset + length))
            {
                encodeBlock(data[plainIndex]);
                plainIndex++;
            }
        }

        public String drain()
        {
            String r = output.toString();
            output.setLength(0);
            return r;
        }

        public String flush()
        {
            return drain();
        }
    }

    public static void main(String[] args)
    {
        boolean printData = false;
        int randomLimit = 500;

        for (int myCount = 0; myCount < 10000; myCount++)
        {
            byte raw[] = new byte[(int) (Math.random() * randomLimit)];

            for (int i = 0; i < raw.length; ++i)
            {
                if ((i % 1024) < 256)
                    raw[i] = (byte) (i % 1024);
                else
                    raw[i] = (byte) ((int) (Math.random() * 255) - 128);
            }
            Hex.Encoder encoder = new Hex.Encoder(100);
            encoder.encode(raw);

            String encoded = encoder.drain();

            Hex.Decoder decoder = new Hex.Decoder();
            decoder.decode(encoded);
            byte check[] = decoder.flush();

            String mesg = "Success!";
            if (check.length != raw.length)
            {
                mesg = "***** length mismatch!";
            }
            else
            {
                for (int i = 0; i < check.length; ++i)
                {
                    if (check[i] != raw[i])
                    {
                        mesg = "***** data mismatch!";
                        break;
                    }
                }
            }
            if (mesg.indexOf("Success") == -1)
            {
                System.out.println(mesg + myCount);
                break;
            }

            if (printData)
            {
                System.out.println("Decoded: " + new String(raw));
                System.out.println("Encoded: " + encoded);
                System.out.println("Decoded: " + new String(check));
            }
        }
    }
}
