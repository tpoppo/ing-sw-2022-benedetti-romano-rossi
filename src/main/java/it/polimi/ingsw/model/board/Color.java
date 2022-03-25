package it.polimi.ingsw.model.board;

import java.util.ArrayList;
import java.util.Arrays;

public enum Color {
    RED,
    GREEN,
    BLUE,
    YELLOW,
    PINK;

    public static ArrayList<Color> getColors(){
        return new ArrayList<>(Arrays.asList(RED, GREEN, BLUE, YELLOW, PINK));
    }
}
