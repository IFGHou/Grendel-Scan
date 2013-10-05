/**
 * 
 */
package com.grendelscan.commons.formatting.encoding;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Formatter;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.StringUtils;
import com.grendelscan.commons.collections.BidiMap;

/**
 * @author david
 * 
 */
public class HtmlEncodingUtils {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(HtmlEncodingUtils.class);

	private static BidiMap<String, Character> htmlCodes = new BidiMap<String, Character>();

	static {
		htmlCodes.put("amp", new Character('&'));
		htmlCodes.put("lt", new Character('<'));
		htmlCodes.put("gt", new Character('>'));
		htmlCodes.put("quot", new Character('"'));
		htmlCodes.put("nbsp", new Character((char) 160));

		htmlCodes.put("lsquo", new Character('`'));
		htmlCodes.put("rsquo", new Character((char) 8217));

		htmlCodes.put("frasl", new Character((char) 47));
		htmlCodes.put("ndash", new Character((char) 8211));
		htmlCodes.put("mdash", new Character((char) 8212));
		htmlCodes.put("iexcl", new Character((char) 161));
		htmlCodes.put("cent", new Character((char) 162));
		htmlCodes.put("pound", new Character((char) 163));
		htmlCodes.put("curren", new Character((char) 164));
		htmlCodes.put("yen", new Character((char) 165));
		htmlCodes.put("brvbar", new Character((char) 166));
		htmlCodes.put("brkbar", new Character((char) 166));
		htmlCodes.put("sect", new Character((char) 167));
		htmlCodes.put("uml", new Character((char) 168));
		htmlCodes.put("die", new Character((char) 168));
		htmlCodes.put("copy", new Character((char) 169));
		htmlCodes.put("ordf", new Character((char) 170));
		htmlCodes.put("laquo", new Character((char) 171));
		htmlCodes.put("not", new Character((char) 172));
		htmlCodes.put("shy", new Character((char) 173));
		htmlCodes.put("reg", new Character((char) 174));
		htmlCodes.put("macr", new Character((char) 175));
		htmlCodes.put("hibar", new Character((char) 175));
		htmlCodes.put("deg", new Character((char) 176));
		htmlCodes.put("plusmn", new Character((char) 177));
		htmlCodes.put("sup2", new Character((char) 178));
		htmlCodes.put("sup3", new Character((char) 179));
		htmlCodes.put("acute", new Character((char) 180));
		htmlCodes.put("micro", new Character((char) 181));
		htmlCodes.put("para", new Character((char) 182));
		htmlCodes.put("middot", new Character((char) 183));
		htmlCodes.put("cedil", new Character((char) 184));
		htmlCodes.put("sup1", new Character((char) 185));
		htmlCodes.put("ordm", new Character((char) 186));
		htmlCodes.put("raquo", new Character((char) 187));
		htmlCodes.put("frac14", new Character((char) 188));
		htmlCodes.put("frac12", new Character((char) 189));
		htmlCodes.put("frac34", new Character((char) 190));
		htmlCodes.put("iquest", new Character((char) 191));
		htmlCodes.put("Agrave", new Character((char) 192));
		htmlCodes.put("Aacute", new Character((char) 193));
		htmlCodes.put("Acirc", new Character((char) 194));
		htmlCodes.put("Atilde", new Character((char) 195));
		htmlCodes.put("Auml", new Character((char) 196));
		htmlCodes.put("Aring", new Character((char) 197));
		htmlCodes.put("AElig", new Character((char) 198));
		htmlCodes.put("Ccedil", new Character((char) 199));
		htmlCodes.put("Egrave", new Character((char) 200));
		htmlCodes.put("Eacute", new Character((char) 201));
		htmlCodes.put("Ecirc", new Character((char) 202));
		htmlCodes.put("Euml", new Character((char) 203));
		htmlCodes.put("Igrave", new Character((char) 204));
		htmlCodes.put("Iacute", new Character((char) 205));
		htmlCodes.put("Icirc", new Character((char) 206));
		htmlCodes.put("Iuml", new Character((char) 207));
		htmlCodes.put("ETH", new Character((char) 208));
		htmlCodes.put("Ntilde", new Character((char) 209));
		htmlCodes.put("Ograve", new Character((char) 210));
		htmlCodes.put("Oacute", new Character((char) 211));
		htmlCodes.put("Ocirc", new Character((char) 212));
		htmlCodes.put("Otilde", new Character((char) 213));
		htmlCodes.put("Ouml", new Character((char) 214));
		htmlCodes.put("times", new Character((char) 215));
		htmlCodes.put("Oslash", new Character((char) 216));
		htmlCodes.put("Ugrave", new Character((char) 217));
		htmlCodes.put("Uacute", new Character((char) 218));
		htmlCodes.put("Ucirc", new Character((char) 219));
		htmlCodes.put("Uuml", new Character((char) 220));
		htmlCodes.put("Yacute", new Character((char) 221));
		htmlCodes.put("THORN", new Character((char) 222));
		htmlCodes.put("szlig", new Character((char) 223));
		htmlCodes.put("agrave", new Character((char) 224));
		htmlCodes.put("aacute", new Character((char) 225));
		htmlCodes.put("acirc", new Character((char) 226));
		htmlCodes.put("atilde", new Character((char) 227));
		htmlCodes.put("auml", new Character((char) 228));
		htmlCodes.put("aring", new Character((char) 229));
		htmlCodes.put("aelig", new Character((char) 230));
		htmlCodes.put("ccedil", new Character((char) 231));
		htmlCodes.put("egrave", new Character((char) 232));
		htmlCodes.put("eacute", new Character((char) 233));
		htmlCodes.put("ecirc", new Character((char) 234));
		htmlCodes.put("euml", new Character((char) 235));
		htmlCodes.put("igrave", new Character((char) 236));
		htmlCodes.put("iacute", new Character((char) 237));
		htmlCodes.put("icirc", new Character((char) 238));
		htmlCodes.put("iuml", new Character((char) 239));
		htmlCodes.put("eth", new Character((char) 240));
		htmlCodes.put("ntilde", new Character((char) 241));
		htmlCodes.put("ograve", new Character((char) 242));
		htmlCodes.put("oacute", new Character((char) 243));
		htmlCodes.put("ocirc", new Character((char) 244));
		htmlCodes.put("otilde", new Character((char) 245));
		htmlCodes.put("ouml", new Character((char) 246));
		htmlCodes.put("divide", new Character((char) 247));
		htmlCodes.put("oslash", new Character((char) 248));
		htmlCodes.put("ugrave", new Character((char) 249));
		htmlCodes.put("uacute", new Character((char) 250));
		htmlCodes.put("ucirc", new Character((char) 251));
		htmlCodes.put("uuml", new Character((char) 252));
		htmlCodes.put("yacute", new Character((char) 253));
		htmlCodes.put("thorn", new Character((char) 254));
		htmlCodes.put("yuml", new Character((char) 255));
	}

