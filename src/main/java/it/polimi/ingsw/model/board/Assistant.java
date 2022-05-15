package it.polimi.ingsw.model.board;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Assistant implements Serializable {
    final private int power;
    final private int steps;
    final private int ID;
    final private int wizard;

    private Assistant(int power, int steps, int id, int wizard) {
        this.power = power;
        this.steps = steps;
        this.ID = id;
        this.wizard = wizard;
    }

    // Returns all the assistant of the specified wizard
    static public ArrayList<Assistant> getAssistants(int wizard){
        ArrayList<Assistant> assistants = new ArrayList<Assistant>();

        // NOTE: WizardID starts from 1
        // Inserting all the assistant cards
        assistants.add(new Assistant(1, 1, 10 * (wizard - 1) + 1, wizard));
        assistants.add(new Assistant(2, 1, 10 * (wizard - 1) + 2, wizard));
        assistants.add(new Assistant(3, 2, 10 * (wizard - 1) + 3, wizard));
        assistants.add(new Assistant(4, 2, 10 * (wizard - 1) + 4, wizard));
        assistants.add(new Assistant(5, 3, 10 * (wizard - 1) + 5, wizard));
        assistants.add(new Assistant(6, 3, 10 * (wizard - 1) + 6, wizard));
        assistants.add(new Assistant(7, 4, 10 * (wizard - 1) + 7, wizard));
        assistants.add(new Assistant(8, 4, 10 * (wizard - 1) + 8, wizard));
        assistants.add(new Assistant(9, 5, 10 * (wizard - 1) + 9, wizard));
        assistants.add(new Assistant(10, 5, 10 * (wizard - 1) + 10, wizard));

        return assistants;
    }

    public int getWizard() {
        return wizard;
    }

    public int getPower() { return power; }

    public int getSteps() { return steps; }

    public int getID() { return ID; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assistant assistant = (Assistant) o;
        return power == assistant.power && steps == assistant.steps;
    }

    @Override
    public int hashCode() {
        return Objects.hash(power, steps);
    }
}
