package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.Optional;

public class Player {
    final private Optional<Assistant> current_assistant;
    final private int coins;
    final private SchoolBoard schoolBoard; //TODO
    final private ArrayList<Assistant> playerHand; //TODO

    public Player(Assistant current_assistant, int coins, SchoolBoard schoolBoard, ArrayList<Assistant> playerHand) {
        this.current_assistant = Optional.ofNullable(current_assistant);
        this.coins = coins;
        this.schoolBoard = schoolBoard;
        this.playerHand = playerHand;
    }

    public Professors getProfessors(){
        return null;
    }
}