	/**
	 * Based on the entity parser in Lobo
	 * 
	 * @param rawText
	 * @return
	 */
	public static byte[] decodeHtml(final byte[] data) {
		String rawText = new String(data);
		int startIdx = 0;
		StringBuffer sb = null;

		for (;;) {
			int ampIdx = rawText.indexOf("&", startIdx);
			if (ampIdx == -1) {
				if (sb == null) {
					return rawText.getBytes();
				}
				sb.append(rawText.substring(startIdx));
				return sb.toString().getBytes();
			}
			if (sb == null) {
				sb = new StringBuffer();
			}
			sb.append(rawText.substring(startIdx, ampIdx));
			int colonIdx = rawText.indexOf(";", ampIdx);
			if (colonIdx == -1) {
				sb.append('&');
				startIdx = ampIdx + 1;
				continue;
			}
			String spec = rawText.substring(ampIdx + 1, colonIdx);
			if (spec.startsWith("#")) {
				String number = spec.substring(1).toLowerCase();
				int decimal;
				try {
					if (number.startsWith("x")) {
						decimal = Integer.parseInt(number.substring(1), 16);
					} else {
						decimal = Integer.parseInt(number);
					}
					sb.append((char) decimal);
				} catch (NumberFormatException nfe) {
					sb.append(spec);
				}
			} else {
				int chInt = getEntityChar(spec);
				if (chInt == -1) {
					sb.append('&');
					sb.append(spec);
					sb.append(';');
				} else {
					sb.append((char) chInt);
				}
			}
			startIdx = colonIdx + 1;
		}
	}

	public static byte[] encodeFullHtml(final byte[] data) {
		Formatter formatter = new Formatter();
		for (byte b : data) {
			formatter.format("&%02X;", b);
		}
		return formatter.toString().getBytes(StringUtils.getDefaultCharset());
	}

	public static byte[] encodeHtmlDefault(final byte[] data) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			encodeHtmlDefault(out, data);
		} catch (IOException e) {
			LOGGER.error("Very weird problem encoding HTML: " + e.toString(), e);
		}
		return out.toByteArray();
	}

	public static void encodeHtmlDefault(final OutputStream out,
			final byte[] data) throws IOException {
		for (byte b : data) {
			if (htmlCodes.containsValue(b)) {
				out.write('&');
				out.write(htmlCodes.getKey((char) b).getBytes());
				out.write(';');
			} else {
				out.write(b);
			}
		}
	}

	private static final int getEntityChar(final String spec) {
		Character c = htmlCodes.get(spec);
		if (c == null) {
			String specTL = spec.toLowerCase();
			c = htmlCodes.get(specTL);
			if (c == null) {
				return -1;
			}
		}
		return c.charValue();
	}
}
