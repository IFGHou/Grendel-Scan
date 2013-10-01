/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2002 - 2007 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/
package flex.messaging.services;


/* TODO UCdetector: Remove unused code: 
public class AuthenticationService extends AbstractService
{
    private static final int INVALID_CREDENTIALS_ERROR = 10064;
    
    private final String id = "authentication-service";

    public AuthenticationService()
    {
        this(false);
    }
    
    public AuthenticationService(boolean enableManagement)
    {
        // this service can never be managed
        super(false);
        super.setId(id);
    }
     
    // This service's id should never be changed
    public void setId(String id)
    {
        // No-op
    }

    // This service should not be visible to the client
    public ConfigMap describeService(Endpoint endpoint)
    { 
        return null;
    }
    
    public Object serviceMessage(Message message)
    {
        return null;
    }

    public Object serviceCommand(CommandMessage msg)
    {
        LoginManager lm = getMessageBroker().getLoginManager();
        switch (msg.getOperation())
        {
            case CommandMessage.LOGIN_OPERATION:
                if (msg.getBody() instanceof String)
                {
                    String encoded = (String)msg.getBody();
                    Object charsetHeader = msg.getHeader(CommandMessage.CREDENTIALS_CHARSET_HEADER); 
                    if (charsetHeader instanceof String)
                        decodeAndLoginWithCharset(encoded, lm, (String)charsetHeader);
                    else
                        decodeAndLoginWithCharset(encoded, lm, null);
                }
                break;
            case CommandMessage.LOGOUT_OPERATION:
                lm.logout();
                break;
            default:
                throw new MessageException("Service Does Not Support Command Type " + msg.getOperation());
        }
        return "success";
    }

    public static void decodeAndLogin(String encoded, LoginManager lm)
    {
        decodeAndLoginWithCharset(encoded, lm, null);
    }

    private static void decodeAndLoginWithCharset(String encoded, LoginManager lm, String charset)
    {
        String username = null;
        String password = null;
        Base64.Decoder decoder = new Base64.Decoder();
        decoder.decode(encoded);
        String decoded = "";

        // Charset-aware decoding of the credentials bytes 
        if (charset != null)
        {
            try
            {
                decoded = new String(decoder.drain(), charset);
            }
            catch (UnsupportedEncodingException ex)
            {
            }
        }
        else
        {
            decoded = new String(decoder.drain());
        }

        int colon = decoded.indexOf(":");
        if (colon > 0 && colon < decoded.length() - 1)
        {
            username = decoded.substring(0, colon);
            password = decoded.substring(colon + 1);
        }

        if (username != null && password != null)
        {
            lm.login(username, password);
        }
        else
        {
            SecurityException se = new SecurityException();
            se.setCode(SecurityException.CLIENT_AUTHENTICATION_CODE);
            se.setMessage(INVALID_CREDENTIALS_ERROR);
            throw se;
        }
    }
    
    protected void setupServiceControl(MessageBroker broker)
    {
        // not doing anything
    }

}
*/
