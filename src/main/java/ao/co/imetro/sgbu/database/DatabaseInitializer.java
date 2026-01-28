package ao.co.imetro.sgbu.database;

import java.sql.*;

/**
 * Classe responsável por inicializar o banco de dados com as tabelas necessárias
 */
public class DatabaseInitializer {

    /**
     * Inicializa o banco de dados criando as tabelas se não existirem
     */
    public static void initializeDatabase() {
        // Primeiro conectar sem especificar banco de dados para criar o banco
        String serverUrl = "jdbc:mysql://localhost:3306/?serverTimezone=UTC";
        
        try (Connection conn = DriverManager.getConnection(serverUrl, "sgbu_user", "sgbu_password")) {
            // Criar banco de dados se não existir
            createDatabase(conn);
            
            System.out.println("✓ Banco de dados 'sgbu_db' criado/verificado com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao criar banco de dados: " + e.getMessage());
            return;
        }
        
        // Agora conectar ao banco específico para criar as tabelas
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Criar tabelas
            createUsuariosTable(conn);
            createObrasTable(conn);
            createExemplaresTable(conn);
            createEmprestimosTable(conn);
            createReservasTable(conn);
            createMultasTable(conn);
            
            System.out.println("✓ Tabelas criadas/verificadas com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar tabelas: " + e.getMessage());
        }
    }

    private static void createDatabase(Connection conn) throws SQLException {
        String sql = "CREATE DATABASE IF NOT EXISTS sgbu_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static void createUsuariosTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "nome VARCHAR(100) NOT NULL," +
                "email VARCHAR(100) UNIQUE NOT NULL," +
                "senha VARCHAR(255) NOT NULL," +
                "perfil ENUM('ADMINISTRADOR', 'BIBLIOTECARIO', 'DOCENTE', 'ESTUDANTE') NOT NULL," +
                "ativo BOOLEAN DEFAULT true," +
                "limite_emprestimos INT DEFAULT 3," +
                "prazo_dias INT DEFAULT 7," +
                "limite_multa DECIMAL(10,2) DEFAULT 100000," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                ")";
        executeUpdate(conn, sql);
    }

    private static void createObrasTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS obras (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "titulo VARCHAR(255) NOT NULL," +
                "autor VARCHAR(255) NOT NULL," +
                "assunto VARCHAR(255)," +
                "isbn VARCHAR(20) UNIQUE," +
                "editora VARCHAR(100)," +
                "ano_publicacao INT," +
                "numero_paginas INT," +
                "descricao TEXT," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                ")";
        executeUpdate(conn, sql);
    }

    private static void createExemplaresTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS exemplares (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "obra_id INT NOT NULL," +
                "codigo_tombo VARCHAR(50) UNIQUE NOT NULL," +
                "estado ENUM('DISPONIVEL', 'EMPRESTADO', 'RESERVADO', 'DANIFICADO') DEFAULT 'DISPONIVEL'," +
                "localizacao VARCHAR(100)," +
                "data_aquisicao DATE," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "FOREIGN KEY (obra_id) REFERENCES obras(id) ON DELETE CASCADE" +
                ")";
        executeUpdate(conn, sql);
    }

    private static void createEmprestimosTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS emprestimos (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "usuario_id INT NOT NULL," +
                "exemplar_id INT NOT NULL," +
                "data_emprestimo TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "data_devolucao_prevista DATE NOT NULL," +
                "data_devolucao_real DATE," +
                "renovacoes INT DEFAULT 0," +
                "ativo BOOLEAN DEFAULT true," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "FOREIGN KEY (usuario_id) REFERENCES usuarios(id)," +
                "FOREIGN KEY (exemplar_id) REFERENCES exemplares(id)," +
                "INDEX idx_usuario (usuario_id)," +
                "INDEX idx_exemplar (exemplar_id)," +
                "INDEX idx_ativo (ativo)" +
                ")";
        executeUpdate(conn, sql);
    }

    private static void createReservasTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS reservas (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "usuario_id INT NOT NULL," +
                "exemplar_id INT NOT NULL," +
                "data_reserva TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "status ENUM('ATIVA', 'ATENDIDA', 'CANCELADA') DEFAULT 'ATIVA'," +
                "posicao_fila INT DEFAULT 1," +
                "data_atendimento DATE," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "FOREIGN KEY (usuario_id) REFERENCES usuarios(id)," +
                "FOREIGN KEY (exemplar_id) REFERENCES exemplares(id)," +
                "INDEX idx_exemplar (exemplar_id)," +
                "INDEX idx_usuario (usuario_id)," +
                "INDEX idx_status (status)," +
                "INDEX idx_exemplar_status (exemplar_id, status)" +
                ")";
        executeUpdate(conn, sql);
    }

    private static void createMultasTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS multas (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "usuario_id INT NOT NULL," +
                "emprestimo_id INT NOT NULL," +
                "valor DECIMAL(10,2) NOT NULL," +
                "dias_atraso INT NOT NULL," +
                "data_geracao TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "data_pagamento DATE," +
                "status ENUM('ABERTA', 'PAGA', 'CANCELADA') DEFAULT 'ABERTA'," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "FOREIGN KEY (usuario_id) REFERENCES usuarios(id)," +
                "FOREIGN KEY (emprestimo_id) REFERENCES emprestimos(id)," +
                "INDEX idx_usuario (usuario_id)," +
                "INDEX idx_status (status)" +
                ")";
        executeUpdate(conn, sql);
    }

    private static void executeUpdate(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    /**
     * Insere um usuário administrador padrão
     */
    public static void insertDefaultAdmin() {
        String sql = "INSERT INTO usuarios (nome, email, senha, perfil, limite_emprestimos, prazo_dias) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE id=id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "Administrador");
            stmt.setString(2, "admin@biblioteca.ao");
            stmt.setString(3, hashPassword("admin123")); // Em produção, usar bcrypt
            stmt.setString(4, "ADMINISTRADOR");
            stmt.setInt(5, 10);
            stmt.setInt(6, 30);
            
            stmt.executeUpdate();
            System.out.println("✓ Usuário administrador padrão criado/verificado!");
        } catch (SQLException e) {
            System.err.println("Erro ao inserir admin padrão: " + e.getMessage());
        }
    }

    /**
     * Hash simples de senha (em produção, usar bcrypt)
     */
    private static String hashPassword(String password) {
        return Integer.toString(password.hashCode());
    }
}
