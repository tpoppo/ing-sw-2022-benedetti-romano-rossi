package it.polimi.ingsw;

public class SchoolBoard {
    final private int num_towers;
    final private Professors professors;
    private Students dining_students;
    private Students entrance_students;


    public SchoolBoard(int num_towers, Professors professors, Students dining_students, Students entrance_students) {
        this.num_towers = num_towers;
        this.professors = professors;
        this.dining_students = dining_students;
        this.entrance_students = entrance_students;
    }

    public Students getDiningStudents() {
        return (Students) dining_students.clone();
    }

    public void setDiningStudents(Students dining_students) {
        this.dining_students = dining_students;
    }

    public Students getEntranceStudents() {
        return (Students) entrance_students.clone();
    }

    public void setEntranceStudents(Students entrance_students) {
        this.entrance_students = entrance_students;
    }


}
