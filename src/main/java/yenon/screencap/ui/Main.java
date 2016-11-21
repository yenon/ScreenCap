package yenon.screencap.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by yenon on 11/19/16.
 */
public class Main extends Application {

    public static void main(String... args){
        Main.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/CapWindow.fxml"));
        Parent parent = loader.load();
        primaryStage.setFullScreen(true);
        primaryStage.setScene(new Scene(parent));
        CapWindowController controller = loader.getController();
        controller.setPrimaryStage(primaryStage);
        try {
            controller.setHostServices(getHostServices());
        }catch (Exception ignored){}
    }
}