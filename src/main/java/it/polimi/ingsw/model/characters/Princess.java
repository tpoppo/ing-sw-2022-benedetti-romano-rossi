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

        Color selected_color = colors.get(0);
        Students dining_students = game.getCurrentPlayer().getSchoolBoard().getDiningStudents();
        Students princess_students = new Students(students);

        // the dining room must not be full
        if(dining_students.get(selected_color) >= Game.MAX_DINING_STUDENTS){
            throw new BadPlayerChoiceException();
        }

        try {
            princess_students.moveTo(dining_students, selected_color);
        } catch (EmptyMovableException e) { // Invalid input. There must be at least one student of that color on this card.
            throw new BadPlayerChoiceException();
        }

        try {
            Color bag_color = game.drawStudentFromBag();
            princess_students.put(bag_color, princess_students.get(bag_color) + 1);
        } catch (EmptyBagException ignored) {} // the bag is empty

        students = princess_students;
        game.getCurrentPlayer().getSchoolBoard().setDiningStudents(dining_students);
        game.updateProfessor(game.getCurrentPlayer(), selected_color);
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
