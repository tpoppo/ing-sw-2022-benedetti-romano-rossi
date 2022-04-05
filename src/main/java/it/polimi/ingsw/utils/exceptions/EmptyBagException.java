package it.polimi.ingsw.utils.exceptions;

public class EmptyBagException extends GameException {
    public EmptyBagException(){
        super("EmptyBagException: Empty Bag");
    }
}
