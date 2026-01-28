package ao.co.imetro.sgbu.model.service;

import ao.co.imetro.sgbu.model.dao.*;
import ao.co.imetro.sgbu.model.entity.*;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.SQLException;
/**
 * Serviço de autenticação de usuários
 */
public class AutenticacaoService {
    private static AutenticacaoService instance;
    private final UsuarioDAO usuarioDAO;
    private Usuario usuarioLogado;
    
    private AutenticacaoService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Retorna a instância única do serviço (Singleton)
     */
    public static AutenticacaoService getInstance() {
        if (instance == null) {
            instance = new AutenticacaoService();
        }
        return instance;
    }

    /**
     * Autentica um usuário com email e senha
     */
    public boolean autenticar(String email, String senha) throws SQLException {
        if (email == null || email.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("Email e senha são obrigatórios");
        }
        
        Usuario usuario = usuarioDAO.buscarPorEmail(email);
        
        if (usuario == null) {
            throw new IllegalArgumentException("Email não encontrado");
        }
        
        if (!usuario.isAtivo()) {
            throw new IllegalArgumentException("Usuário inativo");
        }
        
        // Comparar senha (em produção usar bcrypt)
        if (!verificarSenha(senha, usuario.getSenha())) {
            throw new IllegalArgumentException("Senha incorreta");
        }
        
        this.usuarioLogado = usuario;
        return true;
    }

    /**
     * Faz logout do usuário
     */
    public void logout() {
        this.usuarioLogado = null;
    }

    /**
     * Retorna o usuário logado
     */
    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    /**
     * Verifica se há usuário logado
     */
    public boolean estaLogado() {
        return usuarioLogado != null;
    }

    /**
     * Registra um novo usuário
     */
    public boolean registrar(Usuario usuario) throws SQLException {
        if (usuario == null || usuario.getNome().trim().isEmpty() || 
            usuario.getEmail().trim().isEmpty() || usuario.getSenha().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome, email e senha são obrigatórios");
        }
        
        // Validar email
        if (!isEmailValido(usuario.getEmail())) {
            throw new IllegalArgumentException("Email inválido");
        }
        
        // Verificar se email já existe
        Usuario existe = usuarioDAO.buscarPorEmail(usuario.getEmail());
        if (existe != null) {
            throw new IllegalArgumentException("Email já cadastrado");
        }
        
        // Hash a senha
        usuario.setSenha(hashPassword(usuario.getSenha()));
        usuario.setAtivo(true);
        
        return usuarioDAO.inserir(usuario);
    }

    /**
     * Valida as credenciais
     */
    public String validarCredenciais(String email, String senha) {
        if (email == null || email.trim().isEmpty()) {
            return "Email é obrigatório";
        }
        
        if (!isEmailValido(email)) {
            return "Email inválido";
        }
        
        if (senha == null || senha.trim().isEmpty()) {
            return "Senha é obrigatória";
        }
        
        if (senha.length() < 6) {
            return "Senha deve ter no mínimo 6 caracteres";
        }
        
        return null; // sem erros
    }

    /**
     * Hash simples de senha (em produção, usar bcrypt)
     */
    /**
     * Hash de senha usando BCrypt
     */
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Verifica se a senha informada corresponde ao hash
     */
    /**
     * Verifica se a senha informada corresponde ao hash BCrypt
     */
    private boolean verificarSenha(String senha, String hash) {
        return BCrypt.checkpw(senha, hash);
    }

    /**
     * Valida formato de email básico
     */
    private boolean isEmailValido(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    /**
     * Altera senha do usuário logado
     */
    public boolean alterarSenha(String senhaAtual, String novaSenha) throws SQLException {
        if (usuarioLogado == null) {
            throw new IllegalArgumentException("Nenhum usuário logado");
        }
        
        if (!verificarSenha(senhaAtual, usuarioLogado.getSenha())) {
            throw new IllegalArgumentException("Senha atual incorreta");
        }
        
        if (novaSenha == null || novaSenha.length() < 6) {
            throw new IllegalArgumentException("Nova senha deve ter no mínimo 6 caracteres");
        }
        
        usuarioLogado.setSenha(hashPassword(novaSenha));
        return usuarioDAO.atualizar(usuarioLogado);
    }
}
