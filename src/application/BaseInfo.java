package application;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BaseInfo 
{
	SQLConnection sql;
	int baseType;
	Stage window;

	BaseInfo(SQLConnection sql)
	{
		this.sql = sql;
	}
	
	public void exit()
	{
		if(this.window != null && this.window.isShowing())
		{
			this.window.close();
		}
	}
	public void showBaseInfo()
	{
		window = new Stage();
		window.setTitle("Tables structure");
		window.getIcons().add(new Image(Records.IMG_MAIN_ICON));
		
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 800, 600);
		scene.getStylesheets().add("application/application.css");

		//---------------------------------------------------------------------------
		
		TabPane basesPane = new TabPane();

		LinkedList<String> dababases = getTabNames(null);
		
		for(String s : dababases)
		{
			Tab baseTab = new Tab();
			baseTab.setText(s);
			baseTab.getStyleClass().add("tabName");
			
			baseTab.setContent(createTablePane(s));
			
			basesPane.getTabs().add(baseTab);
		}

		//---------------------------------------------------------------------------
		
		root.setCenter(basesPane);
		window.setScene(scene);
		window.show();
	}
	
	private BorderPane createTablePane(String parent)
	{
		BorderPane tablesPane = new BorderPane();
		
		//----------------------------------------------------------------------
		// right panel with info about selected table
		
		TextArea tableInfo = new TextArea("Select table");
		
		BorderPane rightPane = new BorderPane();
		rightPane.setCenter(tableInfo);
	
		//----------------------------------------------------------------------
		// left panel with tables names
		
		ScrollPane leftPane = new ScrollPane();
		leftPane.setHbarPolicy(ScrollBarPolicy.NEVER);

		LinkedList<String> tables = getTabNames(parent);
		
		VBox tableNames = new VBox();
		tableNames.getStyleClass().add("leftPannel");
		
		if(tables.size() != 0)
			for(String s : tables)
			{
				Label l = new Label(s);

				l.setOnMouseClicked(new EventHandler<MouseEvent>() 
				{
					@Override
					public void handle(MouseEvent e) 
					{
						tableInfo.setText( " \n" + parent + " -> " + s + "\n" + getTableInfo(s, parent));
					}
				});
				
				tableNames.getChildren().add(l);
			}
		else
		{
			Label l = new Label("Table empty or you haven't access to this table");
			l.getStyleClass().add("tableName");
			tableInfo.clear();
			
			tableNames.getChildren().add(l);
		}
		
		leftPane.setContent(tableNames);

		//----------------------------------------------------------------------
		
		tablesPane.setLeft(leftPane);
		tablesPane.setCenter(rightPane);
		
		return tablesPane;
	}

	private String getTableInfo(String table, String base) 
	{
		String ret = "";
		
		if(sql.getBaseType() == sql.MYSQL)
		{
			ret = sql.selectFormatted("SHOW COLUMNS FROM " + base + "." + table);
		}
		else if(sql.getBaseType() == sql.MSSQLSERVER)
		{
			ret = sql.selectFormatted("SELECT COLUMN_NAME AS Name, COLUMN_DEFAULT AS 'Default', "
					+ "IS_NULLABLE, DATA_TYPE, CHARACTER_SET_NAME "
					+ "  FROM information_schema.columns WHERE table_name = '" 
					+ table + "' AND TABLE_CATALOG='" + base + "'");
		}

		return ret;
	}
	
	private LinkedList<String> getTabNames(String parent)
	{
		ResultSet rs = null;
		LinkedList<String> ret = new LinkedList<String>();
		
		// get databases list
		
		if(sql.getBaseType() == sql.MYSQL)
		{
			if(parent == null)
			{
				rs = sql.select("show databases");
			}
			else
			{
				rs = sql.select("show tables from " + parent);
			}
		}
		else if(sql.getBaseType() == sql.MSSQLSERVER)
		{
			if(parent == null)
			{
				rs = sql.select("SELECT name FROM sys.databases WHERE name NOT IN('master', 'tempdb', 'model', 'msdb')");
			}
			else
			{
				rs = sql.select("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES where TABLE_CATALOG='" + parent + "'"); 
			}
		}
		
		// convert result to LinkedList<String>
		
		try 
		{
			while (rs.next())
			{
				ret.add(rs.getString(1));
			}
		} 
		catch (SQLException e) 
		{
			Messages.showError(e.getMessage());
			e.printStackTrace();
		}
		catch(Exception e)
		{
			Messages.showError(e.getMessage());
		}
		
		return ret;
	}
}
