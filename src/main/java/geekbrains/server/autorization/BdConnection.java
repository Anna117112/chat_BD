package geekbrains.server.autorization;


import java.sql.*;

public class
BdConnection {
    private static Connection connection;

    // для отправки запросов
    private static Statement stmt;



    public BdConnection() {
        // логика открытия соеденения с бд
        try {
            Class.forName("org.sqlite.JDBC");
            // открывается соеденение
            this.connection = DriverManager.getConnection("jdbc:sqlite:data.db");

            this.stmt = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            // если невозможноподключится то нет смысла продолжать работать и бросаем RuntimeException
            // который завершит работу приложения и ег оперехватывать не будем
            throw new RuntimeException("Невозможно подключится к БД");
        }
    }
    public static Statement getStmt() {
        return stmt;
    }

    public void close() {
        try {
            if (stmt != null) {
                stmt.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // проверяем что соеденение было
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }


        }

    }
}










