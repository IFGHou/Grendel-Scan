/**
 * 
 */
package com.grendelscan.data.database;

/**
 * @author david
 *
 */
public interface DatabaseUser
{
	public void shutdown(boolean nice) throws InterruptedException;
}
