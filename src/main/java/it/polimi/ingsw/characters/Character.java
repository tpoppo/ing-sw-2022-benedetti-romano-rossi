package it.polimi.ingsw.characters;

public class Character {
    final private int cost;
    final private boolean activated;

    public Character(int cost, boolean activated) {
        this.cost = cost;
        this.activated = activated;
    }


    public int getCost(){
        return cost;
    }

    public void activate(){}

    public void deactivate(){}

    public void onActivation(){}
}
