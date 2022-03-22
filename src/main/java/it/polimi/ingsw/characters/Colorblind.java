package it.polimi.ingsw.characters;

import it.polimi.ingsw.Game;
import it.polimi.ingsw.PlayerChoices;
import it.polimi.ingsw.Requirements;
import it.polimi.ingsw.board.Color;
import it.polimi.ingsw.exceptions.BadPlayerChoiceException;

public class Colorblind extends Character{
    public Colorblind() {
        super(3);
    }

    @Override
    public Requirements require() {
        return Requirements.STUDENT_COLOR;
    }

    @Override
    void onActivation(Game game, PlayerChoices playerChoices) throws BadPlayerChoiceException {
        Color chosen_color = playerChoices.getStudent().get(0);

        game.getGameModifiers().setInhibitColor(chosen_color);
    }

    @Override
    void onDeactivation(Game game, PlayerChoices playerChoices){
        game.getGameModifiers().setInhibitColor(null);
    }

}
