package com.grendelscan.requester;



// TODO UCdetector: Remove unused code: 
// public class UriQuery
// {
// 	/*
// 	 * This is primarily for handling a URI query string,
// 	 * although a URL-encoded POST parameter string works
// 	 * equally well. Only one parameter of each name is
// 	 * allowed. Parameter order is never guaranteed. If
// 	 * order is important, or if duplicate names are
// 	 * required, don't use this class.
// 	 */
// 	boolean duplicateNamesAllowed = false;
// 	HashMap<String, String> parameters;
// 
// 	public UriQuery(String query)
// 	{
// 		parameters = new HashMap<String, String>();
// 		if (query != null)
// 		{
// 			for (String param: query.split("&"))
// 			{
// 				String pair[];
// 				pair = param.split("=");
// 				addParameter(pair[0], pair[1]);
// 			}
// 		}
// 	}
// 
// 	public boolean containsParameter(String name)
// 	{
// 		return parameters.containsKey(name);
// 	}
// 	
// 	public String getParameter(String name)
// 	{
// 		return parameters.get(name);
// 	}
// 	
// 	public void addParameter(String name, String value)
// 	{
// 		parameters.put(name, value);
// 	}
// 
// 	public void removeParameter(String name)
// 	{
// 		parameters.remove(name);
// 	}
// 
// 	@Override
// 	public String toString()
// 	{
// 		String query = "";
// 		for (String name: parameters.keySet())
// 		{
// 			query += name + "=" + parameters.get(name) + "&";
// 		}
// 		return query.replaceAll("&$", "");
// 	}
// 
// 	public HashMap<String, String> getParameters()
//     {
//     	return parameters;
//     }
// }
