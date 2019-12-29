package Comum;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Playlist {
    @Expose
    private String nome;
    @Expose
    private Utilizador criador;
    private ArrayList<Song> musicas;

    public Playlist(String name, Utilizador owner, ArrayList<Song> musicas) {
        this.nome = name;
        this.criador = owner;
        this.musicas = musicas;
    }

    public Playlist(Playlist p){
        this.nome = p.nome;
        this.musicas = new ArrayList<>(p.getMusicas());
        this.criador = p.criador;
    }

    public void setMusicas(ArrayList<Song> musicas) {
        this.musicas = musicas;
    }

    public String getNome() {
        return nome;
    }

    public Utilizador getCriador() {
        return criador;
    }

    public ArrayList<Song> getMusicas() {
        return musicas;
    }
    public void addMusica(Song music){
        this.musicas.add(music);
    }




    @Override
    public String toString() {
        return nome;
    }
}
