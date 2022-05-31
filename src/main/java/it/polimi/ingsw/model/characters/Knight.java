package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;

public class Knight extends Character{

    private static final long serialVersionUID = 3470080749611807623L;

    public Knight() {
        super(2);
        setDescription("During the influence calculation this turn, you count as having 2 more influence. ");
    }

    @Override
    void onActivation(Game game, PlayerChoices playerChoices) {
        game.getGameModifiers().setBuffInfluence(2);
    }

    @Override
    void onDeactivation(Game game) {
        game.getGameModifiers().setBuffInfluence(0);
    }
}
