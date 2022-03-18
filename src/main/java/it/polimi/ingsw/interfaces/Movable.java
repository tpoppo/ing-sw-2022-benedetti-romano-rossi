package it.polimi.ingsw.interfaces;

import it.polimi.ingsw.Color;
import it.polimi.ingsw.Professors;
import it.polimi.ingsw.Students;

public interface Movable<T> {
    // Removes one student of the provided color from this, and adds it to the to parameter
    void moveTo(T to, Color color);
}
