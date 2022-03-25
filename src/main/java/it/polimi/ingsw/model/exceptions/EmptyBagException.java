package it.polimi.ingsw.model.exceptions;

public class EmptyBagException extends GameException {
    public EmptyBagException(){
        super("EmptyBagException: Empty Bag");
    }
}
