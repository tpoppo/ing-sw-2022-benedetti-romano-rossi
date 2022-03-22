package it.polimi.ingsw.characters;

import it.polimi.ingsw.Game;
import it.polimi.ingsw.GameModifiers;
import it.polimi.ingsw.PlayerChoices;
import it.polimi.ingsw.exceptions.BadPlayerChoiceException;

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
