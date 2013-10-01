package com.grendelscan.smashers.types;

import java.util.Collection;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.TestType;
import com.grendelscan.smashers.utils.tokens.TokenContext;
import com.grendelscan.smashers.utils.tokens.TokenContextType;

public interface ByOutputContextTest extends TestType
{
    public TokenContextType[] getDesiredContexts();

    public void testByOutputContext(Collection<TokenContext> contexts, int testJobId) throws InterruptedScanException;
}
