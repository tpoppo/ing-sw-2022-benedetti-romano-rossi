package it.polimi.ingsw.board;

import it.polimi.ingsw.exceptions.EmptyMovableException;

import java.util.HashMap;

public class Students extends HashMap<Color, Integer> {
    public Students(int greens, int blues, int yellows, int pinks, int reds){
        this.put(Color.GREEN, greens);
        this.put(Color.BLUE, blues);
        this.put(Color.YELLOW, yellows);
        this.put(Color.PINK, pinks);
        this.put(Color.RED, reds);
    }

    public Students(){
        this.put(Color.GREEN, 0);
        this.put(Color.BLUE, 0);
        this.put(Color.YELLOW, 0);
        this.put(Color.PINK, 0);
        this.put(Color.RED, 0);
    }

    public Students(Students students) {
        super(students);
    }

    public void moveTo(Students to, Color color) throws EmptyMovableException {
        if(get(color) == 0){
            throw new EmptyMovableException();
        }
        put(color, get(color)-1);
        to.put(color, to.get(color)+1);
    }

    public void add(Color color, int delta){
        this.put(color, this.get(color) + delta);
    }

    public void add(Color color){
        this.add(color, 1);
    }
}
