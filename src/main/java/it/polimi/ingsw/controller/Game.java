package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import it.polimi.ingsw.model.GameConfig;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.*;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.characters.Characters;
import it.polimi.ingsw.model.characters.PlayerChoices;
import it.polimi.ingsw.utils.exceptions.*;

import java.io.*;
import java.util.*;

/**
 * This class keeps all the objects used during the game.
 * It provides the methods to perform the action of the turn.
 */
public class Game implements Serializable{
    @Serial
    private static final long serialVersionUID = -8385917117979059599L;
    public static final int MAX_COINS = 20;
    public static final int MAX_DINING_STUDENTS = 10;

    private final boolean expert_mode;
    private Player first_player;
    private Queue<Player> play_order;
    private ArrayList<Player> planning_order;
    private final ArrayList<Island> islands;
    private Bag bag;
    private final ArrayList<Students> clouds;
    private final ArrayList<Player> players;
    private final ArrayList<Character> characters;
    private final GameConfig gameConfig;
    private final GameModifiers gameModifiers;

    /**
     * Constructor, creates the main Game controller
     *
     * @param expert_mode true if the game is in expert mode.
     * @param lobby the lobby of the game.
     * @throws EmptyBagException if the bag has not been correctly initialized.
     */
    public Game(boolean expert_mode, LobbyHandler lobby) throws EmptyBagException {
        int num_players = lobby.getPlayers().size();
        this.expert_mode = expert_mode;
        gameModifiers = new GameModifiers();
        characters = new ArrayList<>();
        play_order = new LinkedList<>();

        // Adding all the players from the lobby to the game
        players = new ArrayList<>();

        ArrayList<LobbyPlayer> lobbyPlayers = lobby.getPlayers();
        for(LobbyPlayer lobbyPlayer : lobbyPlayers){
            players.add(new Player(lobbyPlayer));
        }

        // Importing game config from file (the file is chosen based on the # of players playing)
        InputStream file_path = getClass().getResourceAsStream("/config/" + num_players + "PlayersGame.json");

        Gson gson = new Gson();
        Random rng = new Random();

        // Parsing the json
        assert file_path != null;
        gameConfig = gson.fromJson(new BufferedReader(new InputStreamReader(file_path)), GameConfig.class);

        // 1: Placing the # of islands on the table
        islands = new ArrayList<>();
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

            // Randomly choosing the characters
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

    /**
     * Manages the progress of the play_order queue
     */
    public void nextTurn(){
        play_order.remove();
    }

    /**
     * Places the # of students on each cloud. If there are not enough students in the bag it returns false
     * @return true if it was able to fill all the clouds, otherwise it returns false
     */
    public boolean fillClouds() {
        for (Students cloud : clouds) {
            for (int i=0; i < gameConfig.CLOUD_SPACE; i++) {
                Color drawnColor;
                try {
                    drawnColor = bag.drawStudent();
                } catch (EmptyBagException e) {
                    return false;
                }
                cloud.add(drawnColor);
            }
        }
        return true;
    }

    /**
     * Computes the player_order for the planning phase based on the first_player
     * Removes current_assistant from each player
     */
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

        planning_order = new ArrayList<>(play_order);
    }

    /**
     * Sets the current player's assistant
     *
     * @param assistant the assistant to be played
     * @throws AssistantAlreadyPlayedException if the provided assistant has already been played by another player (and the player has other assistants)
     */
    public void playAssistant(Assistant assistant) throws AssistantAlreadyPlayedException {
        boolean assistant_already_played = false;
        Set<Assistant> played_assistants = new HashSet<>();
        Player current_player = getCurrentPlayer();

        // checks if exists a player who've already played the chosen assistant
        // the check is done on the players that have already played an assistant
        for(Player player : players.stream().filter(x -> x.getCurrentAssistant() != null).toList()){
            played_assistants.add(player.getCurrentAssistant());
            if(player.getCurrentAssistant().equals(assistant))
                assistant_already_played = true;
        }

        // if the assistant has already been played and the player has other assistants (not played by anyone),
        // the chosen assistant cannot be played
        if(assistant_already_played && !played_assistants.containsAll(current_player.getPlayerHand()))
            throw new AssistantAlreadyPlayedException();

        current_player.setCurrentAssistant(assistant);

        // Removes the chosen assistant from the player_hand
        ArrayList<Assistant> updated_player_hand = new ArrayList<>(current_player.getPlayerHand());
        updated_player_hand.remove(assistant);
        current_player.setPlayerHand(updated_player_hand);
    }

