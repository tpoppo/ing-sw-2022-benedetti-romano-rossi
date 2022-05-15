package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;

public class Demolisher extends Character{
    public Demolisher() {
        super(3);
    }

    @Override
    void onActivation(Game game, PlayerChoices playerChoices){
        game.getGameModifiers().setInhibitTowers(true);
    }

    @Override
    void onDeactivation(Game game){
        game.getGameModifiers().setInhibitTowers(false);
    }
}
