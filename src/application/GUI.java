package application;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GUI 
{
	private BorderPane mainPane;
	private TextArea outputBoxText;
	private TextArea queueField;
	
	private SQLConnection sqlConnection;
	private BaseInfo basesInfo;

	private boolean queueFieldClean;
	private boolean outFieldClean;
	private boolean isConnected; 
	
	//private int baseType;
	
	private MenuBar topMenu;
	private Button createConnection;
	private Button clear;
	private Button closeConnection;
	private Button sentRequest;
	private Button additionalQueryBox;
	private Button addQueryToCollection;
	
	// top menu items witch must be disabled or enabled after close/open connection
	
	MenuItem openConnection;
	MenuItem offConnection;
	MenuItem investigation;
	
	//----------------------------------------------------------------------------------
	
	QueryList queryList;
	CollectionGUI collectionGUI;

	//----------------------------------------------------------------------------------
	
	GUI(Scene s, BorderPane bp, SQLConnection sqlConnection)
	{
		this.mainPane = bp;

		this.outputBoxText = new TextArea();
		this.queueField = new TextArea();
		this.queueField.setWrapText(true);
		
		this.outputBoxText.setEditable(false);

		queueField.setFont(Font.font("Courier New"));
		outputBoxText.setFont(Font.font("Courier New"));
	
		//----------------------------
		// base connection settings
		
		queueFieldClean = false;
		outFieldClean = true;
		this.isConnected = false;

		//----------------------------

		queryList = new QueryList();
		collectionGUI = new CollectionGUI(this);

		//----------------------------
		
		VBox topCont = new VBox();		
		this.topMenu = createMainMenu();
		topCont.getChildren().addAll(topMenu, queueField);
		
		this.mainPane.setTop(topCont);
		this.mainPane.setCenter(outputBoxText);
		
		this.sqlConnection = sqlConnection;
		basesInfo = new BaseInfo(this.sqlConnection);

		this.getControlButtons();
		
		bp.setOnKeyPressed(new EventHandler<KeyEvent>() 
		{
            public void handle(final KeyEvent keyEvent) 
            {
            	if(keyEvent.isAltDown())
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
            				
            			case T:			// open table structure
            				
            				if(isConnected)
            					basesInfo.showBaseInfo();
            				else
            					Messages.showInfo("No connection with database");
            					
            				break;
            				
            			case C:			// open query collection
            				collectionGUI.show();
            				break; 		
            				
            			case N:			// open new query window
            				additionalQueryBox();
            				break;
            				
            			case A: 		// add query to collection
            				
            				if(!queueField.getText().equals(""))
            					collectionGUI.addQuery(queueField.getText());
            				else
            					Messages.showInfo("Query field is empty");
            				
            				break;	
            				
            			default:
            		}
            	}
            	else if(keyEvent.isShiftDown())
            	{
            		switch(keyEvent.getCode())
            		{
	            		case ENTER:
	            			
	            			if(isConnected)
	            				sendSQLRequest(queueField, outputBoxText, outFieldClean, queueFieldClean);
            				else
            					Messages.showInfo("No connection with database");
	            			
	            			break;
	            			
	            		default:
            		}
            	}
            }
        });
	}

	public void openSQLConnection()
	{
		if(sqlConnection.createConnection())
		{
			// bottom buttons 
			
			createConnection.setDisable(true);
			clear.setDisable(false);
			closeConnection.setDisable(false);
			sentRequest.setDisable(false);
			additionalQueryBox.setDisable(false);
			addQueryToCollection.setDisable(false);
			
			// top menu items
			
			openConnection.setDisable(true);
			offConnection.setDisable(false);
			investigation.setDisable(false);
			
			this.isConnected = true;
		}
	}
	
	public void closeSQLConnection()
	{
		sqlConnection.closeConnection();
		
		// bottom buttons
		
		createConnection.setDisable(false);
		clear.setDisable(true);
		closeConnection.setDisable(true);
		sentRequest.setDisable(true);
		additionalQueryBox.setDisable(true);
		addQueryToCollection.setDisable(true);
		
		// top menu items
		
		openConnection.setDisable(false);
		offConnection.setDisable(true);
		investigation.setDisable(false);
		
		this.isConnected = false;
	}
	
	public boolean isConnected()
	{
		return this.isConnected;
	}
	
	public void sendSQLRequest(TextArea queueField, TextArea outputBoxText, boolean outFieldClean, boolean queueFieldClean)
	{
		String q = queueField.getText();

		if(!q.equals(""))
		{
			if(outFieldClean)
				outputBoxText.clear();
			
			for (String send : q.split(";")) 
			{
	            send = send.trim();
	            
	            QueryGroup currentRequestGroup = queryList.getQueryGroup(send);
	            
	            if(currentRequestGroup.equals(QueryGroup.SELECT_GROUP))
	            {
	            	System.out.println("select group " + send);
	            	outputBoxText.setText(outputBoxText.getText() + this.sqlConnection.selectFormatted(send));
	            }
	            else if(currentRequestGroup.equals(QueryGroup.UPDATE_GROUP))
	            {
	            	System.out.println("update group " + send);
	            	this.update(send);
	            }
	            else if(currentRequestGroup.equals(QueryGroup.NOT_AUTORIZED))
	            {
	            	String message = "This type of the request not autorized : \n" + send;
	            	Messages.showInfo(message);
	            }
	            else if(currentRequestGroup.equals(QueryGroup.CUSTOM_SELECT_GROUP))
	            {
	            	send = send.substring(QueryType.SEL.getName().length(), send.length());
	            	outputBoxText.setText(outputBoxText.getText() + this.sqlConnection.selectFormatted(send));
	            }
	            else if(currentRequestGroup.equals(QueryGroup.CUSTOM_UPDATE_GROUP))
	            {
	            	send = send.substring(QueryType.UPD.getName().length(), send.length());
	            	this.update(send);
	            }
	            else 
	            {
	            	String message = "Unknown type of the request : \n" + send;
	            	Messages.showInfo(message);
	            }
	        }

			 if(queueFieldClean)
			    	queueField.clear();
		}
		else
		{
			outputBoxText.setText("Queue field is empty");
		}
	}
	
	private void update(String q)
	{
		long beforeRequest = System.currentTimeMillis();
		int result = sqlConnection.update(q);
		long afterRequest = System.currentTimeMillis();
		
		if(sqlConnection.getQueueTime())
		{
			outputBoxText.setText(outputBoxText.getText() + "\nRequest execution time : " 
					+ ((afterRequest - beforeRequest) / 1000.0) + " s\n\n");
		}
		
		String text = "";
		text += '\n';
		text += result + " records was updated";
		text += '\n';
		
	    outputBoxText.setText(outputBoxText.getText() + text);
		
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
		MenuItem authorizedQueries = new MenuItem("Authorized queries");
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
		
		authorizedQueries.setOnAction(new EventHandler<ActionEvent>() 
		{
            public void handle(ActionEvent t) 
            {
            	openAuthorizedQueriesWindow();
            }
        });    
		
		editSettings.setOnAction(new EventHandler<ActionEvent>() 
		{
            public void handle(ActionEvent t) 
            {
            	openEditSettingsWindow();
            }
        });    
		
		edit.getItems().addAll(cleanQueueField, cleanOutField, authorizedQueries, editSettings);

		//--------------------------------------------
		
		Menu about = new Menu("About");
		MenuItem functionKeys = new MenuItem("Function keys");
		MenuItem notStandardQuery = new MenuItem("Not standard queries");
		
		functionKeys.setOnAction(new EventHandler<ActionEvent>() 
		{
            public void handle(ActionEvent t) 
            {
            	String s = "ALT + O - Open SQL connection \n"
            			+ "ALT + Q - End SQL connection \n"
						+ "ALT + T - Open table structure window \n"
            			+ "ALT + C - Open queries collection window \n"
						+ "ALT + N - Open additional query window \n"
						+ "ALT + A - Add query to collection \n"
            			+ "ALT + D - Delete text \n\n"
            			
            			+ "SHIFT + ENTER - Dend request \n\n"
            			
            			+ "In Queries collection window : \n\n"
            			+ "CTRL + L - Load collection \n"
            			+ "CTRL + S - Save collection \n";
            	
            	Messages.showInfo(s);
            }
        });   
		
		notStandardQuery.setOnAction(new EventHandler<ActionEvent>() 
		{
            public void handle(ActionEvent t) 
            {
            	String s = "Info will be addes soon...";
            	
            	Messages.showInfo(s);
            }
        });   
		
		about.getItems().addAll(functionKeys, notStandardQuery);
		
		//--------------------------------------------
		
		Menu tools = new Menu("Tools");
		investigation = new MenuItem("Tables structure");
		investigation.setDisable(true);
		
		MenuItem queryCollection = new MenuItem("Queries collection");
		
		investigation.setOnAction(new EventHandler<ActionEvent>() 
		{
            public void handle(ActionEvent t) 
            {
            	basesInfo.showBaseInfo();
            }
        });   
		
		queryCollection.setOnAction(new EventHandler<ActionEvent>() 
		{
            public void handle(ActionEvent t) 
            {
            	collectionGUI.show();
            }
        });  
		
		tools.getItems().addAll(investigation, queryCollection);
		
		//--------------------------------------------

		topMenu.getMenus().addAll(connection, edit, tools, about);
	
		return topMenu;
	}

	private void openAuthorizedQueriesWindow()
	{
		Stage settingsWindow = new Stage();
		settingsWindow.setTitle("Authorized queries");
		settingsWindow.setResizable(false);
		settingsWindow.getIcons().add(new Image(Records.IMG_MAIN_ICON));
		
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root);
		
		scene.getStylesheets().add("application/application.css");
		
		//----------------------------------------------------

		Label standardQueryLabel = new Label("Select allowed queries");
		
		FlowPane standardQueryBox = new FlowPane();
		FlowPane customQueryBox = new FlowPane();

		for(QueriesListItem qli : queryList.getQueriesList())
		{
			CheckBox cb = new CheckBox(qli.getName());
			cb.setSelected(qli.getGrant());
			
			if(qli.isStandard())
				standardQueryBox.getChildren().add(cb);
			else
				customQueryBox.getChildren().add(cb);

			
		}
		
		//----------------------------------------------------
		
		Label notStandardQueryLabel = new Label("Select not standart allowed queries");

		//----------------------------------------------------
		
		HBox controlCont = new HBox();
		
		Button apply = new Button("Apply");
		
		apply.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent arg0) 
			{
				ObservableList<Node> nodes = standardQueryBox.getChildren();
				nodes.addAll(customQueryBox.getChildren());
				
				for(Node n : nodes)
				{
					CheckBox cb = (CheckBox)n;

					queryList.setGrant(cb.getText(), cb.isSelected());
				}

				settingsWindow.close();
			}
		});
		
		controlCont.getChildren().add(apply);
		
		//----------------------------------------------------
		
		VBox mainCont = new VBox();
		mainCont.getStyleClass().add("AuthorizedQueriesWindow");
		mainCont.getChildren().addAll(standardQueryLabel, standardQueryBox, 
				notStandardQueryLabel, customQueryBox, controlCont);

		//----------------------------------------------------
		
		
		//----------------------------------------------------

		root.setCenter(mainCont);
		settingsWindow.setScene(scene);
		settingsWindow.show();
	}

	private void openEditSettingsWindow()
	{
		Stage settingsWindow = new Stage();
		settingsWindow.setResizable(false);
		settingsWindow.setTitle("Output settings");
		settingsWindow.getIcons().add(new Image(Records.IMG_MAIN_ICON));
		
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root);
		
		scene.getStylesheets().add("application/application.css");
		root.getStyleClass().add("editSettings");
		
		//----------------------------------------------------
		
		HBox widthCont = new HBox();
		
		Label lWidth = new Label("Field width");
		TextField tfWidth = new TextField();
		tfWidth.setText(String.valueOf(this.sqlConnection.getColumnWidth()));
		
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
		cbQueryTime.setSelected(this.sqlConnection.getQueueTime());
		
		queryTimeCont.getChildren().addAll(lQueryTime, cbQueryTime);

		HBox applyCont = new HBox();
		applyCont.getStyleClass().add("controlCont");
		Button apply = new Button("Apply");
		
		apply.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent arg0) 
			{
				tfWidth.getText();
				try
				{
					sqlConnection.setColumnWidth(Integer.parseInt(tfWidth.getText()));
					queueFieldClean = cbCleanQuery.isSelected();
					outFieldClean = cbCleanOut.isSelected();
					sqlConnection.setQueueTime(cbQueryTime.isSelected());
					
					settingsWindow.close();
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
		settingsWindow.setScene(scene);
		settingsWindow.show();
	}
	
	private void openConnectionSettingsWindwo()
	{
		Stage settingsWindow = new Stage();
		settingsWindow.setResizable(false);
		settingsWindow.getIcons().add(new Image(Records.IMG_MAIN_ICON));
		settingsWindow.setTitle("Connection settings");
		
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root,480,250);
		scene.getStylesheets().add("application/application.css");
		root.getStyleClass().add("connectionSettingsWindow");
		//----------------------
		// Base type choice
		
		VBox choiceBaseCont = new VBox();
		
		final ToggleGroup group = new ToggleGroup();

		RadioButton mySQL = new RadioButton();
		mySQL.setToggleGroup(group);
		mySQL.setGraphic(new ImageView(Records.IMG_MYSQL_ICON));

		RadioButton msSQL = new RadioButton();
		msSQL.setToggleGroup(group);
		msSQL.setGraphic(new ImageView(Records.IMG_MSSQL_ICON));
		
		if(sqlConnection.getBaseType() == sqlConnection.MYSQL)
			mySQL.setSelected(true);
		else if(sqlConnection.getBaseType() == sqlConnection.MSSQLSERVER)
			msSQL.setSelected(true);
		else
		{
			sqlConnection.setBaseType(sqlConnection.MYSQL);
			mySQL.setSelected(true);
		}
	
		
		choiceBaseCont.getChildren().addAll(mySQL, msSQL);
		
		//----------------------
		// connection settings
		
		Label lConnectionURL = new Label("Base address : ");
		TextArea taConnectionURL = new TextArea(this.sqlConnection.getURL());
		taConnectionURL.setWrapText(true);
		
		//----------------------
		
		Label lUser = new Label("User : ");
		TextField eUser = new TextField(this.sqlConnection.getUser());
		
		//----------------------
		
		Label lPassword = new Label("Password : ");
		PasswordField pfPassword = new PasswordField();
		pfPassword.setText(this.sqlConnection.getPassword());
		
		//----------------------
		
		HBox controlCont = new HBox();
		
		Button applySettins = new Button("Apply");
		
		applySettins.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent arg0) 
			{
				sqlConnection.setURL(taConnectionURL.getText());
				sqlConnection.setUser(eUser.getText());
				sqlConnection.setPassword(pfPassword.getText());
				
				if(mySQL.isSelected())
					sqlConnection.setBaseType(sqlConnection.MYSQL);
				else if(msSQL.isSelected())
					sqlConnection.setBaseType(sqlConnection.MSSQLSERVER);
				
				settingsWindow.close();
			}
		});
		
		controlCont.getChildren().add(applySettins);
		
		//----------------------
		
		VBox baseSettingsCont = new VBox();
		baseSettingsCont.getChildren().addAll(lConnectionURL, taConnectionURL, lUser, eUser, lPassword, pfPassword, controlCont);
		
		root.setLeft(choiceBaseCont);
		root.setRight(baseSettingsCont);
		
		settingsWindow.setScene(scene);
		settingsWindow.show();
	}

	private void getControlButtons()
	{
		HBox controlCont = new HBox();
		
		createConnection = new Button();
		createConnection.setGraphic(new ImageView(Records.IMG_CREATE_CONNECTION));
		createConnection.setTooltip(new Tooltip(Records.TOOLTIP_CREATE_CONNECTION));
		
		closeConnection = new Button();
		closeConnection.setGraphic(new ImageView(Records.IMG_CLOSE_CONNECTION));
		closeConnection.setTooltip(new Tooltip(Records.TOOLTIP_CLOSE_CONNECTION));
		
		sentRequest = new Button();
		sentRequest.setGraphic(new ImageView(Records.IMG_SEND_QUERY));
		sentRequest.setTooltip(new Tooltip(Records.TOOLTIP_SEND_QUERY));
		
		clear = new Button();
		clear.setGraphic(new ImageView(Records.IMG_CLEAR_BOXES));
		clear.setTooltip(new Tooltip(Records.TOOLTIP_CLEAR_BOXES));
		
		additionalQueryBox = new Button();
		additionalQueryBox.setGraphic(new ImageView(Records.IMG_ADDITIONAL_QUERY_WINDOW));
		additionalQueryBox.setTooltip(new Tooltip(Records.TOOLTIP_ADDITIONAL_QUERY_WINDOW));
		
		addQueryToCollection = new Button();
		addQueryToCollection.setGraphic(new ImageView(Records.IMG_ADD_TO_COLLECTION));
		addQueryToCollection.setTooltip(new Tooltip(Records.TOOLTIP_ADD_TO_COLLECTION));
	
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
				sendSQLRequest(queueField, outputBoxText, outFieldClean, queueFieldClean);
			}
		});
		
		additionalQueryBox.setDisable(true);
		additionalQueryBox.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent arg0) 
			{
				additionalQueryBox();
			}
		});
		
		addQueryToCollection.setDisable(true);
		addQueryToCollection.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent arg0) 
			{
				if(!queueField.getText().equals(""))
					collectionGUI.addQuery(queueField.getText());
				else
					Messages.showInfo("Query field is empty");
			}
		});
		
		controlCont.getChildren().addAll(createConnection, closeConnection, sentRequest, addQueryToCollection,
				clear, additionalQueryBox);
		
		this.mainPane.setBottom(controlCont);
	}
	
	public void additionalQueryBox()
	{
		Stage settingsWindow = new Stage();	
		settingsWindow.setTitle("");
		settingsWindow.getIcons().add(new Image(Records.IMG_MAIN_ICON));
		settingsWindow.initOwner(this.mainPane.getScene().getWindow());
		
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root);
		scene.getStylesheets().add("application/application.css");
		
		//------------------------------------------------------
		
		TextArea additionalQueryField = new TextArea();
		additionalQueryField.setWrapText(true);
		
		//------------------------------------------------------
		
		VBox controlsCont = new VBox();
		controlsCont.getStyleClass().add("controls");
		
		//-------------------------
		
		Button send = new Button();
		send.setGraphic(new ImageView(Records.IMG_SEND_QUERY));
		send.setTooltip(new Tooltip(Records.TOOLTIP_SEND_QUERY));

		send.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent arg0) 
			{
				sendSQLRequest(additionalQueryField, outputBoxText, outFieldClean, queueFieldClean);
			}
		});

		//-------------------------
		
		Button toCollection = new Button();
		toCollection.setGraphic(new ImageView(Records.IMG_ADD_TO_COLLECTION));
		toCollection.setTooltip(new Tooltip(Records.TOOLTIP_ADD_TO_COLLECTION));
		
		toCollection.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			
			
			@Override
			public void handle(MouseEvent arg0) 
			{
				if(!additionalQueryField.getText().equals(""))
				{
					collectionGUI.addQuery(additionalQueryField.getText());
				}
				else
					Messages.showInfo("Query field is empty");
			}
		});
		
		//-------------------------
		
		Button toMainBox = new Button();
		toMainBox.setGraphic(new ImageView(Records.IMG_MOVE_QUERY_TO_MAIN_BOX));
		toMainBox.setTooltip(new Tooltip(Records.TOOLTIP_MOVE_QUERY_TO_MAIN_BOX));
		
		toMainBox.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent arg0) 
			{
				queryToMainBox(additionalQueryField.getText());
			}
		});
		
		//-------------------------
		
		Button clear = new Button();
		clear.setGraphic(new ImageView(Records.IMG_CLEAR_BOXES));
		clear.setTooltip(new Tooltip(Records.TOOLTIP_CLEAR_BOXES));
		
		clear.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent arg0) 
			{
				additionalQueryField.clear();
			}
		});
		
		controlsCont.getChildren().addAll(send, toMainBox, toCollection, clear);
		
		//------------------------------------------------------

		root.setOnKeyPressed(new EventHandler<KeyEvent>() 
		{
            public void handle(final KeyEvent keyEvent) 
            {
            	if(keyEvent.isAltDown())
            	{
            		switch(keyEvent.getCode())
            		{
            			case D:			// delete data from queue and out boxes
            				
            				if(additionalQueryField.getText().equals(""))
            					outputBoxText.setText("");
            				else
            					additionalQueryField.setText("");
            				
            				break;
	
            			case T:			// open table structure
            				
            				if(isConnected)
            					basesInfo.showBaseInfo();
            				else
            					Messages.showInfo("No connection with database");
            					
            				break;
            				
            			case C:			// open query collection
            				collectionGUI.show();
            				break; 		
            				
            			case N:			// open new query window
            				
            				additionalQueryBox();
            				
            				break;
            				
            			case A: 		// add query to collection
            				
            				if(!additionalQueryField.getText().equals(""))
            					collectionGUI.addQuery(additionalQueryField.getText());
            				else
            					Messages.showInfo("Query field is empty");
            				
            				break;	
            				
            			default:
            				
            		}
            	}
            	else if(keyEvent.isShiftDown())
            	{
            		switch(keyEvent.getCode())
            		{
	            		case ENTER:

	            			if(isConnected)
	            				sendSQLRequest(additionalQueryField, outputBoxText, outFieldClean, queueFieldClean);
            				else
            					Messages.showInfo("No connection with database");
            					
            				
	            			break;
	            			
	            		default:
            		}
            	}
            }
        });
		
		//------------------------------------------------------

		root.setCenter(additionalQueryField);
		root.setLeft(controlsCont);
		
		settingsWindow.setScene(scene);
		settingsWindow.show();
	}

	public void exit()
	{
		if(!this.collectionGUI.isSaved())
		{
			if(Messages.showAndWaitChoice("Queries collection not saved. Do you want to save it ?", "Yes", "No"	))
			{
				this.collectionGUI.save();
			}
		}
		
		this.collectionGUI.exit();
		this.basesInfo.exit();
	}

	public void queryToMainBox(String query)
	{
		this.queueField.setText(query);
		
		Stage s = (Stage)this.mainPane.getScene().getWindow();
		s.toFront();
	}
}
