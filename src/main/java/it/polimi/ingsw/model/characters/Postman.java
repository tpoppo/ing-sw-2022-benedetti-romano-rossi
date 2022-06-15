package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameModifiers;

import java.io.Serial;

public class Postman extends Character{
    @Serial
    private static final long serialVersionUID = -4653799686486683334L;

    public Postman() {
        super(1);
        setDescription("You may move Mother Nature up to 2 additional Islands than is indicated by the Assistant card you've played. ");
    }

    @Override
    protected void onActivation(Game game, PlayerChoices playerChoices) {
        GameModifiers gameModifiers = game.getGameModifiers();
        gameModifiers.setExtraSteps(2);
    }

    @Override
    protected void onDeactivation(Game game) {
        GameModifiers gameModifiers = game.getGameModifiers();
        gameModifiers.setExtraSteps(0);
    }

}
