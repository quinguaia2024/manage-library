package ao.co.imetro.sgbu.model.entity;

import ao.co.imetro.sgbu.model.enums.StatusMulta;
import ao.co.imetro.sgbu.model.enums.MotivoMulta;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade representando uma multa por atraso de devolução ou danos
 */
public class Multa {
    private int id;
    private int usuarioId;
    private int emprestimoId;
    private double valor;
    private int diasAtraso;
    private MotivoMulta motivo;
    private LocalDateTime dataGeracao;
    private LocalDate dataPagamento;
    private StatusMulta status;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    // Referências para facilitar joins
    private Usuario usuario;
    private Emprestimo emprestimo;

    // Constantes de cálculo
    public static final double TAXA_DIARIA = 200; // 200 Kz por dia

    // Construtores
    public Multa() {}

    public Multa(int usuarioId, int emprestimoId, int diasAtraso) {
        this.usuarioId = usuarioId;
        this.emprestimoId = emprestimoId;
        this.diasAtraso = diasAtraso;
        this.valor = diasAtraso * TAXA_DIARIA;
        this.motivo = MotivoMulta.ATRASO;
        this.dataGeracao = LocalDateTime.now();
        this.status = StatusMulta.ABERTA;
    }
    
    public Multa(int usuarioId, int emprestimoId, double valor, MotivoMulta motivo) {
        this.usuarioId = usuarioId;
        this.emprestimoId = emprestimoId;
        this.valor = valor;
        this.motivo = motivo;
        this.diasAtraso = 0;
        this.dataGeracao = LocalDateTime.now();
        this.status = StatusMulta.ABERTA;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public int getEmprestimoId() { return emprestimoId; }
    public void setEmprestimoId(int emprestimoId) { this.emprestimoId = emprestimoId; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public int getDiasAtraso() { return diasAtraso; }
    public void setDiasAtraso(int diasAtraso) { this.diasAtraso = diasAtraso; }

    public MotivoMulta getMotivo() { return motivo; }
    public void setMotivo(MotivoMulta motivo) { this.motivo = motivo; }

    public LocalDateTime getDataGeracao() { return dataGeracao; }
    public void setDataGeracao(LocalDateTime dataGeracao) { this.dataGeracao = dataGeracao; }

    public LocalDate getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDate dataPagamento) { this.dataPagamento = dataPagamento; }

    public StatusMulta getStatus() { return status; }
    public void setStatus(StatusMulta status) { this.status = status; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Emprestimo getEmprestimo() { return emprestimo; }
    public void setEmprestimo(Emprestimo emprestimo) { this.emprestimo = emprestimo; }

    /**
     * Marca a multa como paga
     */
    public void marcarComoPaga() {
        this.status = StatusMulta.PAGA;
        this.dataPagamento = LocalDate.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Multa{" +
                "id=" + id +
                ", usuarioId=" + usuarioId +
                ", valor=" + valor +
                ", diasAtraso=" + diasAtraso +
                ", status=" + status +
                '}';
    }
}
