package Comum.Pedidos;

import Comum.FilteredResult;
import Comum.Pedidos.Enums.TipoPedido;
import Comum.Utilizador;
import com.google.gson.annotations.Expose;

public class PedidoSearch extends Pedido {

    @Expose
    boolean songs;
    @Expose
    boolean playlists;
    @Expose
    String nome;
    @Expose
    String album;
    @Expose
    String genero;
    @Expose
    int ano;
    @Expose
    int duracao;

    FilteredResult filteredResult = null;

    public PedidoSearch(Utilizador cliente, boolean songs, boolean playlists, String nome, String album, String genero, int ano, int duracao) {
        super(cliente, TipoPedido.PedidoSearch);
        this.songs = songs;
        this.playlists = playlists;
        this.nome = nome;
        this.album = album;
        this.genero = genero;
        this.ano = ano;
        this.duracao = duracao;
    }

    public PedidoSearch(boolean songs, boolean playlists, String nome, String album, String genero, int ano, int duracao) {
        super(TipoPedido.PedidoSearch);
    }

    public boolean isSongs() {
        return songs;
    }

    public boolean isPlaylists() {
        return playlists;
    }

    public String getNome() {
        return nome;
    }

    public String getAlbum() {
        return album;
    }

    public String getGenero() {
        return genero;
    }

    public int getAno() {
        return ano;
    }

    public int getDuracao() {
        return duracao;
    }

    public void setSongs(boolean songs) {
        this.songs = songs;
    }

    public void setPlaylists(boolean playlists) {
        this.playlists = playlists;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }

    public FilteredResult getFilteredResult() {
        return filteredResult;
    }

    public void setFilteredResult(FilteredResult filteredResult) {
        this.filteredResult = filteredResult;
    }

    @Override
    public String toString() {
        return "PedidoSearch{" +
                "songs=" + songs +
                ", playlists=" + playlists +
                ", nome='" + nome + '\'' +
                ", album='" + album + '\'' +
                ", genero='" + genero + '\'' +
                ", ano=" + ano +
                ", duracao=" + duracao +
                ", filteredResult=" + filteredResult +
                '}';
    }
}
