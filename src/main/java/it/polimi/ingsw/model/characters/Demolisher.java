package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;

import java.io.Serial;

public class Demolisher extends Character{
    @Serial
    private static final long serialVersionUID = -6527201778112407383L;

    public Demolisher() {
        super(3);
        setDescription("When resolving a Conquering on an Island, Towers do not count towards influence.");
    }

    @Override
    protected void onActivation(Game game, PlayerChoices playerChoices){
        game.getGameModifiers().setInhibitTowers(true);
    }

    @Override
    protected void onDeactivation(Game game){
        game.getGameModifiers().setInhibitTowers(false);
    }
}
