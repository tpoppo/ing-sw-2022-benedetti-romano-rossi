package it.polimi.ingsw.board;

import it.polimi.ingsw.exceptions.EmptyMovableException;
import it.polimi.ingsw.interfaces.Movable;

import java.util.HashSet;

public class Professors extends HashSet<Color> implements Movable<Professors> {

    public Professors(){
        super();
    }

    public Professors(Professors professors){
        super(professors);
    }

    @Override
    public void moveTo(Professors to, Color color) throws EmptyMovableException {
        if(contains(color)){
            throw new EmptyMovableException();
        }
        remove(color);
        to.add(color);
    }
}