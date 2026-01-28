package ao.co.imetro.sgbu.model.enums;

/**
 * Enum representando os estados possíveis de um exemplar
 */
public enum EstadoExemplar {
    DISPONIVEL("Disponível", "Pronto para empréstimo"),
    EMPRESTADO("Emprestado", "Fora da biblioteca"),
    RESERVADO("Reservado", "Reservado por um usuário"),
    DANIFICADO("Danificado", "Não disponível para empréstimo");

    private final String descricao;
    private final String detalhes;

    EstadoExemplar(String descricao, String detalhes) {
        this.descricao = descricao;
        this.detalhes = detalhes;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDetalhes() {
        return detalhes;
    }

    public boolean isPodeEmprestar() {
        return this == DISPONIVEL;
    }
}
