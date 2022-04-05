package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.exceptions.EmptyMovableException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProfessorsTest {
    @Test
    public void GetterSetter(){
        Professors professor1 = new Professors();
        professor1.add(Color.BLUE);
        professor1.add(Color.GREEN);
        professor1.add(Color.RED);
        professor1.add(Color.PINK);

        Professors professor2 = new Professors();
        professor2.add(Color.BLUE);
        professor2.add(Color.GREEN);
        professor2.add(Color.PINK);
        professor2.add(Color.YELLOW);

        assertEquals(true, professor1.contains(Color.RED));
        assertEquals(false, professor2.contains(Color.RED));

        Professors professor3 = new Professors(professor1);
        assertEquals(professor1, professor3);
    }

    @Test
    public void moveTo() throws EmptyMovableException {
        Professors professor1 = new Professors();
        professor1.add(Color.BLUE);
        professor1.add(Color.GREEN);
        professor1.add(Color.PINK);
        professor1.add(Color.YELLOW);

        Professors professor2 = new Professors();
        professor2.add(Color.GREEN);
        professor2.add(Color.RED);
        professor2.add(Color.YELLOW);

        professor1.moveTo(professor2, Color.PINK);
        assertEquals(true, professor2.contains(Color.PINK));
    }

    @Test
    public void moveToException(){
        Professors professor1 = new Professors();
        professor1.add(Color.BLUE);
        professor1.add(Color.GREEN);
        professor1.add(Color.PINK);
        professor1.add(Color.YELLOW);

        Professors professor2 = new Professors();
        professor2.add(Color.GREEN);
        professor2.add(Color.YELLOW);

        assertThrows(EmptyMovableException.class, () -> professor1.moveTo(professor2, Color.RED));
        assertEquals(false, professor2.contains(Color.RED));
    }
}
