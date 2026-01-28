package ao.co.imetro.sgbu.model.dao;

import ao.co.imetro.sgbu.model.entity.Exemplar;
import ao.co.imetro.sgbu.model.enums.EstadoExemplar;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para a entidade Exemplar
 */
public class ExemplarDAO extends BaseDAO<Exemplar> {

    public ExemplarDAO() {
        super();
    }

    @Override
    public boolean inserir(Exemplar exemplar) throws SQLException {
        String sql = "INSERT INTO exemplares (obra_id, codigo_tombo, estado, localizacao, data_aquisicao) " +
                "VALUES (?, ?, ?, ?, ?)";
        
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exemplar.getObraId());
            stmt.setString(2, exemplar.getCodigoTombo());
            stmt.setString(3, exemplar.getEstado().name());
            stmt.setString(4, exemplar.getLocalizacao());
            
            if (exemplar.getDataAquisicao() != null) {
                stmt.setDate(5, java.sql.Date.valueOf(exemplar.getDataAquisicao()));
            } else {
                stmt.setDate(5, java.sql.Date.valueOf(java.time.LocalDate.now()));
            }
            
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean atualizar(Exemplar exemplar) throws SQLException {
        String sql = "UPDATE exemplares SET obra_id=?, codigo_tombo=?, estado=?, localizacao=?, " +
                "data_aquisicao=?, updated_at=NOW() WHERE id=?";
        
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exemplar.getObraId());
            stmt.setString(2, exemplar.getCodigoTombo());
            stmt.setString(3, exemplar.getEstado().name());
            stmt.setString(4, exemplar.getLocalizacao());
            stmt.setDate(5, java.sql.Date.valueOf(exemplar.getDataAquisicao()));
            stmt.setInt(6, exemplar.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deletar(int id) throws SQLException {
        String sql = "DELETE FROM exemplares WHERE id=?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Exemplar buscarPorId(int id) throws SQLException {
        String sql = "SELECT e.*, o.id as obra_id_full, o.titulo, o.autor, o.isbn, o.editora, " +
                     "o.ano_publicacao, o.numero_paginas " +
                     "FROM exemplares e " +
                     "LEFT JOIN obras o ON e.obra_id = o.id " +
                     "WHERE e.id=?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToExemplarComObra(rs);
            }
        }
        return null;
    }

    @Override
    public List<Exemplar> listarTodos() throws SQLException {
        List<Exemplar> exemplares = new ArrayList<>();
        String sql = "SELECT e.*, o.id as obra_id_full, o.titulo, o.autor, o.isbn, o.editora, " +
                     "o.ano_publicacao, o.numero_paginas " +
                     "FROM exemplares e " +
                     "LEFT JOIN obras o ON e.obra_id = o.id " +
                     "ORDER BY e.codigo_tombo";
        
        try (var stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                exemplares.add(mapResultSetToExemplarComObra(rs));
            }
        }
        return exemplares;
    }

    /**
     * Busca exemplares por obra
     */
    public List<Exemplar> buscarPorObra(int obraId) throws SQLException {
        List<Exemplar> exemplares = new ArrayList<>();
        String sql = "SELECT * FROM exemplares WHERE obra_id=? ORDER BY codigo_tombo";
        
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, obraId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                exemplares.add(mapResultSetToExemplar(rs));
            }
        }
        return exemplares;
    }

    /**
     * Busca exemplares disponíveis de uma obra
     */
    public List<Exemplar> buscarDisponiveisPorObra(int obraId) throws SQLException {
        List<Exemplar> exemplares = new ArrayList<>();
        String sql = "SELECT * FROM exemplares WHERE obra_id=? AND estado='DISPONIVEL' ORDER BY codigo_tombo";
        
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, obraId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                exemplares.add(mapResultSetToExemplar(rs));
            }
        }
        return exemplares;
    }

    /**
     * Busca exemplar por código de tombo
     */
    public Exemplar buscarPorCodigoTombo(String codigoTombo) throws SQLException {
        String sql = "SELECT e.*, o.id as obra_id_full, o.titulo, o.autor, o.isbn, o.editora, " +
                     "o.ano_publicacao, o.numero_paginas " +
                     "FROM exemplares e " +
                     "LEFT JOIN obras o ON e.obra_id = o.id " +
                     "WHERE e.codigo_tombo=?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, codigoTombo);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToExemplarComObra(rs);
            }
        }
        return null;
    }

    /**
     * Lista exemplares por estado
     */
    public List<Exemplar> listarPorEstado(EstadoExemplar estado) throws SQLException {
        List<Exemplar> exemplares = new ArrayList<>();
        String sql = "SELECT * FROM exemplares WHERE estado=? ORDER BY codigo_tombo";
        
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, estado.name());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                exemplares.add(mapResultSetToExemplar(rs));
            }
        }
        return exemplares;
    }

    /**
     * Atualiza o estado de um exemplar
     */
    public boolean atualizarEstado(int exemplarId, EstadoExemplar novoEstado) throws SQLException {
        String sql = "UPDATE exemplares SET estado=?, updated_at=NOW() WHERE id=?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, novoEstado.name());
            stmt.setInt(2, exemplarId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Conta exemplares disponíveis de uma obra
     */
    public int contarDisponiveisPorObra(int obraId) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM exemplares WHERE obra_id=? AND estado='DISPONIVEL'";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, obraId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    /**
     * Conta exemplares totais de uma obra
     */
    public int contarPorObra(int obraId) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM exemplares WHERE obra_id=?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, obraId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    private Exemplar mapResultSetToExemplar(ResultSet rs) throws SQLException {
        Exemplar exemplar = new Exemplar();
        exemplar.setId(rs.getInt("id"));
        exemplar.setObraId(rs.getInt("obra_id"));
        exemplar.setCodigoTombo(rs.getString("codigo_tombo"));
        exemplar.setEstado(EstadoExemplar.valueOf(rs.getString("estado")));
        exemplar.setLocalizacao(rs.getString("localizacao"));
        
        var dataAquisicao = rs.getDate("data_aquisicao");
        if (dataAquisicao != null) {
            exemplar.setDataAquisicao(dataAquisicao.toLocalDate());
        }
        
        var created = rs.getTimestamp("created_at");
        if (created != null) {
            exemplar.setCriadoEm(created.toLocalDateTime());
        }
        
        var updated = rs.getTimestamp("updated_at");
        if (updated != null) {
            exemplar.setAtualizadoEm(updated.toLocalDateTime());
        }
        
        return exemplar;
    }
    
    /**
     * Mapeia ResultSet para Exemplar incluindo dados da Obra (JOIN)
     */
    private Exemplar mapResultSetToExemplarComObra(ResultSet rs) throws SQLException {
        Exemplar exemplar = mapResultSetToExemplar(rs);
        
        // Carregar dados da obra se existirem no ResultSet
        try {
            int obraIdFull = rs.getInt("obra_id_full");
            if (!rs.wasNull() && obraIdFull > 0) {
                ao.co.imetro.sgbu.model.entity.Obra obra = new ao.co.imetro.sgbu.model.entity.Obra();
                obra.setId(obraIdFull);
                obra.setTitulo(rs.getString("titulo"));
                obra.setAutor(rs.getString("autor"));
                obra.setIsbn(rs.getString("isbn"));
                obra.setEditora(rs.getString("editora"));
                obra.setAnoPublicacao(rs.getInt("ano_publicacao"));
                obra.setNumeroPaginas(rs.getInt("numero_paginas"));
                
                exemplar.setObra(obra);
            }
        } catch (SQLException e) {
            // Se as colunas da obra não existirem, ignora e continua
        }
        
        return exemplar;
    }
}
