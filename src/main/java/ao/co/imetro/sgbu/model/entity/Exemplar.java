package ao.co.imetro.sgbu.model.entity;

import ao.co.imetro.sgbu.model.enums.EstadoExemplar;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade representando um exemplar (cópia física) de uma obra
 */
public class Exemplar {
    private int id;
    private int obraId;
    private String codigoTombo;
    private EstadoExemplar estado;
    private String localizacao;
    private LocalDate dataAquisicao;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    // Referência para a obra (opcional, para facilitar joins)
    private Obra obra;

    // Construtores
    public Exemplar() {}

    public Exemplar(int obraId, String codigoTombo) {
        this.obraId = obraId;
        this.codigoTombo = codigoTombo;
        this.estado = EstadoExemplar.DISPONIVEL;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getObraId() { return obraId; }
    public void setObraId(int obraId) { this.obraId = obraId; }

    public String getCodigoTombo() { return codigoTombo; }
    public void setCodigoTombo(String codigoTombo) { this.codigoTombo = codigoTombo; }

    public EstadoExemplar getEstado() { return estado; }
    public void setEstado(EstadoExemplar estado) { this.estado = estado; }

    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }

    public LocalDate getDataAquisicao() { return dataAquisicao; }
    public void setDataAquisicao(LocalDate dataAquisicao) { this.dataAquisicao = dataAquisicao; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }

    public Obra getObra() { return obra; }
    public void setObra(Obra obra) { this.obra = obra; }

    @Override
    public String toString() {
        return "Exemplar{" +
                "id=" + id +
                ", codigoTombo='" + codigoTombo + '\'' +
                ", estado=" + estado +
                ", localizacao='" + localizacao + '\'' +
                '}';
    }
}
