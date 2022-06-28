package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.board.Students;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;

import java.io.Serial;
import java.io.Serializable;

/**
 * Class Character represents a character of the game.
 * The actual character classes extends this class.
 */
abstract public class Character implements Serializable {
    @Serial
    private static final long serialVersionUID = -6948735520110438443L;
    private int cost;
    private boolean activated;
    private String description;

    /**
     * Constructor, creates a Character with the given cost.
     *
     * @param cost the cost to be paid to activate the character.
     */
    public Character(int cost){
        this.cost = cost;
        activated = false;
    }

    /**
     * Creates the provided character.
     *
     * @param character_to_create the character to be created.
     * @param game the controller to modify.
     * @return the created character.
     */
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

    /**
     * Activates the character by calling onActivation, increasing the cost value and updating the activated value
     *
     * @param game current game
     * @param playerChoices character parameters
     * @throws BadPlayerChoiceException if the parameters are invalid
     */
    public void activate(Game game, PlayerChoices playerChoices) throws BadPlayerChoiceException {
        activated = true;
        onActivation(game, playerChoices);
        cost++;
    }

    /**
     * Activates the character by calling onDeactivation and updating the activated value
     *
     * @param game current game
     */
    public void deactivate(Game game){
        if(!activated) return;

        activated = false;
        onDeactivation(game);
    }

    /**
     * Tells which students are on top of the card
     *
     * @return students on the card
     */
    public Students getStudents(){
        return new Students();
    }

    /**
     * Returns what the character requires
     *
     * @return the requirement of the character
     */
    public Requirements require(){
        return Requirements.NOTHING;
    }

    /**
     * Number of no entry tiles
     *
     * @return Number of no entry tiles
     */
    public int getNoEntryTiles(){
        return 0;
    }

    public boolean isActivated() { return activated; }

    /**
     * It is called when the character is activated
     * @param game current game
     * @param playerChoices character parameters
     * @throws BadPlayerChoiceException if the parameters are invalid
     */
    protected abstract void onActivation(Game game, PlayerChoices playerChoices) throws BadPlayerChoiceException;

    /**
     * It is called when the character is deactivated
     * @param game current game
     */
    protected abstract void onDeactivation(Game game);

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
