package it.polimi.ingsw.model;

import it.polimi.ingsw.model.board.Color;

import javax.swing.text.html.Option;
import java.util.Optional;

public class GameModifiers {
    private int buff_influence;
    private Optional<Color> inhibit_color;
    private boolean inhibit_towers;
    private int professor_modifier; // reduce the number of student needed to get the control of the professor
    private int extra_steps;

    public GameModifiers(){
        buff_influence = 0;
        inhibit_color = Optional.empty();
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

    public Optional<Color> getInhibitColor() {
        return inhibit_color;
    }

    public void setInhibitColor(Color inhibit_color) {
        this.inhibit_color = Optional.ofNullable(inhibit_color);
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
