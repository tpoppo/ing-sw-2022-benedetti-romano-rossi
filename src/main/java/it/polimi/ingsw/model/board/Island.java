package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.Player;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Island implements Serializable {
    private int num_towers;
    private Player owner;
    private boolean mother_nature;
    private Students students;
    private int no_entry_tiles;
    private int num_islands;

    public Island(int num_towers, Player owner, boolean mother_nature, Students students, int no_entry_tiles, int num_islands) {
        this.num_towers = num_towers;
        this.owner = owner;
        this.mother_nature = mother_nature;
        this.students = students;
        this.no_entry_tiles = no_entry_tiles;
        this.num_islands = num_islands;
    }

    public Island(){
        this(0, null, false, new Students(), 0, 1);
    }

    public void merge(Island island){
        num_towers += island.num_towers;
        mother_nature |= island.mother_nature;
        no_entry_tiles += island.no_entry_tiles;
        num_islands += island.num_islands;

        for(Map.Entry<Color, Integer> entry : island.getStudents().entrySet()) {
            Color key = entry.getKey();
            int value = entry.getValue();
            students.put(key, students.get(key) + value);
        }
    }

    public void merge(List<Island> islands){
        for(Island island_to_merge : islands){
            merge(island_to_merge);
        }
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

    public int getNoEntryTiles() {
        return no_entry_tiles;
    }

    public void setNoEntryTiles(int no_entry_tiles) {
        this.no_entry_tiles = no_entry_tiles;
    }

    public int getNumTowers() {
        return num_towers;
    }

    public void setNumTowers(int num_towers) {
        this.num_towers = num_towers;
    }

    public int getNumIslands() {return num_islands;}

    public void setNumIslands(int num_islands) {this.num_islands = num_islands;}

}
