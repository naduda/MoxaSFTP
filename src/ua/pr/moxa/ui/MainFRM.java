package ua.pr.moxa.ui;

import java.net.URL;

import ua.pr.common.ToolsPrLib;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainFRM extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		    @Override
		    public void handle(WindowEvent event) {
		        System.exit(0);
		    }
		});

//		Parent root = FXMLLoader.load(new URL("file:/" + 
//				ToolsPrLib.getFullPath("./sftp.xml")));
		Parent root = FXMLLoader.load(getClass().getResource("sftp.xml"));
	    
        Scene scene = new Scene(root);
    
        stage.setTitle("SFTP transfer");
        stage.setScene(scene);
        stage.show();
        
	}

}
