package it.polimi.ingsw;

import it.polimi.ingsw.board.Assistant;
import it.polimi.ingsw.board.Professors;
import it.polimi.ingsw.board.SchoolBoard;

import java.util.ArrayList;
import java.util.Optional;

public class Player {
    private Optional<Assistant> current_assistant;
    final private String username;
    final private int coins;
    final private SchoolBoard schoolBoard;
    private ArrayList<Assistant> playerHand; //TODO
    private int wizard;

    public Player(String username, Assistant current_assistant, int coins, SchoolBoard schoolBoard, ArrayList<Assistant> playerHand, int wizard) {
        this.username = username;
        this.current_assistant = Optional.ofNullable(current_assistant);
        this.coins = coins;
        this.schoolBoard = schoolBoard;
        this.playerHand = playerHand;
        this.wizard = wizard;
    }

    public void setPlayerHand(ArrayList<Assistant> playerHand) {
        this.playerHand = playerHand;
    }

    public ArrayList<Assistant> getPlayerHand() {
        return playerHand;
    }

    public int getWizard() {
        return wizard;
    }

    public Professors getProfessors(){
        return getSchoolBoard().getProfessors();
    }

    public SchoolBoard getSchoolBoard() {
        return schoolBoard;
    }

    public Optional<Assistant> getCurrentAssistant() {
        return current_assistant;
    }

    public void setCurrentAssistant(Assistant current_assistant) {
        this.current_assistant = Optional.ofNullable(current_assistant);
    }

    public void setWizard(int wizard){
        this.wizard = wizard;
    }

    public int getCoins() {
        return coins;
    }

}
