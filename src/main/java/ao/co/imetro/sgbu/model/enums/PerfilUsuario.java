package ao.co.imetro.sgbu.model.enums;

/**
 * Enum representando os perfis de usuário do sistema
 */
public enum PerfilUsuario {
    ADMINISTRADOR("Administrador", "Acesso total ao sistema"),
    BIBLIOTECARIO("Bibliotecário", "Gestão de acervo e circulação"),
    DOCENTE("Docente", "Empréstimos com prazo maior"),
    ESTUDANTE("Estudante", "Empréstimos básicos");

    private final String descricao;
    private final String detalhes;

    PerfilUsuario(String descricao, String detalhes) {
        this.descricao = descricao;
        this.detalhes = detalhes;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDetalhes() {
        return detalhes;
    }

    /**
     * Retorna os limites de empréstimo por perfil
     */
    public int getLimiteEmprestimos() {
        return switch (this) {
            case ADMINISTRADOR, BIBLIOTECARIO -> 10;
            case DOCENTE -> 5;
            case ESTUDANTE -> 3;
        };
    }

    /**
     * Retorna o prazo em dias por perfil
     */
    public int getPrazoDias() {
        return switch (this) {
            case ADMINISTRADOR -> 30;
            case BIBLIOTECARIO -> 30;
            case DOCENTE -> 14;
            case ESTUDANTE -> 7;
        };
    }

    /**
     * Retorna o limite de multa por perfil (em Kwanzas)
     */
    public double getLimiteMulta() {
        return switch (this) {
            case ADMINISTRADOR, BIBLIOTECARIO -> 20000.0; // 20.000 Kz
            case DOCENTE -> 10000.0; // 10.000 Kz
            case ESTUDANTE -> 4000.0; // 4.000 Kz
        };
    }
}
