package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.board.Island;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;

public class Witch extends  Character{
    int tiles;

    public Witch(){
        super(2);
        setDescription("""
                Place a No Entry tile on an Island of your choice. The first time Mother Nature ends her movement there, put the No Entry tile back onto this card DO NOT calculate influence on that Island, or place any Towers.
                
                Requirements: <island position>""");
        tiles = 4;
    }

    @Override
    void onActivation(Game game, PlayerChoices playerChoices) throws BadPlayerChoiceException {
        if(tiles <= 0) throw new BadPlayerChoiceException();
        tiles--;
        Island island = playerChoices.getIsland();
        island.setNoEntryTiles(island.getNoEntryTiles() + 1);
    }

    @Override
    void onDeactivation(Game game) {

    }

    public Requirements require(){
        return Requirements.ISLAND;
    }

    @Override
    public int getNoEntryTiles(){
        return tiles;
    }

}
