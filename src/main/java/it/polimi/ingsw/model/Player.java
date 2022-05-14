package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.model.board.Assistant;
import it.polimi.ingsw.model.board.Professors;
import it.polimi.ingsw.model.board.SchoolBoard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Player extends LobbyPlayer implements Serializable {
    private Assistant current_assistant;
    private final String username;
    private int coins;
    final private SchoolBoard schoolBoard;
    private ArrayList<Assistant> playerHand;
    private final int wizard;

    public Player(String username, Assistant current_assistant, int coins, SchoolBoard schoolBoard, ArrayList<Assistant> playerHand, int wizard) {
        super(username);
        this.username = username;
        this.current_assistant = current_assistant;
        this.coins = coins;
        this.schoolBoard = schoolBoard;
        this.playerHand = playerHand;
        this.wizard = wizard;
    }

    public Player(String username, int wizard){
        super(username);
        this.username = username;
        current_assistant = null;
        coins = 0;
        schoolBoard = new SchoolBoard(0);
        this.wizard = wizard;
        playerHand = Assistant.getAssistants(this.wizard);
    }

    public Player(LobbyPlayer player){
        this(player.getUsername(), player.getWizard()); // this should always be != null (as every lobbyPlayer must choose a wizard when entering the lobby)
    }

    public void setPlayerHand(ArrayList<Assistant> playerHand) {
        this.playerHand = playerHand;
    }

    public ArrayList<Assistant> getPlayerHand() {
        return playerHand;
    }

    public Integer getWizard() {
        return wizard;
    }

    public Professors getProfessors(){
        return getSchoolBoard().getProfessors();
    }

    public SchoolBoard getSchoolBoard() {
        return schoolBoard;
    }

    public Assistant getCurrentAssistant() {
        return current_assistant;
    }

    public void setCurrentAssistant(Assistant current_assistant) {
        this.current_assistant = current_assistant;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins){
        this.coins = coins;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Player player = (Player) o;
        return username.equals(player.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username);
    }

    @Override
    public String toString() {
        return "Player{" +
                "current_assistant=" + current_assistant +
                ", username='" + username + '\'' +
                ", coins=" + coins +
                ", schoolBoard=" + schoolBoard +
                ", playerHand=" + playerHand +
                ", wizard=" + wizard +
                '}';
    }
}
