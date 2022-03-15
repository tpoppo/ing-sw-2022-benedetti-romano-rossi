package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.Queue;

public abstract class Game {
    final private boolean expert_mode;
    final private Player first_player;
    final private Queue<Player> play_order;
    final private ArrayList<Island> islands;
    final private Bag bag;
    final private ArrayList<Students> clouds;
    final private ArrayList<Player> players;
    final private ArrayList<Character> characters;
    final private int inhibit_towers;

    protected Game(boolean expert_mode, Player first_player, Queue<Player> play_order, ArrayList<Island> islands, Bag bag, ArrayList<Students> clouds, ArrayList<Player> players, ArrayList<Character> characters, boolean inhibit_towers, int inhibit_towers1) {
        this.expert_mode = expert_mode;
        this.first_player = first_player;
        this.play_order = play_order;
        this.islands = islands;
        this.bag = bag;
        this.clouds = clouds;
        this.players = players;
        this.characters = characters;
        this.inhibit_towers = inhibit_towers1;
    }

    public static Game setup(int num_players, boolean expert_mode){ return null; }

    public void nextTurn(){}

    public void fillClouds(){}

    public void beginPlanning(){}

    public void playAssistant(Assistant assistant){}

    public void endPlanning(){}

    public void moveStudent(Color color, Island island){}

    public void moveStudent(Color color){}

    public void moveMotherNature(Island island){}

    private Player conquerIsland(Island island){ return null; }

    private void mergeIslands(){}

    public void chooseCloud(Students cloud){}

    public boolean checkVictory(){ return false; }
}
