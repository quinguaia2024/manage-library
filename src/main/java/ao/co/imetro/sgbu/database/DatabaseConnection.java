package ao.co.imetro.sgbu.database;

import java.sql.*;

/**
 * Classe para gerenciar a conexão com o banco de dados MySQL
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/sgbu_db";
    private static final String USER = "sgbu_user";
    private static final String PASSWORD = "sgbu_password";

    private static Connection connection;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL não encontrado: " + e.getMessage());
        }
    }

    /**
     * Obtém a conexão com o banco de dados (singleton)
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    /**
     * Fecha a conexão com o banco de dados
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }

    /**
     * Verifica se a conexão está ativa
     */
    public static boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