    /**
     * Computes the player_order for the action phase and sets the first_player for the new round
     */
    public void endPlanning(){
        ArrayList<Player> new_play_order = new ArrayList<>(players);

        // Computing the new player order
        new_play_order.sort((o1, o2) -> {
            int power1, power2;

            // Not checking if the currentAssistant is set as it should've been chosen in the previous method
            power1 = o1.getCurrentAssistant().getPower();
            power2 = o2.getCurrentAssistant().getPower();

            // Comparing elements first by the power of their assistant
            int first_compare = Integer.compare(power1, power2);

            // If the assistants powers' are different the result is returned
            if(first_compare != 0)
                return first_compare;

            // If the assistants powers' are equal, the priority should be given to the one who played their assistant first
            // (that is the first player among the two in the player order of the planning phase)
            return Integer.compare(planning_order.indexOf(o1), planning_order.indexOf(o2));
        });

        // Copying the new play_order to the official queue
        play_order = new LinkedList<>(new_play_order);

        // Saving the first player of the new queue for the planning phase of the next turn
        first_player = play_order.peek();
    }

    /**
     * It moves a student from the entrance to the given island
     *
     * @param color the color of the student in the entrance
     * @param island the target island
     * @throws EmptyMovableException if there are no student of the required color in the entrance
     */
    public void moveStudent(Color color, Island island) throws EmptyMovableException {
        SchoolBoard schoolboard = getCurrentPlayer().getSchoolBoard();
        Students entrance_students = schoolboard.getEntranceStudents();
        Students island_students = island.getStudents();

        entrance_students.moveTo(island_students, color);

        schoolboard.setEntranceStudents(entrance_students);
        island.setStudents(island_students);
    }

    /**
     * It moves a student from the entrance to the dining room
     * It also updates the professor if needed
     *
     * @param color color of the student that it is moved into the dining room
     * @throws EmptyMovableException if there are no student of the required color in the entrance
     */
    public void moveStudent(Color color) throws EmptyMovableException, FullDiningRoomException {
        SchoolBoard schoolboard = getCurrentPlayer().getSchoolBoard();
        Students entrance_students = schoolboard.getEntranceStudents();
        Students dining_students = schoolboard.getDiningStudents();

        if(dining_students.getOrDefault(color, 0) >= MAX_DINING_STUDENTS){
            throw new FullDiningRoomException();
        }

        entrance_students.moveTo(dining_students, color);

        schoolboard.setEntranceStudents(entrance_students);
        schoolboard.setDiningStudents(dining_students);

        // [Expert Mode] it checks whether to add a coin or not
        if(expert_mode){
            final HashSet<Integer> coin_positions = new HashSet<>(Arrays.asList(3, 6, 9));
            if(coin_positions.contains(dining_students.get(color))){
                int total_coins = 0;
                for(Player player : players){
                    total_coins += player.getCoins();
                }
                if(total_coins < MAX_COINS){
                    Player player = getCurrentPlayer();
                    player.setCoins(player.getCoins() + 1);
                }
            }
        }

        updateProfessor(getCurrentPlayer(), color);

    }

    /**
     * It moves mother nature to the target island, if the target island is too far it throws MoveMotherNatureException.
     * The maximum distance is given by the current assistant. It assumes that the current assistant and getCurrentPlayer are not null.
     *
     * @param island where mother nature will move to
     */
    public void moveMotherNature(Island island) {
        int next_position = islands.indexOf(island);
        int current_position = findMotherNaturePosition();

        islands.get(current_position).setMotherNature(false);
        islands.get(next_position).setMotherNature(true);
    }

    /**
     * Execute the island conquering phase on the island where mother nature is currently placed.
     * It calls conquerIsland(Island), where island is the island where mother nature is currently placed.
     */
    public void conquerIsland(){
        int mother_nature_position = findMotherNaturePosition();
        conquerIsland(islands.get(mother_nature_position));
    }

