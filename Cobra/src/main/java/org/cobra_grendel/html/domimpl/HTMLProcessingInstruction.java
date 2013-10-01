package org.cobra_grendel.html.domimpl;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

/**
 * HTML DOM object representing processing instruction as per HTML 4.0
 * specification.
 * 
 * @author vitek
 */
public class HTMLProcessingInstruction extends NodeImpl implements ProcessingInstruction, Cloneable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3111022786155047914L;
	String data;
	String target;
	
	public HTMLProcessingInstruction(String target, String data, int transactionId)
	{
		super(transactionId);
		this.target = target;
		this.data = data;
	}
	
	@Override
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new IllegalStateException(e);
		}
	}
	
	@Override public String getData()
	{
		return data;
	}
	
	@Override
	public String getLocalName()
	{
		return target;
	}
	
	@Override
	public String getNodeName()
	{
		return target;
	}
	
	@Override
	public short getNodeType()
	{
		return Node.PROCESSING_INSTRUCTION_NODE;
	}
	
	@Override
	public String getNodeValue() throws DOMException
	{
		return data;
	}
	
	@Override public String getTarget()
	{
		return target;
	}
	
	@Override public void setData(String data) throws DOMException
	{
		this.data = data;
	}
	
	@Override
	public void setNodeValue(String nodeValue) throws DOMException
	{
		data = nodeValue;
	}
	
	@Override
	public String toString()
	{
		return "<?" + target + " " + data + ">";
	}
	
	@Override
	protected Node createSimilarNode()
	{
		return (Node) clone();
	}
}
