package com.grendelscan.scan.sessionState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.RegexUtils;
import com.grendelscan.commons.http.apache_overrides.serializable.SerializableBasicCookie;
import com.grendelscan.commons.http.dataHandling.containers.DataContainer;
import com.grendelscan.commons.http.dataHandling.containers.DataContainerUtils;
import com.grendelscan.commons.http.dataHandling.containers.NameValuePairDataContainer;
import com.grendelscan.commons.http.dataHandling.data.DataUtils;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.data.database.collections.DatabaseBackedMap;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;

public class SessionStates
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionStates.class);
    private static SessionStates instance;

    public static synchronized SessionStates getInstance()
    {
        if (instance == null)
        {
            initialize();
        }
        return instance;
    }

    public static void initialize()
    {
        instance = new SessionStates();
    }

    private final DatabaseBackedMap<String, SessionState> namedSessions;
    private final DatabaseBackedMap<String, List<Integer>> sessionJobHistory;
    private final Set<Pattern> sessionValuePatterns;
    private final Set<String> commonSessionNames;
    private final Set<String> promptedSessionNames;

    private static final int TEST_JOB_CHECKPOINT_SIZE = 100;

    private final Map<String, List<Integer>> profileBuilding;

    private static final Pattern GUID_PATTERN = Pattern.compile("\\b\\{?[0-9a-f]{8}-(?:[0-9a-f]{4}-){3}[0-9a-f]{12}\\}?\\b", Pattern.CASE_INSENSITIVE);

    private static final Pattern MD5_PATTERN = Pattern.compile("\\b[0-9a-f]{32}\\b", Pattern.CASE_INSENSITIVE);

    private static final Pattern SHA1_PATTERN = Pattern.compile("\\b[0-9a-f]{40}\\b", Pattern.CASE_INSENSITIVE);

    private SessionStates()
    {
        namedSessions = new DatabaseBackedMap<String, SessionState>("session_state_map");
        sessionJobHistory = new DatabaseBackedMap<String, List<Integer>>("session_job_histories");
        sessionValuePatterns = new HashSet<Pattern>(1);
        commonSessionNames = new HashSet<String>(1);
        promptedSessionNames = new HashSet<String>(1);
        profileBuilding = new HashMap<String, List<Integer>>(1);
    }

    public void addIdToProfileBuilding(final String sessionKey, final int id)
    {
        List<Integer> ids;
        if (profileBuilding.containsKey(sessionKey))
        {
            ids = profileBuilding.get(sessionKey);
        }
        else
        {
            ids = new ArrayList<Integer>(1);
            profileBuilding.put(sessionKey, ids);
        }
        if (!ids.contains(id))
        {
            ids.add(id);
        }
    }

    private void checkForNewSessionSession(final String paramName, final String domain, final String value, final StandardHttpTransaction transaction, final SessionLocation location)
    {
        String key = SessionState.buildSessionKey(transaction.getUsername(), paramName, domain);
        if (namedSessions.containsKey(key))
        {
            transaction.addSessionStateName(key);
        }
        else if (!transaction.isLoginTransaction())
        {
            SessionState session = new SessionState(paramName, domain, value, transaction.getUsername(), transaction.getRefererId(), transaction.getId(), location);
            if (transaction.isAuthenticated())
            {
                // Removes the old, unauthenticated session from the transaction
                transaction.getSessionStateNames().remove(SessionState.buildSessionKey("", paramName, transaction.getRequestWrapper().getHost()));
                session.setFirstAuthenticatedTransactionId(transaction.getId());
                StandardHttpTransaction loginTransaction = Scan.getInstance().getTransactionRecord().getTransaction(transaction.getRefererId());
                if (loginTransaction.isLoginTransaction())
                {
                    session.setLoginTransactionId(loginTransaction.getId());
                    session.setAuthenticated(true);
                }
                else
                {
                    LOGGER.warn("Why isn't the refering transaction a login?");
                }
            }
            addIdToProfileBuilding(session.getSessionKey(), transaction.getId());
            namedSessions.put(key, session);
            transaction.addSessionStateName(key);
        }
    }

    private synchronized void checkSessionHealth(final StandardHttpTransaction transaction, final SessionState session, final List<Integer> history) throws InterruptedScanException
    {
        if (history.size() >= TEST_JOB_CHECKPOINT_SIZE)
        {
            if (!session.isSessionHealthy())
            {
                Scan.getInstance().getTesterQueue().redoJobs(history);
            }
            history.clear();
        }
        else if (transaction.getTestJobId() > 0)
        {
            history.add(transaction.getTestJobId());
        }

    }

    private boolean containsSessionInProfileBuilding(final StandardHttpTransaction transaction)
    {
        for (String sessionName : transaction.getSessionStateNames())
        {
            if (profileBuilding.containsKey(sessionName))
            {
                return true;
            }
        }
        return false;
    }

    // private void checkLoggedOutProfiles(StandardHttpTransaction transaction) throws InterruptedScanException
    // {
    // for(SessionState session: getSessionStates(transaction))
    // {
    // // This must be a newly authenticated session
    // if (transaction.isAuthenticated() && !session.isAuthenticated())
    // {
    // StandardHttpTransaction loginTransaction = Scan.getInstance().getTransactionRecord().getTransaction(transaction.getRefererId());
    // if (!loginTransaction.isLoginTransaction())
    // {
    // LOGGER.warn("Login transaction isn't a login transaction???");
    // }
    // else
    // {
    // session.setLoginTransactionId(loginTransaction.getId());
    // session.setFirstAuthenticatedTransactionId(transaction.getId());
    // addIdToProfileBuilding(session.getSessionKey(), transaction.getId());
    // session.generateDeadSessionProfile(); // needs to be regenerated since it is now authenticated
    // }
    // }
    // }
    // }

    private void findCookieSessionStates(final StandardHttpTransaction transaction)
    {
        for (SerializableBasicCookie cookie : transaction.getUsedCookies())
        {
            if (isSessionIdentifier("A cookie", cookie.getName(), cookie.getValue(), transaction.getRequestWrapper().toString()))
            {
                checkForNewSessionSession(cookie.getName(), cookie.getDomain(), cookie.getValue(), transaction, SessionLocation.COOKIE);
            }
        }
    }

    private void findParameterSessionStates(final StandardHttpTransaction transaction)
    {
        for (NameValuePairDataContainer param : DataContainerUtils.getAllNamedContaners(transaction.getTransactionContainer()))
        {
            if (isSessionIdentifier("A parameter", new String(DataUtils.getBytes(param.getNameData())), new String(DataUtils.getBytes(param.getValueData())), transaction.getRequestWrapper().toString()))
            {
                SessionLocation location = null;
                if (transaction.getTransactionContainer().getBodyData() instanceof DataContainer && param.isDataAncestor((DataContainer<?>) transaction.getTransactionContainer().getBodyData()))
                {
                    location = SessionLocation.BODY;
                }
                else if (param.isDataAncestor(transaction.getTransactionContainer().getUrlQueryDataContainer()))
                {
                    location = SessionLocation.URL_QUERY;
                }
                else
                {
                    throw new IllegalStateException("Unknown location type");
                }
                checkForNewSessionSession(new String(DataUtils.getBytes(param.getNameData())), transaction.getRequestWrapper().getHost(), new String(DataUtils.getBytes(param.getValueData())), transaction, location);
            }
        }
    }

    public Set<SessionState> getSessionStates(final StandardHttpTransaction transaction)
    {
        Set<SessionState> states = new HashSet<SessionState>(1);
        for (String name : transaction.getSessionStateNames())
        {
            states.add(namedSessions.get(name));
        }
        return states;
    }

    public void identifySessions(final StandardHttpTransaction transaction) throws InterruptedScanException
    {

        while (containsSessionInProfileBuilding(transaction) && !isProfileBuildingRelatedRequest(transaction))
        {
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                throw new InterruptedScanException(e);
            }
        }

        synchronized (namedSessions)
        {
            findParameterSessionStates(transaction);
            findCookieSessionStates(transaction);
        }

        if (transaction.getSessionStateNames() == null || transaction.getSessionStateNames().size() == 0)
        {
            return;
        }

        if (containsSessionInProfileBuilding(transaction) && isProfileBuildingRelatedRequest(transaction))
        {
            return;
        }
        // checkLoggedOutProfiles(transaction);
        updateSessions(transaction);
    }

    private boolean isProfileBuildingRelatedRequest(final StandardHttpTransaction transaction)
    {
        for (String sessionName : transaction.getSessionStateNames())
        {
            if (profileBuilding.containsKey(sessionName))
            {
                List<Integer> testingTransactions = profileBuilding.get(sessionName);
                if (testingTransactions.contains(transaction.getId()))
                {
                    return true;
                }
                if (transaction.getRefererChain() != null)
                {
                    for (int referer : transaction.getRefererChain())
                    {
                        if (testingTransactions.contains(referer))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isSessionIdentifier(final String type, final String name, final String value, final String transaction)
    {
        if (commonSessionNames.contains(name))
        {
            return true;
        }

        if (RegexUtils.matchAnyPattern(Scan.getScanSettings().getReadOnlyKnownSessionIDRegexs(), name))
        {
            return true;
        }

        if (RegexUtils.matchAnyPattern(sessionValuePatterns, value))
        {
            return true;
        }

        if (promptedSessionNames.contains(name))
        {
            return false;
        }

        if (GUID_PATTERN.matcher(value).matches())
        {
            promptedSessionNames.add(name);
            return promptUser(type, name, value, " the value contains a GUID/UUID.", transaction);
        }

        if (MD5_PATTERN.matcher(value).matches())
        {
            promptedSessionNames.add(name);
            return promptUser(type, name, value, " the value may contain an MD5 hash.", transaction);
        }

        if (SHA1_PATTERN.matcher(value).matches())
        {
            promptedSessionNames.add(name);
            return promptUser(type, name, value, " the value may contain a SHA1 hash.", transaction);
        }

        return false;
    }

    public void postExecutionFollowup(final StandardHttpTransaction transaction) throws InterruptedScanException
    {

        if (profileBuildingTransaction(transaction.getId()))
        {
            Set<String> profileBuildingSessionNames = new HashSet<String>();
            profileBuildingSessionNames.addAll(profileBuilding.keySet());
            for (String sessionName : profileBuildingSessionNames)
            {
                // If this is the original transaction generating profile building
                if (profileBuilding.get(sessionName).get(0) == transaction.getId())
                {
                    namedSessions.get(sessionName).generateDeadSessionProfile();
                    profileBuilding.remove(sessionName);
                }
            }
        }
        else
        {
            for (SessionState session : getSessionStates(transaction))
            {
                List<Integer> history;
                if (sessionJobHistory.containsKey(session.getSessionKey()))
                {
                    history = sessionJobHistory.get(session.getSessionKey());
                }
                else
                {
                    history = new ArrayList<Integer>(TEST_JOB_CHECKPOINT_SIZE);
                    sessionJobHistory.put(session.getSessionKey(), history);
                }
                checkSessionHealth(transaction, session, history);
            }
        }

    }

    private boolean profileBuildingTransaction(final int id)
    {
        for (List<Integer> ids : profileBuilding.values())
        {
            if (ids.contains(id))
            {
                return true;
            }
        }
        return false;
    }

    private boolean promptUser(final String type, final String name, final String value, final String message, final String transaction)
    {
        if (Scan.getInstance().isGUI())
        {
            String m = type + " has been discovered that could be a session identifier. The name is \"" + name + "\" and the value is \"" + value + "\". It looks like a session identifier because " + message
                            + "\n\nShould Grendel-Scan treat it as a session identifier? You will only be asked once. " + "The full HTTP request is below:\n\n" + transaction;
            int response = MainWindow.getInstance().displayPrompt("Session Identifier Question", m, SWT.YES | SWT.NO, true);
            return response == SWT.YES;
        }
        return false;
    }

    private void updateSessions(final StandardHttpTransaction transaction)
    {
        for (SessionState session : getSessionStates(transaction))
        {
            session.updateTransaction(transaction);
        }
    }
}
