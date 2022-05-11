package it.polimi.ingsw.model.board;

import it.polimi.ingsw.utils.exceptions.EmptyMovableException;

import java.io.Serializable;
import java.util.HashMap;

public class Students extends HashMap<Color, Integer> implements Serializable {
    public Students(int greens, int blues, int yellows, int pinks, int reds){
        this.put(Color.GREEN, greens);
        this.put(Color.BLUE, blues);
        this.put(Color.YELLOW, yellows);
        this.put(Color.MAGENTA, pinks);
        this.put(Color.RED, reds);
    }

    public Students(){
        clear();
    }

    public Students(Students students) {
        super(students);
    }

    public void moveTo(Students to, Color color) throws EmptyMovableException {
        if(get(color) == 0){
            throw new EmptyMovableException();
        }
        add(color, -1);
        to.add(color, 1);
    }

    public void add(Color color, int delta){
        this.put(color, this.get(color) + delta);
    }

    public void add(Color color){
        this.add(color, 1);
    }

    // Returns the number of students, no matter the color
    public int count(){
        return values().stream().reduce(0, Integer::sum);
    }

    @Override
    public void clear() {
        put(Color.GREEN, 0);
        put(Color.BLUE, 0);
        put(Color.YELLOW, 0);
        put(Color.MAGENTA, 0);
        put(Color.RED, 0);
    }
}
