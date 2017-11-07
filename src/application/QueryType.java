package application;

public class QueryType 
{
	public static final QueryType SELECT	= new QueryType("Select ", QueryGroup.SELECT_GROUP);
	public static final QueryType SHOW		= new QueryType("Show ", QueryGroup.SELECT_GROUP);
	public static final QueryType DESCRIBE	= new QueryType("Describe ", QueryGroup.SELECT_GROUP);
	
	public static final QueryType DELETE	= new QueryType("Delete ", QueryGroup.UPDATE_GROUP);
	public static final QueryType USE		= new QueryType("Use ", QueryGroup.UPDATE_GROUP);
	public static final QueryType INSERT	= new QueryType("Insert ", QueryGroup.UPDATE_GROUP);
	public static final QueryType UPDATE	= new QueryType("Update ", QueryGroup.UPDATE_GROUP);
	public static final QueryType DROP		= new QueryType("Drop ", QueryGroup.UPDATE_GROUP);
	public static final QueryType CREATE	= new QueryType("Create ", QueryGroup.UPDATE_GROUP);
	public static final QueryType ALTER		= new QueryType("Alter ", QueryGroup.UPDATE_GROUP);

	public static final QueryType SEL		= new QueryType("Sel ", QueryGroup.CUSTOM_SELECT_GROUP);
	public static final QueryType UPD	= new QueryType("Upd ", QueryGroup.CUSTOM_UPDATE_GROUP);
	
	private String typeName;
	private QueryGroup queryGroup;

	QueryType(String typeName, QueryGroup queryGroup)
	{
		this.typeName = typeName;
		this.queryGroup = queryGroup;
	}

	public String getName()
	{
		return typeName;
	}
	
	public QueryGroup getGroup()
	{
		return queryGroup;
	}
	
}
