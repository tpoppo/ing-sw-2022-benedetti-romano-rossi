package it.polimi.ingsw;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.exceptions.EmptyEntranceException;
import it.polimi.ingsw.exceptions.MoveMotherNatureException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;

public class Game {
    final private boolean expert_mode;
    final private Player first_player;
    final private Queue<Player> play_order;
    final private ArrayList<Island> islands;
    private Bag bag;
    final private ArrayList<Students> clouds;
    final private ArrayList<Player> players;
    final private ArrayList<Character> characters;
    final private int num_players;
    private GameConfig gameConfig;
    private GameModifiers gameModifiers;

    public Game(boolean expert_mode, Lobby lobby) throws FileNotFoundException, EmptyBagException {
        this.num_players = lobby.getPlayers().size();
        this.expert_mode = expert_mode;

        // Adding all the players from the lobby to the game
        players = new ArrayList<Player>(lobby.getPlayers());

        // Importing game config from file (the file is chosen based on the # of players playing)
        String file_path = "src/main/resources/" + num_players + "PlayersGame.json";
        Gson gson = new Gson();
        Random rng = new Random();

        // Parsing the json
        try {
            gameConfig = gson.fromJson(Files.readAllLines(Paths.get(file_path)).toString(), GameConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 1: Placing the # of islands on the table
        islands = new ArrayList<Island>();
        for(int i=0; i < gameConfig.NUM_ISLANDS; i++)
            islands.add(new Island());

        // 2: Placing mother nature randomly on one of the islands
        int mother_nature_position = rng.nextInt(gameConfig.NUM_ISLANDS);
        islands.get(mother_nature_position).setMotherNature(true);

        // 3: Place 1 student on each island except the one with mother nature and the opposite one
        // The students are drawn out randomly from a bag that contains 2 students for each color

        // Filling up the bag with the required subset
        Students subset = new Students(2, 2, 2, 2, 2);
        bag = new Bag(subset);

        for(int i=0; i<islands.size(); i++){
            if(i != mother_nature_position && i != mother_nature_position + islands.size() / 2) {
                Color drawnColor = bag.drawStudent();

                Students islandStudents = islands.get(i).getStudents();
                bag.getStudents().moveTo(islandStudents, drawnColor);
                islands.get(i).setStudents(islandStudents);
            }
        }

        // 4: Put all remaining students in the bag
        bag = new Bag();

        // 5: Placing the # of clouds on the table
        clouds = new ArrayList<Students>();
        for(int i = 0; i < gameConfig.NUM_CLOUDS; i++)
            clouds.add(new Students());

        // 6: Placing the professors
        // Not implemented as we don't foresee the presence of an "origin" for the professors:
        //      each of them is "generated" the first time that is needed

        // 7: Each player takes a schoolBoard
        // Already implemented in the player constructor:
        //      every player gets its own schoolBoard the moment that it's created

        // 8: Each player takes the # of towers of its color
        for(Player player : players){
            player.getSchoolBoard().setNum_towers(gameConfig.NUM_TOWERS);
        }

        // 9: Each player takes a deck of the # of assistants
        for(Player player : players){
            player.setPlayerHand(Assistant.getAssistants(player.getWizard()));
        }

        // 10: Each player draws the # of students from the bag and puts them on their entrance
        for(Player player : players){
            for(int i=0; i<gameConfig.NUM_ENTRANCE_STUDENTS; i++){
                Color drawnColor = bag.drawStudent();

                Students entranceStudents = player.getSchoolBoard().getEntranceStudents();
                bag.getStudents().moveTo(entranceStudents, drawnColor);
                player.getSchoolBoard().setEntranceStudents(entranceStudents);
            }
        }

        // 11: Randomly determining the first player
        first_player = players.get(rng.nextInt(players.size()));
    }

    public void nextTurn(){}

    public void fillClouds() throws EmptyBagException {
        for (Students cloud : clouds) {
            for (int i=0; i < gameConfig.CLOUD_SPACE; i++) {
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
