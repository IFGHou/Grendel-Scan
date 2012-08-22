/**
 * 
 */
package com.grendelscan.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.grendelscan.data.database.collections.DatabaseBackedSet;

/**
 * @author david
 *
 */
public class WordList
{
	private final DatabaseBackedSet<String> words;
	
	public WordList(String name)
	{
		words = new DatabaseBackedSet<String>(name + "_wordlist", 100);
	}

	public WordList(String name, String file)
	{
		this(name);
		if (words.size() == 0) // in case something was already restored
		{
			loadFromFile(file);
		}
	}

	public void loadFromFile(String file)
	{
		String contents = FileUtils.readFile(file);
		Pattern p = Pattern.compile("\\b(\\w+)\\b");
		Matcher m = p.matcher(contents);
		ArrayList<String> w = new ArrayList<String>(1);
		while(m.find())
		{
			w.add(m.group(1).toLowerCase());
		}
		words.addAll(w);
	}
	
	public void addWord(String word)
	{
		words.add(word.toLowerCase());
	}

	public final String[] getReadOnlyWordsSortedBySize()
	{
		List<String> w = new ArrayList<String>(words.size());
		w.addAll(words);
		Comparator<String> c = new Comparator<String>()
		{
			@Override
			public int compare(String o1, String o2)
			{
				return o2.length() - o1.length();
			}
		};
		Collections.sort(w, c);
		return w.toArray(new String[0]);
	}
	
}
