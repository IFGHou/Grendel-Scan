/**
 * 
 */
package com.grendelscan.requester.http.dataHandling.data;

import java.io.OutputStream;
import java.io.Serializable;

import com.grendelscan.requester.http.dataHandling.containers.DataContainer;
import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.references.DataReferenceChain;

/**
 * @author david
 *
 */
public interface Data extends Serializable
{
	public void writeBytes(OutputStream out);
	public DataContainer<?> getParent();
	public boolean isDataAncestor(DataContainer<?> container);
	public void removeFromCollection();
	public DataReference getReference();
	public DataReferenceChain getReferenceChain();
	public int getTransactionId();
	public void setTransactionId(int transactionId);
}
