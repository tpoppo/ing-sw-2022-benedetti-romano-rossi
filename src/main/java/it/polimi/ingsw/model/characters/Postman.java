package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameModifiers;

public class Postman extends Character{

    public Postman() {
        super(1);
        setDescription("You may move Mother Nature up to 2 additional Islands than is indicated by the Assistant card you've played. ");
    }

    @Override
    void onActivation(Game game, PlayerChoices playerChoices) {
        GameModifiers gameModifiers = game.getGameModifiers();
        gameModifiers.setExtraSteps(2);
    }

    @Override
    void onDeactivation(Game game) {
        GameModifiers gameModifiers = game.getGameModifiers();
        gameModifiers.setExtraSteps(0);
    }

}
