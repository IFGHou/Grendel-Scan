package com.grendelscan.requester.http.dataHandling.simplifiedAMF.messages;

public enum CommandMessageTypeEnum
{
	SUBSCRIBE_OPERATION(0, "Subscribe"), 
	UNSUBSCRIBE_OPERATION(1, "Unsubscribe"), POLL_OPERATION(2, "Poll"), CLIENT_SYNC_OPERATION(
	        4, "Client sync"), CLIENT_PING_OPERATION(5, "Client ping"), CLUSTER_REQUEST_OPERATION(7, "Cluster request"), LOGIN_OPERATION(
	        8, "Login"), LOGOUT_OPERATION(9, "Logout"), SUBSCRIPTION_INVALIDATE_OPERATION(10, "Subscription invalidate"), MULTI_SUBSCRIBE_OPERATION(
	        11, "Multi-subscribe"), DISCONNECT_OPERATION(12, "Disconnect"), TRIGGER_CONNECT_OPERATION(13, "Trigger"), UNKNOWN_OPERATION(
	        10000, "Unknown");
	
	private final int value;
	private final String description;
	
	public static CommandMessageTypeEnum getByValue(int value)
	{
		if (value == 0)
		{
			return SUBSCRIBE_OPERATION;
		}
		else if (value == 1)
		{
			return SUBSCRIBE_OPERATION;
		}
		else if (value == 2)
		{
			return POLL_OPERATION;
		}
		else if (value == 4)
		{
			return CLIENT_SYNC_OPERATION;
		}
		else if (value == 5)
		{
			return CLIENT_PING_OPERATION;
		}
		else if (value == 7)
		{
			return CLUSTER_REQUEST_OPERATION;
		}
		else if (value == 8)
		{
			return LOGIN_OPERATION;
		}
		else if (value == 9)
		{
			return LOGOUT_OPERATION;
		}
		else if (value == 10)
		{
			return SUBSCRIPTION_INVALIDATE_OPERATION;
		}
		else if (value == 11)
		{
			return MULTI_SUBSCRIBE_OPERATION;
		}
		else if (value == 12)
		{
			return DISCONNECT_OPERATION;
		}
		else if (value == 13)
		{
			return TRIGGER_CONNECT_OPERATION;
		}
		else
		{
			return UNKNOWN_OPERATION;
		}
	}
	
	private CommandMessageTypeEnum(int value, String description)
	{
		this.value = value;
		this.description = description;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public int getValue()
	{
		return value;
	}
	
}
