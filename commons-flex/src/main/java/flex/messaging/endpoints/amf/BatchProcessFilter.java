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

import flex.messaging.io.MessageIOConstants;
import flex.messaging.io.RecoverableSerializationException;
import flex.messaging.io.amf.ActionContext;
import flex.messaging.io.amf.MessageBody;

/**
 * Filter that breaks down the batched message buffer into individual invocations.
 * 
 * @author PS Neville
 */
public class BatchProcessFilter extends AMFFilter
{
    public BatchProcessFilter()
    {
    }

    @Override
    public void invoke(final ActionContext context)
    {
        // Process each action in the body
        int bodyCount = context.getRequestMessage().getBodyCount();

        // Report batch size in Debug mode
        // gateway.getLogger().logDebug("Processing batch of " + bodyCount + " request(s)");

        for (context.setMessageNumber(0); context.getMessageNumber() < bodyCount; context.incrementMessageNumber())
        {
            try
            {
                // create the response body
                MessageBody responseBody = new MessageBody();
                responseBody.setTargetURI(context.getRequestMessageBody().getResponseURI());

                // append the response body to the output message
                context.getResponseMessage().addBody(responseBody);

                // Check that deserialized message body data type was valid. If not, skip this message.
                Object o = context.getRequestMessageBody().getData();

                if (o != null && o instanceof RecoverableSerializationException)
                {
                    context.getResponseMessageBody().setData(((RecoverableSerializationException) o).createErrorMessage());
                    context.getResponseMessageBody().setReplyMethod(MessageIOConstants.STATUS_METHOD);
                    continue;
                }

                // invoke next filter in the chain
                next.invoke(context);
            }
            catch (Exception e)
            {
                // continue invoking on next message body despite error
            }
        }
    }
}
