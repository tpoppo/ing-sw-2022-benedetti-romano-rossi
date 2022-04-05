package it.polimi.ingsw.utils.exceptions;

public class EmptyMovableException extends GameException{
    public EmptyMovableException(){
        super("EmptyMovableException: not enough movable");
    }

}
