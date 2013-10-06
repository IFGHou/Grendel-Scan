/*
 * Scan.java
 * 
 * Created on August 19, 2007, 1:25 PM
 * 
 * To change this template, choose Tools | Template Manager and open the template in the editor.
 */

package com.grendelscan.scan;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.categorizers.Categorizer;
import com.grendelscan.categorizers.Categorizers;
import com.grendelscan.commons.FileUtils;
import com.grendelscan.commons.WordList;
import com.grendelscan.commons.collections.CollectionUtils;
import com.grendelscan.commons.flex.output.AmfOutputStreamRegistry;
import com.grendelscan.commons.formatting.DataFormatUtils;
import com.grendelscan.commons.http.RequestOptions;
import com.grendelscan.commons.http.URIStringUtils;
import com.grendelscan.commons.http.apache_overrides.client.ClientUtilities;
import com.grendelscan.commons.http.apache_overrides.client.CustomHttpClient;
import com.grendelscan.commons.http.transactions.HttpTransactionFields;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.transactions.TransactionSource;
import com.grendelscan.data.database.DatabaseUser;
import com.grendelscan.data.database.collections.DatabaseBackedCollection;
import com.grendelscan.fuzzing.Fuzzer;
import com.grendelscan.proxy.Proxies;
import com.grendelscan.queues.AbstractQueueThread;
import com.grendelscan.queues.categorizer.CategorizerQueue;
import com.grendelscan.queues.monitor.QueueMonitor;
import com.grendelscan.queues.requester.RequesterQueue;
import com.grendelscan.queues.tester.TesterQueue;
import com.grendelscan.queues.tester.TesterThread;
import com.grendelscan.scan.authentication.AuthenticationPackages;
import com.grendelscan.scan.data.PersistedTestData;
import com.grendelscan.scan.data.ResponseCodeOverrides;
import com.grendelscan.scan.data.TransactionRecord;
import com.grendelscan.scan.data.findings.FindingsCollector;
import com.grendelscan.scan.data.sessionState.SessionStates;
import com.grendelscan.scan.settings.ScanSettings;
import com.grendelscan.testing.misc.ModuleDependencyException;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.MasterTestModuleCollection;
import com.grendelscan.testing.utils.nikto.Nikto;
import com.grendelscan.testing.utils.platformErrorMessages.PlatformErrorMessages;
import com.grendelscan.testing.utils.sessionIDs.SessionID;
import com.grendelscan.testing.utils.spidering.SpiderConfig;
import com.grendelscan.testing.utils.spidering.SpiderUtils;
import com.grendelscan.testing.utils.tokens.TokenTesting;

/**
 * 
 * @author David Byrne
 */
public class Scan {
	private static final Logger LOGGER = LoggerFactory.getLogger(Scan.class);

	private static Scan scan;

	public static Scan getInstance() {
		return scan;
	}

	public static ScanSettings getScanSettings() {
		return getInstance().scanSettings;
	}

	public static void instantiate(final boolean useGUI,
			final String newOutputDirectory) {
		scan = new Scan(useGUI, newOutputDirectory);
		scan.init();
	}

	private CategorizerQueue categorizerQueue;
	private Categorizers categorizers;
	private CustomHttpClient httpClient;
	private boolean paused;
	private Proxies proxies;
	private FindingsCollector findings;
	private RequesterQueue requesterQueue;
	private ResponseCodeOverrides responseCodeOverrides;
	private boolean shutdownComplete;
	private ScanSettings scanSettings;
	private TesterQueue testerQueue;
	private TransactionRecord transactionRecord;
	private List<Fuzzer> fuzzers;
	public static final String settingsFile = "settings.xml";
	private final String outputDirectory;
	private final boolean useGUI;
	private List<DatabaseUser> databaseUsers;
	private PersistedTestData testData;
	private AuthenticationPackages authenticationPackages;
	private final Set<AbstractTestModule> enabledModules;

	private long domTime;

	private WordList wordList;

	private QueueMonitor queueMonitor;

