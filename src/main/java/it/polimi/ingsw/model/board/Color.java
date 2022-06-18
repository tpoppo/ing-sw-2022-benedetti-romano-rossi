package it.polimi.ingsw.model.board;

import java.io.Serializable;
import java.util.Random;

// TODO: javadocs
public enum Color implements Serializable {
    RED,
    GREEN,
    CYAN,
    YELLOW,
    MAGENTA;

    private static final Random rng = new Random();

    public static Color getRandomColor(){
        return values()[rng.nextInt(values().length)];
    }

    public static Color parseColor(Object s) {
        return switch (s.toString().toUpperCase()) {
            case "RED" -> RED;
            case "GREEN" -> GREEN;
            case "BLUE", "CYAN" -> CYAN;
            case "YELLOW" -> YELLOW;
            case "MAGENTA" -> MAGENTA;
            default -> null;
        };
    }
}
