package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Island;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;

import java.util.ArrayList;

public class PlayerChoices {
    private Island island;
    private ArrayList<Color> student;

    public Island getIsland() throws BadPlayerChoiceException {
        if(island == null) throw new BadPlayerChoiceException();
        return island;
    }

    public void setIsland(Island island) {
        this.island = island;
    }

    public ArrayList<Color> getStudent() throws BadPlayerChoiceException {
        if(student == null) throw new BadPlayerChoiceException();
        return student;
    }

    public void setStudent(ArrayList<Color> student) {
        this.student = student;
    }

    public void setStudent(Color color){
        if(student == null) student = new ArrayList<>();
        student.add(color);
    }
}
