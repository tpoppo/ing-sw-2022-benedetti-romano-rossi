package it.polimi.ingsw.model.board;

import it.polimi.ingsw.utils.exceptions.EmptyBagException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test of the class bag
 */
public class BagTest {
    /**
     * Test that the number of possible extractions are equal to the number of expected students in bag
     * @throws EmptyBagException if the bag is empty and someone tries to draw a student
     */
    @Test
    public void Bag() throws EmptyBagException {
        Bag bag = new Bag();
        for(int i=0; i<24*5; i++){
            bag.drawStudent();
        }
        assertEquals(0, bag.capacity());
        Bag bag2 = new Bag();
        Students student = new Students(2, 3, 1, 0, 4);
        bag2.setStudents(student);
        assertEquals(student, bag2.getStudents());
    }

    /**
     * Test that when the bag is empty and someone tries to draw a student the exception EmptyBagException is called
     * @throws EmptyBagException when the bag is empty and someone tries to draw a student
     */
    @Test
    public void BagException() throws EmptyBagException {
        Bag bag = new Bag();
        for(int i=0; i<24*5; i++){
            bag.drawStudent();
        }
        assertThrows(EmptyBagException.class, bag::drawStudent);
    }
}
