package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Game;

public class GameHandler {
    private Game model;
    private GameState current_state;
    private GameState saved_state;
    private int student_moves;
    private int selected_character;
    private boolean action_completed;
}
