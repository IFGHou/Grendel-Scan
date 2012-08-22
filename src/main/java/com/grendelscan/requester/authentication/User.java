/**
 * 
 */
package com.grendelscan.requester.authentication;

/**
 * @author david
 *
 */
public class User
{
	private String name;
	private String password;
	public final String getName()
	{
		return name;
	}
	public final String getPassword()
	{
		return password;
	}
	public User(String name, String password)
	{
		super();
		this.name = name;
		this.password = password;
	}
	
}
