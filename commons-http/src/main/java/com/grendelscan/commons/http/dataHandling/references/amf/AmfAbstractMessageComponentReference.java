package com.grendelscan.commons.http.dataHandling.references.amf;
///**
// * 
// */
//package com.grendelscan.commons.http.dataHandling.references.amf;
//
//import com.grendelscan.commons.http.dataHandling.references.DataReference;
//
///**
// * @author david
// *
// */
//public class AmfAbstractMessageComponentReference implements DataReference
//{
//	public enum Location
//	{
//		BODY("body"), HEADERS("headers"), CLIENT_ID("ClientId"), DESTINATION("destination"), 
//		MESSAGE_ID("messageId"), TIMESTAMP("timestamp"), TIME_TO_LIVE("timeToLive"), ;
//		
//		Location(String string)
//		{
//			this.string = string;
//		}
//		
//		private final String string;
//		
//		@Override public String toString()
//		{
//			return string;
//		}
//	}
//	
//	
//	public static final AmfAbstractMessageComponentReference BODY = new AmfAbstractMessageComponentReference(Location.BODY);
//	public static final AmfAbstractMessageComponentReference HEADERS = new AmfAbstractMessageComponentReference(Location.HEADERS);
//	public static final AmfAbstractMessageComponentReference CLIENT_ID = new AmfAbstractMessageComponentReference(Location.CLIENT_ID);
//	public static final AmfAbstractMessageComponentReference DESTINATION = new AmfAbstractMessageComponentReference(Location.DESTINATION);
//	public static final AmfAbstractMessageComponentReference MESSAGE_ID = new AmfAbstractMessageComponentReference(Location.MESSAGE_ID);
//	public static final AmfAbstractMessageComponentReference TIMESTAMP = new AmfAbstractMessageComponentReference(Location.TIMESTAMP);
//	public static final AmfAbstractMessageComponentReference TIME_TO_LIVE = new AmfAbstractMessageComponentReference(Location.TIME_TO_LIVE);
//	
//	private final Location location;
//	
//	private AmfAbstractMessageComponentReference(Location location)
//	{
//		this.location = location;
//	}
//	
//	@Override public AmfAbstractMessageComponentReference clone()
//	{
//		return this;
//	}
//
//	/**
//	 * @return the location
//	 */
//	public Location getLocation()
//	{
//		return location;
//	}
//
//}
