/**
 * 
 */
package com.grendelscan.testing.utils.tokens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author david
 * 
 */
public class DiscoveredContexts
{
    private final Map<TokenContextType, Map<String, List<TokenContext>>> contexts;
    private int count;

    public DiscoveredContexts()
    {
        contexts = new HashMap<TokenContextType, Map<String, List<TokenContext>>>();
    }

    public void addContext(final TokenContext context)
    {
        count++;
        Map<String, List<TokenContext>> typeSpecificContexts = contexts.get(context.getContextType());
        if (typeSpecificContexts == null)
        {
            typeSpecificContexts = new HashMap<String, List<TokenContext>>();
            contexts.put(context.getContextType(), typeSpecificContexts);
        }

        List<TokenContext> tokenSpecificContexts = typeSpecificContexts.get(context.getToken());
        if (tokenSpecificContexts == null)
        {
            tokenSpecificContexts = new ArrayList<TokenContext>();
            typeSpecificContexts.put(context.getToken(), tokenSpecificContexts);
        }
        if (!tokenSpecificContexts.contains(context))
        {
            tokenSpecificContexts.add(context);
        }
    }

    public List<TokenContext> getAllContexts()
    {
        List<TokenContext> all = new ArrayList<TokenContext>();

        for (Map<String, List<TokenContext>> byType : contexts.values())
        {
            for (List<TokenContext> byToken : byType.values())
            {
                for (TokenContext context : byToken)
                {
                    all.add(context);
                }
            }
        }

        return all;
    }

    public DiscoveredContexts getAllOfToken(final String token)
    {
        DiscoveredContexts newContexts = new DiscoveredContexts();

        for (Map<String, List<TokenContext>> byType : contexts.values())
        {
            for (List<TokenContext> byToken : byType.values())
            {
                for (TokenContext context : byToken)
                {
                    if (context.getToken().equals(token))
                    {
                        newContexts.addContext(context);
                    }
                }
            }
        }

        return newContexts;
    }

    public DiscoveredContexts getAllOfType(final TokenContextType type)
    {
        DiscoveredContexts newContexts = new DiscoveredContexts();
        if (contexts.containsKey(type))
        {
            for (List<TokenContext> byToken : contexts.get(type).values())
            {
                for (TokenContext context : byToken)
                {
                    newContexts.addContext(context);
                }
            }
        }

        return newContexts;
    }

    public List<String> getAllTokens()
    {
        List<String> tokens = new ArrayList<String>();
        for (TokenContext context : getAllContexts())
        {
            if (!tokens.contains(context.getToken()))
            {
                tokens.add(context.getToken());
            }
        }
        return tokens;
    }

    public Collection<TokenContextType> getAllTypes()
    {
        return contexts.keySet();
    }

    //
    // public Map<String, List<TokenContext>> getByType(TokenContextType type)
    // {
    // return contexts.get(type);
    // }

    public final int getCount()
    {
        return count;
    }

    // public boolean contains(String token, TokenContextType type)
    // {
    // Map<String, List<TokenContext>> typeSpecificContexts = contexts.get(type);
    // if (typeSpecificContexts != null)
    // {
    // return typeSpecificContexts.containsKey(token);
    // }
    // return false;
    // }
}
