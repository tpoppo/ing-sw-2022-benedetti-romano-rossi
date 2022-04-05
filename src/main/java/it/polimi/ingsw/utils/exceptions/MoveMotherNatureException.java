package it.polimi.ingsw.utils.exceptions;

public class MoveMotherNatureException extends GameException {
    public MoveMotherNatureException(){
        super("MoveMotherNatureException: the island is too far");
    }
}
