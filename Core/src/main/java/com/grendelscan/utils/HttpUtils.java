package com.grendelscan.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.NoHttpResponseException;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.io.AbstractMessageParser;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.CharArrayBuffer;

import com.grendelscan.GUI.http.transactionDisplay.HttpFormatException;
import com.grendelscan.logging.Log;
import com.grendelscan.requester.TransactionSource;
import com.grendelscan.requester.http.CookieJar;
import com.grendelscan.requester.http.apache_overrides.serializable.SerializableBasicCookie;
import com.grendelscan.requester.http.apache_overrides.serializable.SerializableHttpHeader;
import com.grendelscan.requester.http.apache_overrides.serializable.SerializableStatusLine;
import com.grendelscan.requester.http.dataHandling.containers.DataContainerUtils;
import com.grendelscan.requester.http.dataHandling.containers.NameValuePairDataContainer;
import com.grendelscan.requester.http.dataHandling.data.DataUtils;
import com.grendelscan.requester.http.transactions.HttpTransactionFields;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.requester.http.wrappers.HttpHeadersWrapper;
import com.grendelscan.requester.http.wrappers.HttpMessageWrapper;
import com.grendelscan.requester.http.wrappers.HttpRequestWrapper;
import com.grendelscan.requester.http.wrappers.HttpResponseWrapper;
import com.grendelscan.utils.dataFormating.encoding.UrlEncodingUtils;

public class HttpUtils
{
	/**
	 * 
	 * @param entity
	 * @param maxSize Set to zero for no limit
	 * @return
	 * @throws IOException
	 */
	public static byte[] entityToByteArray(final HttpEntity entity, int maxSize) throws IOException
	{
		if (maxSize <= 0)
		{
			maxSize = Integer.MAX_VALUE;
		}
		if (entity == null)
		{
			throw new IllegalArgumentException("HTTP entity may not be null");
		}
		InputStream instream = entity.getContent();
		if (instream == null)
		{
			return new byte[] {};
		}
		int i = (int) entity.getContentLength();
		if (entity.getContentLength() > maxSize)
		{
			i = maxSize;
		}
		else if (i < 0)
		{
			i = 4096;
		}
		ByteArrayBuffer buffer = new ByteArrayBuffer(i);
		try
		{
			byte[] tmp = new byte[4096];
			int l;
			while ((l = instream.read(tmp)) != -1 && buffer.length() <= maxSize)
			{
				buffer.append(tmp, 0, l);
			}
		}
		finally
		{
			instream.close();
		}
		return buffer.toByteArray();
	}
	
	public static boolean isRedirectCode(int httpStatusCode)
	{
		return httpStatusCode == 301 || httpStatusCode == 302 || httpStatusCode == 303 || httpStatusCode == 307;
	}
	
	
	/**
	 * It might seem strange to say that a 302 (or other redirect) isn't a file
	 * found. Keep in mind two things. First, many file not found messages are
	 * in redirects. Second, the redirect's destination will be tested as a file
	 * found (if it is found).
	 * 
	 * @param httpStatusCode
	 * @return
	 */
	public static boolean fileExists(int httpStatusCode)
	{
		boolean exists = false;
		// 12/10/10 - changed this to include all 3xx codes
		if (((httpStatusCode >= 100) && (httpStatusCode < 400))
		        || ((httpStatusCode >= 401) && (httpStatusCode <= 403)))
		{
			exists = true;
		}
		return exists;
	}
	
	public static Set<String> getSetCookieNames(StandardHttpTransaction transaction)
	{
		Set<String> names = new HashSet<String>();
		for(Cookie cookie: getSetCookies(transaction))
		{
			names.add(cookie.getName());
		}
		
		return names;
	}
	
	public static List<SerializableBasicCookie> getSetCookies(HttpTransactionFields transaction)
	{
		List<SerializableBasicCookie> cookies = new ArrayList<SerializableBasicCookie>();
		for (Header setCookie: transaction.getResponseWrapper().getHeaders().getHeaders("Set-Cookie"))
		{
			try
			{
				for (Cookie cookie: CookieJar.getCookieSpec().parse(setCookie, transaction.getCookieOrigin()))
				{
					cookies.add(new SerializableBasicCookie(cookie));
				}
			}
			catch (MalformedCookieException e)
			{
				Log.error("Problem with set-cookie header in HttpUtils.getSetCookies: " + e.toString(), e);
			}
		}
		return cookies;
	}
	
	public static Cookie getSetCookie(StandardHttpTransaction transaction, String cookieName)
	{
		for (Header setCookie: transaction.getResponseWrapper().getHeaders().getHeaders("Set-Cookie"))
		{
			try
			{
				for (Cookie cookie: transaction.getCookieJar().getCookieSpec().parse(setCookie, transaction.getCookieOrigin()))
				{
					if (cookie.getName().equalsIgnoreCase(cookieName))
					{
						return cookie;
					}
				}
			}
			catch (MalformedCookieException e)
			{
				Log.error("Problem with set-cookie header in HttpUtils.getSetCookies: " + e.toString(), e);
			}
		}
		return null;
	}

