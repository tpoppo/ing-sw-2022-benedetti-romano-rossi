package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Students;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;
import it.polimi.ingsw.utils.exceptions.EmptyMovableException;

import java.io.Serial;

public class Thief extends Character{
    @Serial
    private static final long serialVersionUID = 2326534445678915384L;
    final private int THEFT_COUNT = 3;

    public Thief() {
        super(3);
        setDescription("""
                Choose a type of Student: every player (including yourself) must return 3 Students of that type from their Dining Room to the bag. If any player has fewer than 3 Students of that type, return as many Students as they have.

                Requirements: <color of the student>""");
    }

    @Override
    public Requirements require() {
        return Requirements.STUDENT_COLOR;
    }

    @Override
    protected void onActivation(Game game, PlayerChoices playerChoices) throws BadPlayerChoiceException {
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
    protected void onDeactivation(Game game){}
}
