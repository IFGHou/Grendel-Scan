package com.grendelscan.requester.http.dataHandling.simplifiedAMF.dataTypeDefinitions;

import java.io.IOException;

import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AmfOutputStream;


public enum AmfDataType implements AmfConstants
{
	
	kAmfMessageRoot("AMF Message Root", noAmfConstant, noAmfConstant),
	kAmfMessageHeaders("Message headers", noAmfConstant, noAmfConstant),
	kAmfMessageBodies("Message bodies", noAmfConstant, noAmfConstant),
	kActionMessageHeader("Action message header", noAmfConstant, noAmfConstant),
	
	kUndefined("Undefined", a3UndefinedType, kUndefinedType),
	kNull("Null", a3NullType, kNullType),
	kBoolean("Boolean", noAmfConstant, kBooleanType),
	kFalse("False", a3FalseType, kBooleanType),
	kTrue("True", a3TrueType, kBooleanType),
	kInteger("Integer", a3IntegerType, a3IntegerType),
	kDouble("Double", a3DoubleType, kNumberType),

	kString("String", a3StringType, kStringType),
	kAvmPlusXml("Avm plus XML", a3AvmPlusXmlType, kXMLObjectType),
	kXML("XML", a3XMLType, kXMLObjectType),
	kDate("Date", a3DateType, kDateType),

	kBooleanArray("Array of Boolean", a3ArrayType, kStrictArrayType),
	kIntArray("Array of int", a3ArrayType, kStrictArrayType),
	kDoubleArray( "Array of double", a3ArrayType, kStrictArrayType),
	kByteArray("Byte array", a3ByteArrayType, kStrictArrayType),

	kAssociativeArray("Associative array", a3ArrayType, kECMAArrayType),
	kObjectArray("Array of objects", a3ArrayType, kStrictArrayType),

	kASObject("AS object", a3ObjectType, kObjectType),
	kServerSideObject("Server-side object", a3ObjectType, kObjectType),
	kAmfBody("AMF Message Body", a3ObjectType, kObjectType),

	kAmfCommandMessage("AMF Command Message", a3ObjectType, kObjectType),
	kCommandType("Command type", a3IntegerType, kNumberType),
	kAmfMessage("AMF Message", a3ObjectType, kObjectType),
	kAmfAsyncMessage("AMF Async Message", a3ObjectType, kObjectType),
	kAmfRemotingMessage("AMF Remoting Message", a3ObjectType, kObjectType),
	kAmfAcknowledgeMessage("AMF Acknowledge Message", a3ObjectType, kObjectType),
	kAmfErrorMessage("AMF Error Message", a3ObjectType, kObjectType)
	
	;
	
	
	 
	private final String description;
	
	private final int amf3Code;
	private final int amfCode;
	
	public static AmfDataType[] getCreatableTypes()
	{
		AmfDataType[] data =
		        new AmfDataType[] { kNull, kBoolean, kInteger, kDouble, kString, kXML, kAvmPlusXml, kDate, kASObject,
		                kServerSideObject, kAssociativeArray, kObjectArray, kBooleanArray, kIntArray, kDoubleArray,
		                kByteArray, kAmfCommandMessage, kAmfAsyncMessage, kAmfRemotingMessage, kUndefined };
		return data;
	}
	
	private AmfDataType(String description, int code3, int code)
	{
		this.description = description;
		this.amfCode = code;
		this.amf3Code = code3;
	}
	
	
	public void writeCode(AmfOutputStream outputStream, boolean useAmf3Code) throws IOException
	{
		if (useAmf3Code)
		{
			outputStream.write(amf3Code);
		}
		else
		{
			outputStream.write(amfCode);
		}
		
	}
	
	public String getDescription()
	{
		return description;
	}

	public int getAmf3Code()
    {
    	return amf3Code;
    }

	public int getAmfCode()
    {
    	return amfCode;
    }

}
