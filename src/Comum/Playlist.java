package Comum;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Playlist {
    @Expose
    private String nome;
    @Expose
    private Utilizador criador;
    private ArrayList<Song> musicas;
    @Expose
    private int id;

    public Playlist(String name, Utilizador owner, ArrayList<Song> musicas, int id) {
        this.nome = name;
        this.criador = owner;
        this.musicas = musicas;
        this.id = id;
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

    public int getId(){
        return id;
    }


    @Override
    public String toString() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome=nome;
    }
}