	private Scan(final boolean useGUI, final String newOutputDirectory) {
		paused = true;
		this.useGUI = useGUI;
		if (!newOutputDirectory.matches(File.separator + "$")) {
			outputDirectory = newOutputDirectory + File.separator;
		} else {
			outputDirectory = newOutputDirectory;
		}
		enabledModules = new HashSet<AbstractTestModule>();
	}

	/**
	 * Does not add the URI to ScanSettings or to whitelist
	 * 
	 * @param uri
	 * @throws URISyntaxException
	 */
	public void addBaseURIToScan(String uri) {
		uri = URIStringUtils.fixBaseUri(uri);
		String name = "baseuri-" + uri;
		if (!testData.containsItem(name)) {
			RequestOptions baseUriRequestOptions = new RequestOptions();
			baseUriRequestOptions.reason = "Base URL processing";
			baseUriRequestOptions.followRedirects = true;
			StandardHttpTransaction transaction = new StandardHttpTransaction(
					TransactionSource.BASE, -1);
			transaction.getRequestWrapper().setURI(uri, true);
			transaction.getRequestWrapper().setMethod("GET");
			transaction.setRequestOptions(baseUriRequestOptions);
			requesterQueue.addTransaction(transaction);
			testData.setBoolean(name, true);
		}
	}

	private void createHttpClients() {
		HttpParams params = ClientUtilities.createHttpParams();
		HttpContext context = ClientUtilities.createHttpContext();
		ClientConnectionManager ccm = ClientUtilities
				.createClientConnectionManager(
						scanSettings.getMaxTotalConnections(),
						scanSettings.getMaxConnectionsPerServer());
		httpClient = new CustomHttpClient(ccm, params, context);
	}

	private void datastoreInit() {
		databaseUsers = new ArrayList<DatabaseUser>(1);
		try {
			transactionRecord = new TransactionRecord();
			databaseUsers.add(transactionRecord);

			testData = new PersistedTestData();
			databaseUsers.add(testData);

			findings = new FindingsCollector();
			databaseUsers.add(findings);

			// don't add SpiderUtils here
		} catch (Exception e) {
			String message = "Error loading/creating transasction database: "
					+ e.toString();
			fatalError(message);
		}
	}

	public boolean disableTestModule(
			final Class<? extends AbstractTestModule> moduleClass)
			throws ModuleDependencyException {
		AbstractTestModule module;
		if ((module = MasterTestModuleCollection.getInstance().getTestModule(
				moduleClass)) == null) {
			return false;
		}
		String message = "";
		for (Class<? extends AbstractTestModule> i : module.getDependents()) {
			AbstractTestModule dependency = MasterTestModuleCollection
					.getInstance().getTestModule(i);
			if (enabledModules.contains(dependency)) {
				message += "Module \"" + dependency.getName()
						+ "\" is enabled and requires \"" + module.getName()
						+ "\". Disable \"" + dependency.getName() + "\" first";
			}
		}
		if (!message.isEmpty()) {
			throw new ModuleDependencyException(message);
		}
		testerQueue.disableModule(moduleClass);
		return enabledModules.remove(module);
	}

	public void displayMessage(final String title, final String message) {
		if (MainWindow.getInstance() != null) {
			MainWindow.getInstance().displayMessage(title, message, false);
		} else {
			System.out.println(title + "\n" + message);
		}
	}

	private void enablePrerequsites(final AbstractTestModule module) {
		for (Class<? extends AbstractTestModule> prereq : module
				.getPrerequisites()) {
			enablePrerequsites(MasterTestModuleCollection.getInstance()
					.getTestModule(prereq));
			enableTestModule(prereq);
		}
	}

	public boolean enableTestModule(
			final Class<? extends AbstractTestModule> moduleClass) {
		AbstractTestModule module;

		// Check to see if there is such a module
		if ((module = MasterTestModuleCollection.getInstance().getTestModule(
				moduleClass)) == null) {
			return false;
		}
		enablePrerequsites(module);
		testerQueue.enableModule(moduleClass);
		return enabledModules.add(module);
	}

	public synchronized void fatalError(final String message) {
		try {
			LOGGER.error("FATAL ERROR: " + message);
		} catch (Exception e) {
			// Don't care at this point
		}
		try {
			displayMessage("Error:", message);
		} catch (Exception e) {
			// Don't care at this point
		}
		System.exit(1);
	}

