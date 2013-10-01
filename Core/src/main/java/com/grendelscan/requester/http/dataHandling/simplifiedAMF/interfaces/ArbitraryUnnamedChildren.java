package com.grendelscan.requester.http.dataHandling.simplifiedAMF.interfaces;

import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfData;

public interface ArbitraryUnnamedChildren extends ArbitraryChildren, Orderable
{
	public void remove(AbstractAmfData child);
}
