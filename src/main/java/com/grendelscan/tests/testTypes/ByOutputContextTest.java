package com.grendelscan.tests.testTypes;

import java.util.Collection;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.libraries.TokenTesting.TokenContext;
import com.grendelscan.tests.libraries.TokenTesting.TokenContextType;

public interface ByOutputContextTest extends TestType
{
	public TokenContextType[] getDesiredContexts();

	public void testByOutputContext(Collection<TokenContext> contexts, int testJobId) throws InterruptedScanException;
}
