package it.polimi.ingsw.utils.exceptions;

public class AssistantAlreadyPlayedException extends  GameException{
    public AssistantAlreadyPlayedException() {
        super("AssistantAlreadyPlayedException : The assistant has already been chosen");
    }
}
