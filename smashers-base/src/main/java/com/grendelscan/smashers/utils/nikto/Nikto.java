package com.grendelscan.smashers.utils.nikto;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvReader;
import com.grendelscan.commons.ConfigurationManager;
import com.grendelscan.commons.FileUtils;
import com.grendelscan.commons.http.HttpUtils;
import com.grendelscan.commons.http.RequestOptions;
import com.grendelscan.commons.http.responseCompare.HttpResponseScoreUtils;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.transactions.UnrequestableTransaction;
import com.grendelscan.data.database.collections.DatabaseBackedMap;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.scan.TransactionSource;

public class Nikto
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Nikto.class);
    private static Nikto instance;

    private static final String INDEX_PHP_TESTS_MAP_NAME = "index_php_tests_map_name";

    public static Nikto getInstance()
    {
        return instance;
    }

    public synchronized static void initialize()
    {
        if (instance == null)
        {
            instance = new Nikto();
        }
    }

    public static void wipeInstance()
    {
        instance = null;
    }

    private final DatabaseBackedMap<String, List<String>> cgiDirs;
    private final Set<CurrentSoftwareVersion> currentSoftwareVersions;
    private DatabaseBackedMap<String, Integer> indexPHPTests;
    private final int indexPhpThreshold;
    private final Pattern linePattern = Pattern.compile("(@\\w+)=(.*)$");
    private int majorHttpVersion;
    private int minorHttpVersion;
    private final RequestOptions niktoCGIRequestOptions;
    private final String niktoDatabasePath;
    private final RequestOptions niktoPHPRequestOptions;
    private final Set<NiktoTest> niktoTests;
    private Map<String, List<String>> niktoVariables;

    private final Set<WebServerNote> serverNotes;

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

    private void findCgiDirs(final String baseUri, final int testJobId) throws InterruptedScanException
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
                LOGGER.warn("Nikto find CGI dir request unrequestable ( " + get.getRequestWrapper().getAbsoluteUriString() + "): " + e.toString(), e);
            }
        }
        cgiDirs.put(baseUri, dirs);
    }

    public synchronized List<String> getCgiDirs(final String baseUri, final int testJobId) throws InterruptedScanException
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

    private void getHttpVersion()
    {
        String version[] = ConfigurationManager.getString("nikto.default_http_version").split("\\.");
        majorHttpVersion = Integer.valueOf(version[0]);
        minorHttpVersion = Integer.valueOf(version[1]);
    }

    public int getMajorHttpVersion()
    {
        return majorHttpVersion;
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

    public int getMinorHttpVersion()
    {
        return minorHttpVersion;
    }

    public String getNiktoCredits()
    {
        return "This module uses databases distributed with Nikto 2.x. Permission granted " + "in writing by Sullo. Please see the database files for licensing information. " + "Thank you to Sullo and the other contributors to Nikto.";
    }

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

    private void handleFatalNiktoError(final Exception e)
    {
        LOGGER.error("Fatal problem loading Nikto database: " + e.toString(), e);
        System.exit(1);
    }

    /**
     * Gets /index.php for the host and stores the result.
     * 
     * @param transaction
     * @throws InterruptedScanException
     */
    public synchronized void initializeIndexPHP(final String uri, final int testJobId) throws InterruptedScanException
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
                LOGGER.error("Nikto index PHP test request unrequestable ( " + uri + "index.php" + "): " + e.toString(), e);
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
    public boolean isNormalIndexPHP(final StandardHttpTransaction transaction, final String baseUri, final int testJobId) throws InterruptedScanException
    {
        boolean normal = true;
        initializeIndexPHP(baseUri, testJobId);
        StandardHttpTransaction benchmarkTransaction = Scan.getInstance().getTransactionRecord().getTransaction(indexPHPTests.get(baseUri));
        if (HttpResponseScoreUtils.scoreResponseMatch(benchmarkTransaction, transaction, 100, Scan.getScanSettings().isParseHtmlDom(), false) < indexPhpThreshold)
        {
            normal = false;
        }

        return normal;
    }

    private Set<CurrentSoftwareVersion> loadCurrentVersionDatabase(final String configString)
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

    // public Map<String, Map<String, Integer>> getServerHeaderLocations()
    // {
    // return serverHeaderLocations;
    // }

    private List<String[]> loadNiktoDatabase(final String configString)
    {
        List<String[]> lines = new ArrayList<String[]>(100);
        String fileName = FileUtils.correctFilePathFormat(niktoDatabasePath + ConfigurationManager.getString(configString));
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

    private Set<NiktoTest> loadNiktoTests(final String configString)
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
        String fileName = FileUtils.correctFilePathFormat(niktoDatabasePath + ConfigurationManager.getString("nikto.db_variables"));
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

    private Set<WebServerNote> loadWebServerNoteDatabase(final String configString)
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

    public Set<String> replaceNiktoVariables(final String template)
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
}
