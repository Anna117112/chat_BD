package geekbrains.server;

import geekbrains.server.ClientHandler;
import geekbrains.server.CommonConstants;
import geekbrains.server.autorization.AuthService;
import geekbrains.server.autorization.BdAuhtProvider;
import geekbrains.server.autorization.InMemoryAuthServiceImpl;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private AuthService authService;
    //потоки
    private ExecutorService executorService;

    public AuthService getAuthService() {
        return authService;
    }

    private List<ClientHandler> connectedUsers;

    public Server() {

        // создаем пул потоков (столько потоков сколько ядер)
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.connectedUsers = new ArrayList<>();
        this.authService = new BdAuhtProvider();
        this.authService.start();
        try (ServerSocket server = new ServerSocket(CommonConstants.SERVER_PORT);) {

            while (true) {
                System.out.println("Сервер ожидает подключения");
                Socket socket = server.accept();
                System.out.println("Клиент подключился");
                // передаем в клиентхендолер потоки. Теперь при создании нового соединения открывается новый поток
                new ClientHandler(executorService,this, socket);
            }
        } catch (IOException exception) {
            System.out.println("Ошибка в работе сервера");
            exception.printStackTrace();
        } finally {

            authService.end();


        }
    }


    // проверяем занят ли ник
    public boolean isNickNameBusy(String nickName) {
        for (ClientHandler handler : connectedUsers) {
            if (handler.getNickName().equals(nickName)) {
                return true;
            }
        }

        return false;
    }

    // рассылка сообщений по всем клиентам
    public synchronized void broadcastMessage(String message) {
        for (ClientHandler handler : connectedUsers) {
            handler.sendMessage(message);
        }

    }

    // добавление клиента в список подключенных
    public synchronized void addConnectedUser(ClientHandler handler) {
        connectedUsers.add(handler);
        broadcastMessage("Вошел в чат  " + handler.getNickName());
        // рассылаем список подключившихся клиетов
        broadcastClientList();


    }

    // отключение клиента
    public synchronized void disconnectUser(ClientHandler handler) {
        connectedUsers.remove(handler);
        broadcastMessage("Вышел из чата  " + handler.getNickName());
        broadcastClientList();
    }

    // список клиентов которые покдлючились
    public synchronized void broadcastClientList() {
        StringBuilder stringBuilder = new StringBuilder();
        //отправляем всем эту строчку
        stringBuilder.append("/clients_list ");
        for (ClientHandler c : connectedUsers) {
            stringBuilder.append(c.getNickName()).append(" ");
        }
        String clientsLister = stringBuilder.toString();
        // получим строку /clients_list dod nik
        broadcastMessage(clientsLister);
    }

    // отправляем личные сообщения
    public synchronized void sendPersonalMessage(ClientHandler sender, String receiverUsername, String message) {
        for (ClientHandler c : connectedUsers) {
            // перебираем всех если попадается клиент которому мы хотим написать
            if (c.getNickName().equals(receiverUsername)) {
                // отправляем личное сообщение
                c.sendMessage("от " + sender.getNickName() + ": " + message);
                // у того кто отправил личное сообщение высветится
                sender.sendMessage("пользователю " + receiverUsername + ": " + message);
                return;
            }
        }
        // если в списке не нашли пользователя
        sender.sendMessage("пользователь " + receiverUsername + "не в сети ");
    }




}
