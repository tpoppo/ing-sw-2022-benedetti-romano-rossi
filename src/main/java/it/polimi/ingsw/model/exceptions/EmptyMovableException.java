package it.polimi.ingsw.model.exceptions;

public class EmptyMovableException extends GameException{
    public EmptyMovableException(){
        super("EmptyMovableException: not enough movable");
    }

}
