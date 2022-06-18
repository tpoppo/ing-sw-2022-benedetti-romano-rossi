package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.Color;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class contains the modifiers used in the game and updated by the characters.
 */
public class GameModifiers implements Serializable {
    @Serial
    private static final long serialVersionUID = 7267446910559981331L;
    private int buff_influence;
    private Color inhibit_color;
    private boolean inhibit_towers;
    private int professor_modifier; // reduce the number of student needed to get the control of the professor
    private int extra_steps;

    /**
     * Constructor, sets the modifiers to their default values.
     */
    public GameModifiers(){
        buff_influence = 0;
        inhibit_color = null;
        inhibit_towers = false;
        professor_modifier = 0;
        extra_steps = 0;
    }

    public int getBuffInfluence() {
        return buff_influence;
    }

    public void setBuffInfluence(int buff_influence) {
        this.buff_influence = buff_influence;
    }

    public Color getInhibitColor() {
        return inhibit_color;
    }

    public void setInhibitColor(Color inhibit_color) {
        this.inhibit_color = inhibit_color;
    }

    public boolean isInhibitTowers() {
        return inhibit_towers;
    }

    public void setInhibitTowers(boolean inhibit_towers) {
        this.inhibit_towers = inhibit_towers;
    }

    public int getProfessorModifier() {
        return professor_modifier;
    }

    public void setProfessorModifier(int professor_modifier) {
        this.professor_modifier = professor_modifier;
    }

    public int getExtraSteps() {
        return extra_steps;
    }

    public void setExtraSteps(int extra_steps) {
        this.extra_steps = extra_steps;
    }
}
