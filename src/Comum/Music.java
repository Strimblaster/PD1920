package Comum;

public class Music {
    private String nome;
    private String autor;
    private String album;
    private int ano;
    private int duracao;
    private String genero;
    private String filename;

    public Music(String nome, String autor, String album, int ano, int duracao, String genero, String filename) {
        this.nome = nome;
        this.autor = autor;
        this.album = album;
        this.ano = ano;
        this.duracao = duracao;
        this.genero = genero;
        this.filename = filename;
    }

    public String getNome() {
        return nome;
    }

    public String getAutor() {
        return autor;
    }

    public String getAlbum() {
        return album;
    }

    public int getAno() {
        return ano;
    }

    public int getDuracao() {
        return duracao;
    }

    public String getGenero() {
        return genero;
    }

    public String getFilename() {
        return filename;
    }
}
