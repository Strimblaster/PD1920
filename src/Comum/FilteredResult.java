package Comum;

import java.util.ArrayList;

public class FilteredResult {

    public ArrayList<Song> songs;
    public ArrayList<Playlist> playlists;

    public FilteredResult(ArrayList<Song> songs, ArrayList<Playlist> playlists) {
        this.songs = songs;
        this.playlists = playlists;
    }

    @Override
    public String toString() {
        return "FilteredResult{" +
                "songs=" + songs +
                ", playlists=" + playlists +
                '}';
    }
}
