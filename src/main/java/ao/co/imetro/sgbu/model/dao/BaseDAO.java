package ao.co.imetro.sgbu.model.dao;

import ao.co.imetro.sgbu.database.DatabaseConnection;
import java.sql.*;
import java.util.List;

/**
 * Classe abstrata base para todos os DAOs
 */
public abstract class BaseDAO<T> {
    protected Connection connection;

    public BaseDAO() {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }

    /**
     * Insere uma entidade
     */
    public abstract boolean inserir(T entity) throws SQLException;

    /**
     * Atualiza uma entidade
     */
    public abstract boolean atualizar(T entity) throws SQLException;

    /**
     * Deleta uma entidade
     */
    public abstract boolean deletar(int id) throws SQLException;

    /**
     * Busca uma entidade por ID
     */
    public abstract T buscarPorId(int id) throws SQLException;

    /**
     * Lista todas as entidades
     */
    public abstract List<T> listarTodos() throws SQLException;

    /**
     * Método auxiliar para executar queries
     */
    protected ResultSet executarSelect(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        return stmt.executeQuery();
    }

    /**
     * Método auxiliar para executar updates/inserts/deletes
     */
    protected int executarUpdate(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        return stmt.executeUpdate();
    }

    /**
     * Método auxiliar para executar updates/inserts/deletes com auto-generated keys
     */
    protected int executarUpdateComRetorno(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        stmt.executeUpdate();
        
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return -1;
    }
}
