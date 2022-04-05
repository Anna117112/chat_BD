package geekbrains.server.autorization;

import java.sql.SQLException;

public interface AuthService {
    void start();
    String getNickNameByLoginAndPassword(String login, String password) throws SQLException;
    void end();

    void changeNickname(String oldNik, String newNik );
    boolean isNickBusy(String nik);
}
