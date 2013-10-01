package org.cobra_grendel.js;

import java.io.Serializable;

public interface JavaInstantiator extends Serializable
{
	public Object newInstance() throws InstantiationException, IllegalAccessException;
}
