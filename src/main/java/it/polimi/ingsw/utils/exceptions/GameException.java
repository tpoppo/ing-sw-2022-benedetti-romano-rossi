package it.polimi.ingsw.utils.exceptions;

public class GameException extends Exception{
    final private String info;
    GameException(String info){
        this.info = info;
    }

    @Override
    public String toString() {
        return "GameException("+this.info+")";
    }
}