	public String generateStatus() {
		String s = "";

		s += "Enabled modules:\n";
		for (AbstractTestModule module : enabledModules) {
			s += "\t" + module.getName() + " (" + module.getClass().getName()
					+ ")\n";
		}
		s += "\n\n";

		s += "DOM parsing time: " + domTime + "\n\n";

		s += "Processing times:\n";
		for (Class<? extends AbstractTestModule> moduleId : testerQueue
				.getTimes().keySet()) {
			s += "\t"
					+ MasterTestModuleCollection.getInstance()
							.getTestModule(moduleId).getName() + " ("
					+ moduleId + "): "
					+ roundMilis(testerQueue.getTimes().get(moduleId)) + "\n";
		}

		s += "Database times:\n";
		s += "\tLoading: " + roundMilis(transactionRecord.getLoadingTime())
				+ "\n";
		s += "\tSaving: " + roundMilis(transactionRecord.getSavingTime())
				+ "\n";
		s += "\n";

		s += "Status text update: "
				+ roundMilis(MainWindow.getInstance().getLogComposite()
						.getStatusMessageUpdateTime()) + "\n";
		s += "\n";

		s += "ResponseCodeOverrides: \n";
		s += "\tExecute: "
				+ roundMilis(responseCodeOverrides.getTotalExecuteTime())
				+ "\n";
		s += "\tCompare: "
				+ roundMilis(responseCodeOverrides.getTotalCompareTime())
				+ "\n";
		s += "\tTotal: " + roundMilis(responseCodeOverrides.getTotalGenTime())
				+ "\n";

		s += "\nTester queue stats:\n";
		for (AbstractTestModule module : MasterTestModuleCollection
				.getInstance().getAllTestModules()) {
			s += "\t\t" + module.getName() + " (" + module.getClass().getName()
					+ "): " + testerQueue.getPendingCount(module.getClass())
					+ "\n";
		}

		s += "\tCompleted tests:\n";
		for (Class<? extends AbstractTestModule> module : testerQueue
				.getProcessedCount().keySet()) {
			s += "\t\t"
					+ MasterTestModuleCollection.getInstance()
							.getTestModule(module).getName() + " (" + module
					+ "): " + testerQueue.getProcessedCount().get(module)
					+ "\n";
		}
		s += "\n\n";

		s += scanSettings.getMaxTesterThreads()
				+ " tester threads originally created\n";
		for (AbstractQueueThread thread : testerQueue.getThreads()) {
			s += "\t" + thread.getName() + ": "
					+ thread.getThreadState().getText() + "  --  "
					+ ((TesterThread) thread).getCurrentModule() + "\n";
		}
		s += "\n\n";

		s += scanSettings.getMaxCategorizerThreads()
				+ " categorizer threads originally created\n";
		for (AbstractQueueThread thread : categorizerQueue.getThreads()) {
			s += "\t" + thread.getName() + ": "
					+ thread.getThreadState().getText() + "\n";
		}
		s += "\n\n";

		s += scanSettings.getMaxRequesterThreads()
				+ " requester threads originally created\n";
		for (AbstractQueueThread thread : requesterQueue.getThreads()) {
			s += "\t" + thread.getName() + ": "
					+ thread.getThreadState().getText() + "\n";
		}
		s += "\n\n";

		s += "Completed HTTP transactions: "
				+ HttpTransactionFields.getTotalExecutions() + "\n";

		return s;
	}

	public final AuthenticationPackages getAuthenticationPackages() {
		return authenticationPackages;
	}

	public CategorizerQueue getCategorizerQueue() {
		return categorizerQueue;
	}

	public Categorizers getCategorizers() {
		return categorizers;
	}

	public final Set<AbstractTestModule> getEnabledModules() {
		return enabledModules;
	}

	public FindingsCollector getFindings() {
		return findings;
	}

	public final List<Fuzzer> getFuzzers() {
		return fuzzers;
	}

	public CustomHttpClient getHttpClient() {
		return httpClient;
	}

