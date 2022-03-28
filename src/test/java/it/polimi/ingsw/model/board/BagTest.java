package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.exceptions.EmptyBagException;
import it.polimi.ingsw.model.exceptions.EmptyMovableException;
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
    }

    public void BagException() throws EmptyBagException {
        Bag bag = new Bag();
        Color color = null;
        for(int i=0; i<24*5; i++){
            color = bag.drawStudent();
        }
        assertThrows(EmptyMovableException.class, bag::drawStudent);
    }
}
