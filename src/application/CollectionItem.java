package application;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CollectionItem 
{
	private String name;
	private String query;
	private String description;
	
	public static final String XML_ITEM = "collectionItem";
	public static final String XML_NAME = "name";
	public static final String XML_QUERY = "query";
	public static final String XML_DESCRIPTION = "description";
	
	CollectionItem(String name)
	{
		this.name = name;
		this.query = "";
		this.description = "";
	}
	
	CollectionItem(String name, String query, String description)
	{
		this.name = name;
		this.query = query;
		this.description = description;
	}
	
	public Element getXML(Document doc) 
	{
		Element collectionItem = doc.createElement(XML_ITEM);
		
		Element name = doc.createElement(XML_NAME);
		name.setTextContent(this.name);
		
		Element query = doc.createElement(XML_QUERY);
		query.setTextContent(this.query);
		
		Element description = doc.createElement(XML_DESCRIPTION);
		description.setTextContent(this.description);
		
		collectionItem.appendChild(name);
		collectionItem.appendChild(query);
		collectionItem.appendChild(description);
		
		return collectionItem;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void ParseXML(NodeList nodeList)
	{
		for(int i = 0; i < nodeList.getLength(); i++)
	    {
			System.out.println(nodeList.item(i).getNodeName() + "  " + nodeList.item(i).getTextContent());
	    }
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getQueue()
	{
		return this.query;
	}
	
	public void setQueue(String queue)
	{
		this.query = queue;
	}
	
	public String getDescription()
	{
		return this.description;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
}
