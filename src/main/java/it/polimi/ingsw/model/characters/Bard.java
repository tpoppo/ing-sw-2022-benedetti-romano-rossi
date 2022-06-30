package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Students;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;
import it.polimi.ingsw.utils.exceptions.EmptyMovableException;

import java.io.Serial;
import java.util.ArrayList;

public class Bard extends Character{
    @Serial
    private static final long serialVersionUID = -4064825437837380668L;

    public Bard() {
        super(1);
        setDescription("""
                You may exchange up to 2 Students between your Entrance and your Dining Room.

                Requirements: <color of the student in the entrance> <color of the student in the dining>.
                You can also use only the first 2 arguments""");
    }

    @Override
    public Requirements require() {
        return Requirements.SWAP_DINING_ENTRANCE;
    }

    @Override
    protected void onActivation(Game game, PlayerChoices playerChoices) throws BadPlayerChoiceException {
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

        for(int i=0; i<swap_list.size(); i+=2){
            game.updateProfessor(game.getCurrentPlayer(), swap_list.get(i));
        }
    }

    @Override
    protected void onDeactivation(Game game) {}
}
