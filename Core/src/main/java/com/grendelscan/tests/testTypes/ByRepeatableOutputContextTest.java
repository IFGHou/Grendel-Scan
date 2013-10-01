package com.grendelscan.tests.testTypes;

import java.util.Collection;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.libraries.TokenTesting.TokenContext;
import com.grendelscan.tests.libraries.TokenTesting.TokenContextType;

public interface ByRepeatableOutputContextTest extends TestType
{
	public TokenContextType[] getDesiredRepeatableContexts();

	public void testByRepeatableOutputContext(Collection<TokenContext> contexts, int testJobId) throws InterruptedScanException;
}
