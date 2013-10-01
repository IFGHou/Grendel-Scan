/**
 * 
 */
package com.grendelscan.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.grendelscan.commons.FileUtils;
import com.grendelscan.data.database.collections.DatabaseBackedSet;

/**
 * @author david
 * 
 */
public class WordList
{
    private final DatabaseBackedSet<String> words;
    private String[] sortedList;

    public WordList(final String name)
    {
        words = new DatabaseBackedSet<String>(name + "_wordlist", 100);
    }

    public WordList(final String name, final String file)
    {
        this(name);
        if (words.size() == 0) // in case something was already restored
        {
            loadFromFile(file);
        }
    }

    public synchronized void addWord(final String word)
    {
        words.add(word.toLowerCase());
        sortedList = null;
    }

    public synchronized final String[] getReadOnlyWordsSortedBySize()
    {
        if (sortedList == null)
        {
            ArrayList<String> w = new ArrayList<String>(words.size());
            w.addAll(words);
            Comparator<String> c = new Comparator<String>()
            {
                @Override
                public int compare(final String o1, final String o2)
                {
                    return o2.length() - o1.length();
                }
            };
            Collections.sort(w, c);
            sortedList = w.toArray(new String[0]);
        }
        return sortedList;
    }

    public void loadFromFile(final String file)
    {
        sortedList = null;
        String contents = FileUtils.readFile(file);
        Pattern p = Pattern.compile("\\b(\\w+)\\b");
        Matcher m = p.matcher(contents);
        ArrayList<String> w = new ArrayList<String>(1);
        while (m.find())
        {
            w.add(m.group(1).toLowerCase());
        }
        words.addAll(w);
    }

}
