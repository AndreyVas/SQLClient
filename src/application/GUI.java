package application;


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GUI 
{
	private BorderPane mainPane;
	private TextArea outputBoxText;
	private TextArea queueField;
	private TableView<String> outputBoxTable;
	
	private SQLConnection sqlConnection;
	
	private int COLUMN_WIDTH = 20;
	private boolean queueFieldClean;
	private boolean outFieldClean;
	private boolean queryTime;
	
	private MenuBar topMenu;
	private Button createConnection;
	private Button clear;
	private Button closeConnection;
	private Button sentRequest;
	
	MenuItem openConnection;
	MenuItem offConnection;
	
	GUI(Scene s, BorderPane bp, SQLConnection sqlConnection)
	{
		this.mainPane = bp;
		
		this.outputBoxText = new TextArea();
		this.queueField = new TextArea();
		
		this.outputBoxText.setEditable(false);
		
		this.outputBoxTable = new TableView<String>();
		
		queueField.setFont(Font.font("Courier New"));
		outputBoxText.setFont(Font.font("Courier New"));
	
		queueFieldClean = false;
		outFieldClean = true;
		queryTime = true;
		
		VBox topCont = new VBox();		
		this.topMenu = createMainMenu();
		topCont.getChildren().addAll(topMenu, queueField);
		
		this.mainPane.setTop(topCont);
		this.mainPane.setCenter(outputBoxText);
		
		this.sqlConnection = sqlConnection;

		this.getControlButtons();
		
		bp.setOnKeyPressed(new EventHandler<KeyEvent>() 
		{
            public void handle(final KeyEvent keyEvent) 
            {
            	if(keyEvent.isControlDown())
            	{
            		switch(keyEvent.getCode())
            		{
            			case D:			// delete data from queue and out boxes
            				
            				if(queueField.getText().equals(""))
            					outputBoxText.setText("");
            				else
            					queueField.setText("");
            				
            				break;
            				
            			case O:			// open sql connection
            				openSQLConnection();
            				break;
            				
            			case Q: 		// close sql connection
            				closeSQLConnection();
            				break;
            				
            			default:
            		}
            	}
            	else if(keyEvent.isShiftDown())
            	{
            		switch(keyEvent.getCode())
            		{
	            		case ENTER:
	            			sendSQLRequest();
	            			break;
	            			
	            		default:
            		}
            	}
            }
        });
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
	
	public void openSQLConnection()
	{
		sqlConnection.createConnection();
		
		createConnection.setDisable(true);
		
		clear.setDisable(false);
		closeConnection.setDisable(false);
		sentRequest.setDisable(false);
		
		openConnection.setDisable(true);
		offConnection.setDisable(false);
	}
	
	public void closeSQLConnection()
	{
		sqlConnection.closeConnection();
		
		createConnection.setDisable(false);
		
		clear.setDisable(true);
		closeConnection.setDisable(true);
		sentRequest.setDisable(true);
		
		openConnection.setDisable(false);
		offConnection.setDisable(true);
	}
	
	public void sendSQLRequest()
	{
		if(!queueField.getText().equals(""))
		{
			long beforeRequest = System.currentTimeMillis();
			ResultSet rs = sqlConnection.queue(queueField.getText());
			long afterRequest = System.currentTimeMillis();
			
			if(outFieldClean)
				outputBoxText.clear();
			
			if(queryTime)
			{
				outputBoxText.setText(outputBoxText.getText() + "\nRequest execution time : " 
						+ ((afterRequest - beforeRequest) / 1000.0) + " s\n\n");
			}

			try 
			{
				String text = "";
				
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
				
				outputBoxText.setText(outputBoxText.getText() + text);
			
				while (rs.next())
				{
					text = "";
					
					for(int i = 0; i < rs.getMetaData().getColumnCount(); i++)
					{
						text += (rs.getString(i + 1).length() < COLUMN_WIDTH 
								? rs.getString(i + 1) + getBackSpaces(COLUMN_WIDTH - rs.getString(i + 1).length()) 
									: rs.getString(i + 1).substring(0, COLUMN_WIDTH)) + " |";
					}

					text += '\n';
					text += getDivider(rs.getMetaData().getColumnCount());
					text += '\n';
					
				    outputBoxText.setText(outputBoxText.getText() + text);
				    
				    if(queueFieldClean)
				    	queueField.clear();
				}
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
				Messages.showError(e.getMessage());
			}
		}
		else
		{
			outputBoxText.setText("Queue field is empty");
		}
	}
	
	public MenuBar createMainMenu()
	{
		topMenu = new MenuBar();
		
		Menu connection = new Menu("Conntection");
		
		openConnection = new MenuItem("Create");
		offConnection = new MenuItem("Close");
		MenuItem settings = new MenuItem("Settings");
		
		offConnection.setDisable(true);
		
		openConnection.setOnAction(new EventHandler<ActionEvent>() 
		{
            public void handle(ActionEvent t) 
            {
            	openSQLConnection();
            }
        });     
		
		offConnection.setOnAction(new EventHandler<ActionEvent>() 
		{
            public void handle(ActionEvent t) 
            {
            	closeSQLConnection();
            }
        });    
		
		settings.setOnAction(new EventHandler<ActionEvent>() 
		{
            public void handle(ActionEvent t) 
            {
            	openConnectionSettingsWindwo();
            }
        });    
		
		connection.getItems().addAll(openConnection, offConnection, settings);
		
		//--------------------------------------------
		
		Menu edit = new Menu("Edit");
		MenuItem cleanQueueField = new MenuItem("Clean queue field");
		MenuItem cleanOutField = new MenuItem("Clean out filed");
		MenuItem editSettings = new MenuItem("Settings");
		
		cleanQueueField.setOnAction(new EventHandler<ActionEvent>() 
		{
            public void handle(ActionEvent t) 
            {
            	queueField.clear();
            }
        });    
		
		cleanOutField.setOnAction(new EventHandler<ActionEvent>() 
		{
            public void handle(ActionEvent t) 
            {
            	outputBoxText.clear();
            }
        });    
		
		editSettings.setOnAction(new EventHandler<ActionEvent>() 
		{
            public void handle(ActionEvent t) 
            {
            	openEditSettingsWindow();
            }
        });    

		//--------------------------------------------
		
		Menu about = new Menu("About");
		MenuItem functionKeys = new MenuItem("Function keys");
		MenuItem urlRule = new MenuItem("URL rules");
		
		functionKeys.setOnAction(new EventHandler<ActionEvent>() 
		{
            public void handle(ActionEvent t) 
            {
            	String s = "CTRL + O - start SQL connection \n"
            			+ "CTRL + Q - end SQL connection \n"
            			+ "CTRL + D - delete text \n\n"
            			+ "SHIFT + ENTER - send request";
            	
            	Messages.showInfo(s);
            }
        });   
		
		urlRule.setOnAction(new EventHandler<ActionEvent>() 
		{
            public void handle(ActionEvent t) 
            {
            	String s = "For connection to MySQL server \n"
            			+ "\t jdbc:mysql://<base_address> \n\n"
            			+ "For connection to Microsoft SQL Server \n" 
            			+ "\t jdbc:sqlserver://<base_address> \n";
            	
            	Messages.showInfo(s);
            }
        });   
		
		about.getItems().addAll(functionKeys, urlRule);
		
		//--------------------------------------------
		
		edit.getItems().addAll(cleanQueueField, cleanOutField, editSettings);
		
		topMenu.getMenus().addAll(connection, edit, about);
	
		return topMenu;
	}

	private void openEditSettingsWindow()
	{
		Stage settingsWindwo = new Stage();
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root);
		
		scene.getStylesheets().add("application/application.css");
		root.getStyleClass().add("editSettings");
		
		//----------------------------------------------------
		
		HBox widthCont = new HBox();
		
		Label lWidth = new Label("Field width");
		TextField tfWidth = new TextField();
		tfWidth.setText(String.valueOf(this.COLUMN_WIDTH));
		
		widthCont.getChildren().addAll(lWidth, tfWidth);
		
		HBox cleanQueryCont = new HBox();
		
		Label lCleanQuery = new Label("clean query after query sent");
		CheckBox cbCleanQuery = new CheckBox();
		cbCleanQuery.setSelected(this.queueFieldClean);
		
		cleanQueryCont.getChildren().addAll(lCleanQuery, cbCleanQuery);
		
		HBox cleanOutCont = new HBox();
		
		Label lCleanOut = new Label("clean out box before query sent");
		CheckBox cbCleanOut = new CheckBox();
		cbCleanOut.setSelected(this.outFieldClean);
		
		cleanOutCont.getChildren().addAll(lCleanOut, cbCleanOut);
		
		HBox queryTimeCont = new HBox();
		
		Label lQueryTime = new Label("Show query time");
		CheckBox cbQueryTime = new CheckBox();
		cbQueryTime.setSelected(this.queryTime);
		
		queryTimeCont.getChildren().addAll(lQueryTime, cbQueryTime);

		HBox applyCont = new HBox();
		Button apply = new Button("Apply");
		
		apply.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent arg0) 
			{
				tfWidth.getText();
				try
				{
					COLUMN_WIDTH = Integer.parseInt(tfWidth.getText());
					queueFieldClean = cbCleanQuery.isSelected();
					outFieldClean = cbCleanOut.isSelected();
					queryTime = cbQueryTime.isSelected();
					
					settingsWindwo.close();
				}
				catch(NumberFormatException e)
				{
					Messages.showError("Incorrect \"Field width\" value");
				}
			}
		});
		
		applyCont.getChildren().add(apply);
		
		VBox settingsCont = new VBox();
		settingsCont.getChildren().addAll(widthCont, cleanQueryCont, cleanOutCont, queryTimeCont, applyCont);
		
		//----------------------------------------------------
		
		root.setCenter(settingsCont);
		settingsWindwo.setScene(scene);
		settingsWindwo.show();
	}
	
	private void openConnectionSettingsWindwo()
	{
		Stage settingsWindwo = new Stage();
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root,250,200);
		
		VBox cont = new VBox();
		
		Label lConnectionURL = new Label("Base address : ");
		TextArea taConnectionURL = new TextArea(this.sqlConnection.getURL());
		
		Label lUser = new Label("User : ");
		TextField eUser = new TextField(this.sqlConnection.getUser());

		Label lPassword = new Label("Password");
		PasswordField pfPassword = new PasswordField();
		pfPassword.setText(this.sqlConnection.getPassword());
		
		Button applySettins = new Button("Apply");
		
		applySettins.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent arg0) 
			{
				sqlConnection.setURL(taConnectionURL.getText());
				sqlConnection.setUser(eUser.getText());
				sqlConnection.setPassword(pfPassword.getText());
				
				settingsWindwo.close();
			}
			
		});
		
		cont.getChildren().addAll(lConnectionURL, taConnectionURL, lUser, eUser, lPassword, pfPassword, applySettins);
		
		root.setCenter(cont);
		
		settingsWindwo.setScene(scene);
		settingsWindwo.show();
	}

	private void getControlButtons()
	{
		HBox controlCont = new HBox();
		
		createConnection = new Button("Create connection");
		closeConnection = new Button("Close connection");
		sentRequest = new Button("Sent request");
		clear = new Button("Clear");
	
		createConnection.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent arg0) 
			{
				openSQLConnection();
			}
		});
		
		clear.setDisable(true);
		clear.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent arg0) 
			{
				outputBoxText.clear();
			}
			
		});
	
		closeConnection.setDisable(true);
		closeConnection.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent arg0) 
			{
				closeSQLConnection();
			}
		});
	
		sentRequest.setDisable(true);
		sentRequest.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent arg0) 
			{
				sendSQLRequest();
			}
		});
		
		controlCont.getChildren().addAll(createConnection, sentRequest, clear, closeConnection);
		this.mainPane.setBottom(controlCont);
	}
}
