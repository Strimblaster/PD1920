package Comum;

import java.util.ArrayList;

public class Playlist {
    private String name;
    private ArrayList<Song>musicList;
    private Utilizador owner;

    public Playlist(String name, Utilizador owner) {
        this.name = name;
        this.owner = owner;
        this.musicList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Utilizador getOwner() {
        return owner;
    }

    public ArrayList<Song> getMusics() {
        return musicList;
    }
    public void addMusic(Song music){
        this.musicList.add(music);
    }
    public void removeMusic(String name){
        Song toRemove = null;
        for (Song music:musicList) {
            if (music.getNome().equals(name)){
                toRemove = music;
            }
        }
        if(toRemove != null){
            this.musicList.remove(toRemove);
        }
    }
    public void removeMusic(Song music){
        if (isMusicInList(music)){
            this.musicList.remove(music);
        }
    }

    public boolean isMusicInList(Song music){
        for (Song m: musicList) {
            if(m.equals(music)){
                return true;
            }
        }
        return false;
    }

    public ArrayList<Song> getMusicsByAuthor(String author) {
        ArrayList<Song> list = new ArrayList<>();
        for (Song music : musicList) {
            if(music.getAutor().equals( author)){
                list.add(music);
            }
        }
        return list;
    }
    public ArrayList<Song> getMusicsByAlbum(String album) {
        ArrayList<Song> list = new ArrayList<>();
        for (Song music : musicList) {
            if(music.getAlbum().equals(album)){
                list.add(music);
            }
        }
        return list;
    }
    public ArrayList<Song> getMusicsByYear(int year) {
        ArrayList<Song> list = new ArrayList<>();
        for (Song music : musicList) {
            if(music.getAno() == year){
                list.add(music);
            }
        }
        return list;
    }
    public ArrayList<Song> getMusicsByGenre(String genre) {
        ArrayList<Song> list = new ArrayList<>();
        for (Song music : musicList) {
            if(music.getGenero().equals(genre)){
                list.add(music);
            }
        }
        return list;
    }
}
