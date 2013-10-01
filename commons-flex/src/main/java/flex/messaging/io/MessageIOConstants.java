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
package flex.messaging.io;

/**
 * @exclude
 */
public interface MessageIOConstants
{
    int AMF0 = 0;
    int AMF1 = 1; // There is no AMF1 but FMS uses it for some reason, hence special casing.
    int AMF3 = 3;
    Double AMF3_INFO_PROPERTY = new Double(3);

    String CONTENT_TYPE_XML = "text/xml; charset=utf-8";
    String AMF_CONTENT_TYPE = "application/x-amf";
    String XML_CONTENT_TYPE = "application/xml";

    String RESULT_METHOD = "/onResult";
    String STATUS_METHOD = "/onStatus";

    int STATUS_OK = 0;
    int STATUS_ERR = 1;
    int STATUS_NOTAMF = 2;

    String SECURITY_HEADER_NAME = "Credentials";
    String SECURITY_PRINCIPAL = "userid";
    String SECURITY_CREDENTIALS = "password";

    String URL_APPEND_HEADER = "AppendToGatewayUrl";
    String SERVICE_TYPE_HEADER = "ServiceType";

    String REMOTE_CLASS_FIELD = "_remoteClass";
    String SUPPORT_REMOTE_CLASS = "SupportRemoteClass";
    String SUPPORT_DATES_BY_REFERENCE = "SupportDatesByReference";

    String METHOD_POST = "POST";
    String HEADER_SOAP_ACTION = "SOAPAction";
}
