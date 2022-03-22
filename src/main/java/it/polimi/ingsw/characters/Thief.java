package it.polimi.ingsw.characters;

import it.polimi.ingsw.Game;
import it.polimi.ingsw.Player;
import it.polimi.ingsw.PlayerChoices;
import it.polimi.ingsw.Requirements;
import it.polimi.ingsw.board.Color;
import it.polimi.ingsw.board.Students;
import it.polimi.ingsw.exceptions.BadPlayerChoiceException;
import it.polimi.ingsw.exceptions.EmptyMovableException;

public class Thief extends Character{
    final private int THEFT_COUNT = 3;

    public Thief(Game game) {
        super(3, game);
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
                    e.printStackTrace();
                }
            }
        }

        game.getBag().setStudents(bagStudents);
    }

    @Override
    void onDeactivation(Game game, PlayerChoices playerChoices){}
}
