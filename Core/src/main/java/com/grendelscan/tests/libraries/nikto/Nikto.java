package com.grendelscan.tests.libraries.nikto;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.csvreader.CsvReader;
import com.grendelscan.data.database.collections.DatabaseBackedMap;
import com.grendelscan.logging.Log;
import com.grendelscan.requester.RequestOptions;
import com.grendelscan.requester.TransactionSource;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.requester.http.transactions.UnrequestableTransaction;
import com.grendelscan.scan.ConfigurationManager;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.utils.FileUtils;
import com.grendelscan.utils.HttpUtils;
import com.grendelscan.utils.ResponseCompare.HttpResponseScoreUtils;

public class Nikto
{
	private static Nikto	instance;

	public static Nikto getInstance()
	{
		return instance;
	}

	public static void wipeInstance()
	{
		instance = null;
	}
	
	public synchronized static void initialize()
	{
		if (instance == null)
		{
			instance = new Nikto();
		}
	}

	private DatabaseBackedMap<String, List<String>>			cgiDirs;
	private Set<CurrentSoftwareVersion>						currentSoftwareVersions;
	private DatabaseBackedMap<String, Integer>	indexPHPTests;
	private static final String INDEX_PHP_TESTS_MAP_NAME = "index_php_tests_map_name"; 
	private int												indexPhpThreshold;
	private Pattern											linePattern	= Pattern.compile("(@\\w+)=(.*)$");
	private int												majorHttpVersion;
	private int												minorHttpVersion;
	private RequestOptions									niktoCGIRequestOptions;
	private String											niktoDatabasePath;
	private RequestOptions									niktoPHPRequestOptions;
	private Set<NiktoTest>									niktoTests;
	private Map<String, List<String>>						niktoVariables;


	private Set<WebServerNote>								serverNotes;

	private Nikto()
	{
		niktoDatabasePath = ConfigurationManager.getString("nikto.database_directory");
		niktoTests = loadNiktoTests("nikto.db_tests");
		currentSoftwareVersions = loadCurrentVersionDatabase("nikto.web_server_version_file");
		serverNotes = loadWebServerNoteDatabase("nikto.web_server_notes_file");
		loadNiktoVariables();
		getHttpVersion();
		indexPhpThreshold = ConfigurationManager.getInt("nikto.index_php_threshold");
		cgiDirs = new DatabaseBackedMap<String, List<String>>("nikto_cgi_dirs");

		niktoPHPRequestOptions = new RequestOptions();
		niktoPHPRequestOptions.reason = "Nikto index.php test";
		niktoPHPRequestOptions.testTransaction = false;

		niktoCGIRequestOptions = new RequestOptions();
		niktoCGIRequestOptions.reason = "Nikto cgi dir search";
		niktoCGIRequestOptions.testTransaction = false;
	}



	public synchronized List<String> getCgiDirs(String baseUri, int testJobId) throws InterruptedScanException
	{
		if (!cgiDirs.containsKey(baseUri))
		{
			findCgiDirs(baseUri, testJobId);
		}
		return cgiDirs.get(baseUri);
	}

	public Set<CurrentSoftwareVersion> getCurrentSoftwareVersions()
	{
		return currentSoftwareVersions;
	}

	public int getMajorHttpVersion()
	{
		return majorHttpVersion;
	}

	public int getMinorHttpVersion()
	{
		return minorHttpVersion;
	}

	public String getNiktoCredits()
	{
		return "This module uses databases distributed with Nikto 2.x. Permission granted "
				+ "in writing by Sullo. Please see the database files for licensing information. "
				+ "Thank you to Sullo and the other contributors to Nikto.";
	}

	// public Map<String, Set<String>> getServerHeaders()
	// {
	// return serverHeaders;
	// }

	// public Set<String> getServerHeaders(String scheme, String host, int port)
	// {
	// initialize();
	// String key = scheme + "://" + host + ":" + port;
	// return serverHeaders.get(key);
	// }

