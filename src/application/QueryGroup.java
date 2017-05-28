package application;

public class QueryGroup 
{
	public static final QueryGroup SELECT_GROUP = new QueryGroup("select_group");
	public static final QueryGroup UPDATE_GROUP = new QueryGroup("update_group");
	public static final QueryGroup UNKNOWN_GROUP = new QueryGroup("unknown_group");
	public static final QueryGroup NOT_AUTORIZED = new QueryGroup("not_authorized");
	
	public static final QueryGroup CUSTOM_SELECT_GROUP = new QueryGroup("custom_select_group");
	public static final QueryGroup CUSTOM_UPDATE_GROUP = new QueryGroup("custom_update_group");
	
	String goupName;
	
	QueryGroup(String groupName)
	{
		this.goupName = groupName;
	}
}
