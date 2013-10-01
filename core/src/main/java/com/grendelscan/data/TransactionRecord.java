/*
 * TransactionRecord.java
 * 
 * Created on September 16, 2007, 1:15 PM
 * 
 * To change this template, choose Tools | Template Manager and open the template in the editor.
 */

package com.grendelscan.data;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteJob;
import com.almworks.sqlite4java.SQLiteStatement;
import com.grendelscan.commons.http.URIStringUtils;
import com.grendelscan.commons.http.transactions.HttpTransactionFields;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.wrappers.HttpResponseWrapper;
import com.grendelscan.data.database.CommandJob;
import com.grendelscan.data.database.DataNotFoundException;
import com.grendelscan.data.database.Database;
import com.grendelscan.data.database.DatabaseUser;
import com.grendelscan.scan.Scan;

/**
 * 
 * @author Administrator
 */
public class TransactionRecord implements DatabaseUser
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionRecord.class);
    private final static String dbFile = "transactions.db";
    private final static String tableName = "http_transactions";
    private final Database database;
    private final Map<String, Integer> failureCount;
    private long loadingTime;
    private long savingTime;
    private final Map<String, Integer> sourceCounts;
    private TransactionSummaryProvider summaryProvider;
    private final Object timeLock = new Object();
    private final Map<Integer, SoftReference<StandardHttpTransaction>> transactionCache;
    private int transactionCommandCount = 0;

    /**
     * Creates a new instance of TransactionRecord
     * 
     * @throws SQLException
     */
    public TransactionRecord()
    {
        savingTime = 0;
        loadingTime = 0;
        transactionCache = Collections.synchronizedMap(new HashMap<Integer, SoftReference<StandardHttpTransaction>>(200));
        sourceCounts = new HashMap<String, Integer>(1);
        failureCount = new HashMap<String, Integer>(1);
        database = new Database(dbFile);
        initializeDatabase();
        if (Scan.getInstance().isGUI())
        {
            createTransactionSummaryProvider();
        }
    }

    public void addOrRefreshTransactionReference(final StandardHttpTransaction transaction)
    {
        SoftReference<StandardHttpTransaction> ref = null;
        ref = new SoftReference<StandardHttpTransaction>(transaction);
        cleanMap();
        synchronized (transactionCache)
        {
            transactionCache.put(transaction.getId(), ref);
        }
    }

    /**
     * This will purge the null references from the map. This isn't critical, so it will only run periodically
     */
    private synchronized void cleanMap()
    {
        if (transactionCommandCount++ > 500)
        {
            transactionCommandCount = 0;
            synchronized (transactionCache)
            {
                Set<Integer> keys = new HashSet<Integer>(transactionCache.keySet());
                for (int key : keys)
                {
                    Reference<StandardHttpTransaction> ref = transactionCache.get(key);
                    if (ref != null && ref.get() == null)
                    {
                        transactionCache.remove(key);
                    }
                }
            }
        }
    }

    private void createTransactionSummaryProvider()
    {
        LOGGER.debug("Loading existing transaction summaries");
        Map<Integer, TransactionSummary> transactions = new HashMap<Integer, TransactionSummary>(HttpTransactionFields.getLastID());
        // for (int i = 1; i <= HttpTransactionFields.getLastID(); i++)
        {
            Object results[][];
            try
            {
                results = database.selectAll("SELECT method, host, path, query, source, request_time, response_code, reason, id " + "FROM " + tableName + " ", new Object[0]);
                for (int j = 0; j < results.length; j++)
                {

                    if (results[j][5] instanceof Integer)
                    {
                        results[j][5] = ((Integer) results[j][5]).longValue();
                    }
                    transactions.put((Integer) results[j][8], new TransactionSummary((String) results[j][0], (String) results[j][1], (String) results[j][2], (String) results[j][3], (String) results[j][4], (Long) results[j][5], (Integer) results[j][8],
                                    (Integer) results[j][6], (String) results[j][7]));
                }
            }
            catch (DataNotFoundException e)
            {
                LOGGER.info("No transactions found: " + e.toString(), e);
            }
            catch (Throwable e)
            {
                LOGGER.error("Very weird problem with loading existing transactions: " + e.toString(), e);
            }
        }
        summaryProvider = new TransactionSummaryProvider(transactions);
        LOGGER.debug("Done loading existing transaction summaries");
    }

    public Integer[] getAllCompletedTransactionIDs() throws DataNotFoundException
    {
        SQLiteJob<Integer[]> job = new SQLiteJob<Integer[]>()
        {
            @Override
            protected Integer[] job(final SQLiteConnection connection) throws Throwable
            {
                ArrayList<Integer> ids = new ArrayList<Integer>(1000);
                SQLiteStatement transactionSelectStatement = connection.prepare("SELECT id " + "FROM " + tableName + " " + "WHERE executed = 1 ");
                while (transactionSelectStatement.step())
                {
                    ids.add(transactionSelectStatement.columnInt(0));
                }
                transactionSelectStatement.dispose();
                return ids.toArray(new Integer[0]);
            }
        };
        Integer[] ids;
        try
        {
            database.execute(job);
            ids = job.get();
        }
        catch (Throwable e)
        {
            throw new DataNotFoundException(e);
        }

        return ids;
    }

    public Map<String, Integer> getFailureCount()
    {
        return failureCount;
    }

    public long getLoadingTime()
    {
        return loadingTime;
    }

    public long getSavingTime()
    {
        return savingTime;
    }

    public final TransactionSummaryProvider getSummaryProvider()
    {
        return summaryProvider;
    }

    public StandardHttpTransaction getTransaction(final int id)
    {
        if (id <= 0)
        {
            LOGGER.error("Invalid transaction ID: " + id, new DataNotFoundException());
        }

        StandardHttpTransaction transaction = null;

        cleanMap();
        synchronized (transactionCache)
        {
            if (transactionCache.containsKey(id))
            {
                Reference<StandardHttpTransaction> ref = transactionCache.get(id);
                transaction = ref.get();
            }
        }

        if (transaction == null)
        {
            try
            {
                transaction = loadTransaction(id);
                transaction.setLoadedFromDisk(true);
            }
            catch (DataNotFoundException e)
            {
                LOGGER.error("Invalid transaction ID: " + id, e);
            }
        }
        return transaction;
    }

    private Object[] getTransactionInsertQueryValues(final StandardHttpTransaction transaction)
    {
        int executed = transaction.isSuccessfullExecution() ? 1 : 0;

        HttpResponseWrapper response = transaction.getResponseWrapper();
        int statusCode = 0;
        String reasonPhrase = "";
        if (response != null)
        {
            statusCode = response.getStatusLine().getStatusCode();
            reasonPhrase = response.getStatusLine().getReasonPhrase();
        }
        Object[] values;
        try
        {
            values = new Object[] { executed, transaction.getRequestSentTime(), transaction.getRequestWrapper().getMethod(), URIStringUtils.getHostUri(transaction.getRequestWrapper().getAbsoluteUriString()),
                            transaction.getRequestWrapper().getPath() + URIStringUtils.getFilename(transaction.getRequestWrapper().getURI()), URIStringUtils.getQuery(transaction.getRequestWrapper().getURI()), statusCode, reasonPhrase,
                            transaction.getSource().getText(), transaction, transaction.getId(), transaction.getRequestOptions().reason

            };
        }
        catch (URISyntaxException e)
        {
            IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing", e);
            LOGGER.error(e.toString(), e);
            throw ise;
        }
        return values;
    }

    private Object[] getTransactionUpdateQueryValues(final StandardHttpTransaction transaction)
    {
        Object[] values = getTransactionInsertQueryValues(transaction);
        Object tmp = values[11];
        values[11] = values[10];
        values[10] = tmp;
        return values;
    }

    public void incFailureCount(final String hostUri)
    {
        int oldCount = 1;
        if (failureCount.containsKey(hostUri))
        {
            oldCount = failureCount.get(hostUri) + 1;
        }
        failureCount.put(hostUri, oldCount);

        if (oldCount >= Scan.getScanSettings().getMaxFailedRequestsPerHost())
        {
            Scan.getInstance().fatalError("Too many failed requests to " + hostUri);
            return;
        }
    }

    public synchronized void incSource(final String source)
    {
        Integer i = sourceCounts.get(source);
        if (i == null)
        {
            i = 0;
        }
        i++;
        sourceCounts.put(source, i);
    }

    private void initializeDatabase()
    {
        LOGGER.debug("Initializing database for transaction storage");
        try
        {
            if (!database.tableExists(tableName))
            {
                String tableQuery = "CREATE TABLE " + tableName + " (\n" + "id INT,\n" + "request_time BIGINT, executed SMALLINT,\n" + "method varchar(15),\n" + "host varchar(100),\n" + "path varchar(2000),\n" + "query varchar(2000),\n"
                                + "response_code INT, response_line varchar(200),\n" + "serialized_transaction BLOB,\nreason varchar(100),\n" + "source varchar(20), PRIMARY KEY (id))";
                String indexQuery0 = "CREATE INDEX IDX_DEFAULT_" + tableName + " ON " + tableName + " (id, executed)";
                database.execute(tableQuery);
                database.execute(indexQuery0);
            }
            else
            {
                setNextTransactionID();
            }
        }
        catch (Throwable e)
        {
            LOGGER.error("Problem with creating transaction record database: " + e.toString(), e);
            System.exit(1);
        }
    }

    private StandardHttpTransaction loadTransaction(final int id) throws DataNotFoundException
    {
        StandardHttpTransaction transaction = null;
        long start = Calendar.getInstance().getTimeInMillis();
        try
        {
            transaction = (StandardHttpTransaction) database.selectSimpleObject("SELECT serialized_transaction FROM " + tableName + " WHERE id = ?", new Object[] { id });
        }
        catch (Throwable e)
        {
            throw new DataNotFoundException("Failed to load transaction", e);
        }
        synchronized (timeLock)
        {
            loadingTime += Calendar.getInstance().getTimeInMillis() - start;
        }
        addOrRefreshTransactionReference(transaction);
        return transaction;
    }

    public void saveOrUpdateTransaction(final StandardHttpTransaction transaction)
    {
        cleanMap();
        if (transactionSaved(transaction.getId()))
        {
            if (!transaction.isSuccessfullExecution())
            {
                LOGGER.warn("Why is an unexecuted transaction being updated?");
            }
            updateTransaction(transaction);
        }
        else
        {
            saveTransaction(transaction);
        }
        addOrRefreshTransactionReference(transaction);
    }

    private void saveTransaction(final StandardHttpTransaction transaction)
    {
        summaryProvider.addOrUpdateTransaction(transaction);
        String query = "INSERT INTO " + tableName + " " + "(executed, " + "request_time, " + "method, " + "host, " + "path, " + "query, " + "response_code, " + "response_line, " + "source, " + "serialized_transaction, " + "id, " + "reason) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        long start = Calendar.getInstance().getTimeInMillis();
        CommandJob job = new CommandJob(query, getTransactionInsertQueryValues(transaction));
        try
        {
            database.execute(job);
        }
        catch (Throwable e)
        {
            LOGGER.error("Problem saving transaction: " + e.toString(), e);
        }
        long end = Calendar.getInstance().getTimeInMillis();
        synchronized (timeLock)
        {
            savingTime += end - start;
        }
        // transaction.setSaved();
    }

    private void setNextTransactionID()
    {
        int id = 0;
        try
        {
            id = database.selectSimpleInt("SELECT MAX(id) FROM " + tableName, new Object[] {});
        }
        catch (DataNotFoundException e)
        {
            // Probably no transactions in table
        }
        catch (Throwable e)
        {
            LOGGER.error("Huge problem getting transaction: " + e.toString(), e);
        }
        HttpTransactionFields.setLastID(id);
    }

    @Override
    public void shutdown(final boolean gracefully) throws InterruptedException
    {
        LOGGER.debug("Shutting down transaction record");
        database.stop(gracefully).join();
    }

    private boolean transactionSaved(final int id)
    {
        String query = "SELECT DISTINCT id FROM " + tableName + " WHERE id = ?";
        try
        {
            database.selectSimpleInt(query, new Object[] { id });
        }
        catch (DataNotFoundException e)
        {
            return false;
        }
        catch (Throwable e)
        {
            LOGGER.error("Weird problem checking for transaction (" + id + "): " + e.toString(), e);
            throw new IllegalStateException(e);
        }
        return true;
    }

    private void updateTransaction(final StandardHttpTransaction transaction)
    {
        summaryProvider.addOrUpdateTransaction(transaction);
        String query = "UPDATE " + tableName + " SET " + "executed = ?, " + "request_time = ?, " + "method = ?, " + "host = ?, " + "path = ?, " + "query = ?, " + "response_code = ?, " + "response_line = ?, " + "source = ?, " + "serialized_transaction = ?, "
                        + "reason = ? " + "WHERE id = ?";
        long start = Calendar.getInstance().getTimeInMillis();
        CommandJob job = new CommandJob(query, getTransactionUpdateQueryValues(transaction));
        try
        {
            database.execute(job);
        }
        catch (Throwable e)
        {
            LOGGER.error("Problem updating transaction: " + e.toString(), e);
        }
        long end = Calendar.getInstance().getTimeInMillis();
        synchronized (timeLock)
        {
            savingTime += end - start;
        }
    }

    public void writeAllExecutedToDisk()
    {
        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    for (int id : getAllCompletedTransactionIDs())
                    {
                        StandardHttpTransaction transaction = getTransaction(id);
                        transaction.writeToDisk(true);
                    }
                }
                catch (DataNotFoundException e)
                {
                    LOGGER.error("Failed to get transactions: " + e.toString(), e);
                }
            }
        });
        t.start();
    }

}