	public Set<NiktoTest> getNiktoTests()
	{
		initialize();
		return niktoTests;
	}

	public Map<String, List<String>> getNiktoVariables()
	{
		return niktoVariables;
	}


	public Set<WebServerNote> getServerNotes()
	{
		return serverNotes;
	}

	/**
	 * Gets /index.php for the host and stores the result.
	 * 
	 * @param transaction
	 * @throws InterruptedScanException 
	 */
	public synchronized void initializeIndexPHP(String uri, int testJobId) throws InterruptedScanException
	{
		if (indexPHPTests == null)
		{
			indexPHPTests = new DatabaseBackedMap<String, Integer>(INDEX_PHP_TESTS_MAP_NAME);
		}
		if (!indexPHPTests.containsKey(uri))
		{
			StandardHttpTransaction testTransaction;
			try
			{
				testTransaction = new StandardHttpTransaction(TransactionSource.NIKTO, testJobId);
				testTransaction.getRequestWrapper().setURI(uri + "index.php", true);
				testTransaction.setRequestOptions(niktoPHPRequestOptions);
				testTransaction.execute();
				indexPHPTests.put(uri, testTransaction.getId());
			}
			catch (UnrequestableTransaction e)
			{
				Log.error("Nikto index PHP test request unrequestable ( "
						+ uri + "index.php" + "): "
						+ e.toString(), e);
			}
		}
	}

	/**
	 * Checks to see if this looks the same as a normal /index.php. 
	 * 
	 * @param transaction
	 * @return
	 * @throws InterruptedScanException 
	 */
	public boolean isNormalIndexPHP(StandardHttpTransaction transaction, String baseUri, int testJobId) throws InterruptedScanException
	{
		boolean normal = true;
		initializeIndexPHP(baseUri, testJobId);
		StandardHttpTransaction benchmarkTransaction = Scan.getInstance().getTransactionRecord().
			getTransaction(indexPHPTests.get(baseUri));
		if (HttpResponseScoreUtils.scoreResponseMatch(benchmarkTransaction, transaction,
				100, Scan.getScanSettings().isParseHtmlDom(), false) < indexPhpThreshold)
		{
			normal = false;
		}

		return normal;
	}

	public Set<String> replaceNiktoVariables(String template)
	{
		Set<String> newStrings = new HashSet<String>();
		newStrings.add(template);

		for (String variableName : niktoVariables.keySet())
		{
			for (String target : newStrings)
			{
				if (target.contains(variableName))
				{
					newStrings.remove(target);
					for (String value : niktoVariables.get(variableName))
					{
						newStrings.add(target.replaceAll(Matcher.quoteReplacement(variableName), value));
					}
				}
			}
		}
		return newStrings;
	}

	private void findCgiDirs(String baseUri, int testJobId) throws InterruptedScanException
	{
		List<String> dirs = new ArrayList<String>(1);
		for (String dir : niktoVariables.get("@CGIDIRS"))
		{
			if (dir.startsWith("/"))
			{
				dir = dir.substring(1);
			}
			StandardHttpTransaction get = new StandardHttpTransaction(TransactionSource.NIKTO, testJobId);
			get.getRequestWrapper().setURI(baseUri + dir, true);
			get.setRequestOptions(niktoCGIRequestOptions);
			try
			{
				get.execute();
				if (HttpUtils.fileExists(get.getLogicalResponseCode()))
				{
					dirs.add(dir);
				}
			}
			catch (UnrequestableTransaction e)
			{
				Log.warn("Nikto find CGI dir request unrequestable ( " + get.getRequestWrapper().getAbsoluteUriString() + "): "
						+ e.toString(), e);
			}
		}
		cgiDirs.put(baseUri, dirs);
	}

