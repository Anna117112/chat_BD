package geekbrains.client;



import geekbrains.server.CommonConstants;
import geekbrains.server.ServerCommandConstants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Network {
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private final ChatController controller;

    public Network(ChatController chatController) {
        this.controller = chatController;
    }
// читаем сообщения от сервера
    private void startReadServerMessages() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        // читаем введенные сообщения
                        String messageFromServer = inputStream.readUTF();
                        // отпечатываем изх на сервере
                        System.out.println(messageFromServer);
                        if (messageFromServer.startsWith(ServerCommandConstants.NEWCLIENT)) {
                            String[] client = messageFromServer.split(" ");
                            controller.displayClient(client[0]);
                        } else if (messageFromServer.startsWith(ServerCommandConstants.EXIT)) {
                            String[] client = messageFromServer.split(" ");
                            controller.removeClient(client[1]);
                            controller.displayMessage(client[1] + " покинул чат");
                        } else if (messageFromServer.startsWith(ServerCommandConstants.CLIENTS)) {
                            String[] client = messageFromServer.split(" ");
                            controller.clinDisplayClients();
                            for (int i = 1; i < client.length; i++) {
                                controller.displayClient(client[i]);
                            }
                        } else {
                            controller.displayMessage(messageFromServer);
                        }
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();

                } finally {
                    // закроем соеденение
                    closeConnection();

                }

            }

        }).start();

}
    private void initializeNetwork() throws IOException {
        socket = new Socket(CommonConstants.SERVER_ADDRESS, CommonConstants.SERVER_PORT);
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }


    public void sendMessage(String message) {
        try {
            outputStream.writeUTF(message);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public boolean sendAuth(String login, String password) {
        try {
            if (socket == null || socket.isClosed()) {
                initializeNetwork();
            }
            outputStream.writeUTF(ServerCommandConstants.AUTHENTICATION + " " + login + " " + password);

            boolean authenticated = inputStream.readBoolean();
            if (authenticated) {
                startReadServerMessages();
            }
            return authenticated;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

//    public void closeConnection() {
//        try {
//            outputStream.writeUTF(ServerCommandConstants.EXIT);
//            outputStream.close();
//            inputStream.close();
//            socket.close();
//        } catch (IOException exception) {
//            exception.printStackTrace();
//        }
//
//        System.exit(1);
//    }

    public void closeConnection() {
        controller.setAuthenticated(false);
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
