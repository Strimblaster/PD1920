package Comum;

import java.util.ArrayList;

public class Playlist {
    private String name;
    private ArrayList<Music>musicList;
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

    public ArrayList<Music> getMusics() {
        return musicList;
    }
    public void addMusic(Music music){
        this.musicList.add(music);
    }
    public void removeMusic(String name){
        Music toRemove = null;
        for (Music music:musicList) {
            if (music.getNome().equals(name)){
                toRemove = music;
            }
        }
        if(toRemove != null){
            this.musicList.remove(toRemove);
        }
    }
    public void removeMusic(Music music){
        if (isMusicInList(music)){
            this.musicList.remove(music);
        }
    }

    public boolean isMusicInList(Music music){
        for (Music m: musicList) {
            if(m.equals(music)){
                return true;
            }
        }
        return false;
    }

    public ArrayList<Music> getMusicsByAuthor(String author) {
        ArrayList<Music> list = new ArrayList<>();
        for (Music music : musicList) {
            if(music.getAutor().equals( author)){
                list.add(music);
            }
        }
        return list;
    }
    public ArrayList<Music> getMusicsByAlbum(String album) {
        ArrayList<Music> list = new ArrayList<>();
        for (Music music : musicList) {
            if(music.getAlbum().equals(album)){
                list.add(music);
            }
        }
        return list;
    }
    public ArrayList<Music> getMusicsByYear(int year) {
        ArrayList<Music> list = new ArrayList<>();
        for (Music music : musicList) {
            if(music.getAno() == year){
                list.add(music);
            }
        }
        return list;
    }
    public ArrayList<Music> getMusicsByGenre(String genre) {
        ArrayList<Music> list = new ArrayList<>();
        for (Music music : musicList) {
            if(music.getGenero().equals(genre)){
                list.add(music);
            }
        }
        return list;
    }
}
