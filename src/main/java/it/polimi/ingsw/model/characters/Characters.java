package it.polimi.ingsw.model.characters;

import java.util.Random;

/**
 * enum that contains all the characters
 */
public enum Characters {
    BARD,
    PRINCESS,
    COLORBLIND,
    HERALD,
    DEMOLISHER,
    HEADMASTER,
    KNIGHT,
    JUGGLER,
    WITCH,
    POSTMAN,
    MONK,
    THIEF;

    static final Random rng = new Random();

    public static Characters randomCharacter(){
        return values()[rng.nextInt(values().length)];
    }
}
