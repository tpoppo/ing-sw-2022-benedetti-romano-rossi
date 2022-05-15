package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Students;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;
import it.polimi.ingsw.utils.exceptions.EmptyMovableException;

public class Thief extends Character{
    final private int THEFT_COUNT = 3;

    public Thief() {
        super(3);
    }

    @Override
    public Requirements require() {
        return Requirements.STUDENT_COLOR;
    }

    @Override
    void onActivation(Game game, PlayerChoices playerChoices) throws BadPlayerChoiceException {
        Color chosen_color = playerChoices.getStudent().get(0);
        Students bagStudents = game.getBag().getStudents();

        for(Player player : game.getPlayers()){
            Students diningStudents = player.getSchoolBoard().getDiningStudents();
            int theft_amount = Math.min(THEFT_COUNT, diningStudents.get(chosen_color));

            for(int i=0; i<theft_amount; i++){
                try {
                    diningStudents.moveTo(bagStudents, chosen_color);
                } catch (EmptyMovableException e) {
                    e.printStackTrace(); // it should be unreachable
                }
            }
            player.getSchoolBoard().setDiningStudents(diningStudents);
        }

        game.getBag().setStudents(bagStudents);
    }

    @Override
    void onDeactivation(Game game){}
}
