/**
 * 
 */
package com.grendelscan.testing.utils.spidering;

import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.http.URIStringUtils;
import com.grendelscan.commons.http.dataHandling.containers.DataContainerUtils;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.data.database.CommandJob;
import com.grendelscan.data.database.DataNotFoundException;
import com.grendelscan.data.database.Database;
import com.grendelscan.data.database.DatabaseUser;
import com.grendelscan.scan.Scan;

/**
 * @author david
 * 
 */
public class SpiderUtils implements DatabaseUser {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(SpiderUtils.class);
	private static final String dbFile = "spideredUrls.db";
	private static SpiderUtils instance;

	public static final SpiderUtils getInstance() {
		return instance;
	}

	public static void initialize() {
		instance = new SpiderUtils();
	}

	private final String NULL_HASH;

	private final Database database;

	private MessageDigest md5;

	private SpiderUtils() {
		database = new Database(Scan.getInstance().getOutputDirectory()
				+ dbFile);
		initializeDatabase();
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("What the what???", e);
		}
		NULL_HASH = md5("");
	}

	private void addSpiderSummaryRecord(final String url,
			final List<? extends NameValuePair> params) {
		String urlHash = md5(url);
		if (params.size() == 0) {
			insertHashes(urlHash, NULL_HASH, NULL_HASH);
		}
		for (NameValuePair param : params) {
			insertHashes(urlHash, md5(param.getName()), md5(param.getValue()));
		}
	}

	private void addSpiderTransactionRecord(
			final StandardHttpTransaction transaction) {
		addSpiderSummaryRecord(URIStringUtils.getFileUri(transaction
				.getRequestWrapper().getAbsoluteUriString()),
				DataContainerUtils.getAllNamedContaners(transaction
						.getTransactionContainer()));
	}

	public void initializeDatabase() {
		LOGGER.debug("Initializing database for test module persisted data storage");
		try {
			if (!database.tableExists("transactions")) {
				String tableQuery = "CREATE TABLE transactions (urlhash varchar(16), paramhash varchar(16), valuehash varchar(16))";
				String indexQuery = "CREATE INDEX IDX_DEFAULT_transactions ON transactions (urlhash, paramhash, valuehash)";
				database.execute(tableQuery);
				database.execute(indexQuery);
			}
		} catch (Throwable e) {
			LOGGER.error("Problem with creating database for spider utils: "
					+ e.toString(), e);
			System.exit(1);
		}
	}

	private void insertHashes(final String urlhash, final String paramhash,
			final String valuehash) {
		CommandJob job = new CommandJob("INSERT INTO transactions "
				+ "(urlhash, paramhash, valuehash) VALUES (?, ?, ?)",
				new Object[] { urlhash, paramhash, valuehash });
		try {
			database.execute(job);
		} catch (Throwable e) {
			LOGGER.error("Problem saving spidering record: " + e.toString(), e);
		}

	}

	/**
	 * If it is spiderable, it is added to the list of spidered locations
	 * 
	 * @param transaction
	 * @return
	 */
	public boolean isTransactionSpiderable(
			final StandardHttpTransaction transaction) {
		if (transaction.getRequestWrapper().getMethod().equals("GET")) {
			throw new IllegalArgumentException(
					"This should only be used for POSTs");
		}

		boolean b = isTransactionSummarySpiderable(
				URIStringUtils.getFileUri(transaction.getRequestWrapper()
						.getAbsoluteUriString()),
				DataContainerUtils.getAllNamedContaners(transaction
						.getTransactionContainer()));
		if (!b) {
			transaction.setUnrequestable(true);
		} else {
			addSpiderTransactionRecord(transaction);
		}
		return b;
	}

	private boolean isTransactionSummarySpiderable(final String url,
			final List<? extends NameValuePair> params) {
		boolean spiderable = false;
		if (Scan.getScanSettings().getUrlFilters().isUriAllowed(url)) {
			String urlHash = md5(url);
			if (SpiderConfig.oncePerUrl.isSelected() || params.size() == 0) {
				spiderable = isUrlHashNew(urlHash);
			} else {
				// Go through all the parameters
				for (NameValuePair param : params) {
					String paramhash = md5(param.getName());
					try {
						if (SpiderConfig.allParamNames.isSelected()) {
							// If the param name isn't found, then this must be
							// new
							if (0 == database
									.selectSimpleInt(
											"SELECT EXISTS ( SELECT 1 FROM transactions "
													+ "WHERE urlhash = ? AND paramhash = ? )",
											new Object[] { urlHash, paramhash })) {
								spiderable = true;
								break;
							}
						} else if (SpiderConfig.allParamValues.isSelected()) {
							String valuehash = md5(param.getValue());
							// If the param name and value aren't found, then
							// this must
							// be new
							if (0 == database
									.selectSimpleInt(
											"SELECT EXISTS ( SELECT 1 FROM transactions "
													+ "WHERE urlhash = ? AND paramhash = ? AND valuehash = ? )",
											new Object[] { urlHash, paramhash,
													valuehash })) {
								spiderable = true;
								break;
							}
						}
					} catch (DataNotFoundException e) {
						LOGGER.error(
								"Error with getting cound of URLs, which shouldn't happen: ",
								e);
						throw new IllegalStateException(e);
					} catch (Throwable e) {
						LOGGER.error(
								"Weird problem getting URL count: "
										+ e.toString(), e);
						throw new IllegalStateException(e);
					}
				}
			}
			// All the param names/values matched; it isn't new
		}

		return spiderable;
	}

	private boolean isUrlHashNew(final String urlhash) {
		try {
			return 0 == database.selectSimpleInt(
					"SELECT EXISTS (SELECT 1 FROM transactions "
							+ "WHERE urlhash = ?)", new Object[] { urlhash });
		} catch (DataNotFoundException e) {
			LOGGER.error(
					"Error with getting could of URLs, which shouldn't happen: ",
					e);
			throw new IllegalStateException(e);
		} catch (Throwable e) {
			LOGGER.error("Weird problem getting URL count: " + e.toString(), e);
			throw new IllegalStateException(e);
		}
	}

	public boolean isUrlSpiderable(final String url, final boolean recordUrl)
			throws URISyntaxException {
		String baseUrl = URIStringUtils.getFileUri(url);

		List<NameValuePair> params = URIStringUtils
				.getQueryParametersFromUri(url);
		boolean b = isTransactionSummarySpiderable(baseUrl, params);
		if (b && recordUrl) {
			addSpiderSummaryRecord(baseUrl, params);
		}
		return b;
	}

	private String md5(final byte[] string) {
		return new String(md5.digest(string));
	}

	private String md5(final String string) {
		return new String(md5.digest(string.getBytes()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.grendelscan.data.database.DatabaseUser#shutdown(boolean)
	 */
	@Override
	public void shutdown(final boolean nice) throws InterruptedException {
		LOGGER.debug("Shutting down spider record");
		database.stop(nice).join();
	}
}
