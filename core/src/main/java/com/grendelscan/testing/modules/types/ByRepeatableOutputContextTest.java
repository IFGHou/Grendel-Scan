package com.grendelscan.testing.modules.types;

import java.util.Collection;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.testing.utils.tokens.TokenContext;
import com.grendelscan.testing.utils.tokens.TokenContextType;

public interface ByRepeatableOutputContextTest extends TestType
{
    public TokenContextType[] getDesiredRepeatableContexts();

    public void testByRepeatableOutputContext(Collection<TokenContext> contexts, int testJobId) throws InterruptedScanException;
}
