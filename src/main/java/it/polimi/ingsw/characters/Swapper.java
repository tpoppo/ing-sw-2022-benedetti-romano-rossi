package it.polimi.ingsw.characters;

import it.polimi.ingsw.Game;
import it.polimi.ingsw.PlayerChoices;
import it.polimi.ingsw.Requirements;
import it.polimi.ingsw.board.Color;
import it.polimi.ingsw.board.Students;
import it.polimi.ingsw.exceptions.BadPlayerChoiceException;
import it.polimi.ingsw.exceptions.EmptyMovableException;

import java.util.ArrayList;

public class Swapper extends Character{
    public Swapper(Game game) {
        super(1, game);
    }

    @Override
    public Requirements require() {
        return Requirements.SWAP_DINING_ENTRANCE;
    }

    @Override
    void onActivation(Game game, PlayerChoices playerChoices) throws BadPlayerChoiceException {
        ArrayList<Color> swap_list = playerChoices.getStudent();
        Students entranceStudents = game.getCurrentPlayer().getSchoolBoard().getEntranceStudents();
        Students diningStudents = game.getCurrentPlayer().getSchoolBoard().getDiningStudents();

        // assuming that the students are alternated in the swap list (entrance - dining)
        for(int i=0; i<swap_list.size(); i+=2){
            try {
                entranceStudents.moveTo(diningStudents, swap_list.get(i));
                diningStudents.moveTo(entranceStudents, swap_list.get(i + 1));
            } catch (EmptyMovableException e) {
                throw new BadPlayerChoiceException();
            }
        }

        game.getCurrentPlayer().getSchoolBoard().setEntranceStudents(entranceStudents);
        game.getCurrentPlayer().getSchoolBoard().setDiningStudents(diningStudents);
    }

    @Override
    void onDeactivation(Game game, PlayerChoices playerChoices) {}
}
