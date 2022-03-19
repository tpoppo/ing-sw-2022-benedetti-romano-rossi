package it.polimi.ingsw.exceptions;

public class EmptyMovableException extends GameException{
    public EmptyMovableException(){
        super("EmptyMovableException: not enough movable");
    }

}
