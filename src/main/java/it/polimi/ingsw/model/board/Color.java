package it.polimi.ingsw.model.board;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public enum Color implements Serializable {
    RED,
    GREEN,
    BLUE,
    YELLOW,
    PINK;

    private static final Random rng = new Random();

    public static Color getRandomColor(){
        return values()[rng.nextInt(values().length)];
    }

    public static Color parseColor(Object s) {
        return switch (s.toString().toUpperCase()) {
            case "RED" -> RED;
            case "GREEN" -> GREEN;
            case "BLUE" -> BLUE;
            case "YELLOW" -> YELLOW;
            case "PINK" -> PINK;
            default -> null;
        };
    }
}
