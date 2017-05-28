package application;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.DialogPane;

public class Messages 
{
	public static void showInfo(String body)
	{
		Alert alert = new Alert(AlertType.INFORMATION);
		
		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add("application/application.css");
		dialogPane.getStyleClass().add("window");
		
		// set icon to dialog
		Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().add(new Image(Records.IMG_MAIN_ICON));
		
		alert.setTitle("Information");
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
		
		// set icon to dialog
		Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().add(new Image(Records.IMG_MAIN_ICON));

		alert.setTitle("Error");
		alert.setHeaderText(null);
		alert.setContentText(body);

		alert.show();
	}
	
	public static boolean showAndWaitChoice(String text, String choice1, String choice2)
	{
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText(text);
		
		// set icon to dialog
		
		DialogPane pane = alert.getDialogPane();
		Stage stage = (Stage) pane.getScene().getWindow();
        stage.getIcons().add(new Image(Records.IMG_MAIN_ICON));
		
		ButtonType c1 = new ButtonType(choice1);
		ButtonType c2 = new ButtonType(choice2, ButtonData.CANCEL_CLOSE);
		
		alert.getButtonTypes().setAll(c1, c2);
		
		Optional<ButtonType> result = alert.showAndWait();
		
		if (result.get() == c1)
		{
		    return true;
		} 
		else
		{
		    return false;
		}
	}
	
	public static String showAndWaitChoiceList(LinkedList<String> list, String text )
	{
		List<String> choices = list;
		
		ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(choices.size() - 1), choices);
		dialog.setTitle("Choice Dialog");
		dialog.setHeaderText(text);
		
		// set icon to dialog
		DialogPane pane = dialog.getDialogPane();
		Stage stage = (Stage) pane.getScene().getWindow();
		stage.getIcons().add(new Image(Records.IMG_MAIN_ICON));
		
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
}
