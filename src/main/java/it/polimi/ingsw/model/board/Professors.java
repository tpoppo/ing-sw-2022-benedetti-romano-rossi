package it.polimi.ingsw.model.board;

import it.polimi.ingsw.utils.exceptions.EmptyMovableException;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;

/**
 * This class represents a group of professor in schoolboard
 */
public class Professors extends HashSet<Color> implements Serializable {

    @Serial
    private static final long serialVersionUID = -7180404501872886316L;

    /**
     * Empty group of professors
     */
    public Professors(){
        super();
    }

    /**
     * Creates a new copy of the given {@link Professors}
     * @param professors the object to copy
     */
    public Professors(Professors professors){
        super(professors);
    }

    /**
     * Move a professor from one place to another
     * @param to where to move the professor
     * @param color which color to move
     * @throws EmptyMovableException if the professor is missing
     */
    public void moveTo(Professors to, Color color) throws EmptyMovableException {
        if(!contains(color)){
            throw new EmptyMovableException();
        }
        remove(color);
        to.add(color);
    }
}