package it.polimi.ingsw.exceptions;

public class EmptyBagException extends GameException {
    public EmptyBagException(){
        super("EmptyBagException: Empty Bag");
    }
}
