package Comum;

public class Song {

    private String nome;
    private Utilizador autor;
    private String album;
    private int ano;
    private int duracao;
    private String genero;
    private String filename;

    public Song(String nome, String album, int ano, int duracao, String genero, String filename) {
        this.nome = nome;
        this.album = album;
        this.ano = ano;
        this.duracao = duracao;
        this.genero = genero;
        this.filename = filename;
    }

    public Song(String nome, Utilizador autor, String album, int ano, int duracao, String genero, String filename) {
        this.nome = nome;
        this.album = album;
        this.ano = ano;
        this.duracao = duracao;
        this.genero = genero;
        this.autor = autor;
        this.filename = filename;
    }

    public String getNome() {
        return nome;
    }

    public Utilizador getAutor() {
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

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String toString() {
        return "Song{" +
                "nome='" + nome + '\'' +
                ", autor=" + autor.toString() +
                ", album='" + album + '\'' +
                ", ano=" + ano +
                ", duracao=" + duracao +
                ", genero='" + genero + '\'' +
                ", filename='" + filename + '\'' +
                '}';
    }
}
