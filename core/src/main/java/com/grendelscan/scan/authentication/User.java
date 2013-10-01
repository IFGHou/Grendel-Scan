/**
 * 
 */
package com.grendelscan.scan.authentication;

/**
 * @author david
 * 
 */
public class User
{
    private final String name;
    private final String password;

    public User(final String name, final String password)
    {
        super();
        this.name = name;
        this.password = password;
    }

    public final String getName()
    {
        return name;
    }

    public final String getPassword()
    {
        return password;
    }

}
