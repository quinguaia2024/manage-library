package ao.co.imetro.sgbu.model.enums;

/**
 * Enum representando os motivos possíveis de uma multa
 */
public enum MotivoMulta {
    ATRASO("Atraso na Devolução"),
    DANOS("Danos ao Material");

    private final String descricao;

    MotivoMulta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
