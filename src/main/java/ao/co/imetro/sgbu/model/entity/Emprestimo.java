package ao.co.imetro.sgbu.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade representando um empréstimo de exemplar
 */
public class Emprestimo {
    private int id;
    private int usuarioId;
    private int exemplarId;
    private LocalDateTime dataEmprestimo;
    private LocalDate dataDevolucaoPrevista;
    private LocalDate dataDevoluaoReal;
    private int renovacoes;
    private boolean ativo;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    // Referências para facilitar joins
    private Usuario usuario;
    private Exemplar exemplar;

    // Construtores
    public Emprestimo() {}

    public Emprestimo(int usuarioId, int exemplarId, LocalDate dataDevolucaoPrevista) {
        this.usuarioId = usuarioId;
        this.exemplarId = exemplarId;
        this.dataEmprestimo = LocalDateTime.now();
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
        this.renovacoes = 0;
        this.ativo = true;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public int getExemplarId() { return exemplarId; }
    public void setExemplarId(int exemplarId) { this.exemplarId = exemplarId; }

    public LocalDateTime getDataEmprestimo() { return dataEmprestimo; }
    public void setDataEmprestimo(LocalDateTime dataEmprestimo) { this.dataEmprestimo = dataEmprestimo; }

    public LocalDate getDataDevolucaoPrevista() { return dataDevolucaoPrevista; }
    public void setDataDevolucaoPrevista(LocalDate dataDevolucaoPrevista) { this.dataDevolucaoPrevista = dataDevolucaoPrevista; }

    public LocalDate getDataDevoluaoReal() { return dataDevoluaoReal; }
    public void setDataDevoluaoReal(LocalDate dataDevoluaoReal) { this.dataDevoluaoReal = dataDevoluaoReal; }

    public int getRenovacoes() { return renovacoes; }
    public void setRenovacoes(int renovacoes) { this.renovacoes = renovacoes; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Exemplar getExemplar() { return exemplar; }
    public void setExemplar(Exemplar exemplar) { this.exemplar = exemplar; }

    /**
     * Verifica se o empréstimo está atrasado
     */
    public boolean estaAtrasado() {
        return LocalDate.now().isAfter(dataDevolucaoPrevista);
    }

    /**
     * Calcula o número de dias de atraso
     */
    public long diasAtraso() {
        LocalDate dataComparacao = dataDevoluaoReal != null ? dataDevoluaoReal : LocalDate.now();
        return java.time.temporal.ChronoUnit.DAYS.between(dataDevolucaoPrevista, dataComparacao);
    }

    @Override
    public String toString() {
        return "Emprestimo{" +
                "id=" + id +
                ", usuarioId=" + usuarioId +
                ", exemplarId=" + exemplarId +
                ", dataDevolucaoPrevista=" + dataDevolucaoPrevista +
                ", ativo=" + ativo +
                '}';
    }
}
