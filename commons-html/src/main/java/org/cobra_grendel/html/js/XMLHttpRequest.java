package org.cobra_grendel.html.js;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.cobra_grendel.html.HttpRequest;
import org.cobra_grendel.html.ReadyStateChangeListener;
import org.cobra_grendel.html.UserAgentContext;
import org.cobra_grendel.js.AbstractScriptableDelegate;
import org.cobra_grendel.js.JavaScript;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.Document;

public class XMLHttpRequest extends AbstractScriptableDelegate
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(XMLHttpRequest.class.getName());
    private final java.net.URL codeSource;
    private boolean listenerAdded;
    private Function onreadystatechange;
    private final UserAgentContext pcontext;

    private final HttpRequest request;

    private final Scriptable scope;

    public XMLHttpRequest(final UserAgentContext pcontext, final java.net.URL codeSource, final Scriptable scope, final int referingTransactionId)
    {
        super(referingTransactionId);
        request = pcontext.createHttpRequest(referingTransactionId);
        this.pcontext = pcontext;
        this.scope = scope;
        this.codeSource = codeSource;
    }

    public void abort()
    {
        request.abort();
    }

    private void executeReadyStateChange()
    {
        // Expected to be called in GUI thread.
        try
        {
            Function f = XMLHttpRequest.this.getOnreadystatechange();
            if (f != null)
            {
                Context ctx = Executor.createContext(codeSource, pcontext);
                try
                {
                    Scriptable newScope = (Scriptable) JavaScript.getInstance().getJavascriptObject(XMLHttpRequest.this, scope);
                    f.call(ctx, newScope, newScope, new Object[0]);
                }
                finally
                {
                    Context.exit();
                }
            }
        }
        catch (Exception err)
        {
            logger.log(Level.WARNING, "Error processing ready state change.", err);
        }
    }

    public String getAllResponseHeaders()
    {
        return request.getAllResponseHeaders();
    }

    public Function getOnreadystatechange()
    {
        synchronized (this)
        {
            return onreadystatechange;
        }
    }

    public int getReadyState()
    {
        return request.getReadyState();
    }

    public byte[] getResponseBytes()
    {
        return request.getResponseBytes();
    }

    public String getResponseHeader(final String headerName)
    {
        return request.getResponseHeader(headerName);
    }

    public String getResponseText()
    {
        return request.getResponseText();
    }

    public Document getResponseXML()
    {
        return request.getResponseXML();
    }

    public int getStatus()
    {
        return request.getStatus();
    }

    public String getStatusText()
    {
        return request.getStatusText();
    }

    public void open(final String method, final String url)
    {
        request.open(method, url);
    }

    public void open(final String method, final String url, final boolean asyncFlag)
    {
        request.open(method, url, asyncFlag);
    }

    public void open(final String method, final String url, final boolean asyncFlag, final String userName)
    {
        request.open(method, url, asyncFlag, userName);
    }

    public void open(final String method, final String url, final boolean asyncFlag, final String userName, final String password)
    {
        request.open(method, url, asyncFlag, userName, password);
    }

    public void setOnreadystatechange(final Function value)
    {
        synchronized (this)
        {
            onreadystatechange = value;
            if (value != null && !listenerAdded)
            {
                request.addReadyStateChangeListener(new ReadyStateChangeListener()
                {
                    @Override
                    public void readyStateChanged()
                    {
                        java.awt.EventQueue.invokeLater(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                executeReadyStateChange();
                            }
                        });
                    }
                });
                listenerAdded = true;
            }
        }
    }

}
