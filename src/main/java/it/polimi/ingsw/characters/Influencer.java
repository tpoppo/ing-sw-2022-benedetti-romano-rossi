package it.polimi.ingsw.characters;

import it.polimi.ingsw.Game;
import it.polimi.ingsw.PlayerChoices;

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
