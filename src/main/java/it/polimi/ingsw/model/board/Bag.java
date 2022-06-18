package it.polimi.ingsw.model.board;

import it.polimi.ingsw.utils.exceptions.EmptyBagException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Random;

/**
 * Class Bad represent the bag containing the students.
 */
public class Bag implements Serializable {
    public final static int MAX_STUDENTS = 130;
    @Serial
    private static final long serialVersionUID = 7998489758588563865L;
    private Students students;
    private final Random rng = new Random();

    /**
     * Constructor, creates a bag filled with the given students.
     *
     * @param students the students that form the bag.
     */
    public Bag(Students students) {
        this.students = new Students(students);
    }

    /**
     * Constructor, creates a bag with the default number of students.
     */
    public Bag(){
        students = new Students(24, 24, 24, 24, 24);
    }

    /**
     * Draws a random student from the bag
     *
     * @return the drawn color
     * @throws EmptyBagException if the bag is empty
     */
    public Color drawStudent() throws EmptyBagException {
        int size = capacity();
        if(size == 0) throw new EmptyBagException();

        int color_index = rng.nextInt(size);

        int count = 0;

        for (Color color : students.keySet()) {
            count += students.get(color);

            if(color_index - count <= 0) {
                students.put(color, students.get(color) - 1);
                return color;
            }
        }

        // This line should never be reached, but the compiler requests its presence
        return null;
    }

    /**
     * @return the number of students in the bag, no matter the color
     */
    public int capacity(){
        return students.count();
    }

    public Students getStudents() {
        return new Students(students);
    }

    public void setStudents(Students students) {
        this.students = students;
    }
}
