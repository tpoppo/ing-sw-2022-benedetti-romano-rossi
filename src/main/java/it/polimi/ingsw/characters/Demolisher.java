package it.polimi.ingsw.characters;

import it.polimi.ingsw.Game;
import it.polimi.ingsw.PlayerChoices;
import it.polimi.ingsw.exceptions.BadPlayerChoiceException;

public class Demolisher extends Character{
    public Demolisher(Game game) {
        super(3, game);
    }

    @Override
    void onActivation(Game game, PlayerChoices playerChoices){
        game.getGameModifiers().setInhibitTowers(true);
    }

    @Override
    void onDeactivation(Game game, PlayerChoices playerChoices){
        game.getGameModifiers().setInhibitTowers(false);
    }
}
