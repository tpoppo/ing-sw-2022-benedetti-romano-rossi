package it.polimi.ingsw.exceptions;

public class FullLobbyException extends GameException{
    public FullLobbyException(){
        super("FullLobbyException: The lobby is full");
    }
}
