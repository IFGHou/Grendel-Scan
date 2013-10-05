/**
 * 
 */
package com.grendelscan.testing.modules.types;

import com.grendelscan.scan.InterruptedScanException;

/**
 * @author david
 *
 */
public interface ByHtmlFormTest extends TestType
{
	public void testByHtmlForm(int transactionID, String formHash, int testJobId) throws InterruptedScanException;
}
