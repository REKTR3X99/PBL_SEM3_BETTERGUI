package Layout;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    } //Launching start

    @Override
    public void start(Stage primaryStage) throws Exception{


        Parent root= FXMLLoader.load(getClass().getResource("MainLayout.fxml")); //Setting the layout which is to be loaded i.e MainLayout.fxml
        Scene scene = new Scene(root, 972 , 600); //Creating the scene Width  =  972 and Height = 600
        scene.setRoot(root); //Setting the root of the scene


        primaryStage.setTitle("Application"); //Naming the window
        primaryStage.setScene(scene); //Setting the scene to PrimaryStage
        primaryStage.show(); //Displaying the Primary Stage

    }
}
