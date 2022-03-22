package it.polimi.ingsw.characters;

import it.polimi.ingsw.Game;
import it.polimi.ingsw.PlayerChoices;
import it.polimi.ingsw.Requirements;
import it.polimi.ingsw.board.Island;
import it.polimi.ingsw.exceptions.BadPlayerChoiceException;

public class Conqueror extends Character{
    private Island chosen_island;

    public Conqueror(Game game) {
        super(2, game);
    }

    @Override
    public Requirements require() {
        return Requirements.ISLAND;
    }

    @Override
    void onActivation(Game game, PlayerChoices playerChoices) throws BadPlayerChoiceException {
        chosen_island = playerChoices.getIsland();
        chosen_island.setMotherNature(true);
    }

    @Override
    void onDeactivation(Game game, PlayerChoices playerChoices){
        boolean overlying_mother_nature = true;

        for(Island island : game.getIslands()) {
            if(island.hasMotherNature() && !island.equals(chosen_island))
                overlying_mother_nature = false;
        }

        if(!overlying_mother_nature) chosen_island.setMotherNature(false);
    }
}
