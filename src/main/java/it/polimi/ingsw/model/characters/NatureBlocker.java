package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.board.Island;
import it.polimi.ingsw.model.exceptions.BadPlayerChoiceException;

public class NatureBlocker extends  Character{
    int tiles;

    public NatureBlocker(){
        super(2);
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
    void onDeactivation(Game game, PlayerChoices playerChoices) {

    }

    public Requirements require(){
        return Requirements.ISLAND;
    }

    @Override
    public int getNoEntryTiles(){
        return tiles;
    }

}
