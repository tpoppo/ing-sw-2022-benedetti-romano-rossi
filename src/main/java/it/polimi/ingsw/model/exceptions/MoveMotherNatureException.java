package it.polimi.ingsw.model.exceptions;

public class MoveMotherNatureException extends GameException {
    public MoveMotherNatureException(){
        super("MoveMotherNatureException: the island is too far");
    }
}
