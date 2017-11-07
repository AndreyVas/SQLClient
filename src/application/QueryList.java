package application;

import java.util.LinkedList;

public class QueryList 
{
	private LinkedList<QueriesListItem> queriesList = new LinkedList<QueriesListItem>();
	
	
	QueryList()
	{
		queriesList.add(new QueriesListItem(QueryType.SELECT, true, true));
		queriesList.add(new QueriesListItem(QueryType.SHOW, true, true));
		queriesList.add(new QueriesListItem(QueryType.USE, true, true));
		queriesList.add(new QueriesListItem(QueryType.DESCRIBE, true, true));
		
		queriesList.add(new QueriesListItem(QueryType.INSERT, false, true));
		queriesList.add(new QueriesListItem(QueryType.UPDATE, false, true));
		queriesList.add(new QueriesListItem(QueryType.DELETE, false, true));		
		queriesList.add(new QueriesListItem(QueryType.DROP, false, true));
		queriesList.add(new QueriesListItem(QueryType.CREATE, false, true));
		queriesList.add(new QueriesListItem(QueryType.ALTER, false, true));
		
		
		queriesList.add(new QueriesListItem(QueryType.SEL, false, false));
		queriesList.add(new QueriesListItem(QueryType.UPD, false, false));

	}
	
	public LinkedList<QueriesListItem> getQueriesList()
	{
		return queriesList;
	}
	
	public QueryGroup getQueryGroup(String query)
	{
		for(QueriesListItem qli : queriesList)
		{
			if(query.toLowerCase().startsWith(qli.getName().toLowerCase()))
			{
				if(qli.getGrant())
				{
					return qli.getGroup();
				}
				else
				{
					return QueryGroup.NOT_AUTORIZED;
				}
				
			}
		}
		
		return QueryGroup.UNKNOWN_GROUP;
	}
	
	public boolean getGrant(QueryType qt)
	{
		for(QueriesListItem qli : queriesList)
		{
			System.out.println(qli.getName() + "  " + qt.getName());
			
			if(qli.getName().equals(qt.getName()))
			{
				System.out.println("yes");
				return qli.getGrant();
			}
			else
			{
				System.out.println("no");
			}
		}
		
		return false;
	}
	
	public void setGrant(String name, boolean grant)
	{
		for(QueriesListItem qli : queriesList)
		{
			if(qli.getName().toLowerCase().equals(name.toLowerCase()))
			{
				qli.setGrant(grant);
			}
		}
	}
}
