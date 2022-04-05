package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;

public class Headmaster extends Character{
    public Headmaster() {
        super(2);
    }

    @Override
    void onActivation(Game game, PlayerChoices playerChoices) throws BadPlayerChoiceException {
        game.getGameModifiers().setProfessorModifier(1);
    }

    @Override
    void onDeactivation(Game game, PlayerChoices playerChoices) {
        game.getGameModifiers().setProfessorModifier(0);
    }
}
