package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLConnection 
{
	private Connection con;
	private Statement stmt;
    
	private String user;
	private String password;
	private String url;
	
	SQLConnection()
	{
		/*
		this.user = "ITInvent";
		this.password = "Unilever01";
		
		this.url = "jdbc:sqlserver://156.4.128.92:1433;"
				 + "Provider=SQLOLEDB;Persist Security Info=True;User ID=ITInvent;Password=Unilever01;Data Source=156.4.128.92;Initial Catalog=ITINVENT";
*/
		this.url = "jdbc:mysql://localhost/";
		this.user = "root";
		this.password = "";
	}
	
	public String getURL()
	{
		return this.url;
	}
	
	public void setURL(String url)
	{
		this.url = url;
	}
	
	public String getUser()
	{
		return this.user;
	}
	
	public void setUser(String user)
	{
		this.user = user;
	}
	
	public String getPassword()
	{
		return this.password;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	public void createConnection()
	{
		try 
		{
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			con = DriverManager.getConnection(url, user, password);
			stmt = con.createStatement();
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
			Messages.showError(e.getMessage());
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			Messages.showError(e.getMessage());
		}
		catch(Exception e)
		{
			// unknown exeption
			Messages.showError(e.getMessage());
		}
	}
	
	ResultSet queue(String queue)
	{
		try 
		{
			return stmt.executeQuery(queue);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			Messages.showError(e.getMessage());
		}
	
		return null;
	}
	
	public void closeConnection()
	{
		try 
		{ 
			con.close(); 
			stmt.close();
		} 
		catch(SQLException e) 
		{
			Messages.showError(e.getMessage()); 
		}

	}
}
