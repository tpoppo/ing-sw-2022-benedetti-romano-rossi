package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameModifiers;

public class NatureMover extends Character{

    public NatureMover() {
        super(1);
    }

    @Override
    void onActivation(Game game, PlayerChoices playerChoices) {
        GameModifiers gameModifiers = game.getGameModifiers();
        gameModifiers.setExtraSteps(2);
    }

    @Override
    void onDeactivation(Game game, PlayerChoices playerChoices) {
        GameModifiers gameModifiers = game.getGameModifiers();
        gameModifiers.setExtraSteps(0);
    }

}
