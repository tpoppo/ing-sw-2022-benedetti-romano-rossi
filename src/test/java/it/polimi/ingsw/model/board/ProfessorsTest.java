package it.polimi.ingsw.model.board;

import it.polimi.ingsw.utils.exceptions.EmptyMovableException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of the class Professors
 */
public class ProfessorsTest {
    /**
     * test getter and setter of the class
     */
    @Test
    public void GetterSetter(){
        Professors professor1 = new Professors();
        professor1.add(Color.CYAN);
        professor1.add(Color.GREEN);
        professor1.add(Color.RED);
        professor1.add(Color.MAGENTA);

        Professors professor2 = new Professors();
        professor2.add(Color.CYAN);
        professor2.add(Color.GREEN);
        professor2.add(Color.MAGENTA);
        professor2.add(Color.YELLOW);

        assertTrue(professor1.contains(Color.RED));
        assertFalse(professor2.contains(Color.RED));

        Professors professor3 = new Professors(professor1);
        assertEquals(professor1, professor3);
    }

    /**
     * Test the method moveTo moving a color from a professor to another
     * @throws EmptyMovableException if the color that the method is trying to move isn't in the professor's set
     */
    @Test
    public void moveTo() throws EmptyMovableException {
        Professors professor1 = new Professors();
        professor1.add(Color.CYAN);
        professor1.add(Color.GREEN);
        professor1.add(Color.MAGENTA);
        professor1.add(Color.YELLOW);

        Professors professor2 = new Professors();
        professor2.add(Color.GREEN);
        professor2.add(Color.RED);
        professor2.add(Color.YELLOW);

        professor1.moveTo(professor2, Color.MAGENTA);
        assertTrue(professor2.contains(Color.MAGENTA));
    }

    /**
     * Test that, when the color that the method is trying to move isn't in the professor's set, the exception moveToException is called
     */
    @Test
    public void moveToException(){
        Professors professor1 = new Professors();
        professor1.add(Color.CYAN);
        professor1.add(Color.GREEN);
        professor1.add(Color.MAGENTA);
        professor1.add(Color.YELLOW);

        Professors professor2 = new Professors();
        professor2.add(Color.GREEN);
        professor2.add(Color.YELLOW);

        assertThrows(EmptyMovableException.class, () -> professor1.moveTo(professor2, Color.RED));
        assertFalse(professor2.contains(Color.RED));
    }
}
