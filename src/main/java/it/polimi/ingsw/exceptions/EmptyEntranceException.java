package it.polimi.ingsw.exceptions;

public class EmptyEntranceException extends GameException {
    public EmptyEntranceException(){
        super("EmptyCloudException: not enough students available in the cloud");
    }
}
