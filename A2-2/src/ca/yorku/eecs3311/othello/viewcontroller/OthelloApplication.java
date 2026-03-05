package ca.yorku.eecs3311.othello.viewcontroller;

import ca.yorku.eecs3311.othello.model.Othello;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Ignacio Doria Oberman - EECS 3311 - Assignment 2

public class OthelloApplication extends Application {
    // REMEMBER: To run this in the lab put 
    // --module-path "/usr/share/openjfx/lib" --add-modules javafx.controls,javafx.fxml
    // in the run configuration under VM arguments.
    // You can import the JavaFX.prototype launch configuration and use it as well.
    
    @Override
    public void start(Stage stage) throws Exception {
        // Create and hook up the Model, View and the controller
        
        // MODEL
        Othello othello = new Othello();
        
        // CONTROLLER
        OthelloGUIController controller = new OthelloGUIController(othello);
    
        // VIEW
        OthelloView view = new OthelloView(othello, controller);
        
        // SCENE
        Scene scene = new Scene(view.getRoot(), 400, 450); 
        stage.setTitle("Othello");
        stage.setScene(scene);
                
        // LAUNCH THE GUI
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
