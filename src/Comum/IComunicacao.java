package Comum;

import Comum.Exceptions.*;
import Comum.Pedidos.Resposta;

public interface IComunicacao {

    Resposta login(String username, String password) throws InvalidUsernameException, InvalidPasswordException;
    Resposta signUp(String username, String password) throws InvalidUsernameException, InvalidPasswordException;
    Resposta uploadFile(Utilizador utilizador, Song musica) throws InvalidSongDescriptionException;

}
