/**
 * 
 */
package com.grendelscan.commons.flex.messages;

import com.grendelscan.commons.flex.AbstractAmfDataContainer;
import com.grendelscan.commons.flex.AmfPrimitiveData;
import com.grendelscan.commons.flex.dataTypeDefinitions.AmfDataType;

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
    private static final long serialVersionUID = 1L;
    private final static String REMOTE_USERNAME = "Remote username";
    private final static String REMOTE_PASSWORD = "Remote password";
    private final static String SOURCE = "source";
    private final static String OPERATION = "operation";

    private final AmfPrimitiveData source;
    private final AmfPrimitiveData operation;

    private final AmfPrimitiveData remoteUsername;
    private final AmfPrimitiveData remotePassword;

    public AmfRemotingMessage(final String name, final AbstractAmfDataContainer<?> parent, final int transactionId)
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

    /**
     * @param name
     * @param message
     * @param parent
     * @param type
     * @param className
     */
    public AmfRemotingMessage(final String name, final RemotingMessage message, final AbstractAmfDataContainer<?> parent, final int transactionId)
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

    @Override
    protected String getBodyDisplayName()
    {
        return "parameters";
    }

    // @Override
    // protected ArrayList<String> getParameterNames()
    // {
    // // operation
    // // parameters
    // // remotePassword
    // // remoteUsername
    // // source
    //
    // ArrayList<String> names = super.getParameterNames();
    // names.add(OPERATION);
    // names.add(REMOTE_PASSWORD);
    // names.add(REMOTE_USERNAME);
    // names.add(SOURCE);
    // return names;
    // }

}
