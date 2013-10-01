package com.grendelscan.smashers.utils.tokens;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.grendelscan.commons.ConfigurationManager;
import com.grendelscan.commons.MimeUtils;
import com.grendelscan.commons.StringUtils;
import com.grendelscan.commons.html.HtmlNodeWriter;
import com.grendelscan.commons.html.HtmlUtils;
import com.grendelscan.commons.http.RequestOptions;
import com.grendelscan.commons.http.dataHandling.containers.DataContainerUtils;
import com.grendelscan.commons.http.dataHandling.data.ByteData;
import com.grendelscan.commons.http.dataHandling.data.Data;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.transactions.UnrequestableTransaction;
import com.grendelscan.data.database.CommandJob;
import com.grendelscan.data.database.DataNotFoundException;
import com.grendelscan.data.database.Database;
import com.grendelscan.data.database.DatabaseUser;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.scan.TransactionSource;

/**
 * This is used to track when tokens are submitted in a query to probe where they end up. It is mostly useful for XSS and HTTP response splitting testing.
 * 
 * @author David Byrne
 * 
 */
public class TokenTesting implements DatabaseUser
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenTesting.class);
    private static TokenTesting instance;

    public static TokenTesting getInstance()
    {
        return instance;
    }

    public static void initialize()
    {
        instance = new TokenTesting();
    }

    private final Database database;
    private final String tableName = "tokens";
    private final Pattern tokenPattern;
    private String tokenPrefix;

    private final int tokenPrefixLength;
    private final int tokenSuffixLength;
    private static final String TOKEN_PREFIX_NAME = "token_testing_prefix";

    private TokenTesting()
    {

        tokenPrefixLength = ConfigurationManager.getInt("token_testing.token_prefix_length");
        tokenSuffixLength = ConfigurationManager.getInt("token_testing.token_suffix_length");
        database = Scan.getInstance().getTestData().getDatabase();
        try
        {
            tokenPrefix = Scan.getInstance().getTestData().getString(TOKEN_PREFIX_NAME);
        }
        catch (DataNotFoundException e)
        {
            tokenPrefix = StringUtils.generateRandomString(StringUtils.FORMAT_UPPER_CASE_ALPHA, tokenPrefixLength);
            Scan.getInstance().getTestData().setString(TOKEN_PREFIX_NAME, tokenPrefix);
        }
        tokenPattern = Pattern.compile("(" + Pattern.quote(tokenPrefix) + "[A-Z]{" + tokenSuffixLength + "})");

        initializeDatabase();
    }

    private void addNodeResult(final String token, final Node node, final int outputTransactionID, final int originatingTransactionID, final DiscoveredContexts results)
    {
        for (TokenContextType contextType : findNodeTokenContextTypes(token, node))
        {
            Set<String> quotes = new HashSet<String>(1);
            StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(outputTransactionID);
            String body = new String(transaction.getResponseWrapper().getBody());
            Pattern p = Pattern.compile(token + "(['\"])");
            Matcher m = p.matcher(body);
            while (m.find())
            {
                quotes.add(m.group(1));
            }
            String quoteChars[] = new String[quotes.size()];
            int index = 0;
            for (String quote : quotes)
            {
                quoteChars[index++] = quote;
            }
            HtmlContext context = new HtmlContext(token, contextType, outputTransactionID, originatingTransactionID, quoteChars);
            results.addContext(context);
        }
    }

    /**
     * This takes a discovered context, and repeats the input transaction (where the token is submitted) and output transaction (where the token is observed in the result) with a new token. The new
     * token could also be an attack string. The new output transaction is returned. The original input and output transaction can be the same object. The new transactions are not submitted for
     * testing.
     * 
     * @param context
     * @param newToken
     * @return Element 0 is the new input transaction, element 1 is the new output transaction. They may be the same object, but both elements will be populated
     * @throws UnrequestableTransaction
     * @throws InterruptedScanException
     */
    public StandardHttpTransaction[] duplicateTokenTest(final TokenContext context, final String newToken, final String stringSource, final TransactionSource source, final int testJobId) throws UnrequestableTransaction, InterruptedScanException
    {
        StandardHttpTransaction originalOutputTransaction = Scan.getInstance().getTransactionRecord().getTransaction(context.getOutputTransactionID());
        StandardHttpTransaction originalInputTransaction = Scan.getInstance().getTransactionRecord().getTransaction(context.getOriginatingTransactionID());
        StandardHttpTransaction trans[] = new StandardHttpTransaction[2];
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.testTransaction = false;
        requestOptions.reason = "TokenTesting.duplicateTokenTest on behalf of " + stringSource;

        if (originalInputTransaction.getId() == originalOutputTransaction.getId())
        {
            StandardHttpTransaction newTestTransaction = originalOutputTransaction.cloneFullRequest(source, testJobId);
            ByteData newTestData = (ByteData) DataContainerUtils.resolveReferenceChain(newTestTransaction.getTransactionContainer(), context.getRequestDatum().getReferenceChain());
            newTestData.setBytes(newToken.getBytes());
            // String uri = newTestTransaction.getRequestWrapper().getAbsoluteUriString();
            // uri = uri.replace(context.getToken(), newToken);
            // newTestTransaction.getRequestWrapper().setURI(uri, true);
            newTestTransaction.setRequestOptions(requestOptions);
            newTestTransaction.execute();
            trans[0] = trans[1] = newTestTransaction;
        }
        else
        {
            StandardHttpTransaction newInputTransaction = originalInputTransaction.cloneFullRequest(source, testJobId);
            ByteData newTestData = (ByteData) DataContainerUtils.resolveReferenceChain(newInputTransaction.getTransactionContainer(), context.getRequestDatum().getReferenceChain());
            newTestData.setBytes(newToken.getBytes());
            // String uri = newInputTransaction.getRequestWrapper().getAbsoluteUriString();
            // uri = uri.replace(context.getToken(), newToken);
            // newInputTransaction.getRequestWrapper().setURI(uri, true);
            newInputTransaction.setRequestOptions(requestOptions);
            newInputTransaction.execute();

            StandardHttpTransaction newOutputTransaction = originalOutputTransaction.cloneFullRequest(source, testJobId);
            newOutputTransaction.setRequestOptions(requestOptions);
            newOutputTransaction.execute();

            trans[0] = newInputTransaction;
            trans[1] = newOutputTransaction;
        }

        return trans;
    }

    private Set<TokenContextType> findNodeTokenContextTypes(final String token, final Node node)
    {
        HashSet<TokenContextType> contexts = new HashSet<TokenContextType>();
        String nodeString = HtmlNodeWriter.write(node, true, null);
        String nodeName = node.getNodeName();
        String nodeValue = node.getNodeValue();
        int nodeType = node.getNodeType();

        if (nodeString.toUpperCase().contains(token.toUpperCase()))
        {
            if (nodeName.toUpperCase().contains(token.toUpperCase()))
            {
                if (node instanceof Attr)
                {
                    contexts.add(TokenContextType.HTML_TAG_ATTRIBUTE_NAME);
                }
                else
                {
                    contexts.add(TokenContextType.HTML_TAG_NAME);
                }
            }
            if (nodeValue.toUpperCase().contains(token.toUpperCase()))
            {
                if (node instanceof Attr)
                {

                    if (HtmlUtils.isDOMEvent(nodeName))
                    {
                        contexts.add(TokenContextType.HTML_EVENT_HANDLER);
                    }
                    else
                    {
                        contexts.add(TokenContextType.HTML_TAG_ATTRIBUTE_VALUE);
                    }
                }
                else
                {
                    if (nodeType == Node.TEXT_NODE)
                    {
                        if (node.getParentNode().getNodeName().equalsIgnoreCase("SCRIPT"))
                        {
                            contexts.add(TokenContextType.HTML_SCRIPT);
                        }
                        else if (node.getParentNode().getNodeName().equalsIgnoreCase("STYLE"))
                        {
                            contexts.add(TokenContextType.HTML_STYLE);
                        }
                        else if (node.getParentNode().getNodeName().equalsIgnoreCase("TEXTAREA"))
                        {
                            contexts.add(TokenContextType.HTML_TEXTAREA);
                        }
                        else if (node.getParentNode().getNodeName().equalsIgnoreCase("PRE"))
                        {
                            contexts.add(TokenContextType.HTML_PRE);
                        }
                        else if (node.getParentNode().getNodeName().equalsIgnoreCase("TITLE"))
                        {
                            contexts.add(TokenContextType.HTML_TITLE);
                        }
                        else
                        {
                            contexts.add(TokenContextType.HTML_TEXT);
                        }

                    }
                    else if (nodeType == Node.COMMENT_NODE)
                    {
                        contexts.add(TokenContextType.HTML_COMMENT);
                    }
                }
            }
        }

        return contexts;
    }

    public DiscoveredContexts findTokenContexts(final StandardHttpTransaction transaction) throws InterruptedScanException
    {
        DiscoveredContexts contexts = new DiscoveredContexts();
        Scan.getInstance().getTesterQueue().handlePause_isRunning();
        parseHeaders(transaction, contexts);
        Scan.getInstance().getTesterQueue().handlePause_isRunning();
        parseByMimeType(transaction, contexts);
        return contexts;
    }

    public DiscoveredContexts findTokenContexts(final StandardHttpTransaction transaction, final String token) throws InterruptedScanException
    {
        return findTokenContexts(transaction).getAllOfToken(token);
    }

    public String generateToken()
    {
        return tokenPrefix + StringUtils.generateRandomString(StringUtils.FORMAT_UPPER_CASE_ALPHA, tokenSuffixLength);
    }

    public String getTokenPrefix()
    {
        return tokenPrefix;
    }

    public int getTokenPrefixLength()
    {
        return tokenPrefixLength;
    }

    public int getTokenSuffixLength()
    {
        return tokenSuffixLength;
    }

    public Data getTokenTest(final String token)
    {
        try
        {
            return (Data) database.selectSimpleObject("SELECT serialized_datum FROM " + tableName + " WHERE token = ?", new Object[] { token });
        }
        catch (DataNotFoundException e)
        {
            LOGGER.warn("Token not found in database (" + token + "): " + e.toString());
            return null;
        }
        catch (Throwable e)
        {
            LOGGER.error("Problem getting token from database (" + token + "): " + e.toString(), e);
        }
        return null;
    }

    public void initializeDatabase()
    {
        LOGGER.debug("Initializing database for transaction storage");
        try
        {
            if (!database.tableExists(tableName))
            {
                String tableQuery = "CREATE TABLE " + tableName + " (token varchar(" + (tokenPrefixLength + tokenSuffixLength) + "), " + "serialized_datum blob, PRIMARY KEY (token))";
                String indexQuery0 = "CREATE INDEX IDX_DEFAULT_" + tableName + " ON " + tableName + " (token)";
                database.execute(tableQuery);
                database.execute(indexQuery0);
            }
        }
        catch (Throwable e)
        {
            LOGGER.error("Problem with creating database for token testing: " + e.toString(), e);
            System.exit(1);
        }
    }

    private void parseByMimeType(final StandardHttpTransaction transaction, final DiscoveredContexts contexts)
    {
        String responseBody = new String(transaction.getResponseWrapper().getBody());
        if (!responseBody.toLowerCase().contains(tokenPrefix.toLowerCase()))
        {
            return;
        }
        String mimeType = transaction.getResponseWrapper().getHeaders().getMimeType().toLowerCase();

        if (MimeUtils.isHtmlMimeType(mimeType))
        {
            recursiveNodeSearch(transaction.getResponseWrapper().getResponseDOM(), contexts, transaction.getId());
        }
        else
        {
            String upperResponseBody = responseBody.toUpperCase();
            Matcher m = tokenPattern.matcher(upperResponseBody);
            while (m.find())
            {
                String token = m.group(1);
                TokenContextType contextType;
                // Won't be reached because text/xml can be an XHTML mime type
                if (mimeType.equals("text/xml") || mimeType.equals("appplication/xml"))
                {
                    contextType = TokenContextType.XML;
                }
                else if (mimeType.equals("text/javascript") || mimeType.equals("text/ecmascript") || mimeType.equals("application/x-javascript"))
                {
                    contextType = TokenContextType.JAVASCRIPT_NON_HTML;
                }
                else if (mimeType.equals("text/css"))
                {
                    contextType = TokenContextType.CSS;
                }
                else if (mimeType.equals("text/plain"))
                {
                    contextType = TokenContextType.TEXT;
                }
                else
                {
                    contextType = TokenContextType.OTHER_NON_HTML;
                }

                int originatingTransactionID = getTokenTest(token).getTransactionId();
                NonHtmlBodyContext context = new NonHtmlBodyContext(token, contextType, transaction.getId(), originatingTransactionID, mimeType);
                contexts.addContext(context);
            }
        }
    }

    private void parseHeaders(final StandardHttpTransaction transaction, final DiscoveredContexts contexts)
    {
        for (Header header : transaction.getResponseWrapper().getHeaders().getReadOnlyHeaders())
        {
            String name = header.getName().toUpperCase();
            String value = header.getValue().toUpperCase();
            Matcher m = tokenPattern.matcher(name);
            while (m.find())
            {
                String token = m.group(1);
                contexts.addContext(new HttpHeaderContext(token, TokenContextType.HTTP_HEADER_NAME, transaction.getId(), header, transaction.getId()));
            }

            m = tokenPattern.matcher(value);
            while (m.find())
            {
                String token = m.group(1);
                contexts.addContext(new HttpHeaderContext(token, TokenContextType.HTTP_HEADER_VALUE, transaction.getId(), header, transaction.getId()));
            }
        }
    }

    public void recordTokenTest(final String token, final Data datum)
    {
        String query = "INSERT INTO " + tableName + " " + "(token, serialized_datum) " + "VALUES (?, ?)";
        CommandJob job = new CommandJob(query, new Object[] { token, datum });
        try
        {
            database.execute(job);
        }
        catch (Throwable e)
        {
            LOGGER.error("Problem saving transaction: " + e.toString(), e);
        }
    }

    private void recursiveNodeSearch(final Node node, final DiscoveredContexts results, final int outputTransactionID)
    {
        /*
         * Add the node as a result if the node value contains the token. This should usually be for text nodes and attribute nodes
         */
        if (node == null)
        {
            return;
        }
        String nodeValue = node.getNodeValue();
        if (nodeValue != null)
        {
            for (String token : searchString(nodeValue))
            {
                int originatingTransactionID = getTokenTest(token).getTransactionId();
                addNodeResult(token, node, outputTransactionID, originatingTransactionID, results);
            }
        }

        /*
         * Add the node as a result if the node name contains the token.
         */
        for (String token : searchString(node.getNodeName()))
        {
            int originatingTransactionID = getTokenTest(token).getTransactionId();
            addNodeResult(token, node, outputTransactionID, originatingTransactionID, results);
        }

        short type = node.getNodeType();
        switch (type)
        {
            case Node.DOCUMENT_NODE:
            {
                // Document document = (Document) node;
                // recursiveNodeSearch(document.getDocumentElement(), results, outputTransactionID);
                // break;
            }
            /*
             * I don't think the doc type node needs to be searched.
             */

            case Node.ELEMENT_NODE:
            {
                NamedNodeMap attrs = node.getAttributes();
                if (attrs != null)
                {
                    for (int index = 0; index < attrs.getLength(); index++)
                    {
                        Attr attr = (Attr) attrs.item(index);
                        String name = attr.getNodeName();
                        String value = attr.getNodeValue();
                        if (name != null)
                        {
                            for (String token : searchString(name))
                            {
                                int originatingTransactionID = getTokenTest(token).getTransactionId();
                                addNodeResult(token, attr, outputTransactionID, originatingTransactionID, results);
                            }
                        }
                        if (value != null)
                        {
                            for (String token : searchString(value))
                            {
                                int originatingTransactionID = getTokenTest(token).getTransactionId();
                                addNodeResult(token, attr, outputTransactionID, originatingTransactionID, results);
                            }
                        }
                    }
                }

                Node child = node.getFirstChild();
                while (child != null)
                {
                    recursiveNodeSearch(child, results, outputTransactionID);
                    child = child.getNextSibling();
                }
                break;
            }

            case Node.ENTITY_REFERENCE_NODE:
            {
                Node child = node.getFirstChild();
                while (child != null)
                {
                    recursiveNodeSearch(child, results, outputTransactionID);
                    child = child.getNextSibling();
                }
                break;
            }
        }
    }

    private Set<String> searchString(final String targetString)
    {
        Set<String> results = new HashSet<String>();
        String target = targetString.toUpperCase();
        Matcher m = tokenPattern.matcher(target);
        while (m.find())
        {
            results.add(m.group(1));
        }
        return results;
    }

    @Override
    public void shutdown(final boolean gracefully) throws InterruptedException
    {
        LOGGER.debug("Shutting down PersistedTestData");
        database.stop(gracefully).join();
    }

    /**
     * Confirms that repeating the same requests with a different token will result in the same token context.
     * 
     * @param transaction
     * @param originalContext
     * @return
     * @throws UnrequestableTransaction
     * @throws InterruptedScanException
     * @throws InterruptedException
     */
    public boolean verifyTokenRepeatability(final TokenContext originalContext, final String stringSource, final TransactionSource source, final int testJobId) throws UnrequestableTransaction, InterruptedScanException
    {
        String newToken = generateToken();
        StandardHttpTransaction testTransactions[] = duplicateTokenTest(originalContext, newToken, stringSource, source, testJobId);
        StandardHttpTransaction newInputTransaction = testTransactions[0];
        StandardHttpTransaction newOutputTransaction = testTransactions[1];
        Data newTestDatum = DataContainerUtils.resolveReferenceChain(newInputTransaction.getTransactionContainer(), originalContext.getRequestDatum().getReferenceChain());
        recordTokenTest(newToken, newTestDatum);

        return findTokenContexts(newOutputTransaction).getAllOfToken(newToken).getAllOfType(originalContext.getContextType()).getCount() > 0;
    }

}
