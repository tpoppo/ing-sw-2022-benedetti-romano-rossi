package it.polimi.ingsw.exceptions;

public class BadPlayerChoiceException extends GameException{
    public BadPlayerChoiceException() {
        super("BadPlayerChoiceException: malformed input in player choice");
    }
}
