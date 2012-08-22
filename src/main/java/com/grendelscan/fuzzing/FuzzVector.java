package com.grendelscan.fuzzing;

public interface FuzzVector
{
	public void reset();
	public String getNextValue();
	public boolean done();
}
