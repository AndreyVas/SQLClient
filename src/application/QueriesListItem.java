package application;

public class QueriesListItem 
{
	private String name;
	private boolean grant;
	private QueryGroup group;
	private boolean standard;
	
	QueriesListItem(QueryType query, boolean grant, boolean standard)
	{
		this.name = query.getName();
		this.grant = grant;
		this.group = query.getGroup();
		this.standard = standard;
	}
	
	public String getName()
	{
		return name;
	}
	
	public boolean isStandard()
	{
		return this.standard;
	}
	
	public boolean getGrant()
	{
		return this.grant;
	}
	
	public void setGrant(boolean grant)
	{
		this.grant = grant;
	}
	
	public QueryGroup getGroup()
	{
		return this.group;
	}
	
}
