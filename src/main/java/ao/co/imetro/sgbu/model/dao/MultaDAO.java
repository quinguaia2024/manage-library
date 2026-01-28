package ao.co.imetro.sgbu.model.dao;

import ao.co.imetro.sgbu.model.entity.Multa;
import ao.co.imetro.sgbu.model.enums.StatusMulta;
import ao.co.imetro.sgbu.model.enums.MotivoMulta;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para a entidade Multa
 */
public class MultaDAO extends BaseDAO<Multa> {

    public MultaDAO() {
        super();
    }

    @Override
    public boolean inserir(Multa multa) throws SQLException {
        String sql = "INSERT INTO multas (usuario_id, emprestimo_id, valor, dias_atraso, motivo, data_geracao, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, multa.getUsuarioId());
            stmt.setInt(2, multa.getEmprestimoId());
            stmt.setDouble(3, multa.getValor());
            stmt.setInt(4, multa.getDiasAtraso());
            stmt.setString(5, multa.getMotivo() != null ? multa.getMotivo().name() : MotivoMulta.ATRASO.name());
            stmt.setTimestamp(6, java.sql.Timestamp.valueOf(multa.getDataGeracao()));
            stmt.setString(7, multa.getStatus().name());
            
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean atualizar(Multa multa) throws SQLException {
        String sql = "UPDATE multas SET usuario_id=?, emprestimo_id=?, valor=?, dias_atraso=?, " +
                "motivo=?, data_geracao=?, data_pagamento=?, status=?, updated_at=NOW() WHERE id=?";
        
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, multa.getUsuarioId());
            stmt.setInt(2, multa.getEmprestimoId());
            stmt.setDouble(3, multa.getValor());
            stmt.setInt(4, multa.getDiasAtraso());
            stmt.setString(5, multa.getMotivo() != null ? multa.getMotivo().name() : MotivoMulta.ATRASO.name());
            stmt.setTimestamp(6, java.sql.Timestamp.valueOf(multa.getDataGeracao()));
            
            if (multa.getDataPagamento() != null) {
                stmt.setDate(7, java.sql.Date.valueOf(multa.getDataPagamento()));
            } else {
                stmt.setNull(7, java.sql.Types.DATE);
            }
            
            stmt.setString(8, multa.getStatus().name());
            stmt.setInt(9, multa.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deletar(int id) throws SQLException {
        String sql = "DELETE FROM multas WHERE id=?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Multa buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM multas WHERE id=?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToMulta(rs);
            }
        }
        return null;
    }

    @Override
    public List<Multa> listarTodos() throws SQLException {
        List<Multa> multas = new ArrayList<>();
        String sql = "SELECT * FROM multas ORDER BY data_geracao DESC";
        
        try (var stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                multas.add(mapResultSetToMulta(rs));
            }
        }
        return multas;
    }

    /**
     * Busca todas as multas de um usuário
     */
    public List<Multa> buscarPorUsuario(int usuarioId) throws SQLException {
        List<Multa> multas = new ArrayList<>();
        String sql = "SELECT * FROM multas WHERE usuario_id=? ORDER BY data_geracao DESC";
        
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                multas.add(mapResultSetToMulta(rs));
            }
        }
        return multas;
    }

    /**
     * Busca multas abertas de um usuário
     */
    public List<Multa> buscarAbertasDoUsuario(int usuarioId) throws SQLException {
        List<Multa> multas = new ArrayList<>();
        String sql = "SELECT * FROM multas WHERE usuario_id=? AND status='ABERTA' ORDER BY data_geracao DESC";
        
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                multas.add(mapResultSetToMulta(rs));
            }
        }
        return multas;
    }

    /**
     * Busca multa de um empréstimo
     */
    public Multa buscarPorEmprestimo(int emprestimoId) throws SQLException {
        String sql = "SELECT * FROM multas WHERE emprestimo_id=?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, emprestimoId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToMulta(rs);
            }
        }
        return null;
    }

    /**
     * Calcula total de multas abertas de um usuário
     */
    public double calcularTotalMultasAbertas(int usuarioId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(valor), 0) as total FROM multas WHERE usuario_id=? AND status='ABERTA'";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0;
    }

    /**
     * Lista multas abertas
     */
    public List<Multa> listarAbertas() throws SQLException {
        List<Multa> multas = new ArrayList<>();
        String sql = "SELECT * FROM multas WHERE status='ABERTA' ORDER BY data_geracao DESC";
        
        try (var stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                multas.add(mapResultSetToMulta(rs));
            }
        }
        return multas;
    }

    /**
     * Marca uma multa como paga
     */
    public boolean marcarComoPaga(int multaId) throws SQLException {
        String sql = "UPDATE multas SET status='PAGA', data_pagamento=CURDATE(), updated_at=NOW() WHERE id=?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, multaId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Cancela uma multa
     */
    public boolean cancelar(int multaId) throws SQLException {
        String sql = "UPDATE multas SET status='CANCELADA', updated_at=NOW() WHERE id=?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, multaId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Busca multas por período
     */
    public List<Multa> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) throws SQLException {
        List<Multa> multas = new ArrayList<>();
        String sql = "SELECT * FROM multas WHERE DATE(data_geracao) >= ? AND DATE(data_geracao) <= ? ORDER BY data_geracao DESC";
        
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(dataInicio));
            stmt.setDate(2, java.sql.Date.valueOf(dataFim));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                multas.add(mapResultSetToMulta(rs));
            }
        }
        return multas;
    }

    /**
     * Relatório de multas com estatísticas
     */
    public List<Object[]> getEstatisticasMultas() throws SQLException {
        List<Object[]> stats = new ArrayList<>();
        String sql = "SELECT status, COUNT(*) as quantidade, SUM(valor) as total " +
                "FROM multas GROUP BY status";
        
        try (var stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                stats.add(new Object[]{
                    rs.getString("status"),
                    rs.getInt("quantidade"),
                    rs.getDouble("total")
                });
            }
        }
        return stats;
    }

    private Multa mapResultSetToMulta(ResultSet rs) throws SQLException {
        Multa multa = new Multa();
        multa.setId(rs.getInt("id"));
        multa.setUsuarioId(rs.getInt("usuario_id"));
        multa.setEmprestimoId(rs.getInt("emprestimo_id"));
        multa.setValor(rs.getDouble("valor"));
        multa.setDiasAtraso(rs.getInt("dias_atraso"));
        
        // Mapear motivo
        String motivoStr = rs.getString("motivo");
        if (motivoStr != null) {
            try {
                multa.setMotivo(MotivoMulta.valueOf(motivoStr));
            } catch (IllegalArgumentException e) {
                multa.setMotivo(MotivoMulta.ATRASO);
            }
        } else {
            multa.setMotivo(MotivoMulta.ATRASO);
        }
        
        var dataGeracao = rs.getTimestamp("data_geracao");
        if (dataGeracao != null) {
            multa.setDataGeracao(dataGeracao.toLocalDateTime());
        }
        
        var dataPagamento = rs.getDate("data_pagamento");
        if (dataPagamento != null) {
            multa.setDataPagamento(dataPagamento.toLocalDate());
        }
        
        multa.setStatus(StatusMulta.valueOf(rs.getString("status")));
        
        var created = rs.getTimestamp("created_at");
        if (created != null) {
            multa.setCriadoEm(created.toLocalDateTime());
        }
        
        var updated = rs.getTimestamp("updated_at");
        if (updated != null) {
            multa.setAtualizadoEm(updated.toLocalDateTime());
        }
        
        return multa;
    }
}
