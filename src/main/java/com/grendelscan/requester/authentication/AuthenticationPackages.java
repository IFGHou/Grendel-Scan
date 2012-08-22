/**
 * 
 */
package com.grendelscan.requester.authentication;

import com.grendelscan.data.database.collections.DatabaseBackedMap;

/**
 * @author david
 *
 */
public class AuthenticationPackages extends DatabaseBackedMap<Integer, AuthenticationPackage>
{

	/**
	 * @param uniqueName
	 */
	public AuthenticationPackages()
	{
		super("authentication_packages");
		int lastID = 0;
		for(int id: keySet())
		{
			if (id > lastID)
			{
				lastID = id;
			}
		}
		AuthenticationPackage.setLastID(lastID);
	}
	
	public void addAuthenticationPackage(AuthenticationPackage authenticationPackage)
	{
		put(authenticationPackage.getId(), authenticationPackage);
	}

}