    /**
     * Execute the island conquering phase on the given island.
     *
     * @param island the island to be conquered
     */
    public void conquerIsland(Island island){
        // if there are NoEntryTiles skip conquerIsland
        if(island.getNoEntryTiles() > 0){
            island.setNoEntryTiles(island.getNoEntryTiles()-1);
            return ;
        }

        HashMap<Player, Integer> influence = new HashMap<>();
        int towers_on_island = island.getNumTowers();

        // Computing the influence for each player
        for(Player player : players){
            int student_influence = 0;
            int tower_influence = 0;
            int buff_influence = 0;

            // Students influence
            for(Color professor_color : player.getProfessors()){
                if(gameModifiers.getInhibitColor() == null || !gameModifiers.getInhibitColor().equals(professor_color))
                    student_influence += island.getStudents().get(professor_color);
            }

            // Towers influence
            if(island.getOwner() != null && island.getOwner().equals(player) && !gameModifiers.isInhibitTowers())
                tower_influence = towers_on_island;

            // Buff Influence
            if(player.equals(getCurrentPlayer()))
                buff_influence = gameModifiers.getBuffInfluence();

            // Also adding the gameModifier here
            influence.put(player, student_influence + tower_influence + buff_influence);
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
            }else if(island.getOwner() != new_owner){
                Player old_owner = island.getOwner();
                int towersToMove = Math.min(new_owner.getSchoolBoard().getNumTowers(), towers_on_island);

                island.setOwner(new_owner);
                new_owner.getSchoolBoard().addTowers(-towersToMove);
                old_owner.getSchoolBoard().addTowers(towersToMove);
            }

            mergeIslands();

        }
    }

    /**
     * It merges the islands if two consecutive islands are under the same players.
     */
    private void mergeIslands(){
        int current_position = 0;

        while(current_position < islands.size() && islands.size() > 2){
            int next_position = (current_position + 1) % islands.size();
            Island current_island = islands.get(current_position);
            Island next_island = islands.get(next_position);

            if(current_island.getOwner() != null && current_island.getOwner().equals(next_island.getOwner())){
                current_island.merge(next_island);
                islands.remove(next_position);
            }else current_position++;
        }
    }

    /**
     * It moves the students from the selected cloud to the player's entrance. Additionally, it deactivates the characters
     *
     * @param cloud selected cloud
     */
    public void chooseCloud(Students cloud) throws EmptyCloudException {
        if(cloud.count() == 0) throw new EmptyCloudException();

        Students entrance_students = getCurrentPlayer().getSchoolBoard().getEntranceStudents();

        for (Map.Entry<Color, Integer> entry : cloud.entrySet()) {
            Color key = entry.getKey();
            int delta = entry.getValue();

            entrance_students.add(key, delta);
        }
        getCurrentPlayer().getSchoolBoard().setEntranceStudents(entrance_students);
        cloud.clear();
    }


    /**
     * Checks whether the victory conditions are met
     *
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
     * Checks whether the end-game conditions are met
     *
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
     * Returns the winner of the game. It assumes that the game has ended.
     * The player who has built the most Towers on Islands wins the game. In case of a tie, the player who controls the most Professors wins the game.
     *
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
     * Returns the position of MotherNature in the islands arraylist.
     */
    public int findMotherNaturePosition(){
        for(int i=0; i<islands.size(); i++) {
            if(islands.get(i).hasMotherNature()) return i;
        }
        return -1; // shouldn't happen
    }

    /**
     * Activates the selected character with the given parameters
     *
     * @param character the character you want to activate
     * @param playerChoices the parameters of the character activation
     * @throws BadPlayerChoiceException if the parameters are invalid
     */
    public void activateCharacter(Character character, PlayerChoices playerChoices) throws BadPlayerChoiceException {
        character.activate(this, playerChoices);
    }

    /**
     * Updates the professors values after a player has placed a student in his dining room
     *
     * @param current_player player that placed the student in the dining room
     * @param color color of the student
     */
    public void updateProfessor(Player current_player, Color color){
        Students dining_students = current_player.getSchoolBoard().getDiningStudents();

        // checks whether the professor is in game
        Professors professorsTo = current_player.getProfessors();
        Player professor_owner = players.stream()
                .filter(player -> player.getProfessors().contains(color))
                .findFirst().orElse(null);

        if(professor_owner == null){
            professorsTo.add(color);
        } else if(!professor_owner.equals(current_player)) {
            if(professor_owner.getSchoolBoard().getDiningStudents().get(color) < dining_students.get(color) + gameModifiers.getProfessorModifier()){
                Professors professorsFrom = professor_owner.getProfessors();
                try {
                    professorsFrom.moveTo(professorsTo, color);
                } catch (EmptyMovableException e) {
                    e.printStackTrace(); // It should be impossible
                }
                professor_owner.getSchoolBoard().setProfessors(professorsFrom);
            }
        }

        current_player.getSchoolBoard().setProfessors(professorsTo);
    }

    /**
     * Returns the current player if it exists, otherwise it returns null.
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

    public ArrayList<Students> getClouds() {
        return new ArrayList<>(clouds);
    }

    public ArrayList<Character> getCharacters() {
        return new ArrayList<>(characters);
    }
    
    public boolean getExpertMode() {
        return expert_mode;
    }

    public GameConfig getGameConfig() {
        return gameConfig;
    }

    /**
     * Draws a student from a bag
     *
     * @return color drawn
     */
    public Color drawStudentFromBag() throws EmptyBagException {
        return bag.drawStudent();
    }

    /**
     * Searches and returns the Player with the provided username.
     *
     * @param username the username of the player
     * @return the Player with the given username
     */
    public Player usernameToPlayer(String username){
        return getPlayers().stream().filter(player -> player.getUsername().equals(username)).toList().get(0);
    }

    public Queue<Player> getPlayOrder() {
        return new LinkedList<>(play_order);
    }

    /**
     * @return the minimum cost of the characters in play
     */
    public int minCharacterCost(){
        return characters.stream().map(Character::getCost).reduce(Integer::min).orElse(1000000);
    }

    /**
     * @return the currently active Character, if any
     */
    public Optional<Character> getActiveCharacter(){
        return characters.stream().filter(Character::isActivated).findFirst();
    }

    @Override
    public String toString() {
        return "Game{" +
                "expert_mode=" + expert_mode +
                ", first_player=" + first_player +
                ", play_order=" + play_order +
                ", islands=" + islands +
                ", bag=" + bag +
                ", clouds=" + clouds +
                ", players=" + players +
                ", characters=" + characters +
                ", gameConfig=" + gameConfig +
                ", gameModifiers=" + gameModifiers +
                '}';
    }
}
