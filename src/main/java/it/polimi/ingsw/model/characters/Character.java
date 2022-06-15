package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.board.Students;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;

import java.io.Serial;
import java.io.Serializable;

abstract public class Character implements Serializable {
    @Serial
    private static final long serialVersionUID = -6948735520110438443L;
    private int cost;
    private boolean activated;
    private String description;

    public Character(int cost){
        this.cost = cost;
        activated = false;
    }

    public static Character createCharacter(Characters character_to_create, Game game){
        return switch (character_to_create) {
            case PRINCESS -> new Princess(game);
            case BARD -> new Bard();
            case COLORBLIND -> new Colorblind();
            case HERALD -> new Herald();
            case DEMOLISHER -> new Demolisher();
            case HEADMASTER -> new Headmaster();
            case KNIGHT -> new Knight();
            case JUGGLER -> new Juggler(game);
            case WITCH -> new Witch();
            case POSTMAN -> new Postman();
            case MONK -> new Monk(game);
            case THIEF -> new Thief();
        };
        // This line should never be reached
    }

    public int getCost(){
        return cost;
    }

    public void activate(Game game, PlayerChoices playerChoices) throws BadPlayerChoiceException {
        activated = true;
        onActivation(game, playerChoices);
        cost++;
    }

    public void deactivate(Game game){
        if(!activated) return;

        activated = false;
        onDeactivation(game);
    }

    // Tells which students are on top of the card
    // FIXME: does this actually works?
    public Students getStudents(){
        return new Students();
    }

    // Returns the character requirements
    public Requirements require(){
        return Requirements.NOTHING;
    }

    // Returns the character requirements
    public int getNoEntryTiles(){
        return 0;
    }

    public boolean isActivated() { return activated; }

    protected abstract void onActivation(Game game, PlayerChoices playerChoices) throws BadPlayerChoiceException;

    protected abstract void onDeactivation(Game game);

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
