package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.Game;

public class Influencer extends Character{
    public Influencer() {
        super(2);
    }

    @Override
    void onActivation(Game game, PlayerChoices playerChoices) {
        game.getGameModifiers().setBuffInfluence(2);
    }

    @Override
    void onDeactivation(Game game, PlayerChoices playerChoices) {
        game.getGameModifiers().setBuffInfluence(0);
    }
}
