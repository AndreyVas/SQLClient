package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class CollectionGUI 
{
	private GUI mainGUI;
	private LinkedList<Collection> collections;
	private boolean saved;
	
	private final String TAB_PANE_NAME_TEXT = "Query collection";
	private final String ADD_QUERY_TEXT = "Add query";
	private final String ADD_COLLECTION_TEXT = "Add collection";
	private final String XML_NAME = "collections";
	
	private BorderPane rootPane;
	
	MenuBar topMenu;
	String savePant;
	Stage window;
	Scene scene;
	
	Stage outWindow;
	
	CollectionGUI(GUI gui)
	{
		this.mainGUI = gui;
		
		collections = new LinkedList<Collection>();
		savePant = null;
		this.saved = true;
		
		window = new Stage();
		rootPane = new BorderPane();
		scene = new Scene(rootPane, 800, 600);
		scene.getStylesheets().add("application/application.css");
		
		outWindow = new Stage();
		
		rootPane.setOnKeyPressed(new EventHandler<KeyEvent>() 
		{
            public void handle(final KeyEvent keyEvent) 
            {
            	if(keyEvent.isControlDown())
            	{
            		switch(keyEvent.getCode())
            		{
            			case L:			// load collection
            				load();
            				break;
            				
            			case S:			// save collection
            				save();
            				break;
            			
            			default:
            		}
            	}
            }
		});
		
	}
	
	public void create()
	{
		
	}
	
	public void load()
	{
		//-----------------------------------------------------
		// save current collection if exist
		
		if(collections.size() != 0 )
		{
			if(this.saved == false)
			{
				//-----------------------------------------------------
				
				if(Messages.showAndWaitChoice("Current colletions not seved. Save it ?", "Yes", "No"))
				{
					save();
				}
			}
		}
		
		//-----------------------------------------------------
		// select a file

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		
		File file = fileChooser.showOpenDialog(this.window);
		
		if (file != null) 
	    {
			collections.clear();
			
			
	    	savePant = file.getPath();
	    	parseXML(savePant);
	    	
	    	rootPane.setCenter(createTabPane());
	    }
		
		//-----------------------------------------------------
	}

	private void parseXML(String savePath)
	{
		try 
	    {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
		    
		    DocumentBuilder db = dbf.newDocumentBuilder(); 
		    Document doc = db.parse(savePath);

		    DOMSource source = new DOMSource(doc); 

		    DOMResult result = new DOMResult();
			
			TransformerFactory transFactory = TransformerFactory.newInstance(); 
		    Transformer transformer = transFactory.newTransformer(); 

		    transformer.transform(source, result); 
		    Node node = result.getNode().getFirstChild();

		    NodeList collections = node.getChildNodes();

		    Collection c = null;
		    
		    for(int i = 0; i < collections.getLength(); i++)
		    {
		    	NodeList collection = collections.item(i).getChildNodes();
		    	
		    	for(int j = 0; j < collection.getLength(); j++)
		    	{
		    		switch(collection.item(j).getNodeName())
			    	{
			    		case Collection.XML_NAME:
			    			
			    			c = new Collection(collection.item(j).getTextContent());
			    		break;
			    		
			    		case Collection.XML_CONTENT:
			    			
			    			if(c != null)
			    			{
			    				c.ParseXML(collection.item(j).getChildNodes());
			    				
			    				this.collections.add(c);
			    			}
			    			
			    		break;	
			    	}
		    	}
		    }
	    }
		catch (ParserConfigurationException e) 
	    {
			System.out.println("Error " + e);
		} 
	    catch (FileNotFoundException e) 
		{
	    	System.out.println("Error : Файл с сохранёнными заданиями не найден, возможно это первый запуск");
		}
	    catch (TransformerException e) 
	    {
	    	System.out.println("Error " + e);
		}
		catch(IOException e)
		{
			System.out.println("Error " + e);
		}
		catch(Exception e)
		{
			System.out.println("Error " + e);
			
			e.printStackTrace();
		}
		

	}
	
	public void saveAs()
	{
		FileChooser fileChooser = new FileChooser();
	    fileChooser.setTitle("Save Image");

	    File file = fileChooser.showSaveDialog(window);
	    
	    if (file != null) 
	    {
	    	savePant = file.getPath();
	    	
	    	save();
	    }
	}
	
	public void save()
	{
		if(savePant != null)
		{
			try
			{
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); 
			    DocumentBuilder db = dbf.newDocumentBuilder();
			    Document doc = db.newDocument();
			    
			    //--------------------------------------------------------------------
			    
			    Element root = doc.createElement(XML_NAME); 

			    for(Collection c : collections)
			    {
			    	root.appendChild(c.getXML(doc));
			    }
			    
			    doc.appendChild(root);
			    
			    //--------------------------------------------------------------------
			    
			    DOMSource source = new DOMSource(doc); 
			    StreamResult result;

				result = new StreamResult(new FileOutputStream(this.savePant));
				
				TransformerFactory transFactory = TransformerFactory.newInstance(); 
			    Transformer transformer = transFactory.newTransformer(); 
			    transformer.transform(source, result); 
			    
			    result.getOutputStream().close();
			    
			    this.saved = true;
			    
			    Messages.showInfo("Collection was seved to file : \n" + savePant);
			}
			catch (ParserConfigurationException e) 
		    {
				e.printStackTrace();
			} 
		    catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			}
		    catch (TransformerException e) 
		    {
				e.printStackTrace();
			} 
		    catch(IOException e)
		    {
		    	e.printStackTrace();
		    }
		}
		else
		{
			saveAs();
		}
	}
	
	public boolean isSaved()
	{
		return this.saved;
	}
	
	public void exit()
	{
		if(this.window != null && this.window.isShowing())
		{
			this.window.close();
		}
	}
	
	public boolean isEmpty()
	{
		if(collections.size() == 0)
			return true;
		else
			return false;
	}
	
	public boolean isCollectionNameUnique(String name)
	{
		for(Collection c : collections)
		{
			if(c.getName().equals(name))
			{
				return false;
			}
		}
		
		return true;
	}
	
	public void show()
	{
		if(!window.isShowing())
		{

			window.setTitle(TAB_PANE_NAME_TEXT);
			window.getIcons().add(new Image(Records.IMG_MAIN_ICON));

			// show window
			
			rootPane.setTop(createMainMenu());
			rootPane.setCenter(createTabPane());
			window.setScene(scene);
			window.show();
			
			saved = true;
		}
		else
		{
			window.toFront();
		}
		
	}
	
	private TabPane createTabPane()
	{
		//---------------------------------------------------------------------------
		// create collections tabs
		
		TabPane collectionsPane = new TabPane();

		for(Collection c : collections)
		{
			collectionsPane.getTabs().add(createCollectionTab(c));
		}
		
		//---------------------------------------------------------------------------
		// add new collection tab
		
		Tab addTab = new Tab();
		addTab.getStyleClass().add("tabName");
		
		addTab.setText(ADD_COLLECTION_TEXT);
		addTab.setClosable(false);
		
		VBox addCollectinCont = new VBox();
		addCollectinCont.getStyleClass().add("addCollection");
		
		Label lAddCollectionName = new Label("Write the name of the new collection");
		TextField bAddCollectionName = new TextField();
		Button addCollection = new Button(ADD_COLLECTION_TEXT);
		
		addCollection.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent arg0) 
			{
				String name = bAddCollectionName.getText();
				
				if(!name.equals(""))
					if(isCollectionNameUnique(name))
					{
						Collection c = new Collection(name);
	
						collections.add(c);
						
						Tab t = createCollectionTab(c);
						
						collectionsPane.getTabs().add(collectionsPane.getTabs().size() - 1, t);
						collectionsPane.getSelectionModel().select(t);
						
						saved = false;
					}
					else
					{
						Messages.showInfo("The collection name must be unique");
					}
				else
				{
					Messages.showInfo("Collection name can not be empty");
				}
			}	
		});
		
		addCollectinCont.getChildren().addAll(lAddCollectionName, bAddCollectionName, addCollection);
		
		addTab.setContent(addCollectinCont);
		
		collectionsPane.getTabs().add(addTab);
		
		//---------------------------------------------------------------------------
		
		return collectionsPane;		
	}
	
	public MenuBar createMainMenu()
	{
		topMenu = new MenuBar();
		
		Menu file = new Menu("File");

		MenuItem load = new MenuItem("Load");
		MenuItem save = new MenuItem("Save");
		MenuItem saveAs = new MenuItem("Save as");

		load.setOnAction(new EventHandler<ActionEvent>() 
		{
            public void handle(ActionEvent t) 
            {
            	load();
            }
        });     
		
		save.setOnAction(new EventHandler<ActionEvent>() 
		{
            public void handle(ActionEvent t) 
            {
            	save();
            }
        }); 
		
		saveAs.setOnAction(new EventHandler<ActionEvent>() 
		{
            public void handle(ActionEvent t) 
            {
            	saveAs();
            }
        }); 

		file.getItems().addAll(load, save, saveAs);

		//--------------------------------------------

		topMenu.getMenus().addAll(file);
	
		return topMenu;
	}
	
	private Tab createCollectionTab(Collection c)
	{
		Tab collectionTab = new Tab();
		collectionTab.getStyleClass().add("tabName");
		
		collectionTab.setText(c.getName());
		//collectionTab.setClosable(false);
		
		collectionTab.setOnCloseRequest(new EventHandler<Event>()
		{
		    @Override
		    public void handle(Event arg0) 
		    {
		        if(Messages.showAndWaitChoice("Delete collection " + c.getName() + " with all queries ? ", "yes", "no"))
		        {
		        	collections.remove(c);
		        	saved = false;
		        }
		        else
		        {
		        	arg0.consume();
		        }
		    }
		});
		
		//----------------------------------------------------------------------
		// create tab content
		
		BorderPane contentPane = new BorderPane();

		// right panel with info about selected table
		
		Label queryInfo= new Label("Select a query from the left pane");
		
		BorderPane rightPane = new BorderPane();
		rightPane.setCenter(queryInfo);
		
		//-------------------------------------
		// left panel with tables names
		
		ScrollPane leftPane = new ScrollPane();
		leftPane.setHbarPolicy(ScrollBarPolicy.NEVER);

		VBox queryNames = new VBox();
		queryNames.getStyleClass().add("leftPannel");

		for(CollectionItem item : c.getItems())
		{
			Label l = new Label(item.getName());
			
			l.setOnMouseClicked(new EventHandler<MouseEvent>() 
			{
				@Override
				public void handle(MouseEvent e) 
				{
					queryInfo(rightPane, l, item, c);
					//queryInfo.setText( " \n" + c.getName() + " -> " + item.getName() + "\n");
				}
			});
			
			queryNames.getChildren().add(l);
		}
		
		//-------------------------------------
		// add new query item button to left pane
		
		Label addItem = new Label(ADD_QUERY_TEXT);
		
		addItem.setOnMouseClicked(new EventHandler<MouseEvent>() 
		{
			@Override
			public void handle(MouseEvent e) 
			{
				String name = inputNameDilog("Create new query", "Please enter new query name");
				
				if(name != null && !name.equals(""))
				{
					if(c.isItemNameUnique(name))
					{
						CollectionItem item = new CollectionItem(name);
						c.addItem(item);
						
						Label l = new Label(item.getName());
						
						l.setOnMouseClicked(new EventHandler<MouseEvent>() 
						{
							@Override
							public void handle(MouseEvent e) 
							{
								queryInfo(rightPane, l, item, c);
							}
						});
						
						queryNames.getChildren().add(queryNames.getChildren().size() - 1, l);
						
						saved = false;
					}
					else
					{
						Messages.showError("This name is already in use");
					}
				}
				else
				{
					System.out.println("caseled");
				}
			}
		});
		
		queryNames.getChildren().add(addItem);
		
		leftPane.setContent(queryNames);

		//----------------------------------------------------------------------
		
		contentPane.setLeft(leftPane);
		contentPane.setCenter(rightPane);
		
		collectionTab.setContent(contentPane);
		
		return collectionTab;
	}
	
	private String inputNameDilog(String title, String infoRequest)
	{
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle(title);
		dialog.setHeaderText(infoRequest);
		//dialog.setContentText("Please enter your name:");
		
		Optional<String> result = dialog.showAndWait();
		
		if (result.isPresent())
		{
			return result.get();
		}
		else
		{
			return null;
		}
	}
	
	private void queryInfo(BorderPane rightPane, Label owner, CollectionItem ci, Collection c)
	{
		Label queryTitle = new Label(c.getName() + " -> " + ci.getName());
		queryTitle.getStyleClass().add("queryTitle");
		
		rightPane.setTop(queryTitle);
		
		TextArea query = new TextArea(ci.getQueue());
		query.getStyleClass().add("queryTextArea");
		query.setWrapText(true);
		
		TextArea description = new TextArea(ci.getDescription());
		description.getStyleClass().add("queryTextArea");
		description.setWrapText(true);
		
		rightPane.setCenter(query);
		rightPane.setBottom(description);
		
		//----------------------------------------
		
		VBox controls = new VBox();
		
		Button save = new Button();
		save.setGraphic(new ImageView(Records.IMG_SAVE_QUERY));
		save.setTooltip(new Tooltip(Records.TOOLTIP_SAVE_QUERY));
	
		save.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent arg0) 
			{
				ci.setQueue(query.getText());
				ci.setDescription(description.getText());
				
				saved = false;
				
				save();
			}
		});
		
		//------------------------
		
		Button send = new Button();
		send.setGraphic(new ImageView(Records.IMG_SEND_QUERY));
		send.setTooltip(new Tooltip(Records.TOOLTIP_SEND_QUERY));
		
		send.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent arg0) 
			{
				openOutWindow(query);
			}
		});
		
		//------------------------
		
		Button toMainBox = new Button();
		toMainBox.setGraphic(new ImageView(Records.IMG_MOVE_QUERY_TO_MAIN_BOX));
		toMainBox.setTooltip(new Tooltip(Records.TOOLTIP_MOVE_QUERY_TO_MAIN_BOX));
		
		toMainBox.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent arg0) 
			{
				mainGUI.queryToMainBox(query.getText());
			}
		});
		
		//------------------------
		
		Button delete = new Button();
		delete.setGraphic(new ImageView(Records.IMG_DELETE_QUERY));
		delete.setTooltip(new Tooltip(Records.TOOLTIP_DELETE_QUERY));
		
		delete.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent arg0) 
			{
				c.getItems().remove(ci);
				
				VBox box = (VBox)owner.getParent();
				box.getChildren().remove(owner);
				
				rightPane.setTop(null);
				rightPane.setCenter(new Label("Select a query from the left pane"));
				rightPane.setBottom(null);
				rightPane.setRight(null);
				
				saved = false;
			}
		});
		
		//------------------------
		
		controls.getChildren().addAll(save, send, toMainBox, delete);
		
		//----------------------------------------
		
		rightPane.setRight(controls);
	}

	private LinkedList<String> getCollectionsNames()
	{
		LinkedList<String> list = new LinkedList<String>();
		
		for(Collection c : this.collections)
		{
			list.add(c.getName());
		}
		
		return list;
	}
	
	private Collection getCollectionByName(String name)
	{
		for(Collection c : this.collections)
		{
			if(c.getName().equals(name))
			{
				System.out.println(c.getName() + "   " + name + "  " + c.getName().equals(name));
				return c;
			}
		}
		
		return null;
	}
	
	public void addQuery(String query)
	{
		String collectionName = "";
		String queryName = "";
		boolean proceed = true;
		Collection col;

		if(this.collections.size() == 0)
		{
			if(Messages.showAndWaitChoice("There are no open queries collections.\n"
					+ "Do you want to load an existing collection ? ", 
					"Yes", "No"))	
			{
				// load collections
				
				load();

				if(this.collections.size() == 0)
					if(Messages.showAndWaitChoice("Loaded collection is empty. Create new ?", 
							"Yes", "no"))	
					{
						proceed = true;
					}
					else
					{
						proceed = false;
					}
				else
					proceed = true;
			}
			else
			{
				proceed = true;
			}
		}

		if(this.collections.size() != 0 && proceed == true)
		{
			// if collections list not empty
			
			String createNew = "Create new";
			
			LinkedList<String> list = getCollectionsNames();
			list.add(createNew);
			
			collectionName = Messages.showAndWaitChoiceList(list, "Select collection");
			
			if(collectionName != null)
			{
				if(collectionName.equals(createNew))
				{
					collectionName = "";
				}
				
				proceed = true;
			}
			else
			{
				proceed = false;
			}
		}

		// if collection not loaded or select item - create new collection
		
		if(collectionName != null && collectionName.equals("") && proceed == true)
		{
			// create new collection
			
			while(proceed)
			{
				collectionName = inputNameDilog("Create collection", "Write the name of the new collection");
				
				if(collectionName != null)
				{
					if(this.isCollectionNameUnique(collectionName))
					{
						// create collection
						
						Collection c = new Collection(collectionName);
						
						collections.add(c);
						break;
					}
					else
					{
						Messages.showError("Collection name ust be unique. Please write it again");
					}
				}
				else
				{
					proceed = false;
				}
			}
		}
		
		// get selected collection
		col = this.getCollectionByName(collectionName);

		// select or create query
		if(proceed && col != null)
		{
			if(col.getItems().size() != 0)
			{
				String createNew = "Create new";
				
				LinkedList<String> list = col.getQueriesNames();
				list.add(createNew);
				
				queryName = Messages.showAndWaitChoiceList(list, "Select query");
				
				if(queryName != null)
				{
					if(queryName.equals(createNew))
					{
						queryName = "";
					}
					
					proceed = true;
				}
				else
				{
					proceed = false;
				}
			}
		}
		
		if(proceed == true && queryName.equals(""))
		{
			// create new collection
			
			while(proceed)
			{
				queryName = inputNameDilog("Create query", "Write the name of the new query");
				
				if(queryName != null)
				{
					if(col.isItemNameUnique(queryName))
					{
						// create query
						
						col.addItem(new CollectionItem(queryName, query, ""));
						proceed = true;
						break;
					}
					else
					{
						Messages.showError("Query name ust be unique in collection. Please write it again");
					}
				}
				else
				{
					proceed = false;
				}
			}
		}
		
		// open collection window
		
		if(proceed == true)
		{
			if(this.window.isShowing())
			{
				this.rootPane.setCenter(createTabPane());
			}
			else
			{
				this.show();
			}
			
			// select added collection and query
			
			TabPane tabPane = (TabPane)this.rootPane.getCenter();
			
			for(Tab t : tabPane.getTabs())
			{
				if(t.getText().equals(collectionName))
				{
					tabPane.getSelectionModel().select(t);
				}
			}
		}

		if(proceed)
			this.saved = false;
	}

	private void openOutWindow(TextArea queryBox)
	{
		if(this.mainGUI.isConnected())
		{
			TextArea outBox = new TextArea();
			
			if(!this.outWindow.isShowing())
			{
				BorderPane pane = new BorderPane();
				Scene scene = new Scene(pane, 800, 600);
				scene.getStylesheets().add("application/application.css");
				
				pane.setCenter(outBox);
				
				this.outWindow.setScene(scene);
				this.outWindow.show();
				
				this.mainGUI.sendSQLRequest(queryBox, outBox, false, false);	
			}
			else
			{
				outBox.clear();
				this.mainGUI.sendSQLRequest(queryBox, outBox, false, false);
				
				this.outWindow.toFront();
			}
		}
		else
		{
			Messages.showInfo("No connection with database");
		}
	}
}
