package ao.co.imetro.sgbu.model.dao;

import ao.co.imetro.sgbu.model.entity.Reserva;
import ao.co.imetro.sgbu.model.enums.StatusReserva;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para a entidade Reserva
 */
public class ReservaDAO extends BaseDAO<Reserva> {

    public ReservaDAO() {
        super();
    }

    @Override
    public boolean inserir(Reserva reserva) throws SQLException {
        String sql = "INSERT INTO reservas (usuario_id, exemplar_id, data_reserva, status, posicao_fila) " +
                "VALUES (?, ?, ?, ?, ?)";
        
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, reserva.getUsuarioId());
            stmt.setInt(2, reserva.getExemplarId());
            stmt.setTimestamp(3, java.sql.Timestamp.valueOf(reserva.getDataReserva()));
            stmt.setString(4, reserva.getStatus().name());
            stmt.setInt(5, reserva.getPosicaoFila());
            
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean atualizar(Reserva reserva) throws SQLException {
        String sql = "UPDATE reservas SET usuario_id=?, exemplar_id=?, data_reserva=?, status=?, " +
                "posicao_fila=?, data_atendimento=?, updated_at=NOW() WHERE id=?";
        
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, reserva.getUsuarioId());
            stmt.setInt(2, reserva.getExemplarId());
            stmt.setTimestamp(3, java.sql.Timestamp.valueOf(reserva.getDataReserva()));
            stmt.setString(4, reserva.getStatus().name());
            stmt.setInt(5, reserva.getPosicaoFila());
            
            if (reserva.getDataAtendimento() != null) {
                stmt.setDate(6, java.sql.Date.valueOf(reserva.getDataAtendimento()));
            } else {
                stmt.setNull(6, java.sql.Types.DATE);
            }
            
            stmt.setInt(7, reserva.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deletar(int id) throws SQLException {
        String sql = "DELETE FROM reservas WHERE id=?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Reserva buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM reservas WHERE id=?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToReserva(rs);
            }
        }
        return null;
    }

    @Override
    public List<Reserva> listarTodos() throws SQLException {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reservas ORDER BY posicao_fila, data_reserva";
        
        try (var stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                reservas.add(mapResultSetToReserva(rs));
            }
        }
        return reservas;
    }

    /**
     * Busca todas as reservas de um usuário (ativas, atendidas e canceladas)
     */
    public List<Reserva> buscarPorUsuario(int usuarioId) throws SQLException {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reservas WHERE usuario_id=? ORDER BY data_reserva DESC";
        
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reservas.add(mapResultSetToReserva(rs));
            }
        }
        return reservas;
    }

    /**
     * Busca reservas ativas de um usuário
     */
    public List<Reserva> buscarAtivasDoUsuario(int usuarioId) throws SQLException {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reservas WHERE usuario_id=? AND status='ATIVA' ORDER BY data_reserva";
        
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reservas.add(mapResultSetToReserva(rs));
            }
        }
        return reservas;
    }

    /**
     * Busca reserva ativa de um usuário para um exemplar
     */
    public Reserva buscarReservaAtiva(int usuarioId, int exemplarId) throws SQLException {
        String sql = "SELECT * FROM reservas WHERE usuario_id=? AND exemplar_id=? AND status='ATIVA'";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, exemplarId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToReserva(rs);
            }
        }
        return null;
    }

    /**
     * Busca a primeira reserva ativa (posição 1) de um exemplar
     */
    public Reserva buscarProximaReserva(int exemplarId) throws SQLException {
        String sql = "SELECT * FROM reservas WHERE exemplar_id=? AND status='ATIVA' ORDER BY posicao_fila, data_reserva LIMIT 1";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exemplarId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToReserva(rs);
            }
        }
        return null;
    }

    /**
     * Busca todas as reservas ativas de um exemplar (fila)
     */
    public List<Reserva> buscarFilaDeEspera(int exemplarId) throws SQLException {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reservas WHERE exemplar_id=? AND status='ATIVA' ORDER BY posicao_fila, data_reserva";
        
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exemplarId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reservas.add(mapResultSetToReserva(rs));
            }
        }
        return reservas;
    }

    /**
     * Atualiza a posição na fila de todas as reservas de um exemplar
     */
    public boolean atualizarFilaDeEspera(int exemplarId) throws SQLException {
        String sql = "UPDATE reservas SET posicao_fila = (@row_number:=@row_number+1) " +
                "WHERE exemplar_id=? AND status='ATIVA' " +
                "ORDER BY data_reserva";
        
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exemplarId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Marca uma reserva como atendida
     */
    public boolean marcarComAtendida(int reservaId) throws SQLException {
        String sql = "UPDATE reservas SET status='ATENDIDA', data_atendimento=CURDATE(), updated_at=NOW() WHERE id=?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, reservaId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Cancela uma reserva
     */
    public boolean cancelar(int reservaId) throws SQLException {
        String sql = "UPDATE reservas SET status='CANCELADA', updated_at=NOW() WHERE id=?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, reservaId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Conta reservas ativas de um exemplar
     */
    public int contarReservasAtivasPorExemplar(int exemplarId) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM reservas WHERE exemplar_id=? AND status='ATIVA'";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exemplarId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    /**
     * Verifica se existe alguma reserva ativa para o exemplar (de qualquer usuário)
     */
    public boolean existeReservaAtivaDoExemplar(int exemplarId) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM reservas WHERE exemplar_id=? AND status='ATIVA'";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exemplarId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
        }
        return false;
    }

    /**
     * Verifica se existe reserva ativa do exemplar para outro usuário
     */
    public boolean existeReservaAtivaDeOutroUsuario(int exemplarId, int usuarioId) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM reservas WHERE exemplar_id=? AND usuario_id != ? AND status='ATIVA'";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exemplarId);
            stmt.setInt(2, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
        }
        return false;
    }

    /**
     * Calcula a próxima posição na fila para um exemplar
     * Baseado na maior posicao_fila atual + 1
     */
    public int calcularProximaPosicaoFila(int exemplarId) throws SQLException {
        String sql = "SELECT COALESCE(MAX(posicao_fila), 0) + 1 as proxima_posicao " +
                     "FROM reservas WHERE exemplar_id=? AND status='ATIVA'";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exemplarId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {return rs.getInt("proxima_posicao");
            }
        }
        return 1;
    }

    private Reserva mapResultSetToReserva(ResultSet rs) throws SQLException {
        Reserva reserva = new Reserva();
        reserva.setId(rs.getInt("id"));
        reserva.setUsuarioId(rs.getInt("usuario_id"));
        reserva.setExemplarId(rs.getInt("exemplar_id"));
        
        var dataReserva = rs.getTimestamp("data_reserva");
        if (dataReserva != null) {
            reserva.setDataReserva(dataReserva.toLocalDateTime());
        }
        
        reserva.setStatus(StatusReserva.valueOf(rs.getString("status")));
        reserva.setPosicaoFila(rs.getInt("posicao_fila"));
        
        var dataAtendimento = rs.getDate("data_atendimento");
        if (dataAtendimento != null) {
            reserva.setDataAtendimento(dataAtendimento.toLocalDate());
        }
        
        var created = rs.getTimestamp("created_at");
        if (created != null) {
            reserva.setCriadoEm(created.toLocalDateTime());
        }
        
        var updated = rs.getTimestamp("updated_at");
        if (updated != null) {
            reserva.setAtualizadoEm(updated.toLocalDateTime());
        }
        
        return reserva;
    }
}
