package it.polimi.ingsw.utils.exceptions;

public class FullDiningRoomException extends GameException{
    public FullDiningRoomException(){
        super("FullDiningRoomException: The dining room is full");
    }

}
