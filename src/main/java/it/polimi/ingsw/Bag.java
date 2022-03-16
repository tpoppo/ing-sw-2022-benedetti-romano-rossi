package it.polimi.ingsw;

import java.util.Random;

public class Bag {
    final static int MAX_STUDENTS = 126;
    final private Students students;
    private Random rng;

    public Bag(Students students) {
        this.students = students;
    }

    public Color drawStudent(){
        int size = students.values().stream().reduce(0, Integer::sum);
        int color_index = rng.nextInt(size);

        int count = 0;
        for (Color color : students.keySet()) {
            count += students.get(color);

            if(color_index - count <= 0) return color;
        }

        return null;
    }
}