	private void getHttpVersion()
	{
		String version[] = ConfigurationManager.getString("nikto.default_http_version").split("\\.");
		majorHttpVersion = Integer.valueOf(version[0]);
		minorHttpVersion = Integer.valueOf(version[1]);
	}

	private void handleFatalNiktoError(Exception e)
	{
		Log.fatal("Fatal problem loading Nikto database: " + e.toString(), e);
		System.exit(1);
	}

	// public Map<String, Map<String, Integer>> getServerHeaderLocations()
	// {
	// return serverHeaderLocations;
	// }

	private Set<CurrentSoftwareVersion> loadCurrentVersionDatabase(String configString)
	{
		List<String[]> lines = loadNiktoDatabase(configString);
		Set<CurrentSoftwareVersion> versions = new HashSet<CurrentSoftwareVersion>(lines.size());

		for (String line[] : lines)
		{
			CurrentSoftwareVersion currentVersion = null;
			try
			{
				currentVersion = new CurrentSoftwareVersion(line);
				versions.add(currentVersion);
			}
			catch (java.text.ParseException e)
			{
				// System.err.println("Problem parsing Nikto database line from
				// " + configString + "(" + StringUtilities.join(line, ",") +
				// "): " + e.toString());
			}
		}
		return versions;
	}

	private List<String[]> loadNiktoDatabase(String configString)
	{
		List<String[]> lines = new ArrayList<String[]>(100);
		String fileName =
				FileUtils.correctFilePathFormat(niktoDatabasePath + ConfigurationManager.getString(configString));
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new FileReader(fileName));
			CsvReader csv = new CsvReader(reader);
			csv.setEscapeMode(CsvReader.ESCAPE_MODE_BACKSLASH);
			while (csv.readRecord())
			{
				String line[] = csv.getValues();
				lines.add(line);
			}
		}
		catch (IOException e)
		{
			handleFatalNiktoError(e);
		}
		return lines;
	}

	private Set<NiktoTest> loadNiktoTests(String configString)
	{
		List<String[]> lines = loadNiktoDatabase(configString);
		Set<NiktoTest> tests = new HashSet<NiktoTest>(lines.size());

		for (String line[] : lines)
		{
			NiktoTest test;
			try
			{
				test = new NiktoTest(line);
				tests.add(test);
			}
			catch (java.text.ParseException e)
			{
				// System.err.println("Problem parsing Nikto database line from
				// " + configString + "(" + StringUtilities.join(line, ",") +
				// "): " + e.toString());
			}
		}

		return tests;
	}

	private void loadNiktoVariables()
	{
		String fileName =
				FileUtils.correctFilePathFormat(niktoDatabasePath
						+ ConfigurationManager.getString("nikto.db_variables"));
		List<String> lines = null;
		try
		{
			lines = FileUtils.getFileAsLines(fileName);
		}
		catch (IOException e)
		{
			handleFatalNiktoError(e);
		}

		niktoVariables = new HashMap<String, List<String>>();
		for (String line : lines)
		{
			Matcher matcher = linePattern.matcher(line);
			if (matcher.matches())
			{
				List<String> valueSet = new ArrayList<String>();
				String name = matcher.group(1);
				String values = matcher.group(2);
				niktoVariables.put(name, valueSet);
				for (String value : values.split(" "))
				{
					valueSet.add(value);
				}
			}
		}
	}

	private Set<WebServerNote> loadWebServerNoteDatabase(String configString)
	{
		List<String[]> lines = loadNiktoDatabase(configString);
		Set<WebServerNote> notes = new HashSet<WebServerNote>(lines.size());

		for (String line[] : lines)
		{
			WebServerNote serverNote;
			try
			{
				serverNote = new WebServerNote(line);
				notes.add(serverNote);
			}
			catch (java.text.ParseException e)
			{
				// System.err.println("Problem parsing Nikto database line from
				// " + configString + "(" + StringUtilities.join(line, ",") +
				// "): " + e.toString());
			}
		}
		return notes;
	}
}
