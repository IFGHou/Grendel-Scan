package com.grendelscan.requester.sessionState;

import java.util.*;
import java.util.regex.*;

import org.eclipse.swt.SWT;

import com.grendelscan.GUI.MainWindow;
import com.grendelscan.data.database.collections.DatabaseBackedMap;
import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.apache_overrides.serializable.SerializableBasicCookie;
import com.grendelscan.requester.http.dataHandling.containers.DataContainer;
import com.grendelscan.requester.http.dataHandling.containers.DataContainerUtils;
import com.grendelscan.requester.http.dataHandling.containers.NamedDataContainer;
import com.grendelscan.requester.http.dataHandling.data.DataUtils;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.utils.RegexUtils;

public class SessionStates
{
	private static SessionStates instance;

	private DatabaseBackedMap<String, SessionState> namedSessions;
	private DatabaseBackedMap<String, List<Integer>> sessionJobHistory;
	private Set<Pattern> sessionValuePatterns;
	private Set<String> commonSessionNames;
	private Set<String> promptedSessionNames;
	private static final int TEST_JOB_CHECKPOINT_SIZE = 100;
	private final Map<String, List<Integer>> profileBuilding;
	
	public static void initialize()
	{
		instance = new SessionStates();
	}
	
	public static synchronized SessionStates getInstance()
	{
		if (instance == null)
		{
			initialize();
		}
		return instance;
	}
	
	private SessionStates()
	{
		namedSessions = new DatabaseBackedMap<String, SessionState>("session_state_map");
		sessionJobHistory = new DatabaseBackedMap<String, List<Integer>>("session_job_histories");
		sessionValuePatterns = new HashSet<Pattern>(1);
		commonSessionNames = new HashSet<String>(1);
		promptedSessionNames = new HashSet<String>(1);
		profileBuilding = new HashMap<String, List<Integer>>(1);
	}
	
	
	private boolean isProfileBuildingRelatedRequest(StandardHttpTransaction transaction)
	{
		for (String sessionName: transaction.getSessionStateNames())
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
					for (int referer: transaction.getRefererChain())
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
	
	private boolean containsSessionInProfileBuilding(StandardHttpTransaction transaction)
	{
		for (String sessionName: transaction.getSessionStateNames())
		{
			if (profileBuilding.containsKey(sessionName))
			{
				return true;
			}
		}
		return false;
	}
	
	public void identifySessions(StandardHttpTransaction transaction) throws InterruptedScanException
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

		synchronized(namedSessions)
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
//		checkLoggedOutProfiles(transaction);
		updateSessions(transaction);
	}
	
	public Set<SessionState> getSessionStates(StandardHttpTransaction transaction)
	{
		Set<SessionState> states = new HashSet<SessionState>(1);
		for (String name: transaction.getSessionStateNames())
		{
			states.add(namedSessions.get(name));
		}
		return states;
	}
	
	private synchronized void checkSessionHealth(StandardHttpTransaction transaction, SessionState session, List<Integer> history) throws InterruptedScanException
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
	
	private boolean profileBuildingTransaction(int id)
	{
		for(List<Integer> ids: profileBuilding.values())
		{
			if (ids.contains(id))
			{
				return true;
			}
		}
		return false;
	}
	
