package geekbrains.server;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;

public class ClientHandler {
    private final Server server;
    private final Socket socket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;

    private String nickName;

    public String getNickName() {
        return nickName;
    }

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
// открываем поток а в нем метод который собержит логику авторизации и чтеня сообщений
            new Thread(() -> lodic()).start();

        } catch (IOException exception) {
            throw new RuntimeException("Проблемы при создании обработчика");
        }
    }
// метод авторизации
    public boolean authentication(String message) throws IOException, SQLException {
// если сообщение начинается с
        if (message.startsWith(ServerCommandConstants.AUTHENTICATION)) {
            // делим введенное сообщение на слова по пробелам и записываем в массив строк
            String[] authInfo = message.split(" ");
            // ник = вызываем метод интерфейса getNickNameByLoginAndPassword в который передаем первое и второе слово
            String nickName = server.getAuthService().getNickNameByLoginAndPassword(authInfo[1], authInfo[2]);
            if (nickName != null) {
                // если ник не занят
                if (!server.isNickNameBusy(nickName)) {
                    sendAuthenticationMessage(true);
                    this.nickName = nickName;
              // добавляем авторизованного клиенат в список клиентов
                    server.addConnectedUser(this);
                    return true;
                } else {
                    sendAuthenticationMessage(false);
                    return false;
                }
            } else {
                sendAuthenticationMessage(false);
                return false;
            }
        } else {
            sendMessage("Вам необходимо авторизоваться ");
            return false;
        }


    }

/// отправляем true или false  при авторизации
    private void sendAuthenticationMessage(boolean authenticated) throws IOException {
        outputStream.writeBoolean(authenticated);
    }
/// читаем сообщения от клиента
    private boolean readMessages(String message) {
/// если начинается на
        if (message.startsWith("/")) {
            // System.out.println("от " + nickName + ": " + messageInChat);
            if (message.equals(ServerCommandConstants.EXIT)) {
                sendMessage("/exit");

                return false;
            }
            // отправка персонального сообщения
            if (message.startsWith("/w ")) {
                //  разбиваем по пробелу на 3 части .1 часть служебная команда б вторая ник б 3 - сообщение
                String[] tokens = message.split(" ");
                server.sendPersonalMessage(this, tokens[1], tokens[2]);

            }
            // если получили команду от сервера на изменение никнейма
            if (message.startsWith("/change_nic")){
                String[] tokens = message.split(" ");
                String newNickName = tokens[1];
                // проверяем что новый ник не занят
                if (!server.getAuthService().isNickBusy(newNickName)){
                    server.getAuthService().changeNickname(nickName,newNickName);
                    nickName= newNickName;
                    sendMessage("Вы изменили никнейм на " + newNickName);
                    server.broadcastClientList();

                }
                else
                    sendMessage("Такой никнейм уже занят ");
            }
            return true;
        }

// если не начинается на служебную команду то рассылаем сообщение всем пользователям
        System.out.println("от " + nickName + ": " + message);
        server.broadcastMessage(nickName + ": " + message);
        return true;
    }
// авторизация и чтение сообщений
    private void lodic() {
        try {
            // покак метод авторизации выдает false мы крутимся в этом метода
            while (!authentication(inputStream.readUTF())) ;


            // отправка сообщений пока метод возвращает true
            while (readMessages(inputStream.readUTF())) ;


        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Клиент " + nickName + " отключился ");
            // отписываемся от рассылки
            server.disconnectUser(this);
            // закрываем соедененния
            closeConnection();
        }
    }
/// чтение сооющений
    public void sendMessage(String message) {
        try {
            outputStream.writeUTF(message);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
// закрываем соеденения е
    private void closeConnection() {

        server.broadcastMessage(ServerCommandConstants.EXIT + " " + nickName);
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

// закрываем поток на отправку
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // закрываем соедененние
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

