package geekbrains.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

//public class MainClient extends Application {
//    @Override
//    public void start(Stage stage) throws Exception {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
//        Parent root = loader.load();
//        stage.setTitle("Чат");
//        stage.setScene(new Scene(root, 400, 400));
//        stage.show();
//        ChatController chatController = loader.getController();
//        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//            @Override
//            public void handle(WindowEvent windowEvent) {
//                chatController.close();
//                Platform.exit();
//                System.exit(0);
//            }
//        });
//    }
//}
public class MainClient extends Application {
    @Override
    public void start(Stage ptimaryStage) throws IOException {
        // отдельно загрухаем fxml
        FXMLLoader fxmlLoader = new FXMLLoader();
        // отдельно загружаем сцену
        Parent root = fxmlLoader.load(getClass().getResource("/main.fxml").openStream());
        ChatController controller = fxmlLoader.getController();
        // размер окна
        ptimaryStage.setScene(new Scene(root, 400, 400));
        ptimaryStage.setTitle("Чат!");
        ptimaryStage.setOnCloseRequest(event -> controller.close());

        ptimaryStage.show();
    }

    public static void main (String[]args){
        launch();
    }
}
