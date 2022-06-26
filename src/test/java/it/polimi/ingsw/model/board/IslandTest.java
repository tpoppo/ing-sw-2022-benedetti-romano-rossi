package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test of the class Island
 */
public class IslandTest {

    /**
     * Test getter, setter and merge between two islands
     */
    @Test
    public void checkConstructorSettersGettersMerge() {
        Island island1 = new Island();
        Island island2 = new Island();

        Students student1 = new Students(2, 3, 1, 0, 2);
        Students student2 = new Students(1, 0, 2, 3, 0);

        Player player1 = new Player("Player1", 1);
        Player player2 = new Player("Player2", 2);

        island1.setMotherNature(true);
        island1.setStudents(student1);
        island1.setNumTowers(1);
        island1.setOwner(player1);
        island1.setNoEntryTiles(0);
        island1.setNumIslands(1);

        island2.setMotherNature(false);
        island2.setStudents(student2);
        island2.setNumTowers(2);
        island2.setOwner(player2);
        island2.setNoEntryTiles(0);
        island2.setNumIslands(1);

        Students sum_student = new Students(3, 3, 3, 3, 2);

        island1.merge(island2);

        assertTrue(island1.hasMotherNature());
        assertEquals(sum_student, island1.getStudents());
        assertEquals(3, island1.getNumTowers());
        assertEquals(player1, island1.getOwner());
        assertEquals(0, island1.getNoEntryTiles());
        assertEquals(2, island1.getNumIslands());
    }

    /**
     * Test a merge between three islands
     */
    @Test
    public void mergeListTest(){
        ArrayList<Island> islands = new ArrayList<>();
        Island island1 = new Island();
        Island island2 = new Island();
        Island island3 = new Island();

        Students student1 = new Students(5, 3, 2, 0, 1);
        Students student2 = new Students(1, 5, 2, 6, 4);
        Students student3 = new Students(0, 3, 3, 2, 1);

        Player player1 = new Player("Player1", 0);
        Player player2 = new Player("Player2", 1);
        Player player3 = new Player("Player3", 2);

        island1.setMotherNature(false);
        island1.setStudents(student1);
        island1.setNumTowers(1);
        island1.setOwner(player1);
        island1.setNoEntryTiles(0);

        island2.setMotherNature(true);
        island2.setStudents(student2);
        island2.setNumTowers(2);
        island2.setOwner(player2);
        island2.setNoEntryTiles(0);

        island3.setMotherNature(false);
        island3.setStudents(student3);
        island3.setNumTowers(1);
        island3.setOwner(player3);
        island3.setNoEntryTiles(0);

        islands.add(island2);
        islands.add(island3);

        island1.merge(islands);

        Students sum_student = new Students(6, 11, 7, 8, 6);

        assertTrue(island1.hasMotherNature());
        assertEquals(sum_student, island1.getStudents());
        assertEquals(4, island1.getNumTowers());
        assertEquals(player1, island1.getOwner());
        assertEquals(0, island1.getNoEntryTiles());
    }
}