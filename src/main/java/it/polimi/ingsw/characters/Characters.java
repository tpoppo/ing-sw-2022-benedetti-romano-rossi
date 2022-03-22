package it.polimi.ingsw.characters;

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

    public static Characters randomCharacter(){
        Random rng = new Random();
        return values()[rng.nextInt(values().length)];
    }
}
