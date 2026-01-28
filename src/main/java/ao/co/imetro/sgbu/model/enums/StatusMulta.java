package ao.co.imetro.sgbu.model.enums;

/**
 * Enum representando os status possíveis de uma multa
 */
public enum StatusMulta {
    ABERTA("Aberta", "Multa aguardando pagamento"),
    PAGA("Paga", "Multa já foi paga"),
    CANCELADA("Cancelada", "Multa foi cancelada");

    private final String descricao;
    private final String detalhes;

    StatusMulta(String descricao, String detalhes) {
        this.descricao = descricao;
        this.detalhes = detalhes;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDetalhes() {
        return detalhes;
    }
}
