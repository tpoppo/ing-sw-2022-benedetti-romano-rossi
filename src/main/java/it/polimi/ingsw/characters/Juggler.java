package it.polimi.ingsw.characters;

import it.polimi.ingsw.Game;
import it.polimi.ingsw.Player;
import it.polimi.ingsw.PlayerChoices;
import it.polimi.ingsw.board.Color;
import it.polimi.ingsw.board.Students;
import it.polimi.ingsw.exceptions.BadPlayerChoiceException;
import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.exceptions.EmptyMovableException;

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

        for(int i=0; i<playerChoices.getStudent().size(); i+=2){
            try {
                entrance_students.moveTo(students, playerChoices.getStudent().get(i));
                students.moveTo(entrance_students, playerChoices.getStudent().get(i+1));

            } catch (EmptyMovableException e) {
                throw  new BadPlayerChoiceException(); // shouldn't be reachable
            }
        }

    }

    @Override
    void onDeactivation(Game game, PlayerChoices playerChoices) {

    }

    @Override
    public Students getStudents(){
        return new Students(students);
    }
}
