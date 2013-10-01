/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  [2002] - [2007] Adobe Systems Incorporated
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
package flex.messaging;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;
import flex.messaging.log.Log;
import flex.messaging.log.LogCategories;

/**
 * FlexSession implementation for use with HTTP-based channels.
 *
 * @author shodgson
 */
public class HttpFlexSession extends FlexSession
    implements HttpSessionBindingListener, HttpSessionListener, HttpSessionAttributeListener, Serializable
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------

    /**
     * @exclude
     * Not for public use; create new instances using getFlexSession(...).
     * This no-args constructor allows HttpFlexSession to be registered as a listener in web.xml so
     * we can detect changes to J2EE HttpSession attributes and proxy attribute notifications to FlexSession
     * attribute and binding listeners.
     */
    public HttpFlexSession() {}

    //--------------------------------------------------------------------------
    //
    // Constants
    //
    //--------------------------------------------------------------------------

    /**
     * Serializable version uid.
     */
    private static final long serialVersionUID = -1260409488935306147L;

    /**
     * Attribute name that HttpFlexSession is stored under in the HttpSession.
     */
    private static final String SESSION_ATTRIBUTE = "__flexSession";

    /**
     * This attribute is set on the request associated with a Flex Session when
     * a logout command is being processed.  The reason that this is necessary is
     * that a single HttpServletRequest may contain more than one Flex command/message.
     * In this case, every message following a "logout" command should behave as if the
     * user has logged out.  However, since in getUserPrincipal, we check the request
     * object if the Session has no principal, if the current request object
     * has a principal object associated with it (as it does on Tomcat/JBoss),
     * messages following a "logout" will use this principal.  We thus need to
     * invalidate the user principal in the request on logout.  Since the field
     * is read-only we do so using this attribute.
     */
    private static final String INVALIDATED_REQUEST = "__flexInvalidatedRequest";

    public static final String SESSION_MAP = "LCDS_HTTP_TO_FLEX_SESSION_MAP";

    /**
     * Internal flag indicating whether we are a registered listener in web.xml.
     */
    private static volatile boolean isHttpSessionListener;

    /**
     * Flag to indicate whether we've logged a warning if we weren't registered in web.xml and
     * can't redispatch attribute and binding events to Flex listeners.
     */
    private static volatile boolean warnedNoEventRedispatch;

    /**
     * The log category to send the warning for no event redispatch to.
     */
    private static String WARN_LOG_CATEGORY = LogCategories.CONFIGURATION;

    //--------------------------------------------------------------------------
    //
    // Variables
    //
    //--------------------------------------------------------------------------

    /**
     * Reference to the HttpSession allows us to invalidate it and use it for attribute management.
     */
    private HttpSession httpSession;

    /**
     * @exclude
     * Static lock for creating httpSessionToFlexSession map
     */
    public static final Object mapLock = new Object();


    //--------------------------------------------------------------------------
    //
    // Public Methods
    //
    //--------------------------------------------------------------------------

    /**
     * HttpSessionAttributeListener callback; processes the addition of an attribute to an HttpSession.
     *
     * NOTE: Callback is not made against an HttpFlexSession associated with a request
     * handling thread.
     */
    @Override public void attributeAdded(HttpSessionBindingEvent event)
    {
        if (!event.getName().equals(SESSION_ATTRIBUTE))
        {
            // Accessing flexSession via map because it may have already been unbound from httpSession.
            Map httpSessionToFlexSessionMap = getHttpSessionToFlexSessionMap(event.getSession());
            HttpFlexSession flexSession = (HttpFlexSession)httpSessionToFlexSessionMap.get(event.getSession().getId());
            if (flexSession != null)
            {
                String name = event.getName();
                Object value = event.getValue();
                flexSession.notifyAttributeBound(name, value);
                flexSession.notifyAttributeAdded(name, value);
            }
        }
    }

    /**
     * HttpSessionAttributeListener callback; processes the removal of an attribute from an HttpSession.
     *
     * NOTE: Callback is not made against an HttpFlexSession associated with a request
     * handling thread.
     */
    @Override public void attributeRemoved(HttpSessionBindingEvent event)
    {
        if (!event.getName().equals(SESSION_ATTRIBUTE))
        {
            // Accessing flexSession via map because it may have already been unbound from httpSession.
            Map httpSessionToFlexSessionMap = getHttpSessionToFlexSessionMap(event.getSession());
            HttpFlexSession flexSession = (HttpFlexSession)httpSessionToFlexSessionMap.get(event.getSession().getId());
            if (flexSession != null)
            {
                String name = event.getName();
                Object value = event.getValue();
                flexSession.notifyAttributeUnbound(name, value);
                flexSession.notifyAttributeRemoved(name, value);
            }
        }
    }

    /**
     * HttpSessionAttributeListener callback; processes the replacement of an attribute in an HttpSession.
     *
     * NOTE: Callback is not made against an HttpFlexSession associated with a request
     * handling thread.
     */
    @Override public void attributeReplaced(HttpSessionBindingEvent event)
    {
        if (!event.getName().equals(SESSION_ATTRIBUTE))
        {
            // Accessing flexSession via map because it may have already been unbound from httpSession.
            Map httpSessionToFlexSessionMap = getHttpSessionToFlexSessionMap(event.getSession());
            HttpFlexSession flexSession = (HttpFlexSession)httpSessionToFlexSessionMap.get(event.getSession().getId());
            if (flexSession != null)
            {
                String name = event.getName();
                Object value = event.getValue();
                Object newValue = flexSession.getAttribute(name);
                flexSession.notifyAttributeUnbound(name, value);
                flexSession.notifyAttributeReplaced(name, value);
                flexSession.notifyAttributeBound(name, newValue);
            }
        }
    }

    /**
     * Creates or retrieves a FlexSession for the current Http request.
     * The HttpFlexSession wraps the underlying J2EE HttpSession.
     *
     * @param req The Http request.
     *
     * @return The HttpFlexSession.
     */
    public static HttpFlexSession getFlexSession(HttpServletRequest req)
    {
        HttpFlexSession flexSession;
        HttpSession httpSession = req.getSession(true);

        if (!isHttpSessionListener && !warnedNoEventRedispatch)
        {
            warnedNoEventRedispatch = true;
            if (Log.isWarn())
                Log.getLogger(WARN_LOG_CATEGORY).warn("HttpFlexSession has not been registered as a listener in web.xml for this application so no events will be dispatched to FlexSessionAttributeListeners or FlexSessionBindingListeners. To correct this, register flex.messaging.HttpFlexSession as a listener in web.xml.");
        }

        boolean isNew = false;
        synchronized (httpSession)
        {
            flexSession = (HttpFlexSession)httpSession.getAttribute(HttpFlexSession.SESSION_ATTRIBUTE);
            if (flexSession == null)
            {
                flexSession = new HttpFlexSession();
                // Correlate this FlexSession to the HttpSession before triggering any listeners.
                FlexContext.setThreadLocalSession(flexSession);
                httpSession.setAttribute(SESSION_ATTRIBUTE, flexSession);
                flexSession.setHttpSession(httpSession);
                isNew = true;
            }
            else
            {
                FlexContext.setThreadLocalSession(flexSession);
                if (flexSession.httpSession == null)
                {
                    // httpSession is null if the instance is new or is from
                    // serialization.
                    flexSession.setHttpSession(httpSession);
                    isNew = true;
                }
            }
        }

        if (isNew)
        {
            flexSession.notifyCreated();

            if (Log.isDebug())
                Log.getLogger(FLEX_SESSION_LOG_CATEGORY).debug("FlexSession created with id '" + flexSession.getId() + "' for an Http-based client connection.");
        }

        return flexSession;
    }

    /**
     * Returns the user principal associated with the session. This will
     * be null if the user has not authenticated.
     *
     * @return The Principal associated with the session.
     */
    @Override public Principal getUserPrincipal()
    {
        Principal p = super.getUserPrincipal();
        if (p == null)
        {
            HttpServletRequest req = FlexContext.getHttpRequest();
            if (req != null && req.getAttribute(INVALIDATED_REQUEST) == null)
                p = req.getUserPrincipal();
        }
        return p;
    }

    /**
     * Invalidates the session.
     */
    @Override public void invalidate()
    {
        // If the HttpFlexSession is the current active FlexSession for the thread
        // we'll invalidate it but we need to recreate a new HttpFlexSession because
        // the client's HttpSession is still active.
        boolean recreate = FlexContext.getFlexSession() == this;
        invalidate(recreate);
    }

    /**
     * @exclude
     * Used by Http endpoints when they receive notification from a client that it has
     * disconnected its channel.
     * Supports invalidating the HttpFlexSession and underlying JEE HttpSession without
     * triggering session recreation.
     */
    public void invalidate(boolean recreate)
    {
        synchronized (httpSession)
        {
            try
            {
                // Invalidating the HttpSession will trigger invalidation of the HttpFlexSession
                // either via the sessionDestroyed() event if registration as an HttpSession listener worked
                // or via the valueUnbound() event if it didn't.
                httpSession.invalidate();
            }
            catch (IllegalStateException e)
            {
                // Make sure any related mapping is removed.
                try
                {
                    Map httpSessionToFlexSessionMap = getHttpSessionToFlexSessionMap(httpSession);
                    httpSessionToFlexSessionMap.remove(httpSession.getId());
                }
                catch (Exception ignore)
                {
                    // NOWARN
                }
                
                // And invalidate this FlexSession.
                super.invalidate();
            }
        }
        if (recreate)
        {
            HttpServletRequest req = FlexContext.getHttpRequest();

            if (req != null)
            {
                // Set an attribute on the request denoting that the userPrincipal in the request
                // is now invalid.
                req.setAttribute(INVALIDATED_REQUEST, "true");

                FlexContext.setThreadLocalObjects
                    (FlexContext.getFlexClient(), getFlexSession(req), FlexContext.getMessageBroker(),
                     req, FlexContext.getHttpResponse(), FlexContext.getServletConfig());
            }
            // else, the session was invalidated outside of a request being processed.
        }
    }

    /**
     * Returns the attribute bound to the specified name in the session, or null
     * if no attribute is bound under the name.
     *
     * @param name The name the target attribute is bound to.
     * @return The attribute bound to the specified name.
     */
    @Override public Object getAttribute(String name)
    {
        return httpSession.getAttribute(name);
    }

    /**
     * Returns the names of all attributes bound to the session.
     *
     * @return The names of all attributes bound to the session.
     */
    @Override public Enumeration getAttributeNames()
    {
        return httpSession.getAttributeNames();
    }

    /**
     * Returns the Id for the session.
     *
     * @return The Id for the session.
     */
    @Override public String getId()
    {
        return httpSession.getId();
    }

    /**
     * @exclude
     * FlexClient invokes this to determine whether the session can be used to push messages
     * to the client.
     *
     * @return true if the FlexSession supports direct push; otherwise false (polling is assumed).
     */
    @Override public boolean isPushSupported()
    {
        return false;
    }

    /**
     * Removes the attribute bound to the specified name in the session.
     *
     * @param name The name of the attribute to remove.
     */
    @Override public void removeAttribute(String name)
    {
        httpSession.removeAttribute(name);
    }

    /**
     * Implements HttpSessionListener.
     * HttpSession created events are handled by setting an internal flag indicating that registration
     * as an HttpSession listener was successful and we will be notified of session attribute changes and
     * session destruction.
     * NOTE: This method is not invoked against an HttpFlexSession associated with a request
     * handling thread.
     */
    @Override public void sessionCreated(HttpSessionEvent event)
    {
        isHttpSessionListener = true;
    }

    /**
     * Implements HttpSessionListener.
     * When an HttpSession is destroyed, the associated HttpFlexSession is also destroyed.
     * NOTE: This method is not invoked against an HttpFlexSession associated with a request
     * handling thread.
     */
    @Override public void sessionDestroyed(HttpSessionEvent event)
    {
        HttpSession session = event.getSession();
        Map httpSessionToFlexSessionMap = getHttpSessionToFlexSessionMap(session);
        HttpFlexSession flexSession = (HttpFlexSession)httpSessionToFlexSessionMap.remove(session.getId());
        if (flexSession != null)
        {
            // invalidate the flex session
            flexSession.superInvalidate();

            // Send notifications to attribute listeners if needed
            // This may send extra notifications if attributeRemoved is called first by the server,
            // but Java servlet 2.4 says session destroy is first, then attributes.
            for (Enumeration e = session.getAttributeNames(); e.hasMoreElements(); )
            {
                String name = (String) e.nextElement();
                if (name.equals(SESSION_ATTRIBUTE))
                    continue;
                Object value = session.getAttribute(name);
                if (value != null)
                {
                    flexSession.notifyAttributeUnbound(name, value);
                    flexSession.notifyAttributeRemoved(name, value);
                }
            }
        }

    }

    /**
     * Binds an attribute to the session under the specified name.
     *
     * @param name The name to bind the attribute under.
     *
     * @param value The attribute value.
     */
    @Override public void setAttribute(String name, Object value)
    {
        httpSession.setAttribute(name, value);
    }

    /**
     * Implements HttpSessionBindingListener.
     * This is a no-op.
     * NOTE: This method is not invoked against an HttpFlexSession associated with a request
     * handling thread.
     */
    @Override public void valueBound(HttpSessionBindingEvent event)
    {
        // No-op.
    }

    /**
     * Implements HttpSessionBindingListener.
     * This callback will destroy the HttpFlexSession upon being unbound, only in the
     * case where we haven't been registered as an HttpSessionListener in web.xml and
     * can't shut down based on the HttpSession being invalidated.
     * NOTE: This method is not invoked against an HttpFlexSession associated with a request
     * handling thread.
     */
    @Override public void valueUnbound(HttpSessionBindingEvent event)
    {
        if (!isHttpSessionListener)
        {
            Map httpSessionToFlexSessionMap = getHttpSessionToFlexSessionMap(event.getSession());
            HttpFlexSession flexSession = (HttpFlexSession)httpSessionToFlexSessionMap.remove(event.getSession().getId());
            if (flexSession != null)
                flexSession.superInvalidate();
        }
    }

    //--------------------------------------------------------------------------
    //
    // Protected Methods
    //
    //--------------------------------------------------------------------------

    /**
     * We don't need to do anything here other than log out some info about the session that's shutting down.
     */
    @Override protected void internalInvalidate()
    {
        if (Log.isDebug())
            Log.getLogger(FLEX_SESSION_LOG_CATEGORY).debug("FlexSession with id '" + getId() + "' for an Http-based client connection has been invalidated.");
    }

    //--------------------------------------------------------------------------
    //
    // Private Methods
    //
    //--------------------------------------------------------------------------

    /**
     * Associates a HttpSession with the FlexSession.
     *
     * @param httpSession The HttpSession to associate with the FlexSession.
     */
    private void setHttpSession(HttpSession httpSession)
    {
        synchronized (lock)
        {
            this.httpSession = httpSession;
            // Update lookup table for event redispatch.
            Map httpSessionToFlexSessionMap = getHttpSessionToFlexSessionMap(httpSession);
            httpSessionToFlexSessionMap.put(httpSession.getId(), this);
        }
    }

    /**
     * @exclude
     * Invoked by HttpSessionListener or binding listener on HttpSession invalidation to invalidate the wrapping
     * FlexSession.
     */
    private void superInvalidate()
    {
        super.invalidate();
    }

    /**
     * Implements Serializable; only the Principal needs to be serialized as all
     * attribute storage is delegated to the associated HttpSession.
     *
     * @param stream The stream to read instance state from.
     */
    private void writeObject(ObjectOutputStream stream)
    {
        try
        {
            Principal principal = super.getUserPrincipal();
            if (principal != null && principal instanceof Serializable)
                stream.writeObject(principal);
        }
        catch (IOException e)
        {
            // Principal was Serializable and non-null; if this happens there's nothing we can do.
            // The user will need to reauthenticate if necessary.
        }
        catch (LocalizedException ignore)
        {
            // This catch block added for bug 194144.
            // On BEA WebLogic, writeObject() is sometimes invoked on invalidated session instances
            // and in this case the checkValid() invocation in super.getUserPrincipal() throws.
            // Ignore this exception.
        }
    }

    /**
     * Implements Serializable; only the Principal needs to be serialized as all
     * attribute storage is delegated to the associated HttpSession.
     *
     * @param stream The stream to write instance state to.
     */
    private void readObject(ObjectInputStream stream)
    {
        try
        {
            setUserPrincipal((Principal)stream.readObject());
        }
        catch (Exception e)
        {
            // Principal was not serialized or failed to serialize; ignore.
            // The user will need to reauthenticate if necessary.
        }
    }

    /**
     * Map of HttpSession Ids to FlexSessions. We need this when registered as a listener
     * in web.xml in order to trigger the destruction of a FlexSession when its associated HttpSession
     * is invalidated/destroyed. The Servlet spec prior to version 2.4 defined the session destruction event
     * to be dispatched after attributes are unbound from the session so when we receive notification that
     * an HttpSession is destroyed there's no way to get to the associated FlexSession attribute because it
     * has already been unbound... Additionally, we need it to handle attribute removal events that happen
     * during HttpSession destruction because the FlexSession can be unbound from the session before the
     * other attributes we receive notification for.
     *
     * Because of this, it's simplest to just maintain this lookup table and use it for all HttpSession
     * related event handling.
     *
     * The table is maintained on the servlet context instead of statically in order to prevent collisions
     * across web-apps.
     */
    private Map getHttpSessionToFlexSessionMap(HttpSession session)
    {
        try
        {
            ServletContext context = session.getServletContext();
            Map map = (Map)context.getAttribute(SESSION_MAP);

            if(map==null){
                // map should never be null here as it is created during MessageBrokerServlet start-up
                if (Log.isError())
                    Log.getLogger(FLEX_SESSION_LOG_CATEGORY).error("HttpSession to FlexSession map not created in message broker for "
                            + session.getId());
                MessageException me = new MessageException();
                me.setMessage(10032, new Object[] {session.getId()});
                throw me;
            }
            return map;
        }
        catch(Exception e)
        {
            if (Log.isDebug())
                Log.getLogger(FLEX_SESSION_LOG_CATEGORY).debug("Unable to get HttpSession to FlexSession map for "
                        + session.getId() + " " + e.toString());
            return new ConcurrentHashMap();

        }

    }

}