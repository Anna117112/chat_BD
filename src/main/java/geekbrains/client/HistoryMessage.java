package geekbrains.client;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HistoryMessage {

        private String login;
        private OutputStream out;
// подготовка файла для записи текста
        public void init(String login) {
            try {
                this.login = login;
                //создаем файл для записи и зипись в конец true
                this.out = new FileOutputStream(getFilename(), true);
            } catch (IOException e) {
                throw new RuntimeException("Проблема при работе с историей");
            }
        }
// пишем сообщения в файл
        public void write(String message) {
            try {
                // пишем в кодировки для чтения кирилицы

                out.write(message.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException("Проблема при работе с историей");
            }
        }


//читаем сообщения с файла
    public List<String> read() {
  // создаем лист для записи сообщений из фаила и потом вывести на экрран
    List<String> msg = new ArrayList<>();
    try (BufferedReader in = new BufferedReader(new FileReader(getFilename()))) {
        // для вывода 5 сообщений
        while (true) {
            String str = in.readLine();
            if (str == null) {
               break;
            }
            if (msg.size() < 5) {
                msg.add(str);
            }

            else {
                msg.remove(0);
                msg.add(str);
            }
        }

        for (int i = 0; i<msg.size(); i++){


        }
        // возвращаем лист
        return msg;
    } catch (IOException e) {
        throw new RuntimeException("Проблема при работе с историей");
    }

}

    public void close() {
            login = null;
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private String getFilename() {
            return "history_" + login + ".txt";
        }
    }


