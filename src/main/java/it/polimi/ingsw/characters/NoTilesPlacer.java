package it.polimi.ingsw.characters;

import it.polimi.ingsw.Game;
import it.polimi.ingsw.PlayerChoices;
import it.polimi.ingsw.Requirements;
import it.polimi.ingsw.board.Island;
import it.polimi.ingsw.board.Students;
import it.polimi.ingsw.exceptions.BadPlayerChoiceException;

public class NoTilesPlacer extends  Character{
    int tiles;
    public NoTilesPlacer(){
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
