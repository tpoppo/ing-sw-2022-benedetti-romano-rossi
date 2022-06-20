package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;

import java.io.Serial;

public class Headmaster extends Character{
    @Serial
    private static final long serialVersionUID = 2286509594470422106L;

    public Headmaster() {
        super(2);
        setDescription("During this turn, you take control of any number of Professors even if you have the same number of Students as the player who currently controls them.");
    }

    @Override
    protected void onActivation(Game game, PlayerChoices playerChoices) {
        game.getGameModifiers().setProfessorModifier(1);
    }

    @Override
    protected void onDeactivation(Game game) {
        game.getGameModifiers().setProfessorModifier(0);
    }
}
