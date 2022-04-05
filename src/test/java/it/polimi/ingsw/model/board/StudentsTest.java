package it.polimi.ingsw.model.board;

import it.polimi.ingsw.utils.exceptions.EmptyMovableException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StudentsTest {

    @Test
    public void StudentsGetterSetter(){
        Students student = new Students(2, 5, 1, 0, 2);
        Color color = Color.GREEN;
        student.add(color, 5);
        assertEquals(15, student.count());
        student.clear();
        assertEquals(0, student.count());
    }

    @Test
    public void moveTo() throws EmptyMovableException {
        Students student1 = new Students(1, 2, 1, 3, 2);
        Students student2 = new Students(0, 3, 1, 3, 3);
        Color color = Color.BLUE;
        student1.moveTo(student2, color);
        assertEquals(1, student1.get(color));
        assertEquals(4, student2.get(color));
    }
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
