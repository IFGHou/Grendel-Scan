package com.grendelscan.commons;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MimeUtils
{

    public static String getFileExtension(String mimeType)
    {
        mimeType = mimeType.toLowerCase();
        String extension = "";
        if (isHtmlMimeType("text/html"))
        {
            extension = ".html";
        }
        else if (mimeType.equals("application/xhtml+xml"))
        {
            extension = ".xhtml";
        }
        else if (mimeType.equals("application/xml") || mimeType.equals("text/xml"))
        {
            extension = ".xml";
        }
        else if (mimeType.equals("text/css"))
        {
            extension = ".css";
        }
        else if (mimeType.equals("text/plain"))
        {
            extension = ".txt";
        }
        else if (mimeType.equals("application/json"))
        {
            extension = ".json";
        }
        else if (isJavaScriptMimeType(mimeType))
        {
            extension = ".js";
        }
        else if (mimeType.equals("application/vbscript") || mimeType.equals("text/vbscript"))
        {
            extension = ".vbs";
        }
        else if (mimeType.equals("audio/wav") || mimeType.equals("audio/x-wav"))
        {
            extension = ".wav";
        }
        else if (mimeType.equals("image/jpeg") || mimeType.equals("image/pjpeg"))
        {
            extension = ".jpg";
        }
        else if (mimeType.equals("image/gif"))
        {
            extension = ".gif";
        }
        else if (mimeType.equals("image/png"))
        {
            extension = ".png";
        }
        else if (mimeType.equals("audio/mpeg") || mimeType.equals("video/mpeg") || mimeType.equals("video/x-mpeg"))
        {
            extension = ".mpg";
        }
        else if (mimeType.equals("audio/mpeg3") || mimeType.equals("audio/x-mpeg-3") || mimeType.equals("audio/mpeg3"))
        {
            extension = ".mp3";
        }
        else if (mimeType.equals("application/pdf"))
        {
            extension = ".pdf";
        }
        else if (mimeType.equals("video/quicktime"))
        {
            extension = ".mov";
        }
        else if (mimeType.equals("application/x-troff-msvideo") || mimeType.equals("video/avi") || mimeType.equals("video/msvideo") || mimeType.equals("video/x-msvideo"))
        {
            extension = ".avi";
        }
        else if (mimeType.equals("image/bmp") || mimeType.equals("image/x-windows-bmp"))
        {
            extension = ".bmp";
        }
        return extension;

    }

    /**
     * @param mimeType
     * @return {"text/html", "application/xhtml+xml"}
     */
    public static String[] getHtmlMimeTypes()
    {

        return new String[] { "text/html", "application/xhtml+xml", "application/xhtml", "text/xhtml", "application/vnd.wap.xhtml+xml" };
    }

    /**
     * 
     * @return {"application/javascript", "application/json", "application/x-javascript", "text/ecmascript", "text/javascript"}
     */
    public static String[] getJavaScriptMimeTypes()
    {
        return new String[] { "application/javascript", "application/json", "application/x-javascript", "application/ecmascript", "text/ecmascript", "text/javascript" };
    }

    public static String getMimeFromContentType(final String contentType)
    {
        String mimeType = "";
        if (contentType != null)
        {
            Pattern mimePattern = Pattern.compile("^([^;]+)");
            Matcher matcher = mimePattern.matcher(contentType);
            if (matcher.find())
            {
                mimeType = matcher.group();
            }
        }
        return mimeType;

    }

    public static boolean isAmf(final String mimeType)
    {
        return mimeType.toLowerCase().equals("application/x-amf");
    }

    /**
     * @param mimeType
     * @return
     */
    public static boolean isHtmlMimeType(final String mimeType)
    {
        String m = mimeType.toLowerCase();
        for (String html : getHtmlMimeTypes())
        {
            if (m.equals(html))
            {
                return true;
            }
        }

        return false;
    }

    public static boolean isImageMimeType(final String mimeType)
    {
        String m = mimeType.toLowerCase();
        if (m.startsWith("image/"))
        {
            return true;
        }

        return false;
    }

    public static boolean isJavaScriptMimeType(final String mimeType)
    {
        String m = mimeType.toLowerCase();
        for (String js : getJavaScriptMimeTypes())
        {
            if (m.equals(js))
            {
                return true;
            }
        }

        return false;
    }

    public static boolean isMultiPart(final String mimeType)
    {
        return mimeType.toLowerCase().equals("multipart/form-data");
    }

    public static boolean isTextMimeType(final String mimeType)
    {
        String m = mimeType.toLowerCase();
        if (m.startsWith("text/") || m.startsWith("message/") || m.startsWith("multipart/") || isWebTextMimeType(m))
        {
            return true;
        }

        return false;
    }

    public static boolean isUrlEncoded(final String mimeType)
    {
        return mimeType.toLowerCase().equals("application/x-www-form-urlencoded");
    }

    /**
     * Anything that could be remotely web-related. HTML, XHTML, XML, CSS, text, javascript, vbscript, and json
     * 
     * @param mimeType
     * @return
     */
    public static boolean isWebTextMimeType(final String mimeType)
    {
        boolean html = false;
        String m = mimeType.toLowerCase();
        if (isHtmlMimeType(m) || isJavaScriptMimeType(m) || m.contains("xml") || m.equals("application/vbscript") || m.equals("text/vbscript") || m.equals("text/css") || m.equals("text/plain") || m.isEmpty())
        {
            html = true;
        }

        return html;
    }

    public static boolean treatAsXml(final String mimeType)
    {
        String m = mimeType.toLowerCase();
        if (m.contains("xml") && !isHtmlMimeType(m))
        {
            return true;
        }
        return false;
    }

}
