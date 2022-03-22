package it.polimi.ingsw;

import com.google.gson.Gson;
import it.polimi.ingsw.board.*;
import it.polimi.ingsw.characters.Character;
import it.polimi.ingsw.characters.Characters;
import it.polimi.ingsw.exceptions.AssistantAlreadyPlayedException;
import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.exceptions.EmptyMovableException;
import it.polimi.ingsw.exceptions.MoveMotherNatureException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Game {
    final private boolean expert_mode;
    private Player first_player;
    private Queue<Player> play_order;
    final private ArrayList<Island> islands;
    private Bag bag;
    final private ArrayList<Students> clouds;
    final private ArrayList<Player> players;
    final private ArrayList<Character> characters;
    final private int num_players;
    private GameConfig gameConfig;
    private GameModifiers gameModifiers;

    public Game(boolean expert_mode, Lobby lobby) throws EmptyBagException, EmptyMovableException {
        this.num_players = lobby.getPlayers().size();
        this.expert_mode = expert_mode;
        gameModifiers = new GameModifiers();
        characters = new ArrayList<>();

        // Adding all the players from the lobby to the game
        players = new ArrayList<>(lobby.getPlayers());

        // Importing game config from file (the file is chosen based on the # of players playing)
        String file_path = "src/main/resources/" + num_players + "PlayersGame.json";
        Gson gson = new Gson();
        Random rng = new Random();

        // Parsing the json
        try {
            gameConfig = gson.fromJson(Files.newBufferedReader(Paths.get(file_path)), GameConfig.class);
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
            if(i != mother_nature_position && i != (mother_nature_position + islands.size() / 2) % islands.size()) {
                Color drawnColor = bag.drawStudent();

                Students islandStudents = islands.get(i).getStudents();
                islandStudents.add(drawnColor);
                islands.get(i).setStudents(islandStudents);
            }
        }

        // 4: Put all remaining students in the bag
        bag = new Bag();

        // 5: Placing the # of clouds on the table
        clouds = new ArrayList<>();
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
            player.getSchoolBoard().setNumTowers(gameConfig.NUM_TOWERS);
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
                entranceStudents.add(drawnColor);
                player.getSchoolBoard().setEntranceStudents(entranceStudents);
            }
        }

        // 11: Randomly determining the first player
        first_player = players.get(rng.nextInt(players.size()));

        // EXPERT MODE:
        // Each player takes 1 coin, 3 random characters are chosen
        if(expert_mode) {
            for (Player player : players)
                player.setCoins(1);

            // TODO: Scelta 3 character randomicamente
            int count = 0;
            ArrayList<Characters> drawnCharacters = new ArrayList<>();
            do{
                Characters candidate_character = Characters.randomCharacter();

                if(!drawnCharacters.contains(candidate_character)){
                    drawnCharacters.add(candidate_character);
                    count++;
                }
            }while(count < gameConfig.NUM_CHARACTERS);

            for(Characters drawnCharacter : drawnCharacters){
                characters.add(Character.createCharacter(drawnCharacter, this));
            }
        }
    }

    // Manages the progress of the play_order queue
    public void nextTurn(){
        play_order.remove();  // FIXME: is it better to use poll()?
    }

    // Places the # of students on each cloud
    public void fillClouds() throws EmptyBagException {
        for (Students cloud : clouds) {
            for (int i=0; i < gameConfig.CLOUD_SPACE; i++) {
                Color drawnColor = bag.drawStudent();

                cloud.put(drawnColor, cloud.get(drawnColor) + 1);
            }
        }
    }

    // Computes the player_order for the planning phase based on the first_player
    // Removes current_assistant from each player
    public void beginPlanning(){
        int first_player_index = players.indexOf(first_player);

        play_order = new LinkedList<>();

        // Adding players to the queue starting from the first_player and the ones who follow him
        for(int i=first_player_index; i<players.size(); i++)
            play_order.add(players.get(i));

        // Adding the players prior to first_player to the queue
        for(int i=0; i<first_player_index; i++)
            play_order.add(players.get(i));

        // Removes the current_assistant from each player
        for(Player player : players)
            player.setCurrentAssistant(null);
    }

    public void playAssistant(Assistant assistant) throws AssistantAlreadyPlayedException {
        boolean assistant_already_played = false;
        Player current_player = getCurrentPlayer();
        assert current_player != null;  // FIXME: do we like this?

        // checks if exists a player who've already played the chosen assistant
        for(Player player : players){
            if(player.getCurrentAssistant().isPresent() && player.getCurrentAssistant().get().equals(assistant))
                assistant_already_played = true;
        }

        // if the assistant has already been played and the player has other assistant, the chosen assistant cannot be played
        if(assistant_already_played && current_player.getPlayerHand().size() > 1)
            throw new AssistantAlreadyPlayedException();

        current_player.setCurrentAssistant(assistant);

        // Removes the chosen assistant from the player_hand
        ArrayList<Assistant> updated_player_hand = new ArrayList<>(current_player.getPlayerHand());
        updated_player_hand.remove(assistant);
        current_player.setPlayerHand(updated_player_hand);
    }

    // Computes the player_order for the action phase and sets the first_player for the new round
    public void endPlanning(){
        ArrayList<Player> new_play_order = new ArrayList<>(players);

        // The old play_order is copied to an array list so that we can use indexOf() later
        ArrayList<Player> copy_play_order = new ArrayList<>(play_order);

        // Computing the new player order
        new_play_order.sort((o1, o2) -> {
            int power1, power2;

            // Not checking if the currentAssistant is set as it should've been chosen in the previous method
            // FIXME: do we want to do the check and eventually throw an exception?
            //  - (it shouldn't happen normally... could it be a problem if we contemplate disconnections?)
            power1 = o1.getCurrentAssistant().get().getPower();
            power2 = o2.getCurrentAssistant().get().getPower();

            // Comparing elements first by the power of their assistant
            int first_compare = Integer.compare(power1, power2);

            // If the assistants powers' are different the result is returned
            if(first_compare != 0)
                return first_compare;

            // If the assistants powers' are equal, the priority should be given to the one who played their assistant first
            // (that is the first player among the two in the player order of the planning phase)
            return Integer.compare(copy_play_order.indexOf(o1), copy_play_order.indexOf(o2));
        });

        // Copying the new play_order to the official queue
        play_order = new LinkedList<>(new_play_order);

        // Saving the first player of the new queue for the planning phase of the next turn
        first_player = play_order.peek();
    }

    /**
     * It moves the student from the entrance to the given island
     * @param color
     * @param island
     * @throws EmptyMovableException
     */
    public void moveStudent(Color color, Island island) throws EmptyMovableException {
        // remove a student from the entrance
        SchoolBoard schoolboard = getCurrentPlayer().getSchoolBoard();
        Students entrance_students = schoolboard.getEntranceStudents();
        Students island_students = island.getStudents();
        entrance_students.moveTo(island_students, color);

        schoolboard.setEntranceStudents(entrance_students);
        island.setStudents(island_students);
    }

    /**
     * It moves the student from the entrance to the dining room
     * It also moves the professor is needed
     * @param color
     * @throws EmptyMovableException
     */
    public void moveStudent(Color color) throws EmptyMovableException {

        // move the student from the entrance to the dining room
        SchoolBoard schoolboard = getCurrentPlayer().getSchoolBoard();
        Students entrance_students = schoolboard.getEntranceStudents();
        Students dining_students = schoolboard.getDiningStudents();
        entrance_students.moveTo(dining_students, color);

        schoolboard.setEntranceStudents(entrance_students);
        schoolboard.setDiningStudents(dining_students);

        // [Expert Mode] it checks whether to add a coin or not
        if(expert_mode){
            final HashSet<Integer> coin_positions = new HashSet<>(Arrays.asList(3, 6, 9));
            if(coin_positions.contains(dining_students.get(color))){
                Player player = getCurrentPlayer();
                player.setCoins(player.getCoins() + 1);
            }
        }

        // check whether the professor has changed
        Player playerFrom = null;
        for(Player player : players){
            if(player.getProfessors().contains(color) && player.getSchoolBoard().getDiningStudents().get(color) < dining_students.get(color) + gameModifiers.getProfessorModifier()){
                playerFrom = player;
            }
        }

        Professors professorsFrom;
        Professors professorsTo = getCurrentPlayer().getProfessors();

        if(playerFrom == null){
            professorsFrom = new Professors();
            professorsFrom.add(color);
        }else{
            professorsFrom = playerFrom.getProfessors();
        }
        try {
            professorsFrom.moveTo(professorsTo, color);
        } catch (EmptyMovableException e) {
            e.printStackTrace(); // It should be impossible
        }
        getCurrentPlayer().getSchoolBoard().setProfessors(professorsFrom);
        if(playerFrom != null) playerFrom.getSchoolBoard().setProfessors(professorsTo);

    }

    /**
     * It moves mother nature to the target island, if the target island is too far it throws MoveMotherNatureException.
     * The maximum distance is given by the current assistant. It assumes that the current assistant and getCurrentPlayer are not null.
     */
    public void moveMotherNature(Island island) throws MoveMotherNatureException {
        int next_position = islands.indexOf(island);
        int current_position = findMotherNaturePosition();
        int distance = (next_position - current_position + islands.size()) % islands.size();

        Player current_player = getCurrentPlayer();
        if(distance > current_player.getCurrentAssistant().get().getSteps()){
            throw new MoveMotherNatureException();
        }

        islands.get(current_position).setMotherNature(false);
        islands.get(next_position).setMotherNature(true);
    }

    private Player conquerIsland(Island island){
        HashMap<Player, Integer> influence = new HashMap<>();
        int towers_on_island = island.getNumTowers();

        // FIXME: probably redundant
        for(Player player : players)
            influence.put(player, 0);

        // Computing the influence for each player
        for(Player player : players){
            int student_influence = 0;
            int tower_influence = 0;

            // Students influence
            for(Color professor_color : player.getProfessors()){
                if(!gameModifiers.getInhibitColor().isPresent() && !gameModifiers.getInhibitColor().get().equals(professor_color))
                    student_influence += island.getStudents().get(professor_color);
            }

            // Towers influence
            if(island.getOwner().equals(player) && !gameModifiers.isInhibitTowers())
                tower_influence = towers_on_island;

            // Also adding the gameModifier here
            influence.put(player, student_influence + tower_influence + gameModifiers.getBuffInfluence());
        }

        // Computing max_influence value
        int max_influence = 0;
        for(Player player : players)
            max_influence = Math.max(max_influence, influence.get(player));

        // Retrieving all players that have the max_influence
        ArrayList<Player> candidate_owner  = new ArrayList<>();
        for(Player player : players){
            if(influence.get(player) == max_influence) candidate_owner.add(player);
        }

        // Conquest is to be made only if there's only one candidate owner
        if(candidate_owner.size() == 1){
            Player new_owner = candidate_owner.get(0);

            if(island.getOwner() == null) {
                island.setOwner(new_owner);
                island.setNumTowers(1);
                new_owner.getSchoolBoard().addTowers(-1);
            }else if(new_owner.equals(island.getOwner())){
                island.setNumTowers(towers_on_island + 1);
                new_owner.getSchoolBoard().addTowers(-1);
            }else{
                Player old_owner = island.getOwner();

                island.setOwner(new_owner);
                new_owner.getSchoolBoard().addTowers(-towers_on_island);
                old_owner.getSchoolBoard().addTowers(towers_on_island);
            }

            return new_owner;
        }

        return island.getOwner();
    }

    /**
     * It merges the islands if two consecutive islands are under the same players.
     */
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

    /**
     * It moves students from the selected cloud to the player's entrance
     * @param cloud
     */
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

    /**
     * checkVictory returns true if a player builds the last Tower or there are only 3 groups of Islands remaining on the table.
     * @return true if a player builds the last Tower or there are only 3 groups of Islands remaining on the table
     */
    public boolean checkVictory(){
        // last tower has been built
        for(Player player : players){
            if(player.getSchoolBoard().getNumTowers() == 0) return true;
        }
        // groups of islands <= 3
        return islands.size() <= 3;
    }
    /**
     * checkEndGame returns true if the bag is empty or if the hand of a player is empty.
     * @return true if the bag is empty or if the hand of a player is empty
     */
    public boolean checkEndGame(){
        if(bag.capacity() == 0) return true; // empty bag
        for(Player player : players){
            if(player.getPlayerHand().isEmpty()) return true; // empty hand
        }
        return false;
    }

    /**
     * winner returns the winner of the game. It assumes that the game has ended.
     * The player who has built the most Towers on Islands wins the game. In case of a tie, the player who controls the most Professors wins the game.
     * @return the winner of the game
     */
    public Player winner(){
        ArrayList<Player> candidate_winner = new ArrayList<>(players);
        // sort based on (1) the smallest NumTowers, and if they are equal based on (2) the highest number of professors.
        candidate_winner.sort((a, b) -> {
            if(a.getSchoolBoard().getNumTowers() == b.getSchoolBoard().getNumTowers()){
                return Integer.compare(b.getProfessors().size(), a.getProfessors().size());
            }
            return Integer.compare(a.getSchoolBoard().getNumTowers(), b.getSchoolBoard().getNumTowers());
        });
        return candidate_winner.get(0);
    }

    /**
     * It returns the position of MotherNature in the islands arraylist.
     */
    private int findMotherNaturePosition(){
        for(int i=0; i<islands.size(); i++) {
            if(islands.get(i).hasMotherNature()) return i;
        }
        return -1; // shouldn't happen
    }

    /**
     * It returns the current player if it exists, otherwise it returns null.
     */
    public Player getCurrentPlayer(){
        return play_order.peek();
    }

    public GameModifiers getGameModifiers() {
        return gameModifiers;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public Bag getBag() {
        return bag;
    }

    public ArrayList<Island> getIslands() {
        return islands;
    }

    /**
     * Draw a student from a bag
     * @return color drawn
     */
    public Color drawStudentFromBag() throws EmptyBagException {
        return bag.drawStudent();
    }
}
