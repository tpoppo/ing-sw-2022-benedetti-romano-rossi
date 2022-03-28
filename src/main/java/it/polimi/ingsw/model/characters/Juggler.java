package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Students;
import it.polimi.ingsw.model.exceptions.BadPlayerChoiceException;
import it.polimi.ingsw.model.exceptions.EmptyBagException;
import it.polimi.ingsw.model.exceptions.EmptyMovableException;

public class Juggler extends Character{
    Students students;

    public Juggler(Game game){
        super(1);
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
     * @param game
     * @param playerChoices contains a list of color with even size. (2*i, 2*i+1) values are pairs of color that must be swapped
     * @throws BadPlayerChoiceException
     */
    @Override
    void onActivation(Game game, PlayerChoices playerChoices) throws BadPlayerChoiceException {

        // there must be an even number of students
        if(playerChoices.getStudent().size() % 2 == 0){
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
                throw  new BadPlayerChoiceException(); // shouldn't be reachable
            }
        }
        player.getSchoolBoard().setEntranceStudents(entrance_students);
        students = card_student;

    }

    @Override
    void onDeactivation(Game game, PlayerChoices playerChoices) {

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