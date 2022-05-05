package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Students;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;
import it.polimi.ingsw.utils.exceptions.EmptyMovableException;

import java.util.ArrayList;

public class Bard extends Character{
    public Bard() {
        super(1);
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

        if(swap_list.size() % 2 != 0 || swap_list.size() > 4) {
            throw new BadPlayerChoiceException();
        }

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