	private static void parseMessage(HttpMessageWrapper wrapper, HttpTransactionByteInputBuffer inputBuffer, BasicLineParser lineParser) throws IOException, HttpException
	{
		Header headers[] = AbstractMessageParser.parseHeaders(inputBuffer, 0, 0, lineParser);
		ByteArrayOutputStream bodyStream = new ByteArrayOutputStream();
		int b;
		while ((b = inputBuffer.read()) >= 0)
		{
			bodyStream.write(b);
		}
		wrapper.setBody(bodyStream.toByteArray());
		wrapper.setHeaders(new HttpHeadersWrapper(headers));

	}
	public static HttpRequestWrapper parseRequest(byte[] data) throws IOException, HttpException
	{
		HttpTransactionByteInputBuffer inputBuffer = new HttpTransactionByteInputBuffer(data);
		CharArrayBuffer lineBuf = new CharArrayBuffer(128);

        if (inputBuffer.readLine(lineBuf) == -1) {
            throw new NoHttpResponseException("No CRLFs found. Try again.");
        }
        
        ParserCursor cursor = new ParserCursor(0, lineBuf.length());
		BasicLineParser lineParser = BasicLineParser.DEFAULT;
		RequestLine requestLine = lineParser.parseRequestLine(lineBuf, cursor);
		HttpRequestWrapper wrapper = new HttpRequestWrapper(-123);
		wrapper.setMethod(requestLine.getMethod());
		wrapper.setURI(requestLine.getUri(), false);
		wrapper.setVersion(requestLine.getProtocolVersion());
		parseMessage(wrapper, inputBuffer, lineParser);
		
		return wrapper;
	}
	
	public static HttpResponseWrapper parseResponse(byte[] data) throws IOException, HttpException
	{
		HttpTransactionByteInputBuffer inputBuffer = new HttpTransactionByteInputBuffer(data);
		CharArrayBuffer lineBuf = new CharArrayBuffer(128);

        if (inputBuffer.readLine(lineBuf) == -1) {
            throw new NoHttpResponseException("No CRLFs found. Try again.");
        }
        
        ParserCursor cursor = new ParserCursor(0, lineBuf.length());
		BasicLineParser lineParser = BasicLineParser.DEFAULT;
		StatusLine status = lineParser.parseStatusLine(lineBuf, cursor);
		HttpResponseWrapper wrapper = new HttpResponseWrapper(-123);
		wrapper.setStatusLine(new SerializableStatusLine(status));

		parseMessage(wrapper, inputBuffer, lineParser);
		return wrapper;
	}
	
	static private Pattern httpVersionPattern = Pattern.compile("^(\\w+)/(\\d+)\\.(\\d+)$");
	static private Pattern headerPattern = Pattern.compile("([^ \r\n\t]+): ([^\r\n]+)");
	public static StandardHttpTransaction parseIntoHttpRequest(TransactionSource source, byte[] rawRequest, int testJobId) throws HttpFormatException, URISyntaxException
	{
		String requestString = new String(rawRequest, StringUtils.getDefaultCharset()); 
		String method = "";
		String uri = "";
		String protocolVersion = "";
		String bodyString = "";
		Pattern requestPattern = Pattern.compile("^([^ \t\r\n]+) ([^ \t\r\n]+) ([^ \t\r\n]+)\r?\n((?:[^ \r\n\t]+: [^\r\n]+\r?\n)*+)\r?\n(.*)$", Pattern.DOTALL);
		Matcher requestMatcher = requestPattern.matcher(requestString);
		if (requestMatcher.matches())
		{
			method = requestMatcher.group(1);
			uri = requestMatcher.group(2);
			protocolVersion = requestMatcher.group(3);
			String rawHeaders = requestMatcher.group(4);
			bodyString = requestMatcher.group(5);
			if (bodyString == null)
			{
				bodyString = "";
			}
			if (rawHeaders == null)
			{
				rawHeaders = "";
			}
	
			int major, minor;
			String protocol = "";
			Matcher versionMatcher = httpVersionPattern.matcher(protocolVersion);
			if (versionMatcher.find())
			{
				protocol = versionMatcher.group(1);
				major = Integer.valueOf(versionMatcher.group(2));
				minor = Integer.valueOf(versionMatcher.group(3));
			}
			else
			{
				throw new HttpFormatException("Invalid protocol version format. It should look something like \"HTTP/1.0\".");
			}


			String host = URIStringUtils.getHost(uri);
			Matcher headerMatcher = headerPattern.matcher(rawHeaders);
			List<SerializableHttpHeader> headers = new ArrayList<SerializableHttpHeader>();
			while(headerMatcher.find())
			{
				String name = UrlEncodingUtils.decodeUrl(headerMatcher.group(1));
				String value = UrlEncodingUtils.decodeUrl(headerMatcher.group(2));
				if (name.equalsIgnoreCase("Host"))
				{
					host = value;
				}
				headers.add(new SerializableHttpHeader(name, value));
			}

			int port = URIStringUtils.getPort(uri);
			boolean ssl = URIStringUtils.getScheme(uri).equalsIgnoreCase("https") ? true : false;
			byte[] body = bodyString.getBytes(StringUtils.getDefaultCharset());
			StandardHttpTransaction transaction = new StandardHttpTransaction(source, testJobId);
			if (body.length > 0)
			{
				transaction.getRequestWrapper().setBody(body);
			}
			transaction.getRequestWrapper().setMethod(method);
			transaction.getRequestWrapper().setURI(uri, true);
			transaction.getRequestWrapper().setVersion(protocol, major, minor);
			transaction.getRequestWrapper().setSecure(ssl);
			transaction.getRequestWrapper().setNetworkHost(host);
			transaction.getRequestWrapper().setNetworkPort(port);
			
			transaction.getRequestWrapper().getHeaders().addHeaders(headers.toArray(new Header[0]));
			
			return transaction;
		}
		throw new HttpFormatException("Unparsable request format.");
	}

	public static List<String> getAllQueryParameterNames(StandardHttpTransaction transaction)
	{
		List<String> names = new ArrayList<String>(1);
		
		for (NameValuePairDataContainer param: DataContainerUtils.getAllNamedContaners(transaction.getTransactionContainer()))
		{
			names.add(new String(DataUtils.getBytes(param.getNameData())));
		}
		return names;
	}
	
}
