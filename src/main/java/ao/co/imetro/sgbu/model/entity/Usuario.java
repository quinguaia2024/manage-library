package ao.co.imetro.sgbu.model.entity;

import ao.co.imetro.sgbu.model.enums.PerfilUsuario;
import java.time.LocalDateTime;

/**
 * Entidade representando um usuário do sistema
 */
public class Usuario {
    private int id;
    private String nome;
    private String email;
    private String senha;
    private PerfilUsuario perfil;
    private boolean ativo;
    private int limiteEmprestimos;
    private int prazoDias;
    private double limiteMulta;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    // Construtores
    public Usuario() {}

    public Usuario(String nome, String email, String senha, PerfilUsuario perfil) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.perfil = perfil;
        this.ativo = true;
        this.limiteEmprestimos = perfil.getLimiteEmprestimos();
        this.prazoDias = perfil.getPrazoDias();
        this.limiteMulta = 100000; // 100.000 Kz
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public PerfilUsuario getPerfil() { return perfil; }
    public void setPerfil(PerfilUsuario perfil) { this.perfil = perfil; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public int getLimiteEmprestimos() { return limiteEmprestimos; }
    public void setLimiteEmprestimos(int limiteEmprestimos) { this.limiteEmprestimos = limiteEmprestimos; }

    public int getPrazoDias() { return prazoDias; }
    public void setPrazoDias(int prazoDias) { this.prazoDias = prazoDias; }

    public double getLimiteMulta() { return limiteMulta; }
    public void setLimiteMulta(double limiteMulta) { this.limiteMulta = limiteMulta; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", perfil=" + perfil +
                ", ativo=" + ativo +
                '}';
    }
}
