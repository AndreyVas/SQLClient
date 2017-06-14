package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSetMetaData;

public class SQLConnection 
{
	private Connection con;
	private Statement stmt;
    
	private String user;
	private String password;
	private String url;
	
	private int baseType;

	public final int MYSQL = 0;
	public final int MSSQLSERVER = 1;
	
	private final String mySQLurlStart = "jdbc:mysql://";
	private final String msSQLurlStart = "jdbc:sqlserver://";
	
	private final String nullString = "NULL";
	private int COLUMN_WIDTH;
	private boolean queryTime;

	SQLConnection()
	{
		this.url = "localhost";
		this.user = "root";
		this.password = "кщще";
		
		this.COLUMN_WIDTH = 20;
		queryTime = true;
	}
	
	public void setQueueTime(boolean t)
	{
		this.queryTime = t;
	}
	
	public boolean getQueueTime()
	{
		return this.queryTime;
	}
	
	public void setColumnWidth(int width)
	{
		this.COLUMN_WIDTH = width;
	}
	
	public int getColumnWidth()
	{
		return this.COLUMN_WIDTH;
	}
	
	private String getBackSpaces(int count)
	{
		String s = "";
		
		for(int i = 0; i < count; i++)
		{
			s += " ";
		}
		
		return s;
	}
	
	private String getDivider(int count)
	{
		String s = "";
		
		for(int i = 0; i < COLUMN_WIDTH + 2; i++)
		{
			s += "-";
		}
		
		String ret = "";
		
		for(int i = 0; i < count; i++)
		{
			ret += s;
		}

		return ret;
	}
	
	public void setBaseType(int baseType)
	{
		this.baseType = baseType;
	}
	
	public int getBaseType()
	{
		return this.baseType;
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
	
	public boolean createConnection()
	{
		try 
		{
			String url = "";
			
			switch(baseType)
			{
				case MYSQL:
					url = this.mySQLurlStart + this.url;
				break;
				
				case MSSQLSERVER:
					url = this.msSQLurlStart + this.url;
				break;
			}
			
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			con = DriverManager.getConnection(url, user, password);
			stmt = con.createStatement();
			
			return true;
		} 
		catch (ClassNotFoundException e) 
		{
			Messages.showError(e.getMessage());
		} 
		catch (SQLException e) 
		{
			Messages.showError(e.getMessage());
		}
		catch(Exception e)
		{
			// unknown exception
			Messages.showError(e.getMessage());
		}
		
		return false;
	}
	
	public ResultSet select(String queue)
	{
		try 
		{
			return stmt.executeQuery(queue);
		} 
		catch (SQLException e) 
		{
			Messages.showError(e.getMessage());
		}
	
		return null;
	}
	
	public String selectFormatted(String queue)
	{
		long beforeRequest = System.currentTimeMillis();
		ResultSet rs = select(queue);
		long afterRequest = System.currentTimeMillis();

		String text = "";
		
		if(queryTime)
		{
			text += "\nRequest execution time : " + ((afterRequest - beforeRequest) / 1000.0) + " s\n\n";
		}

		try 
		{
			ResultSetMetaData metaData = rs.getMetaData();
			int count = metaData.getColumnCount();
			
			text += '\n';
			
			for(int i = 1; i <= count; i++)
			{
				text += (metaData.getColumnLabel(i).length() < COLUMN_WIDTH 
						? metaData.getColumnLabel(i) + getBackSpaces(COLUMN_WIDTH - metaData.getColumnLabel(i).length()) 
							: metaData.getColumnLabel(i).substring(0, COLUMN_WIDTH)) + " |";
			}

			text += '\n';
			text += getDivider(rs.getMetaData().getColumnCount());
			text += '\n';

			while (rs.next())
			{
				for(int i = 0; i < rs.getMetaData().getColumnCount(); i++)
				{
					if(rs.getString(i + 1) == null)
					{
						text += nullString + getBackSpaces(COLUMN_WIDTH - nullString.length())  + " |";
					}
					else
					{
						text += (rs.getString(i + 1).length() < COLUMN_WIDTH 
								? rs.getString(i + 1) + getBackSpaces(COLUMN_WIDTH - rs.getString(i + 1).length()) 
									: rs.getString(i + 1).substring(0, COLUMN_WIDTH)) + " |";
					}
				}

				text += '\n';
				text += getDivider(rs.getMetaData().getColumnCount());
				text += '\n';
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			Messages.showError(e.getMessage());
		}
		
		return text;
	}

	int update(String queue)
	{
		try 
		{
			return stmt.executeUpdate(queue);
		} 
		catch (SQLException e) 
		{
			Messages.showError(e.getMessage());
		}
		
		return 0;
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
