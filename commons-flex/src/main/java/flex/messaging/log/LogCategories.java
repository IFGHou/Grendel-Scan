/*************************************************************************
 * 
 * ADOBE CONFIDENTIAL __________________
 * 
 * Copyright 2002 - 2007 Adobe Systems Incorporated All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains the property of Adobe Systems Incorporated and its suppliers, if any. The intellectual and technical concepts contained herein are
 * proprietary to Adobe Systems Incorporated and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of
 * this information or reproduction of this material is strictly forbidden unless prior written permission is obtained from Adobe Systems Incorporated.
 **************************************************************************/
package flex.messaging.log;

/**
 * This class contains all the log categories used in our classes. When adding a new log category, make sure the sample configuration file is updated as well.
 * 
 * @author matamel
 * @exclude
 */
public interface LogCategories
{
    String CLIENT_FLEXCLIENT = "Client.FlexClient";
    String CLIENT_MESSAGECLIENT = "Client.MessageClient";

    String CONFIGURATION = "Configuration";

    String ENDPOINT_GENERAL = "Endpoint.General";
    String ENDPOINT_AMF = "Endpoint.AMF";
    String ENDPOINT_NIO_AMF = "Endpoint.NIOAMF";
    String ENDPOINT_FLEXSESSION = "Endpoint.FlexSession";
    String ENDPOINT_HTTP = "Endpoint.HTTP";
    String ENDPOINT_NIO_HTTP = "Endpoint.NIOHTTP";
    String ENDPOINT_RTMP = "Endpoint.RTMP";
    String ENDPOINT_STREAMING_AMF = "Endpoint.StreamingAMF";
    String ENDPOINT_STREAMING_NIO_AMF = "Endpoint.StreamingNIOAMF";
    String ENDPOINT_STREAMING_HTTP = "Endpoint.StreamingHTTP";
    String ENDPOINT_STREAMING_NIO_HTTP = "Endpoint.StreamingNIOHTTP";
    String ENDPOINT_TYPE = "Endpoint.Type";

    String EXECUTOR = "Executor";

    String MANAGEMENT_GENERAL = "Management.General";
    String MANAGEMENT_MBEANSERVER = "Management.MBeanServer";

    String MESSAGE_GENERAL = "Message.General";
    String MESSAGE_COMMAND = "Message.Command";
    String MESSAGE_DATA = "Message.Data";
    String MESSAGE_REMOTING = "Message.Remoting";
    String MESSAGE_RPC = "Message.RPC";
    String MESSAGE_SELECTOR = "Message.Selector";
    String MESSAGE_TIMING = "Message.Timing";

    String PROTOCOL_HTTP = "Protocol.HTTP";
    String PROTOCOL_RTMP = "Protocol.RTMP";
    String PROTOCOL_RTMPT = "Protocol.RTMPT";

    String RESOURCE = "Resource";

    String SERVICE_GENERAL = "Service.General";
    String SERVICE_CLUSTER = "Service.Cluster";
    String SERVICE_COLLABORATION = "Service.Collaboration";
    String SERVICE_DATA = "Service.Data"; // Not a category but used by TargetSettings to replace DataService
    String SERVICE_DATA_GENERAL = "Service.Data.General";
    String SERVICE_DATA_HIBERNATE = "Service.Data.Hibernate";
    String SERVICE_DATA_SQL = "Service.Data.SQL";
    String SERVICE_DATA_TRANSACTION = "Service.Data.Transaction";
    String SERVICE_HTTP = "Service.HTTP";
    String SERVICE_MESSAGE = "Service.Message";
    String SERVICE_MESSAGE_JMS = "Service.Message.JMS";
    String SERVICE_REMOTING = "Service.Remoting";

    String SECURITY = "Security";

    String SOCKET_SERVER_GENERAL = "SocketServer.General";
    String SOCKET_SERVER_BYTE_BUFFER_MANAGEMENT = "SocketServer.ByteBufferManagement";

    String SSL = "SSL";

    String STARTUP_MESSAGEBROKER = "Startup.MessageBroker";
    String STARTUP_SERVICE = "Startup.Service";
    String STARTUP_DESTINATION = "Startup.Destination";

    String TIMEOUT = "Timeout";

    String WSRP_GENERAL = "WSRP.General";
}
