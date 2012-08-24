package org.cobra_grendel.util.io;

import java.io.IOException;
import java.io.Reader;

public class EmptyReader extends Reader
{
	@Override
	public void close() throws IOException
	{
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException
	{
		return 0;
	}
}