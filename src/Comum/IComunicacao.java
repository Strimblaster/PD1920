package Comum;

import Comum.Exceptions.*;
import Comum.Pedidos.Resposta;

import java.util.ArrayList;

public interface IComunicacao {

    Resposta login(String username, String password) throws InvalidUsernameException, InvalidPasswordException;
    Resposta signUp(String username, String password) throws InvalidUsernameException, InvalidPasswordException;
    Resposta uploadFile(Utilizador utilizador, Song musica) throws InvalidSongDescriptionException;
    byte [] downloadFile(Utilizador utilizador, Song musica);
    ArrayList<Song> getMusicas(Utilizador utilizador);
    FilteredResult search(Utilizador utilizador, boolean songs, boolean playlists, String nome, String album, String genero, int ano, int duracao);



}