	public final String getOutputDirectory() {
		if (outputDirectory == null) {
			throw new IllegalStateException(
					"Output directory hasn't been set yet.");
		}
		return outputDirectory;
	}

	// /**
	// * Returns true if the parameter is forbidden, or irrelevant
	// *
	// * @param parameterName
	// * @return
	// */
	// public boolean isQueryParameterF(String parameterName)
	// {
	// return CollectionUtils.containsStringIgnoreCase(
	// scanSettings.getReadOnlyIrrelevantQueryParameters(), parameterName)
	// || CollectionUtils.containsStringIgnoreCase(
	// scanSettings.getReadOnlyForbiddenQueryParameters(),
	// parameterName);
	// }

	// public boolean isRunning()
	// {
	// return isRunning(false);
	// }
	//
	// public boolean isRunning(boolean proxyOrCategorizerRequest)
	// {
	// while (running && paused && !proxyOrCategorizerRequest)
	// {
	// /*
	// * This will block, but that's okay. It basically means one wait
	// * governs all threads that call isRunning().
	// */
	// synchronized (this)
	// {
	// try
	// {
	// Thread.sleep(250);
	// }
	// catch (InterruptedException e)
	// {
	// }
	// }
	// }
	// return running;
	// }

	public Proxies getProxies() {
		return proxies;
	}

	// public void startProxyOnly()
	// {
	// universalInit();
	//
	// // The requester queue manages some aspects of requests, but no threads
	// // are needed
	// requesterQueue = new RequesterQueue();
	//
	// scanPhase = ScanPhase.COMPLETE;
	// scanSettings.setAllowAllProxyRequests(true);
	// scanSettings.setTestProxyRequests(false);
	// scanSettings.setTestInterceptedRequests(false);
	// scanSettings.setTestManualRequests(false);
	// proxies.startProxies();
	// scanStatusComposite.updateProxyStatus();
	// }

	public RequesterQueue getRequesterQueue() {
		return requesterQueue;
	}

	public ResponseCodeOverrides getResponseCodeOverrides() {
		return responseCodeOverrides;
	}

	public final PersistedTestData getTestData() {
		return testData;
	}

	public TesterQueue getTesterQueue() {
		return testerQueue;
	}

	public TransactionRecord getTransactionRecord() {
		return transactionRecord;
	}

	public final WordList getWordList() {
		return wordList;
	}

	// private boolean threadsProcessing(AbstractQueueThread[] threads)
	// {
	// boolean testing = false;
	// for (AbstractQueueThread thread : threads)
	// {
	// if (thread.getThreadState() == QueueThreadState.PROCESSING)
	// {
	// testing = true;
	// break;
	// }
	// }
	// return testing;
	// }

	public synchronized void incrementDomTime(final long time) {
		domTime += time;
	}

	private void init() {
		FileUtils.createDirectories(outputDirectory);
		datastoreInit();

		DatabaseBackedCollection.clearExistingNames();

		// TODO: Cleanup this and other config file references
		wordList = new WordList("global_scan_wordlist", "conf" + File.separator
				+ "wordlist");
		DataFormatUtils.initialize(wordList);

		// init static libraries must come after init database
		staticLibraryInit();

		MasterTestModuleCollection.initialize();
		initializeQueues();

		scanSettings = new ScanSettings();
		loadOrCreateScanSettingsFile();

		FileUtils.createDirectories(outputDirectory + File.separator
				+ scanSettings.getSavedTextTransactionsDirectory());
		proxies = new Proxies();
		fuzzers = new ArrayList<Fuzzer>(1);

		authenticationPackages = new AuthenticationPackages();

		createHttpClients();

		scanSettings.convertBaseUris2WhiteLists();

		initializeCategorizers();

		// Start with a few threads to improve performance. The count will be
		// reduced if it exceeds the max setting
		testerQueue.start(5);
		requesterQueue.start(5);
		categorizerQueue.start(2);
		proxies.startProxies();

		queueMonitor = new QueueMonitor();
		queueMonitor.addQueue(categorizerQueue);
		queueMonitor.addQueue(requesterQueue);
		queueMonitor.addQueue(testerQueue);

		initResponseCodeOverrides();

		for (String baseUri : scanSettings.getReadOnlyBaseURIs()) {
			addBaseURIToScan(baseUri);
			categorizers.getByBaseUriCategorizer().processBaseUri(baseUri);
		}

	}

