package it.polimi.ingsw.model.board;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Class Assistant represent an assistant card.
 */
public class Assistant implements Serializable {
    @Serial
    private static final long serialVersionUID = -5617454978786526667L;
    final private int power;
    final private int steps;
    final private int ID;
    final private int wizard;

    /**
     * Constructor, creates an assistant with the given parameters.
     *
     * @param power the assistant's power.
     * @param steps the number of steps that mother nature can do when this assistant is played.
     * @param id the assistant's ID
     * @param wizard the wizard ID.
     */
    private Assistant(int power, int steps, int id, int wizard) {
        this.power = power;
        this.steps = steps;
        this.ID = id;
        this.wizard = wizard;
    }

    /**
     * Returns all the assistants of the specified wizard
     *
     * @param wizard the wizard ID.
     * @return all the assistants of the specified wizard.
     */
    static public ArrayList<Assistant> getAssistants(int wizard){
        ArrayList<Assistant> assistants = new ArrayList<>();

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
