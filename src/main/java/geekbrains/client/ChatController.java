package geekbrains.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatController implements Initializable {
    @FXML
    private TextArea textArea;
    @FXML
    private TextField messageField, loginField;
    @FXML
    private HBox messagePanel, authPanel;
    @FXML
    // поля для введения пароля
    private PasswordField passwordField;


// список клиентов справа
    @FXML
    private ListView<String> clientList;

    private final Network network;

    public ChatController() {
        this.network = new Network(this);
    }
// визуал
    public void setAuthenticated(boolean authenticated) {
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);
        messagePanel.setVisible(authenticated);
        messagePanel.setManaged(authenticated);
        clientList.setVisible(authenticated);
        clientList.setManaged(authenticated);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setAuthenticated(false);
    }
// отображение переписки
    public void displayMessage(String text) {
        if (textArea.getText().isEmpty()) {
            textArea.setText(text);
        } else {
            textArea.setText(textArea.getText() + "\n" + text);

        }
    }
// отображение списка клиентов
    public void displayClient(String nickName) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                clientList.getItems().add(nickName);
            }
        });
    }
    // чистим списко клиентов
    public void clinDisplayClients() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                clientList.getItems().clear();

            }
        });
    }
// удаляем клиента из списка
    public void removeClient(String nickName) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                clientList.getItems().remove(nickName);
            }
        });
    }

// вводим логин и пароль и авторизуемся
    public void sendAuth(ActionEvent event) {
        boolean authenticated = network.sendAuth(loginField.getText(), passwordField.getText());
        if(authenticated) {
            loginField.clear();
            passwordField.clear();
            setAuthenticated(true);
        }
    }
// чтение сообщений и отобращжение в соответствующеми поле
    public void sendMessage(ActionEvent event) {
        network.sendMessage(messageField.getText());
        messageField.clear();
    }

    public void close() {
        network.closeConnection();
    }
    // двойное нажатие пмышкой по нику справя для отправки личного сообщения
    public void clientsListDoubleClick(MouseEvent mouseEvent) {
        // отслеживаем нажатие мышкой

        // событие о нажатие мышкой

        // если увидели двойной клик
        if (mouseEvent.getClickCount() == 2){
            String selectedUser = clientList.getSelectionModel().getSelectedItem();
            messageField.setText("/w "+ selectedUser +" ");
            messageField.requestFocus();
            messageField.selectEnd();
        }

    }
}
