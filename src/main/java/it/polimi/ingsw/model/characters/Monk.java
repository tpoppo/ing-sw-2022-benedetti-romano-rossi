package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Island;
import it.polimi.ingsw.model.board.Students;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;
import it.polimi.ingsw.utils.exceptions.EmptyBagException;
import it.polimi.ingsw.utils.exceptions.EmptyMovableException;

import java.util.ArrayList;

public class Monk extends Character{
    Students students;

    Monk(Game game){
        super(1);
        setDescription("""
                Take 1 Student from this card and place it on an Island of your choice. Then, draw a new Student from the Bag and place it on this card.

                Requirements: <island position> <color of the student>""");
        students = new Students();

        for(int i=0; i<4; i++){
            try {
                Color color = game.drawStudentFromBag();
                students.put(color, students.get(color) + 1);
            } catch (EmptyBagException ignored) {}
        }
    }

    @Override
    void onActivation(Game game, PlayerChoices playerChoices) throws BadPlayerChoiceException {
        Island island = playerChoices.getIsland();
        ArrayList<Color> colors = playerChoices.getStudent();

        if(colors.size() != 1){
            throw new BadPlayerChoiceException();
        }

        Color color = colors.get(0);

        Students island_students = island.getStudents();
        try {
            students.moveTo(island_students, color);
        } catch (EmptyMovableException e) {  // not enough students on this card.
            throw new BadPlayerChoiceException();
        }

        island.setStudents(island_students);
    }

    @Override
    void onDeactivation(Game game) {

    }

    public Requirements require(){
        return Requirements.MOVE_CARD_ISLAND;
    }

    @Override
    public Students getStudents(){
        return new Students(students);
    }
}