	public void postExecutionFollowup(StandardHttpTransaction transaction) throws InterruptedScanException
	{
		
		if (profileBuildingTransaction(transaction.getId()))
		{
			Set<String> profileBuildingSessionNames = new HashSet<String>();
			profileBuildingSessionNames.addAll(profileBuilding.keySet());
			for(String sessionName: profileBuildingSessionNames)
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
			for(SessionState session: getSessionStates(transaction))
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
	
//	private void checkLoggedOutProfiles(StandardHttpTransaction transaction) throws InterruptedScanException
//	{
//		for(SessionState session: getSessionStates(transaction))
//		{
//			// This must be a newly authenticated session
//			if (transaction.isAuthenticated() && !session.isAuthenticated())
//			{
//				StandardHttpTransaction loginTransaction = Scan.getInstance().getTransactionRecord().getTransaction(transaction.getRefererId());
//				if (!loginTransaction.isLoginTransaction())
//				{
//					Log.warn("Login transaction isn't a login transaction???");
//				}
//				else
//				{
//					session.setLoginTransactionId(loginTransaction.getId());
//					session.setFirstAuthenticatedTransactionId(transaction.getId());
//					addIdToProfileBuilding(session.getSessionKey(), transaction.getId());
//					session.generateDeadSessionProfile(); // needs to be regenerated since it is now authenticated
//				}
//			}
//		}
//	}

	public void addIdToProfileBuilding(String sessionKey, int id)
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
	
	
	private void updateSessions(StandardHttpTransaction transaction)
	{
		for(SessionState session: getSessionStates(transaction))
		{
			session.updateTransaction(transaction);
		}
	}

	
	private void findCookieSessionStates(StandardHttpTransaction transaction)
	{
		for (SerializableBasicCookie cookie: transaction.getUsedCookies())
		{
			if (isSessionIdentifier("A cookie", cookie.getName(), cookie.getValue(), transaction.getRequestWrapper().toString()))
			{
				checkForNewSessionSession(cookie.getName(), cookie.getDomain(), cookie.getValue(), transaction, SessionLocation.COOKIE);
			}
		}
	}
	
	
	private void findParameterSessionStates(StandardHttpTransaction transaction)
	{
		for (NamedDataContainer param: DataContainerUtils.getAllNamedContaners(transaction.getTransactionContainer()))
		{
			if (isSessionIdentifier("A parameter", new String(DataUtils.getBytes(param.getNameData())), 
					new String(DataUtils.getBytes(param.getValueData())), transaction.getRequestWrapper().toString()))
			{
				SessionLocation location = null;
				if (transaction.getTransactionContainer().getBodyData() instanceof DataContainer &&
					param.isDataAncestor((DataContainer<?>) transaction.getTransactionContainer().getBodyData()))
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
				checkForNewSessionSession(new String(DataUtils.getBytes(param.getNameData())), transaction.getRequestWrapper().getHost(), 
						new String(DataUtils.getBytes(param.getValueData())), transaction, location);
			}
		}
	}
	
	private void checkForNewSessionSession(String paramName, String domain, String value, StandardHttpTransaction transaction, SessionLocation location)
	{
		String key = SessionState.buildSessionKey(transaction.getUsername(), paramName, domain);
		if (namedSessions.containsKey(key))
		{
			transaction.addSessionStateName(key);
		}
		else if (!transaction.isLoginTransaction())
		{
			SessionState session = new SessionState(paramName, domain, value, 
					transaction.getUsername(), transaction.getRefererId(), transaction.getId(), 
					location);
			if(transaction.isAuthenticated())
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
					Log.warn("Why isn't the refering transaction a login?");
				}
			}
			addIdToProfileBuilding(session.getSessionKey(), transaction.getId());
			namedSessions.put(key, session);
			transaction.addSessionStateName(key);
		}
	}

	private static final Pattern GUID_PATTERN = Pattern.compile("\\b\\{?[0-9a-f]{8}-(?:[0-9a-f]{4}-){3}[0-9a-f]{12}\\}?\\b", Pattern.CASE_INSENSITIVE);
	private static final Pattern MD5_PATTERN = Pattern.compile("\\b[0-9a-f]{32}\\b", Pattern.CASE_INSENSITIVE);
	private static final Pattern SHA1_PATTERN = Pattern.compile("\\b[0-9a-f]{40}\\b", Pattern.CASE_INSENSITIVE);
	
	private boolean isSessionIdentifier(String type, String name, String value, String transaction)
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
	
	private boolean promptUser(String type, String name, String value, String message, String transaction)
	{
		if (Scan.getInstance().isGUI())
		{
			String m = type + " has been discovered that could be a session identifier. The name is \"" + name +
					"\" and the value is \"" + value + "\". It looks like a session identifier because " + message + 
					"\n\nShould Grendel-Scan treat it as a session identifier? You will only be asked once. " +
					"The full HTTP request is below:\n\n" + transaction;
			int response = MainWindow.getInstance().displayPrompt("Session Identifier Question", m, SWT.YES | SWT.NO, true);
			return response == SWT.YES;
		}
		return false;
	}
}
