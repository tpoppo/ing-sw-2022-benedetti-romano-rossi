package it.polimi.ingsw.interfaces;

import it.polimi.ingsw.Color;
import it.polimi.ingsw.exceptions.EmptyMovableException;

public interface Movable<T> {

    // Removes one movable of the provided color from this, and adds it to the to parameter
    void moveTo(T to, Color color) throws EmptyMovableException;
}
