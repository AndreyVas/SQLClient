package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;

public class Main extends Application 
{
	SQLConnection con;
	GUI gui;
	
	@Override
	public void start(Stage primaryStage) 
	{
		
		String query = "SHOW TABLES FROM tek";
		
		try 
		{
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,600,600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			
			
			con = new SQLConnection();
			gui = new GUI(scene, root, con);

			primaryStage.setScene(scene);
			primaryStage.show();
			
			primaryStage.getIcons().add(new Image("icon.png"));
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
			Messages.showError(e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
