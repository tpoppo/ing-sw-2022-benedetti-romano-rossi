package it.polimi.ingsw.controller;

public class GameHandler {
    private Game model;
    private GameState current_state;
    private GameState saved_state;
    private int student_moves;
    private int selected_character;
    private boolean action_completed;


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

    public int getSelectedCharacter() {
        return selected_character;
    }

    public void setSelectedCharacter(int selected_character) {
        this.selected_character = selected_character;
    }

    public boolean isActionCompleted() {
        return action_completed;
    }

    public void setActionCompleted(boolean action_completed) {
        this.action_completed = action_completed;
    }
}
