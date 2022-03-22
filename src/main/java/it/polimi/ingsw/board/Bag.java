package it.polimi.ingsw.board;

import it.polimi.ingsw.exceptions.EmptyBagException;

import java.util.Random;

public class Bag {
    final static int MAX_STUDENTS = 130;
    private Students students;
    private Random rng;

    public Bag(Students students) {
        this.students = new Students(students);
    }

    public Bag(){
        students = new Students(24, 24, 24, 24, 24);
    }

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

    // Returns the number of students in the bag, no matter the color
    public int capacity(){
        return students.values().stream().reduce(0, Integer::sum);
    }

    public Students getStudents() {
        return students;
    }

    public void setStudents(Students students) {
        this.students = students;
    }
}