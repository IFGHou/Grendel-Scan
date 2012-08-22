package com.grendelscan.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.factories.UriFactory;
import com.grendelscan.utils.dataFormating.encoding.UrlEncodingUtils;

public class URIStringUtils
{
	
	public static void main(String[] args)
	{
		printComponents("asdf");
		printComponents("asdf?fds");
		printComponents("as/df");
		printComponents("http://localhost");
		printComponents("http://localhost:88");
		printComponents("http://localhost:88#fds");
		printComponents("http://localhost:88?q=a&b=c");
		printComponents("http://127.0.0.1:88?q=a&b=c");
		printComponents("/fdsa/1111/asdf.fds?q=a&b=c");
		printComponents("/asdf?q=a&b=c");
		printComponents("/asdf?q=a&b=c#fdsa");
		printComponents("/");
		printComponents("/asdf");
		printComponents("#asdf");
		printComponents("http://www.example.com/classics/index.html");
		printComponents("http://www.example.com/classics/;JSESS=123456");
		printComponents("http://www.example.com/classics/;veryodd/index.html");
		printComponents("http://www.example.com/classics/index.html;JSESS=123456");
		printComponents("http://www.example.com/classics/index.html;JSESS=123456?a=1&b=2");
		printComponents("http://www.example.com/classics/index.html;JSESS=123456?a=1&b=2&c=asdf;fdsa");
		printComponents("http://www.example.com/classics/index.html" +
				"?a=1&b=2&c=asdf;fdsa");

	
//		parse2("asdf");
//		parse2("asdf?fds");
//		parse2("as/df");
//		parse2("http://localhost");
//		parse2("http://localhost:88");
//		parse2("http://localhost:88#fds");
//		parse2("http://localhost:88?q=a&b=c");
//		parse2("http://127.0.0.1:88?q=a&b=c");
//		parse2("/fdsa/1111/asdf.fds?q=a&b=c");
//		parse2("/asdf?q=a&b=c");
//		parse2("/asdf?q=a&b=c#fdsa");
//		parse2("/");
//		parse2("http://www.example.com/classics/index.html");
//		parse2("http://www.example.com/classics/;veryodd/index.html");
//		parse2("http://www.example.com/classics/;JSESS=123456");
//		parse2("http://www.example.com/classics/index.html;JSESS=123456");
//		parse2("http://www.example.com/classics/index.html;JSESS=123456?a=1&b=2");
//		parse2("http://www.example.com/classics/index.html;JSESS=123456?a=1&b=2&c=asdf;fdsa");
//		parse2("http://www.example.com/classics/index.html" +
//				"?a=1&b=2&c=asdf;fdsa");
}
	
