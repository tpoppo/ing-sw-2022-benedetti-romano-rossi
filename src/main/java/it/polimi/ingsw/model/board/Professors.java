package it.polimi.ingsw.model.board;

import it.polimi.ingsw.utils.exceptions.EmptyMovableException;

import java.util.HashSet;

public class Professors extends HashSet<Color> {

    public Professors(){
        super();
    }

    public Professors(Professors professors){
        super(professors);
    }

    public void moveTo(Professors to, Color color) throws EmptyMovableException {
        if(!contains(color)){
            throw new EmptyMovableException();
        }
        remove(color);
        to.add(color);
    }
}