	private void initializeCategorizers() {
		categorizers = new Categorizers();

		for (Categorizer categorizer : categorizers.getAllCategorizers()) {
			for (AbstractTestModule module : MasterTestModuleCollection
					.getInstance().getAllTestModules()) {
				categorizer.addModule(module);
			}
		}
	}

	private void initializeQueues() {
		testerQueue = new TesterQueue();
		categorizerQueue = new CategorizerQueue();
		// Requester init has to come after categorizer init
		requesterQueue = new RequesterQueue();

		databaseUsers.add(testerQueue);
		databaseUsers.add(categorizerQueue);
		databaseUsers.add(requesterQueue);

	}

	private void initResponseCodeOverrides() {
		responseCodeOverrides = new ResponseCodeOverrides(
				scanSettings.getReadOnlyManualResponseCodeOverrides(),
				scanSettings.getUseAutomaticResponseCodeOverrides(),
				scanSettings
						.getAcceptableAutomaticResponseCodeOverrideThreshold());
	}

	public final boolean isGUI() {
		return useGUI;
	}

	public boolean isModuleEnabled(final AbstractTestModule module) {
		return enabledModules.contains(module);
	}

	public boolean isPaused() {
		return paused;
	}

	public boolean isQueryParameterForbidden(final String parameterName) {
		return CollectionUtils.containsStringIgnoreCase(
				scanSettings.getReadOnlyForbiddenQueryParameters(),
				parameterName);
	}

	public final boolean isShutdownComplete() {
		return shutdownComplete;
	}

	private void loadOrCreateScanSettingsFile() {

		File settings = new File(outputDirectory + settingsFile);
		boolean settingsLoaded = false;
		if (settings.exists()) {
			try {
				scanSettings.loadScanSettings(outputDirectory + settingsFile);
				settingsLoaded = true;
			} catch (ConfigurationException e) {
				LOGGER.error(
						"Failed to load existing settings, using defaults: "
								+ e.toString(), e);
			}
		}
		if (!settingsLoaded) {
			scanSettings.loadDefaultSettings();
		}
	}

	private long roundMilis(final long milis) {
		return Math.round((double) milis / (double) 1000);
	}

	public void setPaused(final boolean paused) {
		this.paused = paused;
	}

	public synchronized void shutdown(final String message) {
		if (!shutdownComplete) {
			if (message != null && !message.equals("")) {
				displayMessage("Scan termination:",
						"The scan is being terminated. Please wait while things are shutdown nicely. "
								+ message);
			}
			String s = "Terminating scan";
			if (!message.isEmpty()) {
				s += ". The reason is: " + message;
			}
			s += generateStatus();

			LOGGER.error(s);

			Scan.getInstance().getProxies().stopProxies();
			httpClient.getConnectionManager().shutdown();

			for (DatabaseUser user : databaseUsers) {
				try {
					user.shutdown(true);
				} catch (InterruptedException e) {
					LOGGER.error(
							"Interupted shutting down the databases: "
									+ e.toString(), e);
					break;
				}
			}

			int timeout = 10000;
			int delay = 250;
			while (timeout > 0
					&& !(categorizerQueue.isShutdown()
							&& requesterQueue.isShutdown() && testerQueue
								.isShutdown())) {
				try {
					wait(delay);
				} catch (InterruptedException e) {
					break;
					// probably a stop; this will be handled elsewhere
				}
				timeout -= delay;
			}

			shutdownComplete = true;
		}
	}

	private void staticLibraryInit() {
		// init static libraries must come after init database
		SessionStates.initialize();
		PlatformErrorMessages.initialize();
		TokenTesting.initialize();
		SessionID.initialize();
		Nikto.wipeInstance();
		SpiderConfig.initialize();
		SpiderUtils.initialize();
		AmfOutputStreamRegistry.initialize();
		databaseUsers.add(SpiderUtils.getInstance());

	}

}
