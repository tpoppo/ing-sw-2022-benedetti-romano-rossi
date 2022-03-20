package it.polimi.ingsw.board;

import it.polimi.ingsw.Player;

import java.util.List;

public class Island {
    private int num_towers;
    private Player owner;
    private boolean mother_nature;
    private Students students;

    public Island(int num_towers, Player owner, boolean mother_nature, Students students) {
        this.num_towers = num_towers;
        this.owner = owner;
        this.mother_nature = mother_nature;
        this.students = students;
    }

    public Island(){
        num_towers = 0;
        owner = null;
        mother_nature = false;
        students = new Students();
    }

    public void merge(Island island){
        num_towers += island.num_towers;
        mother_nature |= island.mother_nature;
        for(Students.Entry<Color, Integer> entry : island.getStudents().entrySet()) {
            Color key = entry.getKey();
            int value = entry.getValue();
            students.put(key, students.get(key) + value);
        }
    }

    public void merge(List<Island> islands){

    }

    public boolean hasMotherNature(){
        return mother_nature;
    }

    public void setMotherNature(boolean mother_nature){
        this.mother_nature = mother_nature;
    }

    public Students getStudents() {
        return (Students) students.clone();
    }

    public void setStudents(Students students) {
        this.students = students;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }
}
