package ao.co.imetro.sgbu.model.dao;

import ao.co.imetro.sgbu.model.entity.Obra;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para a entidade Obra
 */
public class ObraDAO extends BaseDAO<Obra> {

    public ObraDAO() {
        super();
    }

    @Override
    public boolean inserir(Obra obra) throws SQLException {
        String sql = "INSERT INTO obras (titulo, autor, assunto, isbn, editora, ano_publicacao, numero_paginas, descricao) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, obra.getTitulo());
            stmt.setString(2, obra.getAutor());
            stmt.setString(3, obra.getAssunto());
            stmt.setString(4, obra.getIsbn());
            stmt.setString(5, obra.getEditora());
            stmt.setInt(6, obra.getAnoPublicacao());
            stmt.setInt(7, obra.getNumeroPaginas());
            stmt.setString(8, obra.getDescricao());
            
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean atualizar(Obra obra) throws SQLException {
        String sql = "UPDATE obras SET titulo=?, autor=?, assunto=?, isbn=?, editora=?, " +
                "ano_publicacao=?, numero_paginas=?, descricao=?, updated_at=NOW() WHERE id=?";
        
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, obra.getTitulo());
            stmt.setString(2, obra.getAutor());
            stmt.setString(3, obra.getAssunto());
            stmt.setString(4, obra.getIsbn());
            stmt.setString(5, obra.getEditora());
            stmt.setInt(6, obra.getAnoPublicacao());
            stmt.setInt(7, obra.getNumeroPaginas());
            stmt.setString(8, obra.getDescricao());
            stmt.setInt(9, obra.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deletar(int id) throws SQLException {
        String sql = "DELETE FROM obras WHERE id=?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Obra buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM obras WHERE id=?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToObra(rs);
            }
        }
        return null;
    }

    @Override
    public List<Obra> listarTodos() throws SQLException {
        List<Obra> obras = new ArrayList<>();
        String sql = "SELECT * FROM obras ORDER BY titulo";
        
        try (var stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                obras.add(mapResultSetToObra(rs));
            }
        }
        return obras;
    }

    /**
     * Busca obra por ISBN
     */
    public Obra buscarPorIsbn(String isbn) throws SQLException {
        String sql = "SELECT * FROM obras WHERE isbn=?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToObra(rs);
            }
        }
        return null;
    }

    /**
     * Busca obras por título (LIKE)
     */
    public List<Obra> buscarPorTitulo(String titulo) throws SQLException {
        List<Obra> obras = new ArrayList<>();
        String sql = "SELECT * FROM obras WHERE titulo LIKE ? ORDER BY titulo";
        
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + titulo + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                obras.add(mapResultSetToObra(rs));
            }
        }
        return obras;
    }

    /**
     * Busca obras por autor (LIKE)
     */
    public List<Obra> buscarPorAutor(String autor) throws SQLException {
        List<Obra> obras = new ArrayList<>();
        String sql = "SELECT * FROM obras WHERE autor LIKE ? ORDER BY autor";
        
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + autor + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                obras.add(mapResultSetToObra(rs));
            }
        }
        return obras;
    }

    /**
     * Busca obras por assunto (LIKE)
     */
    public List<Obra> buscarPorAssunto(String assunto) throws SQLException {
        List<Obra> obras = new ArrayList<>();
        String sql = "SELECT * FROM obras WHERE assunto LIKE ? ORDER BY titulo";
        
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + assunto + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                obras.add(mapResultSetToObra(rs));
            }
        }
        return obras;
    }

    /**
     * Busca genérica por múltiplos campos
     */
    public List<Obra> buscarGenerico(String termo) throws SQLException {
        List<Obra> obras = new ArrayList<>();
        String sql = "SELECT * FROM obras WHERE titulo LIKE ? OR autor LIKE ? OR assunto LIKE ? OR isbn LIKE ? ORDER BY titulo";
        
        try (var stmt = connection.prepareStatement(sql)) {
            String searchTerm = "%" + termo + "%";
            stmt.setString(1, searchTerm);
            stmt.setString(2, searchTerm);
            stmt.setString(3, searchTerm);
            stmt.setString(4, searchTerm);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                obras.add(mapResultSetToObra(rs));
            }
        }
        return obras;
    }

    /**
     * Retorna as obras mais emprestadas
     */
    public List<Obra> listarMaisEmprestadas(int limite) throws SQLException {
        List<Obra> obras = new ArrayList<>();
        String sql = "SELECT o.* FROM obras o " +
                "INNER JOIN exemplares e ON o.id = e.obra_id " +
                "INNER JOIN emprestimos emp ON e.id = emp.exemplar_id " +
                "GROUP BY o.id " +
                "ORDER BY COUNT(emp.id) DESC " +
                "LIMIT ?";
        
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, limite);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                obras.add(mapResultSetToObra(rs));
            }
        }
        return obras;
    }

    private Obra mapResultSetToObra(ResultSet rs) throws SQLException {
        Obra obra = new Obra();
        obra.setId(rs.getInt("id"));
        obra.setTitulo(rs.getString("titulo"));
        obra.setAutor(rs.getString("autor"));
        obra.setAssunto(rs.getString("assunto"));
        obra.setIsbn(rs.getString("isbn"));
        obra.setEditora(rs.getString("editora"));
        obra.setAnoPublicacao(rs.getInt("ano_publicacao"));
        obra.setNumeroPaginas(rs.getInt("numero_paginas"));
        obra.setDescricao(rs.getString("descricao"));
        
        var created = rs.getTimestamp("created_at");
        if (created != null) {
            obra.setCriadoEm(created.toLocalDateTime());
        }
        
        var updated = rs.getTimestamp("updated_at");
        if (updated != null) {
            obra.setAtualizadoEm(updated.toLocalDateTime());
        }
        
        return obra;
    }
}
