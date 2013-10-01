/**
 * 
 */
package com.grendelscan.smashers.types;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.TestType;

/**
 * @author david
 *
 */
public interface ByHtmlFormTest extends TestType
{
	public void testByHtmlForm(int transactionID, String formHash, int testJobId) throws InterruptedScanException;
}
