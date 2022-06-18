package it.polimi.ingsw.model.board;

import java.io.Serializable;
import java.util.Random;

/**
 * This class represents the color of students and professors
 */
public enum Color implements Serializable {
    RED,
    GREEN,
    CYAN,
    YELLOW,
    MAGENTA;

    private static final Random rng = new Random();

    /**
     * Gets a random color (with uniform probability)
     * @return random color
     */
    public static Color getRandomColor(){
        return values()[rng.nextInt(values().length)];
    }

    /**
     * Parses the given object (casted with .toString()) to a Color
     * @param s string name
     * @return color returned
     */
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
