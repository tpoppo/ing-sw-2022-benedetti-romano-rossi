package it.polimi.ingsw;

import java.util.List;

public class Island {
    final private int num_towers;
    final private Player owner;
    final private boolean mother_nature;
    final private Students students;

    public Island(int num_towers, Player owner, boolean mother_nature, Students students) {
        this.num_towers = num_towers;
        this.owner = owner;
        this.mother_nature = mother_nature;
        this.students = students;
    }

    public boolean hasMotherNature(){
        return mother_nature;
    }

    public void merge(Island island){

    }

    public void merge(List<Island> islands){

    }
}
