package ao.co.imetro.sgbu.model.enums;

/**
 * Enum representando os status possíveis de uma reserva
 */
public enum StatusReserva {
    ATIVA("Ativa", "Reserva aguardando exemplar disponível"),
    ATENDIDA("Atendida", "Exemplar disponível para retirada"),
    CANCELADA("Cancelada", "Reserva cancelada pelo usuário ou sistema");

    private final String descricao;
    private final String detalhes;

    StatusReserva(String descricao, String detalhes) {
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
