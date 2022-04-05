package it.polimi.ingsw.utils.exceptions;

public class NotEnoughCoinsException extends GameException {
    public NotEnoughCoinsException(int required,  int available){
        super(String.format("NotEnoughCoinsException: required %d coins, available %d coins", required, available));
    }
}
