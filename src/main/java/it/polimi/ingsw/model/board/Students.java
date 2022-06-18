package it.polimi.ingsw.model.board;

import it.polimi.ingsw.utils.exceptions.EmptyMovableException;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;

/**
 * This class represents a group of students
 */
public class Students extends HashMap<Color, Integer> implements Serializable {
    @Serial
    private static final long serialVersionUID = -6465882703969744942L;

    /**
     * It creates a group of students given
     * @param greens number of green students
     * @param blues number of blue students
     * @param yellows number of yellow students
     * @param pinks number of pink students
     * @param reds number of red students
     */
    public Students(int greens, int blues, int yellows, int pinks, int reds){
        this.put(Color.GREEN, greens);
        this.put(Color.CYAN, blues);
        this.put(Color.YELLOW, yellows);
        this.put(Color.MAGENTA, pinks);
        this.put(Color.RED, reds);
    }

    public Students(){
        clear();
    }

    public Students(Students students) {
        super(students);
    }

    /**
     * Moves a student from one group to another one
     * @param to where to move to
     * @param color which color to move
     * @throws EmptyMovableException if the color is missing
     */
    public void moveTo(Students to, Color color) throws EmptyMovableException {
        if(get(color) == 0){
            throw new EmptyMovableException();
        }
        add(color, -1);
        to.add(color, 1);
    }

    /**
     * Adds delta student of the given color
     * @param color color to be added
     * @param delta how many student to be added
     */
    public void add(Color color, int delta){
        this.put(color, this.get(color) + delta);
    }

    /**
     * Adds one student of the given color
     * @param color color to be added
     */
    public void add(Color color){
        this.add(color, 1);
    }

    /**
     * Returns the number of students, no matter the color
     * @return number of students
     */
    public int count(){
        return values().stream().reduce(0, Integer::sum);
    }

    /**
     * Set all color to zero
     * Remove all the students
     */
    @Override
    public void clear() {
        put(Color.GREEN, 0);
        put(Color.CYAN, 0);
        put(Color.YELLOW, 0);
        put(Color.MAGENTA, 0);
        put(Color.RED, 0);
    }
}
