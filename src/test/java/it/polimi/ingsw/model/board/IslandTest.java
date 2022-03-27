package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IslandTest {

    @Test
    public void checkConstructorSettersGetters() {
        Island island1 = new Island();
        Island island2 = new Island();

        Students student1 = new Students(2, 3, 1, 0, 2);
        Students student2 = new Students(1, 0, 2, 3, 0);

        Player player1 = new Player("Player1", 0);
        Player player2 = new Player("Player2", 1);

        island1.setMotherNature(true);
        island1.setStudents(student1);
        island1.setNumTowers(1);
        island1.setOwner(player1);
        island1.setNoEntryTiles(0);

        island2.setMotherNature(false);
        island2.setStudents(student2);
        island2.setNumTowers(2);
        island2.setOwner(player2);
        island2.setNoEntryTiles(0);

        Students sum_student = new Students(3, 3, 3, 3, 2);

        island1.merge(island2);

        assertEquals(true, island1.hasMotherNature());
        assertEquals(sum_student, island1.getStudents());
        assertEquals(3, island1.getNumTowers());
        assertEquals(player1, island1.getOwner());
        assertEquals(0, island1.getNoEntryTiles());
    }

}