package it.polimi.ingsw.model.characters;

import java.util.Random;

public enum Characters {
    BARD,
    CHEF,
    COLORBLIND,
    CONQUEROR,
    DEMOLISHER,
    HEADMASTER,
    INFLUENCER,
    JUGGLER,
    NATUREBLOCKER,
    NATUREMOVER,
    RECRUITER,
    THIEF;

    static Random rng = new Random();

    public static Characters randomCharacter(){
        return values()[rng.nextInt(values().length)];
    }
}
