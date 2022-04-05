package it.polimi.ingsw.model.board;

import it.polimi.ingsw.utils.exceptions.EmptyBagException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BagTest {
    @Test
    public void Bag() throws EmptyBagException {
        Bag bag = new Bag();
        Color color = null;
        for(int i=0; i<24*5; i++){
            color = bag.drawStudent();
        }
        assertEquals(0, bag.capacity());
        Bag bag2 = new Bag();
        Students student = new Students(2, 3, 1, 0, 4);
        bag2.setStudents(student);
        assertEquals(student, bag2.getStudents());
    }

    @Test
    public void BagException() throws EmptyBagException {
        Bag bag = new Bag();
        Color color = null;
        for(int i=0; i<24*5; i++){
            color = bag.drawStudent();
        }
        assertThrows(EmptyBagException.class, bag::drawStudent);
    }
}
