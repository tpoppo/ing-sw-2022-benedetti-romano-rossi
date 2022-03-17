package it.polimi.ingsw;

import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.exceptions.EmptyEntranceException;
import it.polimi.ingsw.exceptions.MoveMotherNatureException;

import java.util.ArrayList;
import java.util.Queue;

public abstract class Game {
    final private int CLOUD_SPACE;

    final private boolean expert_mode;
    final private Player first_player;
    final private Queue<Player> play_order;
    final private ArrayList<Island> islands;
    final private Bag bag;
    final private ArrayList<Students> clouds;
    final private ArrayList<Player> players;
    final private ArrayList<Character> characters;
    final private int inhibit_towers;

    protected Game(int cloud_space, boolean expert_mode, Player first_player, Queue<Player> play_order, ArrayList<Island> islands, Bag bag, ArrayList<Students> clouds, ArrayList<Player> players, ArrayList<Character> characters, int inhibit_towers1, int inhibit_towers) {
        CLOUD_SPACE = cloud_space;
        this.expert_mode = expert_mode;
        this.first_player = first_player;
        this.play_order = play_order;
        this.islands = islands;
        this.bag = bag;
        this.clouds = clouds;
        this.players = players;
        this.characters = characters;
        this.inhibit_towers = inhibit_towers;
    }

    public static Game setup(int num_players, boolean expert_mode){ return null; }

    public void nextTurn(){}

    public void fillClouds() throws EmptyBagException {
        for (Students cloud : clouds) {
            for (int i=0; i < CLOUD_SPACE; i++) {
                Color drawnColor = bag.drawStudent();

                cloud.put(drawnColor, cloud.get(drawnColor) + 1);
            }
        }
    }

    public void beginPlanning(){}

    public void playAssistant(Assistant assistant){}

    public void endPlanning(){}

    public void moveStudent(Color color, Island island) throws EmptyEntranceException {
        // remove a student from the entrance
        SchoolBoard schoolboard = play_order.peek().getSchoolBoard();
        Students students = schoolboard.getEntranceStudents();
        int color_cnt = students.get(color) - 1;
        if(color_cnt < 0){
            throw new EmptyEntranceException();
        }
        students.put(color, color_cnt);
        schoolboard.setEntranceStudents(students);

        // add a student to the given island
        students = island.getStudents();
        color_cnt = students.get(color) + 1;
        students.put(color, color_cnt);
        island.setStudents(students);
    }

    public void moveStudent(Color color) throws EmptyEntranceException {
        // remove a student from the entrance
        SchoolBoard schoolboard = play_order.peek().getSchoolBoard();
        Students students = schoolboard.getEntranceStudents();
        int color_cnt = students.get(color) - 1;
        if(color_cnt < 0){
            throw new EmptyEntranceException();
        }
        students.put(color, color_cnt);
        schoolboard.setEntranceStudents(students);

        // add a student to the given island
        students = schoolboard.getDiningStudents();
        color_cnt = students.get(color) + 1;
        students.put(color, color_cnt);
        schoolboard.setDiningStudents(students);
    }

    public void moveMotherNature(Island island) throws MoveMotherNatureException {
        int next_position = islands.indexOf(island);
        int current_position = findMotherNaturePosition();
        int distance = (next_position - current_position + islands.size()) % islands.size();
        Player current_player = play_order.peek();
        if(distance > current_player.getCurrentAssistant().get().getSteps()){
            throw new MoveMotherNatureException();
        }
        islands.get(current_position).setMotherNature(false);
        islands.get(next_position).setMotherNature(true);
    }

    private Player conquerIsland(Island island){ return null; }

    private void mergeIslands(){
        int current_position = 0;
        while(current_position < islands.size() && islands.size() >= 2){
            int next_position = (current_position + 1) % islands.size();
            Island current_island = islands.get(current_position);
            Island next_island = islands.get(next_position);
            if(current_island.getOwner().equals(next_island.getOwner())){
                current_island.merge(next_island);
                islands.remove(next_position);
            }
        }
    }

    public void chooseCloud(Students cloud){

        Students students = getCurrentPlayer().getSchoolBoard().getEntranceStudents();
        for(Students.Entry<Color, Integer> entry : cloud.entrySet()) {
            Color key = entry.getKey();
            int value = entry.getValue();
            students.put(key, students.get(key) + value);
        }
        play_order.remove();
        cloud.clear();
    }

    public boolean checkVictory(){ return false; }


    private int findMotherNaturePosition(){
        for(int i=0; i<islands.size(); i++) {
            if(islands.get(i).hasMotherNature()) return i;
        }
        return -1; // shouldn't happen
    }

    private Player getCurrentPlayer(){
        if(play_order.isEmpty()) return null; //TODO Is it a reasonable result? Do we prefer to return first_player?
        return play_order.peek();
    }

}
