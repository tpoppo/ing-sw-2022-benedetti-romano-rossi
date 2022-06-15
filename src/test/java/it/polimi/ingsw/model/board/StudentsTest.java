package it.polimi.ingsw.model.board;

import it.polimi.ingsw.utils.exceptions.EmptyMovableException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test of the class Students
 */
public class StudentsTest {

    /**
     * Test of the getter, setter of the class
     */
    @Test
    public void StudentsGetterSetter(){
        Students student = new Students(2, 5, 1, 0, 2);
        Color color = Color.GREEN;
        student.add(color, 5);
        assertEquals(15, student.count());
        student.clear();
        assertEquals(0, student.count());
    }

    /**
     * Test the method moveTo moving a color from a student's set to another
     * @throws EmptyMovableException if the color that the method is trying to move isn't in the student's set
     */
    @Test
    public void moveTo() throws EmptyMovableException {
        Students student1 = new Students(1, 2, 1, 3, 2);
        Students student2 = new Students(0, 3, 1, 3, 3);
        Color color = Color.CYAN;
        student1.moveTo(student2, color);
        assertEquals(1, student1.get(color));
        assertEquals(4, student2.get(color));
    }

    /**
     * Test that, when the color that the method is trying to move isn't in the student's set, the exception moveToException is called
     */
    @Test
    public void moveToException() {
        Students student1 = new Students(0, 2, 1, 3, 2);
        Students student2 = new Students(0, 3, 1, 3, 3);
        Color color = Color.GREEN;
        assertThrows(EmptyMovableException.class, () -> student1.moveTo(student2, color));
        assertEquals(0, student1.get(color));
        assertEquals(0, student2.get(color));
    }
}
