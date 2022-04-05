package it.polimi.ingsw.utils.exceptions;

public class BadPlayerChoiceException extends GameException{
    public BadPlayerChoiceException() {
        super("BadPlayerChoiceException: malformed input in player choice");
    }
}
