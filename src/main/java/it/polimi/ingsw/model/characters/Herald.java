package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.board.Island;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;

import java.io.Serial;

public class Herald extends Character{
    @Serial
    private static final long serialVersionUID = -3708599904449301484L;
    private Island chosen_island;

    public Herald() {
        super(3);
        setDescription("""
                Choose an Island and resolve the Island as if Mother Nature had ended her movement there. Mother Nature will still move and the Island where she ends her movement will also be resolved.
                
                Requirements: <island position>""");
    }

    @Override
    public Requirements require() {
        return Requirements.ISLAND;
    }

    @Override
    protected void onActivation(Game game, PlayerChoices playerChoices) throws BadPlayerChoiceException {
        chosen_island = playerChoices.getIsland();
        game.conquerIsland(chosen_island);
    }

    @Override
    protected void onDeactivation(Game game){
        boolean overlying_mother_nature = true;

        for(Island island : game.getIslands()) {
            if(island.hasMotherNature() && !island.equals(chosen_island))
                overlying_mother_nature = false;
        }

        if(!overlying_mother_nature) chosen_island.setMotherNature(false);
    }
}
