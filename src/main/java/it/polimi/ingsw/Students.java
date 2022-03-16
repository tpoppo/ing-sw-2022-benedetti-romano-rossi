package it.polimi.ingsw;

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
}
