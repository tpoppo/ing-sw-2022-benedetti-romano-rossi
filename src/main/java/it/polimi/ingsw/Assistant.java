package it.polimi.ingsw;

public class Assistant {
    final private int power;
    final private int steps;
    final private int id;

    public Assistant(int power, int steps, int id) {
        this.power = power;
        this.steps = steps;
        this.id = id;
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
