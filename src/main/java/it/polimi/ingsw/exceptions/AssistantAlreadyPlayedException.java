package it.polimi.ingsw.exceptions;

public class AssistantAlreadyPlayedException extends  GameException{
    public AssistantAlreadyPlayedException() {
        super("AssistantAlreadyPlayedException : The assistant has already been chosen");
    }
}
