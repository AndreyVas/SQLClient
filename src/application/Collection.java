package application;

import java.util.LinkedList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Collection 
{
	private LinkedList<CollectionItem> collection;
	private String name;
	
	public static final String XML_CONT_NAME = "collection";
	public static final String XML_NAME = "name";
	public static final String XML_CONTENT = "content";
	
	Collection(String name)
	{
		collection = new LinkedList<CollectionItem>();
		this.name = name;
	}
	
	public void addItem(String name, String queue, String desc)
	{
		collection.add(new CollectionItem(name, queue, desc));
	}
	
	public void addItem(CollectionItem i)
	{
		collection.add(i);
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public boolean isItemNameUnique(String name)
	{
		for(CollectionItem ci : collection)
		{
			if(ci.getName().equals(name))
			{
				return false;
			}
		}
		
		return true;
	}
	
	public LinkedList<String> getQueriesNames()
	{
		LinkedList<String> names = new LinkedList<String>();
		
		for(CollectionItem ci : this.collection)
		{
			names.add(ci.getName());
		}
		
		return names;
	}
	
	public void ParseXML(NodeList nodeList)
	{
		for(int i = 0; i < nodeList.getLength(); i++)
	    {
			if(nodeList.item(i).getNodeName().equals(CollectionItem.XML_ITEM))
			{
				NodeList connectionItem = nodeList.item(i).getChildNodes();
				String name = "";
				String query = "";
				String description = "";
				
				for(int j = 0; j < connectionItem.getLength(); j++)
				{
					switch(connectionItem.item(j).getNodeName())
					{
						case CollectionItem.XML_NAME:
							name = connectionItem.item(j).getTextContent();
							break;
							
						case CollectionItem.XML_QUERY:
							query = connectionItem.item(j).getTextContent();
							break;
							
						case CollectionItem.XML_DESCRIPTION:
							description = connectionItem.item(j).getTextContent();
							break;
					}
				}
				
				collection.add(new CollectionItem(name, query, description));
			}
	    }
	}
	
	public Element getXML(Document doc) 
	{
		Element collection = doc.createElement(XML_CONT_NAME); 
		
		Element name = doc.createElement(XML_NAME);
		name.setTextContent(this.name);
		
		Element content = doc.createElement(XML_CONTENT); 
		
		for(CollectionItem ci : this.collection)
		{
			content.appendChild(ci.getXML(doc));
		}
		
		collection.appendChild(name);
		collection.appendChild(content);

		return collection;
	}
	
	public LinkedList<CollectionItem> getItems()
	{
		return this.collection;
	}
}
