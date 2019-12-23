package Comum;

import Comum.Exceptions.*;
import Comum.Pedidos.Resposta;

import java.util.ArrayList;

public interface IComunicacao {

    Resposta login(String username, String password) throws InvalidUsernameException, InvalidPasswordException;
    Resposta signUp(String username, String password) throws InvalidUsernameException, InvalidPasswordException;
    Resposta uploadFile(Utilizador utilizador, Song musica) throws InvalidSongDescriptionException;
    ArrayList<Song> getMusicas(Utilizador utilizador);

}
