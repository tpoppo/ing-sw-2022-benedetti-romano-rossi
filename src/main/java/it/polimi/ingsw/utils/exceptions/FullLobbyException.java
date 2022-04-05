package it.polimi.ingsw.utils.exceptions;

public class FullLobbyException extends GameException{
    public FullLobbyException(){
        super("FullLobbyException: The lobby is full");
    }
}
