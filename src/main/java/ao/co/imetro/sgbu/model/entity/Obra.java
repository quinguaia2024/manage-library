package ao.co.imetro.sgbu.model.entity;

import java.time.LocalDateTime;

/**
 * Entidade representando uma obra (título de livro) no acervo
 */
public class Obra {
    private int id;
    private String titulo;
    private String autor;
    private String assunto;
    private String isbn;
    private String editora;
    private int anoPublicacao;
    private int numeroPaginas;
    private String descricao;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    // Construtores
    public Obra() {}

    public Obra(String titulo, String autor, String isbn) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getAssunto() { return assunto; }
    public void setAssunto(String assunto) { this.assunto = assunto; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getEditora() { return editora; }
    public void setEditora(String editora) { this.editora = editora; }

    public int getAnoPublicacao() { return anoPublicacao; }
    public void setAnoPublicacao(int anoPublicacao) { this.anoPublicacao = anoPublicacao; }

    public int getNumeroPaginas() { return numeroPaginas; }
    public void setNumeroPaginas(int numeroPaginas) { this.numeroPaginas = numeroPaginas; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }

    @Override
    public String toString() {
        return "Obra{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", isbn='" + isbn + '\'' +
                '}';
    }
}
