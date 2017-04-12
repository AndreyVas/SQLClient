package application;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;

public class Messages 
{
	public static void showInfo(String body)
	{
		Alert alert = new Alert(AlertType.INFORMATION);
		
		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add("application/application.css");
		dialogPane.getStyleClass().add("window");
		
		alert.setTitle("Информация");
		alert.setHeaderText(null);
		alert.setContentText(body);

		alert.showAndWait();
	}
	
	public static void showError(String body)
	{
		Alert alert = new Alert(AlertType.ERROR);
		
		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add("application/application.css");
		dialogPane.getStyleClass().add("window");
		
		alert.setTitle("Ошибка");
		alert.setHeaderText(null);
		alert.setContentText(body);

		alert.show();
		
	}
}
