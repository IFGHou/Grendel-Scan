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
package flex.messaging.endpoints.amf;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import flex.messaging.FlexContext;
import flex.messaging.io.MessageIOConstants;
import flex.messaging.io.amf.ActionContext;
import flex.messaging.io.amf.MessageHeader;

/**
 * This filter detects whether a request URL is decorated with a ;jessionid token in the event that the client does not support cookies. In that case, an AppendToGatewayUrl header with jsessionid as
 * its value is added to the response message.
 */
public class SessionFilter extends AMFFilter
{
    public SessionFilter()
    {
    }

    @Override
    public void invoke(final ActionContext context) throws IOException
    {
        next.invoke(context);

        try
        {
            HttpServletRequest request = FlexContext.getHttpRequest();
            HttpServletResponse response = FlexContext.getHttpResponse();

            StringBuffer reqURL = request.getRequestURL();

            if (reqURL != null)
            {
                if (request.getQueryString() != null)
                    reqURL.append("?").append(request.getQueryString());

                String oldFullURL = reqURL.toString().trim();
                String encFullURL = response.encodeURL(oldFullURL).trim();

                String sessionSuffix = null;

                // It's ok to lower case here as URLs must be in ASCII
                int pos = encFullURL.toLowerCase().indexOf(";jsessionid");
                if (pos > 0)
                {
                    StringBuffer sb = new StringBuffer();
                    sb.append(encFullURL.substring(pos));
                    sessionSuffix = sb.toString();
                }

                if (sessionSuffix != null && oldFullURL.indexOf(sessionSuffix) < 0)
                {
                    context.getResponseMessage().addHeader(new MessageHeader(MessageIOConstants.URL_APPEND_HEADER, true /* mustUnderstand */, sessionSuffix));
                }
            }
        }
        catch (Throwable t)
        {
            // Nothing more we can do... don't send 'URL Append' AMF header.
        }
    }
}
