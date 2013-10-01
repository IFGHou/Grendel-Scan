package com.grendelscan.smashers.types;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.TestType;

/**
 * 
 * @author David Byrne
 */
public interface FinalAnalysisTest extends TestType
{
	public void runAnalysis(int testJobId) throws InterruptedScanException;
}
