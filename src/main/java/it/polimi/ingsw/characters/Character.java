package it.polimi.ingsw.characters;

import it.polimi.ingsw.Game;
import it.polimi.ingsw.PlayerChoices;
import it.polimi.ingsw.Requirements;
import it.polimi.ingsw.board.Students;
import it.polimi.ingsw.exceptions.BadPlayerChoiceException;

abstract class Character {
    private int cost;
    private boolean activated;
    private Game game;

    public Character(int cost, Game game){
        this.cost = cost;
        this.game = game;
        activated = false;
    }

    public int getCost(){
        return cost;
    }

    public void activate(Game game, PlayerChoices playerChoices) throws BadPlayerChoiceException {
        activated = true;
        onActivation(game, playerChoices);
        cost++;
    }

    public void deactivate(Game game, PlayerChoices playerChoices){
        activated = false;
        onDeactivation(game, playerChoices);
    }

    // Tells which students are on top of the card
    public Students getStudents(){
        return new Students();
    }

    // Returns the character requirements
    public Requirements require(){
        return Requirements.NOTHING;
    }

    abstract void onActivation(Game game, PlayerChoices playerChoices) throws BadPlayerChoiceException;

    abstract void onDeactivation(Game game, PlayerChoices playerChoices);
}
