package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.model.board.Assistant;
import it.polimi.ingsw.model.board.Professors;
import it.polimi.ingsw.model.board.SchoolBoard;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class Player represents a Player in the Game state.
 */
public class Player extends LobbyPlayer implements Serializable {
    @Serial
    private static final long serialVersionUID = -2605603161368085648L;
    private Assistant current_assistant;
    private int coins;
    final private SchoolBoard schoolBoard;
    private ArrayList<Assistant> playerHand;

    /**
     * Constructor, creates a Player with the given parameters.
     *
     * @param username the username of the Player.
     * @param current_assistant the Player's current assistant.
     * @param coins the number of coins held by this Player.
     * @param schoolBoard the schoolboard of this Player.
     * @param playerHand the deck of assistant of this Player.
     * @param wizard the wizard ID chosen by this Player.
     */
    public Player(String username, Assistant current_assistant, int coins, SchoolBoard schoolBoard, ArrayList<Assistant> playerHand, int wizard) {
        super(username);
        setWizard(wizard);
        this.current_assistant = current_assistant;
        this.coins = coins;
        this.schoolBoard = schoolBoard;
        this.playerHand = playerHand;
    }

    /**
     * Constructor, creates a "vanilla" player.
     *
     * @param username the username of the Player.
     * @param wizard the wizard ID chosen by this Player.
     */
    public Player(String username, int wizard){
        super(username);
        setWizard(wizard);
        current_assistant = null;
        coins = 0;
        schoolBoard = new SchoolBoard(0);
        playerHand = Assistant.getAssistants(wizard);
    }

    /**
     * Constructor, creates a player starting from the given lobbyPlayer.
     *
     * @param player the lobbyPlayer to create the Player from.
     */
    public Player(LobbyPlayer player){
        this(player.getUsername(), player.getWizard()); // this should always be != null (as every lobbyPlayer must choose a wizard when entering the lobby)
    }

    public void setPlayerHand(ArrayList<Assistant> playerHand) {
        this.playerHand = playerHand;
    }

    public ArrayList<Assistant> getPlayerHand() {
        return playerHand;
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
    public String toString() {
        return "Player{" +
                "current_assistant=" + current_assistant +
                ", coins=" + coins +
                ", schoolBoard=" + schoolBoard +
                ", playerHand=" + playerHand +
                '}';
    }
}
