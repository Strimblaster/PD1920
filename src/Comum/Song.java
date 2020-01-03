package Comum;

import com.google.gson.annotations.Expose;

public class Song {

    @Expose
    private String nome;
    @Expose
    private Utilizador autor;
    @Expose
    private String album;
    @Expose
    private int ano;
    @Expose
    private int duracao;
    @Expose
    private String genero;
    @Expose
    private String filename;
    @Expose
    private int id;

    public Song(String nome, String album, int ano, int duracao, String genero, String filename) {
        this.nome = nome;
        this.album = album;
        this.ano = ano;
        this.duracao = duracao;
        this.genero = genero;
        this.filename = filename;
    }

    public Song(String nome, Utilizador autor, String album, int ano, int duracao, String genero, String filename, int id) {
        this.nome = nome;
        this.album = album;
        this.ano = ano;
        this.duracao = duracao;
        this.genero = genero;
        this.autor = autor;
        this.filename = filename;
        this.id = id;
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

    public int getId(){
        return id;
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

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public void setAutor(Utilizador utilizador) {
        this.autor = utilizador;
    }
}
