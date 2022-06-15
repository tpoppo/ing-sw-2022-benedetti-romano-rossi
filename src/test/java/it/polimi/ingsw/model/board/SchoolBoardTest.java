package it.polimi.ingsw.model.board;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test of the class Schoolboard
 */
public class SchoolBoardTest {
    /**
     * Test the getter, setter of the class
     */
    @Test
    public void checkConstructorSettersGetters(){
        Professors professor = new Professors();
        Students dining_students = new Students(4, 5, 2, 6, 1);
        Students entrance_students = new Students(2, 2, 1, 3, 1);

        SchoolBoard schoolboard = new SchoolBoard(3, professor, dining_students, entrance_students);

        assertEquals(3, schoolboard.getNumTowers());
        assertEquals(professor, schoolboard.getProfessors());
        assertEquals(dining_students, schoolboard.getDiningStudents());
        assertEquals(entrance_students, schoolboard.getEntranceStudents());
    }
}
