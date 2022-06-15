package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Students;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;
import it.polimi.ingsw.utils.exceptions.EmptyBagException;
import it.polimi.ingsw.utils.exceptions.EmptyMovableException;

import java.io.Serial;
import java.util.ArrayList;

public class Princess extends Character{
    @Serial
    private static final long serialVersionUID = -8501442375720696617L;
    Students students;

    public Princess(Game game){
        super(2);
        setDescription("""
                 Take 1 Student from this card and place it in your Dining Room. Then, draw a new Student from the Bag and place it on this card.

                Requirements:  <color of the student>""");
        students = new Students();

        for(int i=0; i<4; i++){
            try {
                Color color = game.drawStudentFromBag();
                students.put(color, students.get(color) + 1);
            } catch (EmptyBagException ignored) {}
        }
    }

    @Override
    protected void onActivation(Game game, PlayerChoices playerChoices) throws BadPlayerChoiceException {
        ArrayList<Color> colors = playerChoices.getStudent();

        if(colors.size() != 1){ // Invalid input. There must be only one color in playerChoices
            throw new BadPlayerChoiceException();
        }

        Color color = colors.get(0);
        Students dining_students = game.getCurrentPlayer().getSchoolBoard().getDiningStudents();

        try {
            students.moveTo(dining_students, color);
        } catch (EmptyMovableException e) { // Invalid input. There must be at least one student of that color on this card.
            throw new BadPlayerChoiceException();
        }
        game.getCurrentPlayer().getSchoolBoard().setDiningStudents(dining_students);

        try {
            color = game.drawStudentFromBag();
            students.put(color, students.get(color) + 1);
        } catch (EmptyBagException e) { // the bag is empty
            throw new BadPlayerChoiceException();
        }
    }

    @Override
    protected void onDeactivation(Game game) {

    }

    @Override
    public Students getStudents(){
        return new Students(students);
    }

    @Override
    public Requirements require(){
        return Requirements.CARD_STUDENT;
    }

}
