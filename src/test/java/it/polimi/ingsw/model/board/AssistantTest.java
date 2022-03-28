package it.polimi.ingsw.model.board;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AssistantTest {
    @Test
    public void getAssistant(){
        ArrayList<Assistant> wizard1 = new ArrayList<>();
        ArrayList<Assistant> wizard2 = new ArrayList<>();
        ArrayList<Assistant> wizard3 = new ArrayList<>();
        ArrayList<Assistant> wizard4 = new ArrayList<>();

        wizard1 = Assistant.getAssistants(1);
        wizard2 = Assistant.getAssistants(2);
        wizard3 = Assistant.getAssistants(3);
        wizard4 = Assistant.getAssistants(4);

        assertEquals(1, wizard1.get(0).getPower());
        assertEquals(1, wizard1.get(0).getSteps());
        assertEquals(1, wizard1.get(0).getID());
        assertEquals(1, wizard1.get(0).getWizard());

        assertEquals(2, wizard2.get(1).getPower());
        assertEquals(1, wizard2.get(1).getSteps());
        assertEquals(12, wizard2.get(1).getID());
        assertEquals(2, wizard2.get(1).getWizard());

        assertEquals(3, wizard3.get(2).getPower());
        assertEquals(2, wizard3.get(2).getSteps());
        assertEquals(23, wizard3.get(2).getID());
        assertEquals(3, wizard3.get(2).getWizard());

        assertEquals(4, wizard4.get(3).getPower());
        assertEquals(2, wizard4.get(3).getSteps());
        assertEquals(34, wizard4.get(3).getID());
        assertEquals(4, wizard4.get(3).getWizard());
    }
}
