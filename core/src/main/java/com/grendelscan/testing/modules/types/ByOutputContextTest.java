package com.grendelscan.testing.modules.types;

import java.util.Collection;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.testing.utils.tokens.TokenContext;
import com.grendelscan.testing.utils.tokens.TokenContextType;

public interface ByOutputContextTest extends TestType
{
    public TokenContextType[] getDesiredContexts();

    public void testByOutputContext(Collection<TokenContext> contexts, int testJobId) throws InterruptedScanException;
}
