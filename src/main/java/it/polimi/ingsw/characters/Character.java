package it.polimi.ingsw.characters;

import it.polimi.ingsw.Game;

public class Character {
    private int cost;
    private boolean activated;

    public Character(int cost, boolean activated) {
        this.cost = cost;
        this.activated = activated;
    }


    public int getCost(){
        return cost;
    }

    public void activate(Game game){
        activated = true;
        onActivation(game);
        cost++;
    }

    public void deactivate(Game game){
        activated = false;
        onDeactivation(game);
    }

    private void onActivation(Game game){}

    private void onDeactivation(Game game){}
}
