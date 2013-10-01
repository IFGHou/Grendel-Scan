package com.grendelscan.smashers.types;

import java.util.Collection;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.TestType;
import com.grendelscan.smashers.utils.tokens.TokenContext;
import com.grendelscan.smashers.utils.tokens.TokenContextType;

public interface ByRepeatableOutputContextTest extends TestType
{
    public TokenContextType[] getDesiredRepeatableContexts();

    public void testByRepeatableOutputContext(Collection<TokenContext> contexts, int testJobId) throws InterruptedScanException;
}
