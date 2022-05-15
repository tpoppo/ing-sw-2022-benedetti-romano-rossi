package it.polimi.ingsw.model.board;

import java.io.Serial;
import java.io.Serializable;

public class SchoolBoard implements Serializable {
    @Serial
    private static final long serialVersionUID = 8344161602434039242L;
    private int num_towers;
    private Professors professors;
    private Students dining_students;
    private Students entrance_students;

    public SchoolBoard(int num_towers, Professors professors, Students dining_students, Students entrance_students) {
        this.num_towers = num_towers;
        this.professors = professors;
        this.dining_students = dining_students;
        this.entrance_students = entrance_students;
    }

    public SchoolBoard(int num_towers){
        this.num_towers = num_towers;
        professors = new Professors();
        dining_students = new Students();
        entrance_students = new Students();
    }

    // Adds (or remove if argument is negative) the num_towers from this.num_towers
    public void addTowers(int num_towers){
        this.num_towers += num_towers;
    }

    public int getNumTowers() {
        return num_towers;
    }

    public void setNumTowers(int num_towers) {
        this.num_towers = num_towers;
    }

    public Professors getProfessors() {return (Professors) professors.clone();}

    public void setProfessors(Professors professors) {this.professors = professors;}

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
