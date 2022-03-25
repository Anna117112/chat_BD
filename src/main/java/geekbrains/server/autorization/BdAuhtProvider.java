package geekbrains.server.autorization;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BdAuhtProvider implements AuthService {
    private BdConnection bdConnection;

    @Override
    // закрываем соеденение
    public void end() {
        bdConnection.close();

    }

    @Override
    // при старте открываем соеденение с базой данный
    public void start() {
        bdConnection = new BdConnection();
    }

    @Override
    public synchronized String getNickNameByLoginAndPassword(String login, String password) throws SQLException {
        // ищем по таблице ник где в сторе есть login и password переданные

        String nik = String.format("SELECT nickname  FROM user WHERE login = '%s' and password = '%s';", login, password);
        try (ResultSet rs = bdConnection.getStmt().executeQuery(nik)) {
            // если в таблице есть строка с таким nikName nj курср станет на нее и вернем из нее nikName
if (rs.next()){
    return rs.getString("nickname");

            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return null;
    }
    @Override
    // меняем никнейм
    public void changeNickname(String oldNik, String newNik ){
        String nik = String.format("update user set nickname = '%s'  WHERE nickname = '%s';", newNik, oldNik);
        try {
            bdConnection.getStmt().executeUpdate(nik);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean isNickBusy(String nik) {
        // ищем id с ником который передан в метод. Если он есть то вернется true
        String query = String.format("SELECT id  FROM user WHERE nickname = '%s';", nik);
        try (ResultSet rs = bdConnection.getStmt().executeQuery(query)) {
            // если в таблице есть строка с таким nikName nj курср станет на нее и вернем из нее nikName
            if (rs.next()){
                return true;

            }
        } catch (SQLException e) {
            e.printStackTrace();

        }

        return false;
    }
}

