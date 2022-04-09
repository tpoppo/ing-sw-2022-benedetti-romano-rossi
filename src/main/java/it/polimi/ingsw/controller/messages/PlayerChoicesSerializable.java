package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.characters.PlayerChoices;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;

import java.io.Serializable;
import java.util.ArrayList;

public class PlayerChoicesSerializable implements Serializable {
    private Integer island;
    private ArrayList<Color> student;

    public Integer getIsland() throws BadPlayerChoiceException {
        if(island == null) throw new BadPlayerChoiceException();
        return island;
    }

    public void setIsland(Integer island) {
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
        if(student == null) student = new ArrayList<Color>();
        student.add(color);
    }

    PlayerChoices toPlayerChoices(Game game){
        PlayerChoices p = new PlayerChoices();
        p.setStudent(new ArrayList<>(student));
        p.setIsland(island == null ? null : game.getIslands().get(island));
        return p;
    }

}
