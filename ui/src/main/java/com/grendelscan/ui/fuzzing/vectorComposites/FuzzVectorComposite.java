package com.grendelscan.ui.fuzzing.vectorComposites;
import com.grendelscan.fuzzing.FuzzVector;
import com.grendelscan.fuzzing.FuzzVectorFormatException;

public interface FuzzVectorComposite
{
	public FuzzVector getFuzzVector() throws FuzzVectorFormatException; 
	public void displayFuzzVector(FuzzVector oldVector);
	public String getDescription();
	public Class getFuzzVectorClass();
}
