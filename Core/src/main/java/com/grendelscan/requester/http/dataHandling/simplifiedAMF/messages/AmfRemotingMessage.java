/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.simplifiedAMF.messages;

import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.references.NameOrValueReference;
import com.grendelscan.requester.http.dataHandling.references.NamedDataContainerDataReference;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfData;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfDataContainer;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AmfPrimitiveData;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.dataTypeDefinitions.AmfDataType;

import flex.messaging.messages.RemotingMessage;

/**
 * @author david
 *
 */
public class AmfRemotingMessage extends AmfAbstractMessage
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private final static String REMOTE_USERNAME = "Remote username";
	private final static String REMOTE_PASSWORD = "Remote password";
	private final static String SOURCE = "source";
	private final static String OPERATION = "operation";
	
    private AmfPrimitiveData source;
    private AmfPrimitiveData operation;

    private AmfPrimitiveData remoteUsername;
    private AmfPrimitiveData remotePassword;
	/**
	 * @param name
	 * @param message
	 * @param parent
	 * @param type
	 * @param className
	 */
	public AmfRemotingMessage(String name, RemotingMessage message, AbstractAmfDataContainer<?> parent, int transactionId)
	{
		super(name, message, AmfDataType.kAmfRemotingMessage, message.getClass().getCanonicalName(), parent, transactionId);
		remoteUsername = new AmfPrimitiveData(REMOTE_USERNAME, message.getRemoteUsername().getBytes(), this, transactionId, true);
		remotePassword = new AmfPrimitiveData(REMOTE_PASSWORD, message.getRemotePassword().getBytes(), this, transactionId, true);
		source = new AmfPrimitiveData(SOURCE, message.getSource().getBytes(), this, transactionId, true);
		operation = new AmfPrimitiveData(OPERATION, message.getOperation().getBytes(), this, transactionId, true);
		putChild(REMOTE_USERNAME, remoteUsername);
		putChild(REMOTE_PASSWORD, remotePassword);
		putChild(SOURCE, source);
		putChild(OPERATION, operation);
	}
	
	public AmfRemotingMessage(String name, AbstractAmfDataContainer<?> parent, int transactionId)
	{
		super(name, AmfDataType.kAmfRemotingMessage, false, parent, transactionId);
		remoteUsername = new AmfPrimitiveData(REMOTE_USERNAME, new byte[0], this, transactionId, true);
		remotePassword = new AmfPrimitiveData(REMOTE_PASSWORD, new byte[0], this, transactionId, true);
		source = new AmfPrimitiveData(SOURCE, new byte[0], this, transactionId, true);
		operation = new AmfPrimitiveData(OPERATION, new byte[0], this, transactionId, true);
		
		putChild(REMOTE_USERNAME, remoteUsername);
		putChild(REMOTE_PASSWORD, remotePassword);
		putChild(SOURCE, source);
		putChild(OPERATION, operation);
	}

	@Override protected String getBodyDisplayName()
	{
		return "parameters";
	}

//	@Override
//	protected ArrayList<String> getParameterNames()
//	{
////		operation
////		parameters
////		remotePassword
////		remoteUsername
////		source
//
//		ArrayList<String> names = super.getParameterNames();
//		names.add(OPERATION);
//		names.add(REMOTE_PASSWORD);
//		names.add(REMOTE_USERNAME);
//		names.add(SOURCE);
//		return names;
//	}

}
