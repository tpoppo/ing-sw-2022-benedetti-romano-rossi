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

    public Character(int cost){
        this.cost = cost;
        activated = false;
    }

    public static Character createCharacter(Characters character_to_create, Game game){
        switch (character_to_create){
            case CHEF: return new Chef(game);
            case BARD: return new Bard();
            case COLORBLIND: return new Colorblind();
            case CONQUEROR: return new Conqueror();
            case DEMOLISHER: return new Demolisher();
            case HEADMASTER: return new Headmaster();
            case INFLUENCER: return new Influencer();
            case JUGGLER: return new Juggler(game);
            case NATUREBLOCKER: return new NatureBlocker();
            case NATUREMOVER: return new NatureMover();
            case RECRUITER: return new Recruiter(game);
            case THIEF: return new Thief();
        }
        // This line should never be reached
        return null;
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

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    abstract void onActivation(Game game, PlayerChoices playerChoices) throws BadPlayerChoiceException;

    abstract void onDeactivation(Game game);


}
