package com.grendelscan.testing.utils.tokens;

public class TokenContextTypeUtils
{
    public static final TokenContextType[] getAllContexts()
    {
        TokenContextType[] s = { TokenContextType.HTTP_HEADER_NAME, TokenContextType.HTTP_HEADER_VALUE, TokenContextType.HTML_TAG_ATTRIBUTE_VALUE, TokenContextType.HTML_TAG_ATTRIBUTE_NAME, TokenContextType.HTML_EVENT_HANDLER, TokenContextType.HTML_TEXT,
                        TokenContextType.HTML_SCRIPT, TokenContextType.HTML_COMMENT, TokenContextType.HTML_PRE, TokenContextType.HTML_STYLE, TokenContextType.HTML_TAG_NAME, TokenContextType.HTML_TEXTAREA, TokenContextType.HTML_TITLE,
                        TokenContextType.HTML_OTHER, TokenContextType.CSS, TokenContextType.TEXT, TokenContextType.XML, TokenContextType.JAVASCRIPT_NON_HTML, TokenContextType.OTHER_NON_HTML };
        return s;
    }

    public static final TokenContextType[] getBodyNonHtmlContexts()
    {
        TokenContextType[] s = { TokenContextType.CSS, TokenContextType.TEXT, TokenContextType.XML, TokenContextType.JAVASCRIPT_NON_HTML, TokenContextType.OTHER_NON_HTML };
        return s;
    }

    public static final TokenContextType[] getHtmlContexts()
    {
        TokenContextType[] s = { TokenContextType.HTML_TAG_ATTRIBUTE_VALUE, TokenContextType.HTML_TAG_ATTRIBUTE_NAME, TokenContextType.HTML_EVENT_HANDLER, TokenContextType.HTML_TEXT, TokenContextType.HTML_SCRIPT, TokenContextType.HTML_COMMENT,
                        TokenContextType.HTML_PRE, TokenContextType.HTML_STYLE, TokenContextType.HTML_TAG_NAME, TokenContextType.HTML_TEXTAREA, TokenContextType.HTML_TITLE, TokenContextType.HTML_OTHER };
        return s;
    }

    public static final TokenContextType[] getHttpHeaderContexts()
    {
        TokenContextType[] s = { TokenContextType.HTTP_HEADER_NAME, TokenContextType.HTTP_HEADER_VALUE };
        return s;
    }
}
