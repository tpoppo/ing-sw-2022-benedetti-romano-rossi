package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;

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
    void onDeactivation(Game game){
        game.getGameModifiers().setInhibitColor(null);
    }

}
