package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Students;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;
import it.polimi.ingsw.utils.exceptions.EmptyBagException;
import it.polimi.ingsw.utils.exceptions.EmptyMovableException;

import java.io.Serial;

public class Juggler extends Character{
    @Serial
    private static final long serialVersionUID = -7901721705819512488L;
    Students students;

    public Juggler(Game game){
        super(1);
        setDescription("""
                You may take up to 3 Students from this card and replace them with the same number of Students from your Entrance.

                Requirements: <color of a student in the entrance> <color of a student in this card>\s""");
        students = new Students();

        for(int i=0; i<6; i++){
            try {
                Color color = game.drawStudentFromBag();
                students.put(color, students.get(color) + 1);
            } catch (EmptyBagException e) {}
        }
    }

    /**
     *
     * @param game is the current game
     * @param playerChoices contains a list of color with even size. (2*i, 2*i+1) values are pairs of color that must be swapped
     * @throws BadPlayerChoiceException Invalid parameters in playerChoices
     */
    @Override
    protected void onActivation(Game game, PlayerChoices playerChoices) throws BadPlayerChoiceException {

        // there must be an even number of students
        if(playerChoices.getStudent().size() % 2 != 0){
            throw new BadPlayerChoiceException();
        }

        // there must be less than 7 students
        if(playerChoices.getStudent().size() >= 7){
            throw new BadPlayerChoiceException();
        }

        Player player = game.getCurrentPlayer();
        Students entrance_students = player.getSchoolBoard().getEntranceStudents();
        Students card_student = new Students(students);

        for(int i=0; i<playerChoices.getStudent().size(); i+=2){
            try {
                entrance_students.moveTo(card_student, playerChoices.getStudent().get(i));
                card_student.moveTo(entrance_students, playerChoices.getStudent().get(i+1));
            } catch (EmptyMovableException e) {
                throw  new BadPlayerChoiceException(); // if it can't swap the two color
            }
        }
        player.getSchoolBoard().setEntranceStudents(entrance_students);
        students = card_student;

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
        return Requirements.SWAP_CARD_ENTRANCE;
    }

}
