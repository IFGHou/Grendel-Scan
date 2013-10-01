/*
 * GNU LESSER GENERAL PUBLIC LICENSE Copyright (C) 2006 The Lobo Project
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * Contact info: xamjadmin@users.sourceforge.net
 */
/*
 * Created on Apr 17, 2005
 */
package org.cobra_grendel.util.gui;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author J. H. S.
 */
public class ColorFactory
{
    public static final Color TRANSPARENT = new Color(0, 0, 0, 0);
    private static ColorFactory instance;
    private static final Logger logger = Logger.getLogger(ColorFactory.class.getName());
    private static final String RGB_START = "rgb(";

    public static final ColorFactory getInstance()
    {
        if (instance == null)
        {
            synchronized (ColorFactory.class)
            {
                if (instance == null)
                {
                    instance = new ColorFactory();
                }
            }
        }
        return instance;
    }

    private final Map colorMap = new HashMap();

    private ColorFactory()
    {
        Map colorMap = this.colorMap;
        synchronized (this)
        {
            // Standard
            colorMap.put("transparent", new Color(0, 0, 0, 0));
            colorMap.put("black", Color.black);
            colorMap.put("blue", Color.blue);
            colorMap.put("cyan", Color.cyan);
            colorMap.put("darkGray", Color.darkGray);
            colorMap.put("gray", Color.gray);
            colorMap.put("green", new Color(0, 100, 0));
            colorMap.put("magenta", Color.magenta);
            colorMap.put("orange", Color.orange);
            colorMap.put("pink", Color.pink);
            colorMap.put("red", Color.red);
            colorMap.put("white", Color.white);
            colorMap.put("yellow", Color.yellow);
            // Other colors
            colorMap.put("grey", new Color(50, 100, 0));
            colorMap.put("brown", new Color(100, 50, 0));
        }
    }

    public Color getColor(final String colorSpec)
    {
        String normalSpec = colorSpec.toLowerCase();
        synchronized (this)
        {
            Color color = (Color) colorMap.get(normalSpec);
            if (color == null)
            {
                if (normalSpec.startsWith(RGB_START))
                {
                    int endIdx = normalSpec.lastIndexOf(')');
                    String commaValues = endIdx == -1 ? normalSpec.substring(RGB_START.length()) : normalSpec.substring(RGB_START.length(), endIdx);
                    StringTokenizer tok = new StringTokenizer(commaValues, ",");
                    int r = 0, g = 0, b = 0;
                    if (tok.hasMoreTokens())
                    {
                        String rstr = tok.nextToken().trim();
                        try
                        {
                            r = Integer.parseInt(rstr);
                        }
                        catch (NumberFormatException nfe)
                        {
                            // ignore
                        }
                        if (tok.hasMoreTokens())
                        {
                            String gstr = tok.nextToken().trim();
                            try
                            {
                                g = Integer.parseInt(gstr);
                            }
                            catch (NumberFormatException nfe)
                            {
                                // ignore
                            }
                            if (tok.hasMoreTokens())
                            {
                                String bstr = tok.nextToken().trim();
                                try
                                {
                                    b = Integer.parseInt(bstr);
                                }
                                catch (NumberFormatException nfe)
                                {
                                    // ignore
                                }
                            }
                        }
                    }
                    color = new Color(r, g, b);
                }
                else if (normalSpec.startsWith("#"))
                {
                    int len = normalSpec.length();
                    int[] rgba = new int[4];
                    rgba[3] = 255;
                    for (int i = 0; i < rgba.length; i++)
                    {
                        int idx = 2 * i + 1;
                        if (idx < len)
                        {
                            String hexText = normalSpec.substring(idx, idx + Math.min(2, len - idx));
                            try
                            {
                                rgba[i] = Integer.parseInt(hexText, 16);
                            }
                            catch (NumberFormatException nfe)
                            {
                                // Ignore
                            }
                        }
                    }
                    color = new Color(rgba[0], rgba[1], rgba[2], rgba[3]);
                }
                else
                {
                    if (logger.isLoggable(Level.INFO))
                    {
                        logger.warning("getColor(): Color spec [" + normalSpec + "] unknown.");
                    }
                    return Color.RED;
                }
                colorMap.put(normalSpec, color);
            }
            return color;
        }
    }

    public boolean isColor(final String colorSpec)
    {
        if (colorSpec.startsWith("#"))
        {
            return true;
        }
        String normalSpec = colorSpec.toLowerCase();
        if (normalSpec.startsWith(RGB_START))
        {
            return true;
        }
        synchronized (this)
        {
            return colorMap.containsKey(normalSpec);
        }
    }

}
