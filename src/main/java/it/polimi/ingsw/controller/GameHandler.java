package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.utils.exceptions.EmptyBagException;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class manages the game and contains all the information (both state of the pieces and the game phase)
 */
public class GameHandler implements Serializable {
    @Serial
    private static final long serialVersionUID = -1563449150961541286L;
    public final int ID;
    private Game model;
    private GameState current_state;
    private GameState saved_state;
    private int student_moves;
    private Character selected_character;
    private boolean action_completed;
    private boolean saved_action_completed;

    /**
     * Creates a new game given the lobby.
     *
     * @param ID game id
     * @param expert_mode in which mode the game must start
     * @param lobby_handler lobby (with the player data)
     */
    public GameHandler(int ID, boolean expert_mode, LobbyHandler lobby_handler) {
        this.ID = ID;

        try {
            model = new Game(expert_mode, lobby_handler);
            model.fillClouds();
            model.beginPlanning();
        } catch (EmptyBagException e) {
            // can't create a game
            // this shouldn't happen
            e.printStackTrace();
        }

        current_state = GameState.PLAY_ASSISTANT;
        saved_state = null;
        student_moves = 0;
        selected_character = null;
        action_completed = false;
    }

    /**
     * Given a lobbyPlayer, it finds the matching player (by username) in the current GameHandler
     *
     * @param lobbyPlayer the LobbyPlayer to be translated
     * @return the matching Player in the current GameHandler
     */
    public Player lobbyPlayerToPlayer(LobbyPlayer lobbyPlayer){
        return model.getPlayers().stream()
                .filter(player -> player.getUsername().equals(lobbyPlayer.getUsername()))
                .reduce((a, b) -> {throw new IllegalStateException("Multiple elements: " + a + " " + b);})
                .orElse(null);
    }

    public Game getModel() {
        return model;
    }

    public void setModel(Game model) {
        this.model = model;
    }

    public GameState getCurrentState() {
        return current_state;
    }

    public void setCurrentState(GameState current_state) {
        this.current_state = current_state;
    }

    public GameState getSavedState() {
        return saved_state;
    }

    public void setSavedState(GameState saved_state) {
        this.saved_state = saved_state;
    }

    public int getStudentMoves() {
        return student_moves;
    }

    public void setStudentMoves(int student_moves) {
        this.student_moves = student_moves;
    }

    public Character getSelectedCharacter() {
        return selected_character;
    }

    public void setSelectedCharacter(Character selected_character) {
        this.selected_character = selected_character;
    }

    public boolean isActionCompleted() {
        return action_completed;
    }

    public void setActionCompleted(boolean action_completed) {
        this.action_completed = action_completed;
    }

    public void setSavedActionCompleted(boolean saved_action_completed) {
        this.saved_action_completed = saved_action_completed;
    }

    public boolean isSavedActionCompleted() {
        return saved_action_completed;
    }
}
