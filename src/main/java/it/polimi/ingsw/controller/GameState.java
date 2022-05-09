package it.polimi.ingsw.controller;

import java.io.Serializable;

public enum GameState implements Serializable {
    PLAY_ASSISTANT,
    MOVE_STUDENT,
    MOVE_MOTHER_NATURE,
    CHOOSE_CLOUD,
    ACTIVATE_CHARACTER,
    FINISHED,
}