	private static void printComponents(String uri)
	{
		String[] comps;
		try
		{
			comps = parseUriString(uri);
			System.out.println(
					uri + "\n"+
					"Scheme: " + comps[INDEX_SCHEME] + "\n" + 
					"Host: " + comps[INDEX_HOST] + "\n" + 
					"Port: " + comps[INDEX_PORT] + "\n" + 
					"Dir: " + comps[INDEX_DIRECTORY] + "\n" + 
					"File: " + comps[INDEX_FILE] + "\n" + 
					"Session: " + comps[INDEX_SESSION] + "\n" + 
					"Query: " + comps[INDEX_QUERY] + "\n" + 
					"Frag: " + comps[INDEX_FRAGMENT] + "\n" +
					"\n\n"
					);
		}
		catch (URISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	private static final int INDEX_SCHEME = 0;
	private static final int INDEX_HOST = 1;
	private static final int INDEX_PORT = 2;
	private static final int INDEX_DIRECTORY = 3;
	private static final int INDEX_FILE = 4;
	private static final int INDEX_SESSION = 5;
	private static final int INDEX_QUERY = 6;
	private static final int INDEX_FRAGMENT = 7; // <>#%{}|\^[]`
	
	
	// http :// host :443 /full/ filename query fragment
	private final static Pattern urlPattern =
	        Pattern
	                .compile( 
	                        "^(?:" + // beginning of optional hostname gorup
	                        "([a-z]+)://" + // scheme
	                        "([a-z0-9\\-\\.]++)" + // host
	                        "(?::" + // colon for port and begining of optional port group
	                        	"(\\d++)" + // port
	                        	")?" + // end of optional port group
	                        ")?+" + // end of optional hostname group  
	                        "([^\\?#]*/)?" + // directory
	                        "([^\\?/#;]*+)" + // file
	                        "(?:;([^?]+=[^?]+))?" + // session ID
	                        "(?:\\?([^#]*))?" + // query
	                        "(?:#(.*))?$", // fragment
	                        Pattern.CASE_INSENSITIVE);
	
//	public static String decodeUrlEncodedString(String url)
//	{
//		byte[] bytes = url.getBytes(StringUtils.getDefaultCharset());
//		
//		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//		
//		for (int i = 0; i < bytes.length; i++)
//		{
//			int b = bytes[i];
//			if (b == '+')
//			{
//				buffer.write(' ');
//			}
//			else if (b == '%')
//			{
//				int u = -1;
//				int l = -1;
//				try
//				{
//					u = Character.digit((char) bytes[i + 1], 16);
//					l = Character.digit((char) bytes[i + 2], 16);
//					if ((u == -1) || (l == -1))
//					{
//						throw new DecoderException("Invalid URL encoding");
//					}
//					buffer.write((char) ((u << 4) + l));
//				}
//				catch (Exception e)
//				{
//					if (bytes.length > i)
//					{
//						buffer.write(bytes[i + 1]);
//					}
//					
//					if (bytes.length > i + 1)
//					{
//						buffer.write(bytes[i + 2]);
//					}
//				}
//				i += 2;
//			}
//			else
//			{
//				buffer.write(b);
//			}
//		}
//		
//		return buffer.toString();
//	}
	
	

	
	
	/**
	 * Returns the URI scheme, host, port, path & file. No query or fragment is
	 * included. Forces a trailing slash if it's only a host with no path
	 */
	public static String getFileUri(String uri)
	{
		int queryStart = uri.indexOf("?");
		if (queryStart < 0)
		{
			queryStart = uri.length();
		}
		int fragmentStart = uri.indexOf("#");
		if (fragmentStart < 0)
		{
			fragmentStart = uri.length();
		}
		
		int end;
		if (queryStart < fragmentStart)
		{
			end = queryStart;
		}
		else
		{
			end = fragmentStart;
		}
		String trailingSlash = "";
		boolean absolute = uri.toLowerCase().startsWith("http");
		
		if ((absolute && (uri.lastIndexOf("/") <= 7)) || (!absolute && (uri.lastIndexOf("/") == -1)))
		{
			trailingSlash = "/";
		}
		return uri.substring(0, end) + trailingSlash;
	}
	
	/**
	 * This only returns the directory, not the host, port, scheme, etc
	 * 
	 * @param uri
	 * @return
	 */
	public static String getDirectory(String uri) throws URISyntaxException
	{
		return parseUriString(uri)[INDEX_DIRECTORY];
	}
	
	/**
	 * Returns the URI scheme, host, port, and path. No file, query or fragment
	 * is included.
	 */
	public static String getDirectoryUri(String uri) throws URISyntaxException
	{
		String components[] = parseUriString(uri);
		String dirUri = components[INDEX_SCHEME] + "://" + components[INDEX_HOST];
		if (!components[INDEX_PORT].isEmpty())
		{
			dirUri += ":" + components[INDEX_PORT];
		}
		dirUri += components[INDEX_DIRECTORY];
		
		return dirUri;
	}
	
	
	/**
	 * 
	 * @param uri
	 * @return The scheme, host & port (if in original URI)
	 */
	public static String getHostUri(String uri) throws URISyntaxException
	{
		return getHostUriWithoutTrailingSlash(uri) + "/";
	}


	

	/**
	 * 
	 * @param uri
	 * @return The scheme, host & port (if in original URI), but no slash
	 */
	public static String getHostUriWithoutTrailingSlash(String uri) throws URISyntaxException
	{
		String components[] = parseUriString(uri);
		String hostUri = components[INDEX_SCHEME] + "://" + components[INDEX_HOST];
		if (!components[INDEX_PORT].isEmpty())
		{
			hostUri += ":" + components[INDEX_PORT];
		}
		return hostUri;
	}

	public static String getFilename(String uri) throws URISyntaxException
	{
		return parseUriString(uri)[INDEX_FILE];
	}
	
	public static String getFirstQueryParameter(String URI, String parameterName) throws URISyntaxException
	{
		String value = null;
		for (NameValuePair param: getQueryParametersFromUri(URI))
		{
			if (param.getName().equalsIgnoreCase(parameterName))
			{
				value = param.getValue();
				break;
			}
		}
		return value;
	}
	
	public static String getFragment(String uri) throws URISyntaxException
	{
		return parseUriString(uri)[INDEX_FRAGMENT];
	}
	
	public static String getHost(String uri) throws URISyntaxException
	{
		return parseUriString(uri)[INDEX_HOST];
	}
	
	public static String getRelativeUri(String uri) throws URISyntaxException
	{
		String components[] = parseUriString(uri);
		StringBuilder sb = new StringBuilder();

		sb.append(components[INDEX_DIRECTORY]);
		sb.append(components[INDEX_FILE]);

		if (!components[INDEX_QUERY].isEmpty())
		{
			sb.append("?" + components[INDEX_QUERY]);
		}

		if (!components[INDEX_FRAGMENT].isEmpty())
		{
			sb.append("#" + components[INDEX_FRAGMENT]);
		}
		
		return sb.toString();	
	}
	
	
	public static int getPort(String uri) throws URISyntaxException
	{
		String components[] = parseUriString(uri);
		String portString = components[INDEX_PORT];
		if (portString.equals(""))
		{
			if (components[INDEX_SCHEME].equals(""))
			{
				portString = "0";
			}
			else if (components[INDEX_SCHEME].equalsIgnoreCase("https"))
			{
				portString = "443";
			}
			else
			{
				portString = "80";
			}
		}
		return Integer.valueOf(portString);
	}
	
	public static String getQuery(String uri) throws URISyntaxException
	{
		return parseUriString(uri)[INDEX_QUERY];
	}
	
	public static String getSession(String uri) throws URISyntaxException
	{
		return parseUriString(uri)[INDEX_SESSION];
	}

	public static String replaceQuery(String uri, String newQuery) throws URISyntaxException
	{
		String[] components = parseUriString(uri);
		components[INDEX_QUERY] = newQuery;
		return reconstituteUri(components);
	}
	
	public static String replaceSession(String uri, String newSession) throws URISyntaxException
	{
		String[] components = parseUriString(uri);
		components[INDEX_SESSION] = newSession;
		return reconstituteUri(components);
	}
	
	public static String replaceFilename(String uri, String newFilename) throws URISyntaxException
	{
		String[] components = parseUriString(uri);
		components[INDEX_FILE] = newFilename;
		return reconstituteUri(components);
	}

	private static String reconstituteUri(String[] components)
	{
		StringBuilder sb = new StringBuilder();
		if (!components[INDEX_SCHEME].isEmpty())
		{
			sb.append(components[INDEX_SCHEME] + "://");
		}

		sb.append(components[INDEX_HOST]);

		if (!components[INDEX_PORT].isEmpty())
		{
			sb.append(":" + components[INDEX_PORT]);
		}

		sb.append(components[INDEX_DIRECTORY]);
		sb.append(components[INDEX_FILE]);

		if (!components[INDEX_QUERY].isEmpty())
		{
			sb.append("?" + components[INDEX_QUERY]);
		}

		if (!components[INDEX_SESSION].isEmpty())
		{
			sb.append(";" + components[INDEX_SESSION]);
		}

		if (!components[INDEX_FRAGMENT].isEmpty())
		{
			sb.append("#" + components[INDEX_FRAGMENT]);
		}
		
		return sb.toString();
	}

	
	public static List<NameValuePair> getQueryParametersFromQuery(String query)
	{
		List<NameValuePair> params = new ArrayList<NameValuePair>(1);
		
		if ((query != null) && !query.equals(""))
		{
			String rawPairs[] = query.split("&");
			for (String rawPair: rawPairs)
			{
				String name;
				String value;
				int ampPos = rawPair.indexOf('=');
				if (ampPos >= 0)
				{
					name = rawPair.substring(0, ampPos);
					value = rawPair.substring(ampPos + 1, rawPair.length());
				}
				else
				{
					name = rawPair;
					value = "";
				}
				params.add(new BasicNameValuePair(name, value));
			}
		}
		return params;
	}
	
	public static List<NameValuePair> getQueryParametersFromUri(String URI) throws URISyntaxException
	{
		String query = getQuery(URI);
		return getQueryParametersFromQuery(query);
	}
	
	public static String getScheme(String uri) throws URISyntaxException
	{
		return parseUriString(uri)[INDEX_SCHEME];
	}
	
	/**
	 * Will return something like http://www.grendel-scan.com:80 It doesn't
	 * check to see if this is an absolute URL, so you should check if it could
	 * be relative
	 */
	public static String getSchemeHostPort(String uri) throws URISyntaxException
	{
		String components[] = parseUriString(uri);
		return components[INDEX_SCHEME] + "://" + components[INDEX_HOST] + ":" + components[INDEX_PORT];
	}
	
	/**
	 * Checks to see if a URI is usable from a web point of view. Currently, it
	 * checks for a scheme of http, https or nothing, and a host if there is a
	 * scheme. It also checks for mailto: and javascript: URLs.
	 * 
	 * @param uri
	 * @return
	 * @throws URISyntaxException 
	 */
	public static boolean isUsableUri(String uri) throws URISyntaxException
	{
		boolean usable = false;
		if ((uri != null) && !uri.equals(""))
		{
			String uriLower = uri.toLowerCase();
			if (!(uriLower.startsWith("javascript:") || uriLower.startsWith("mailto:")))
			{
				String components[] = parseUriString(uri);
				if (components[INDEX_SCHEME].equals("")
				        || ((components[INDEX_SCHEME].equalsIgnoreCase("http") || components[INDEX_SCHEME]
				                .equalsIgnoreCase("https")) && !components[INDEX_HOST].equals("")))
				{
					usable = true;
				}
			}
		}
		
		return usable;
	}
	
	public static String cleanupWhitespace(String uri)
	{
		if (uri == null)
			return uri;
		return uri.replaceAll("[\\x00-\\x20]+", "");
	}

	private static final Pattern DOUBLE_SLASH_DIR = Pattern.compile("(?<!(?:https?|ftp):)//", Pattern.CASE_INSENSITIVE);
	public static String removeDoubleSlashesFromDir(String uri)
	{
		if (uri == null)
			return uri;
		return DOUBLE_SLASH_DIR.matcher(uri).replaceAll("/");
	}
	
	public static boolean isAbsolute(String uri) throws URISyntaxException
	{
		URI testUri = new URI(uri);
		return testUri.isAbsolute();

	}
	
	public static void assertAbsoluteHttpAndValid(String uri) throws URISyntaxException
	{
		URI testUri = new URI(uri);
		if (!testUri.isAbsolute())
		{
			throw new URISyntaxException(uri, "Not absolute URI");
		}
		if (!(testUri.getScheme().equalsIgnoreCase("http") || testUri.getScheme().equalsIgnoreCase("https")))
		{
			throw new URISyntaxException(uri, "Try using a web protocol");
		}
				
	}
	
	/**
	 * Takes the URI and adds a port (if the scheme is known), sorts the
	 * parameter order, removes duplicate paramater names (if this breaks your
	 * stuff, follow the standard (RTFRFC)), removes extra ampersands (&) in the
	 * query, sets the scheme and host to all lower case (path & query can be
	 * case sensitive depending on OS & web server), and removes the fragment
	 * (since it isn't relilvent for requests)
	 * 
	 * @param uri
	 * @return
	 * @throws URISyntaxException 
	 */
	public static String normalizeUri(String uri) throws URISyntaxException
	{
		String components[] = parseUriString(uri);
		if (StringUtils.notEmpty(components[INDEX_SCHEME]) && !StringUtils.notEmpty(components[INDEX_PORT]))
		{
			if (components[INDEX_SCHEME].equalsIgnoreCase("HTTPS"))
			{
				components[INDEX_PORT] = "443";
			}
			else if (components[INDEX_SCHEME].equalsIgnoreCase("HTTP"))
			{
				components[INDEX_PORT] = "80";
			}
		}
		
		String newUri = "";
		if (StringUtils.notEmpty(components[INDEX_SCHEME]))
		{
			newUri += components[INDEX_SCHEME].toLowerCase() + "://";
		}
		
		if (StringUtils.notEmpty(components[INDEX_HOST]))
		{
			newUri += components[INDEX_HOST].toLowerCase();
		}
		
		if (StringUtils.notEmpty(components[INDEX_PORT]))
		{
			newUri += ":" + components[INDEX_PORT];
		}
		
		if (StringUtils.notEmpty(components[INDEX_DIRECTORY]))
		{
			newUri += components[INDEX_DIRECTORY];
		}
		
		if (StringUtils.notEmpty(components[INDEX_FILE]))
		{
			newUri += components[INDEX_FILE];
		}
		
		if (StringUtils.notEmpty(components[INDEX_SESSION]))
		{
			newUri += ";" + components[INDEX_SESSION];
		}
		
		if (StringUtils.notEmpty(components[INDEX_QUERY]))
		{
			newUri += "?" + normalizeUriQuery(components[INDEX_QUERY]);
		}
		
		if (StringUtils.notEmpty(components[INDEX_FRAGMENT]))
		{
			newUri += "#" + normalizeUriQuery(components[INDEX_FRAGMENT]);
		}
		
		return newUri;
	}
	
	public static String normalizeUriQuery(String query)
	{
		String newQuery = query;
		String parameters[] = newQuery.split("&");
		java.util.Arrays.sort(parameters, String.CASE_INSENSITIVE_ORDER);
		newQuery = "";
		Set<String> parameterNames = new HashSet<String>(parameters.length);
		for (String parameter: parameters)
		{
			String name;
			String value;
			
			int eq = parameter.indexOf("=");
			if (eq > 0)
			{
				name = parameter.substring(0, eq);
				value = parameter.substring(eq + 1);
			}
			else
			{
				name = parameter;
				value = "";
			}
			if (parameterNames.contains(name))
			{
				continue;
			}
			newQuery += name + "=" + value + "&";
		}
		
		return newQuery.replaceAll("&{2,}", "&").replaceFirst("^&+", "").replaceFirst("&+$", "");
	}
	
	public static String removeQueryParameter(String uri, String parameterName) throws URISyntaxException
	{
		String newUri = uri;
		String query = getQuery(newUri);
		if (StringUtils.notEmpty(query))
		{
			query = query.replaceAll("(?:&|^)" + Pattern.quote(parameterName) + "=[^=&]+", "").replaceFirst("^&", "");
			newUri = getFileUri(newUri) + "?" + query;
		}
		
		return newUri;
	}
	
	public static String replaceQueryParameter(String uri, String parameterName, String parameterValue) throws URISyntaxException
	{
		String newUri = uri;
		String query = getQuery(newUri);
		if (StringUtils.notEmpty(query))
		{
			query = query.replaceAll("&?+" + Pattern.quote(parameterName) + "=[^=&+]", new String(UrlEncodingUtils.encodeForParam(parameterValue.getBytes())));
			newUri = getFileUri(newUri) + "?" + query;
		}
		
		return newUri;
	}
	
	public static String urlEncode(NameValuePair[] pairs)
	{
		return urlEncode(Arrays.asList(pairs));
	}
	
	public static String urlEncode(Iterable<NameValuePair> pairs)
	{
		boolean first = true;
		StringBuilder buf = new StringBuilder();
		for (NameValuePair pair: pairs)
		{
			if (pair.getName() != null)
			{
				if (!first)
				{
					buf.append("&");
				}
				first = false;
				buf.append(new String(UrlEncodingUtils.encodeForParam((pair.getName().getBytes()))));
				buf.append("=");
				if (pair.getValue() != null)
				{
					buf.append(new String(UrlEncodingUtils.encodeForParam((pair.getValue().getBytes()))));
				}
			}
		}
		
		return buf.toString();
	}
	
	public static boolean validateURISyntax(String URI, boolean mustBeAbsolute)
	{
		boolean good = true;
		try
		{
			URI uri = UriFactory.makeUri(URI, false);
			if (mustBeAbsolute && !uri.isAbsolute())
			{
				good = false;
			}
		}
		catch (URISyntaxException e)
		{
			good = false;
		}
		
		return good;
	}
	
	
	private static String[] parseUriString(String uri) throws URISyntaxException
	{
		
		String components[] = { "", "", "", "", "", "", "", "" };
		
		Matcher m = urlPattern.matcher(uri);
		if (m.matches())
		{
			int groupNum = 1;
			components[INDEX_SCHEME] = m.group(groupNum++);
			if (components[INDEX_SCHEME] == null)
			{
				components[INDEX_SCHEME] = "";
			}
			
			components[INDEX_HOST] = m.group(groupNum++);
			if (components[INDEX_HOST] == null)
			{
				components[INDEX_HOST] = "";
			}
			
			components[INDEX_PORT] = m.group(groupNum++);
			if (components[INDEX_PORT] == null)
			{
				components[INDEX_PORT] = "";
			}
			
			components[INDEX_DIRECTORY] = m.group(groupNum++);
			if (components[INDEX_DIRECTORY] == null)
			{
				components[INDEX_DIRECTORY] = "";
			}
			
			components[INDEX_FILE] = m.group(groupNum++);
			if (components[INDEX_FILE] == null)
			{
				components[INDEX_FILE] = "";
			}
			
			if(m.groupCount() > 6)
			{
				components[INDEX_SESSION] = m.group(groupNum++);
				if (components[INDEX_SESSION] == null)
				{
					components[INDEX_SESSION] = "";
				}
			}
			
			components[INDEX_QUERY] = m.group(groupNum++);
			if (components[INDEX_QUERY] == null)
			{
				components[INDEX_QUERY] = "";
			}
			
			components[INDEX_FRAGMENT] = m.group(groupNum++);
			if (components[INDEX_FRAGMENT] == null)
			{
				components[INDEX_FRAGMENT] = "";
			}
		}
		else
		{
			throw new URISyntaxException(uri, uri + " did not match regex");
		}
		return components;
	}
	
	public static List<String> getAllDirectoryURIs(String baseUri) throws URISyntaxException
	{
		List<String> uris = new ArrayList<String>(1);
		String components[] = parseUriString(baseUri);
		String currentPath = components[INDEX_SCHEME] + "://" + components[INDEX_HOST];
		if (!components[INDEX_PORT].isEmpty())
		{
			currentPath += ":" + components[INDEX_PORT];
		}
//		currentPath += "/";
//		uris.add(currentPath);
		for(String section: components[INDEX_DIRECTORY].split("/"))
		{
			currentPath += section + "/";
			uris.add(currentPath);
		}
		
		return uris;
	}

	/**
	 * Adds a trailing slash to a URL for hostname-only URLs
	 * @param uri
	 * @return
	 */
	public static String fixBaseUri(String uri)
	{
		int pos = 0;
		int count = 0;
		while (pos < uri.length() && count < 3)
		{
			pos = uri.indexOf('/', pos + 1);
			if (pos < 0)
			{
				break;
			}
			count++;
		}
		if (count < 3)
		{
			Log.warn("Appended slash to base URI " + uri);
			return uri + "/";
		}
		return uri;
	}
	
	private static final Pattern badUriCharPattern =
	        Pattern.compile("((?:[\\x00-\\x20\\x7f-\\xff\\{\\}\\\\\"'`^#|\\[\\]<>\\(\\)])|(?:%(?![0-9a-f]{2})))",
	        		Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	public static String escapeUri(String uri)
	{
		return escapeString(uri, badUriCharPattern);
	}
	
	private static String escapeString(String string, Pattern p)
	{
		String newString = string;
		if (newString != null)
		{
			Matcher m = p.matcher(newString);
			while (m.find())
			{
				String badchar = m.group(1);
				String firstHalf = newString.substring(0, m.start());
				String lastHalf = newString.substring(m.end());
				String replacement;
				if (badchar.equals(" "))
				{
					replacement = "+";
				}
				else
				{
					replacement = String.format("%%%02x", badchar.codePointAt(0)).toUpperCase();
				}
				newString = firstHalf + replacement + lastHalf;
				m = p.matcher(newString);
			}
		}
		return newString;
	}

}
