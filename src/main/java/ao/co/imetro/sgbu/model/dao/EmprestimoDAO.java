package ao.co.imetro.sgbu.model.dao;

import ao.co.imetro.sgbu.model.entity.Emprestimo;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para a entidade Emprestimo
 */
public class EmprestimoDAO extends BaseDAO<Emprestimo> {

    public EmprestimoDAO() {
        super();
    }

    @Override
    public boolean inserir(Emprestimo emprestimo) throws SQLException {
        String sql = "INSERT INTO emprestimos (usuario_id, exemplar_id, data_emprestimo, data_devolucao_prevista, renovacoes, ativo) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, emprestimo.getUsuarioId());
            stmt.setInt(2, emprestimo.getExemplarId());
            stmt.setTimestamp(3, java.sql.Timestamp.valueOf(emprestimo.getDataEmprestimo()));
            stmt.setDate(4, java.sql.Date.valueOf(emprestimo.getDataDevolucaoPrevista()));
            stmt.setInt(5, emprestimo.getRenovacoes());
            stmt.setBoolean(6, emprestimo.isAtivo());
            
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean atualizar(Emprestimo emprestimo) throws SQLException {
        String sql = "UPDATE emprestimos SET usuario_id=?, exemplar_id=?, data_emprestimo=?, " +
                "data_devolucao_prevista=?, data_devolucao_real=?, renovacoes=?, ativo=?, updated_at=NOW() WHERE id=?";
        
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, emprestimo.getUsuarioId());
            stmt.setInt(2, emprestimo.getExemplarId());
            stmt.setTimestamp(3, java.sql.Timestamp.valueOf(emprestimo.getDataEmprestimo()));
            stmt.setDate(4, java.sql.Date.valueOf(emprestimo.getDataDevolucaoPrevista()));
            
            if (emprestimo.getDataDevoluaoReal() != null) {
                stmt.setDate(5, java.sql.Date.valueOf(emprestimo.getDataDevoluaoReal()));
            } else {
                stmt.setNull(5, java.sql.Types.DATE);
            }
            
            stmt.setInt(6, emprestimo.getRenovacoes());
            stmt.setBoolean(7, emprestimo.isAtivo());
            stmt.setInt(8, emprestimo.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deletar(int id) throws SQLException {
        String sql = "DELETE FROM emprestimos WHERE id=?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Emprestimo buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM emprestimos WHERE id=?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToEmprestimo(rs);
            }
        }
        return null;
    }

    @Override
    public List<Emprestimo> listarTodos() throws SQLException {
        List<Emprestimo> emprestimos = new ArrayList<>();
        String sql = "SELECT * FROM emprestimos ORDER BY data_emprestimo DESC";
        
        try (var stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                emprestimos.add(mapResultSetToEmprestimo(rs));
            }
        }
        return emprestimos;
    }

    /**
     * Busca empréstimos ativos de um usuário
     */
    public List<Emprestimo> buscarAtivosDoUsuario(int usuarioId) throws SQLException {
        List<Emprestimo> emprestimos = new ArrayList<>();
        String sql = "SELECT * FROM emprestimos WHERE usuario_id=? AND ativo=true ORDER BY data_devolucao_prevista";
        
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                emprestimos.add(mapResultSetToEmprestimo(rs));
            }
        }
        return emprestimos;
    }

    /**
     * Busca empréstimo ativo de um exemplar
     */
    public Emprestimo buscarEmprestimoAtivoDoExemplar(int exemplarId) throws SQLException {
        String sql = "SELECT * FROM emprestimos WHERE exemplar_id=? AND ativo=true";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exemplarId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToEmprestimo(rs);
            }
        }
        return null;
    }

    /**
     * Conta empréstimos ativos de um usuário
     */
    public int contarEmprestimosAtivos(int usuarioId) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM emprestimos WHERE usuario_id=? AND ativo=true";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    /**
     * Busca empréstimos atrasados
     */
    public List<Emprestimo> buscarAtrasados() throws SQLException {
        List<Emprestimo> emprestimos = new ArrayList<>();
        String sql = "SELECT * FROM emprestimos WHERE ativo=true AND data_devolucao_prevista < CURDATE() ORDER BY data_devolucao_prevista";
        
        try (var stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                emprestimos.add(mapResultSetToEmprestimo(rs));
            }
        }
        return emprestimos;
    }

    /**
     * Busca empréstimos por período
     */
    public List<Emprestimo> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) throws SQLException {
        List<Emprestimo> emprestimos = new ArrayList<>();
        String sql = "SELECT * FROM emprestimos WHERE data_emprestimo >= ? AND data_emprestimo <= ? ORDER BY data_emprestimo DESC";
        
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(dataInicio));
            stmt.setDate(2, java.sql.Date.valueOf(dataFim));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                emprestimos.add(mapResultSetToEmprestimo(rs));
            }
        }
        return emprestimos;
    }
    
    /**
     * Busca empréstimos por usuário
     */
    public List<Emprestimo> buscarPorUsuario(int usuarioId) throws SQLException {
        List<Emprestimo> emprestimos = new ArrayList<>();
        String sql = "SELECT * FROM emprestimos WHERE usuario_id = ? ORDER BY data_emprestimo DESC";
        
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                emprestimos.add(mapResultSetToEmprestimo(rs));
            }
        }
        return emprestimos;
    }

    /**
     * Incrementa renovações de um empréstimo
     */
    public boolean renovar(int emprestimoId, LocalDate novaDevolucao) throws SQLException {
        String sql = "UPDATE emprestimos SET renovacoes = renovacoes + 1, " +
                "data_devolucao_prevista=?, updated_at=NOW() WHERE id=?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(novaDevolucao));
            stmt.setInt(2, emprestimoId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Registra a devolução de um empréstimo
     */
    public boolean registrarDevolucao(int emprestimoId, LocalDate dataDevolucao) throws SQLException {
        String sql = "UPDATE emprestimos SET data_devolucao_real=?, ativo=false, updated_at=NOW() WHERE id=?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(dataDevolucao));
            stmt.setInt(2, emprestimoId);
            return stmt.executeUpdate() > 0;
        }
    }

    private Emprestimo mapResultSetToEmprestimo(ResultSet rs) throws SQLException {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setId(rs.getInt("id"));
        emprestimo.setUsuarioId(rs.getInt("usuario_id"));
        emprestimo.setExemplarId(rs.getInt("exemplar_id"));
        
        var dataEmprestimo = rs.getTimestamp("data_emprestimo");
        if (dataEmprestimo != null) {
            emprestimo.setDataEmprestimo(dataEmprestimo.toLocalDateTime());
        }
        
        var dataDevolucaoPrevista = rs.getDate("data_devolucao_prevista");
        if (dataDevolucaoPrevista != null) {
            emprestimo.setDataDevolucaoPrevista(dataDevolucaoPrevista.toLocalDate());
        }
        
        var dataDevoluaoReal = rs.getDate("data_devolucao_real");
        if (dataDevoluaoReal != null) {
            emprestimo.setDataDevoluaoReal(dataDevoluaoReal.toLocalDate());
        }
        
        emprestimo.setRenovacoes(rs.getInt("renovacoes"));
        emprestimo.setAtivo(rs.getBoolean("ativo"));
        
        var created = rs.getTimestamp("created_at");
        if (created != null) {
            emprestimo.setCriadoEm(created.toLocalDateTime());
        }
        
        var updated = rs.getTimestamp("updated_at");
        if (updated != null) {
            emprestimo.setAtualizadoEm(updated.toLocalDateTime());
        }
        
        return emprestimo;
    }
}
