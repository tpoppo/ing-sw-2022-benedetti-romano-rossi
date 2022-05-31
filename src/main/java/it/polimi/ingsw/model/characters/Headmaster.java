package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;

public class Headmaster extends Character{
    private static final long serialVersionUID = 2286509594470422106L;

    public Headmaster() {
        super(2);
        setDescription("During this turn, you take control of any number of Professors even if you have the same number of Students as the player who currently controls them.");
    }

    @Override
    void onActivation(Game game, PlayerChoices playerChoices) throws BadPlayerChoiceException {
        game.getGameModifiers().setProfessorModifier(1);
    }

    @Override
    void onDeactivation(Game game) {
        game.getGameModifiers().setProfessorModifier(0);
    }
}
