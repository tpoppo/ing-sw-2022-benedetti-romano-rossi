package it.polimi.ingsw;

public class SchoolBoard {
    final private int num_towers;
    final private Professors professors;
    final private Students dining_students;
    final private Students entrance_students;


    public SchoolBoard(int num_towers, Professors professors, Students dining_students, Students entrance_students) {
        this.num_towers = num_towers;
        this.professors = professors;
        this.dining_students = dining_students;
        this.entrance_students = entrance_students;
    }
}
