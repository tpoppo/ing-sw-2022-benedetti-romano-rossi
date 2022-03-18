package it.polimi.ingsw;

import java.util.ArrayList;

public class Assistant {
    final private int power;
    final private int steps;
    final private int id;
    final private int wizard;

    public Assistant(int power, int steps, int id, int wizard) {
        this.power = power;
        this.steps = steps;
        this.id = id;
        this.wizard = wizard;
    }

    public int getWizard() {
        return wizard;
    }

    // Returns all the assistant of the specified wizard
    static public ArrayList<Assistant> getAssistants(int wizard){
        return null;
    }

    public int getPower() {
        return power;
    }

    public int getSteps() {
        return steps;
    }

    public int getId() {
        return id;
    }
}
