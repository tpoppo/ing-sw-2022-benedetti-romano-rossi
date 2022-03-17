package it.polimi.ingsw;

import it.polimi.ingsw.exceptions.EmptyBagException;

import java.util.Random;

public class Bag {
    final static int MAX_STUDENTS = 126;
    final private Students students;
    private Random rng;

    public Bag(Students students) {
        this.students = students;
    }

    public Color drawStudent() throws EmptyBagException {
        int size = students.values().stream().reduce(0, Integer::sum);
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
}
