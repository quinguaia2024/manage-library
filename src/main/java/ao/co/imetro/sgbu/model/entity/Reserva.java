package ao.co.imetro.sgbu.model.entity;

import ao.co.imetro.sgbu.model.enums.StatusReserva;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade representando uma reserva de um exemplar
 */
public class Reserva {
    private int id;
    private int usuarioId;
    private int exemplarId;
    private LocalDateTime dataReserva;
    private StatusReserva status;
    private int posicaoFila;
    private LocalDate dataAtendimento;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    // Referências para facilitar joins
    private Usuario usuario;
    private Exemplar exemplar;

    // Construtores
    public Reserva() {}

    public Reserva(int usuarioId, int exemplarId) {
        this.usuarioId = usuarioId;
        this.exemplarId = exemplarId;
        this.dataReserva = LocalDateTime.now();
        this.status = StatusReserva.ATIVA;
        this.posicaoFila = 1;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public int getExemplarId() { return exemplarId; }
    public void setExemplarId(int exemplarId) { this.exemplarId = exemplarId; }

    public LocalDateTime getDataReserva() { return dataReserva; }
    public void setDataReserva(LocalDateTime dataReserva) { this.dataReserva = dataReserva; }

    public StatusReserva getStatus() { return status; }
    public void setStatus(StatusReserva status) { this.status = status; }

    public int getPosicaoFila() { return posicaoFila; }
    public void setPosicaoFila(int posicaoFila) { this.posicaoFila = posicaoFila; }

    public LocalDate getDataAtendimento() { return dataAtendimento; }
    public void setDataAtendimento(LocalDate dataAtendimento) { this.dataAtendimento = dataAtendimento; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Exemplar getExemplar() { return exemplar; }
    public void setExemplar(Exemplar exemplar) { this.exemplar = exemplar; }

    @Override
    public String toString() {
        return "Reserva{" +
                "id=" + id +
                ", usuarioId=" + usuarioId +
                ", exemplarId=" + exemplarId +
                ", status=" + status +
                ", posicaoFila=" + posicaoFila +
                '}';
    }
}
