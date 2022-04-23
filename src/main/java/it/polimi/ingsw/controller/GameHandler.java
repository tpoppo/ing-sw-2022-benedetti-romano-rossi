package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.characters.Character;

public class GameHandler {
    private Game model;
    private GameState current_state;
    private GameState saved_state;
    private int student_moves;
    private Character selected_character;
    private boolean action_completed;

    // Given a lobbyPlayer, it finds the matching player (by username) in the current GameHandler
    // TODO: do we want a more generic method (maybe static) that requires a networkManager or even that
    //  searches in the entire server?
    public Player lobbyPlayerToPlayer(LobbyPlayer lobbyPlayer){
        return model.getPlayers().stream()
                .filter(player -> player.getUsername().equals(lobbyPlayer.getUsername()))
                .reduce((a, b) -> {throw new IllegalStateException("Multiple elements: " + a + " " + b);})
                .get();
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
}
