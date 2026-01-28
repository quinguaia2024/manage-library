package ao.co.imetro.sgbu.model.dao;

import ao.co.imetro.sgbu.model.entity.Usuario;
import ao.co.imetro.sgbu.model.enums.PerfilUsuario;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para a entidade Usuario
 */
public class UsuarioDAO extends BaseDAO<Usuario> {

    public UsuarioDAO() {
        super();
    }

    @Override
    public boolean inserir(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nome, email, senha, perfil, ativo, limite_emprestimos, prazo_dias, limite_multa) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getPerfil().name());
            stmt.setBoolean(5, usuario.isAtivo());
            stmt.setInt(6, usuario.getLimiteEmprestimos());
            stmt.setInt(7, usuario.getPrazoDias());
            stmt.setDouble(8, usuario.getLimiteMulta());
            
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean atualizar(Usuario usuario) throws SQLException {
        // Se a senha estiver vazia/null, não atualizamos ela (mantém a senha atual)
        String sql;
        if (usuario.getSenha() == null || usuario.getSenha().isEmpty()) {
            sql = "UPDATE usuarios SET nome=?, email=?, perfil=?, ativo=?, " +
                    "limite_emprestimos=?, prazo_dias=?, limite_multa=?, updated_at=NOW() WHERE id=?";
        } else {
            sql = "UPDATE usuarios SET nome=?, email=?, senha=?, perfil=?, ativo=?, " +
                    "limite_emprestimos=?, prazo_dias=?, limite_multa=?, updated_at=NOW() WHERE id=?";
        }
        
        try (var stmt = connection.prepareStatement(sql)) {
            int paramIndex = 1;
            stmt.setString(paramIndex++, usuario.getNome());
            stmt.setString(paramIndex++, usuario.getEmail());
            
            // Só define senha se não estiver vazia
            if (usuario.getSenha() != null && !usuario.getSenha().isEmpty()) {
                stmt.setString(paramIndex++, usuario.getSenha());
            }
            
            stmt.setString(paramIndex++, usuario.getPerfil().name());
            stmt.setBoolean(paramIndex++, usuario.isAtivo());
            stmt.setInt(paramIndex++, usuario.getLimiteEmprestimos());
            stmt.setInt(paramIndex++, usuario.getPrazoDias());
            stmt.setDouble(paramIndex++, usuario.getLimiteMulta());
            stmt.setInt(paramIndex++, usuario.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deletar(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id=?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id=?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUsuario(rs);
            }
        }
        return null;
    }

    @Override
    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        
        try (var stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
        }
        return usuarios;
    }

    /**
     * Busca um usuário por email
     */
    public Usuario buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE email=?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUsuario(rs);
            }
        }
        return null;
    }

    /**
     * Busca usuários por perfil
     */
    public List<Usuario> buscarPorPerfil(PerfilUsuario perfil) throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE perfil=? AND ativo=true";
        
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, perfil.name());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
        }
        return usuarios;
    }

    /**
     * Busca usuários ativos
     */
    public List<Usuario> listarAtivos() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE ativo=true ORDER BY nome";
        
        try (var stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
        }
        return usuarios;
    }

    /**
     * Desativa um usuário
     */
    public boolean desativar(int id) throws SQLException {
        String sql = "UPDATE usuarios SET ativo=false, updated_at=NOW() WHERE id=?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Verifica se usuário tem multa pendente acima do limite
     */
    public double getTotalMultasPendentes(int usuarioId) throws SQLException {
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

    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setNome(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));
        usuario.setSenha(rs.getString("senha"));
        usuario.setPerfil(PerfilUsuario.valueOf(rs.getString("perfil")));
        usuario.setAtivo(rs.getBoolean("ativo"));
        usuario.setLimiteEmprestimos(rs.getInt("limite_emprestimos"));
        usuario.setPrazoDias(rs.getInt("prazo_dias"));
        usuario.setLimiteMulta(rs.getDouble("limite_multa"));
        
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) {
            usuario.setCriadoEm(created.toLocalDateTime());
        }
        
        Timestamp updated = rs.getTimestamp("updated_at");
        if (updated != null) {
            usuario.setAtualizadoEm(updated.toLocalDateTime());
        }
        
        return usuario;
    }
}
