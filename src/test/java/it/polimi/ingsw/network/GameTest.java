package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.*;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.characters.Witch;
import it.polimi.ingsw.model.characters.PlayerChoices;
import it.polimi.ingsw.utils.exceptions.*;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


public class GameTest {

    /**
     * Check that the number of students is not negative
     * @param students students to check
     */
    void checkStudent(Students students){
        for(Color color : students.keySet()){
            assertTrue(students.get(color) >= 0);
        }
    }

    /**
     * Checks whether the invariants of the game are respected
     * @param game the current game to check
     */
    void checkInvariant(Game game){

        // adjacent islands must not have the same owner
        ArrayList<Island> islands = game.getIslands();
        for(int i=0; i<islands.size(); i++){
            int next_i = (i+1) % islands.size();
            if(islands.get(i).getOwner() != null) assertNotEquals(islands.get(i).getOwner(), islands.get(next_i).getOwner());
        }

        // number of islands tiles always the same
        int islands_tiles_counter = 0;
        for(Island island : islands){
            islands_tiles_counter += island.getNumIslands();
        }
        assertEquals(islands_tiles_counter, game.getGameConfig().NUM_ISLANDS);

        // sum of students equal to Bag.MAX_STUDENTS
        int students_cnt = 0;
        students_cnt += game.getBag().capacity();
        for(Island island : islands){
            students_cnt += island.getStudents().count();
        }
        for(Students students : game.getClouds()){
            students_cnt += students.count();
        }
        for(Player player : game.getPlayers()) {
            students_cnt += player.getSchoolBoard().getEntranceStudents().count();
            students_cnt += player.getSchoolBoard().getDiningStudents().count();
        }

        for(Character character : game.getCharacters()){
            students_cnt += character.getStudents().count();
        }
        assertEquals(students_cnt, Bag.MAX_STUDENTS);

        // only one mother nature
        int mother_nature_cnt = 0;
        for(Island island : game.getIslands()){
            mother_nature_cnt += island.hasMotherNature() ? 1 : 0;
        }
        assertEquals(mother_nature_cnt, 1);

        // at most one professor for each color
        for(Color color : Color.values()){
            boolean has_professor = false;
            for(Player player : game.getPlayers()){
                Professors professors = player.getSchoolBoard().getProfessors();
                if(has_professor) assertFalse(professors.contains(color));
                has_professor |= professors.contains(color);
            }
        }

        //max coins & non negative coins
        int total_coins = 0;
        for(Player player : game.getPlayers()){
            total_coins += player.getCoins();
            assertTrue(player.getCoins() >= 0);
        }
        assertTrue(total_coins <= Game.MAX_COINS);

        // check that getNumIslands() == getNumTowers()
        for(Island island : game.getIslands()){
            if(island.getOwner() == null) assertEquals(0, island.getNumTowers());
            else assertEquals(island.getNumIslands(), island.getNumTowers());
        }

        // if not owner must be alone
        for(Island island : game.getIslands()){
            if(island.getOwner() == null) assertEquals(1, island.getNumIslands());
        }

        // check that there are 8 towers for each player in game
        if(!game.checkEndGame() && !game.checkVictory()) {
            for (Player player : game.getPlayers()) {
                int towers = player.getSchoolBoard().getNumTowers();
                for (Island island : game.getIslands()) {
                    if (player.equals(island.getOwner())) towers += island.getNumTowers();
                }

                assertEquals(towers, game.getGameConfig().NUM_TOWERS);
            }
        }

        // player's stuffs >= 0
        for(Player player : game.getPlayers()){
            checkStudent(player.getSchoolBoard().getDiningStudents());
            checkStudent(player.getSchoolBoard().getEntranceStudents());

            assertTrue(player.getSchoolBoard().getNumTowers() >= 0);
        }

        // island's stuffs >= 0
        for(Island island : game.getIslands()){
            assertTrue(island.getNumIslands() >= 0);
            checkStudent(island.getStudents());
        }
    }

    /**
     * This test simulates a full 2 players game.
     * @throws FullLobbyException should never happen
     * @throws EmptyMovableException should never happen
     * @throws EmptyBagException should never happen
     * @throws AssistantAlreadyPlayedException should never happen
     * @throws FullDiningRoomException should never happen
     */
    @RepeatedTest(200)
    public void simpleRun2Player() throws FullLobbyException, EmptyMovableException, EmptyBagException, AssistantAlreadyPlayedException, FullDiningRoomException {
        LobbyHandler lobby = new LobbyHandler(0, 2);
        LobbyPlayer player0 = new LobbyPlayer("Player 1");
        LobbyPlayer player1 = new LobbyPlayer("Player 2");
        player0.setWizard(1);
        player1.setWizard(2);
        Random rng = new Random();

        lobby.addPlayer(player0);
        lobby.addPlayer(player1);

        Game game = new Game(true, lobby);
        checkInvariant(game);

        while(true) {
            // planning phase
            game.fillClouds();
            game.getClouds().forEach((x) -> assertEquals(3, x.count()));
            checkInvariant(game);

            game.beginPlanning();
            checkInvariant(game);

            // player1
            game.playAssistant(game.getCurrentPlayer().getPlayerHand().get(rng.nextInt(game.getCurrentPlayer().getPlayerHand().size())));
            checkInvariant(game);

            Player first_player = game.getCurrentPlayer();
            game.nextTurn();
            checkInvariant(game);

            // player2
            Assistant assistant;
            boolean assistant_already_played;
            int cnt = 0;
            do{
                assistant = game.getCurrentPlayer().getPlayerHand().get(rng.nextInt(game.getCurrentPlayer().getPlayerHand().size()));
                assistant_already_played = false;
                for(Player player : game.getPlayers()){
                    if (player.getCurrentAssistant() != null && player.getCurrentAssistant().equals(assistant)) {
                        assistant_already_played = true;
                        break;
                    }
                }
                cnt++; // it might fail even if it is correct
            }while(assistant_already_played && cnt < 100000);

            game.playAssistant(assistant);
            checkInvariant(game);

            assertNotEquals(game.getCurrentPlayer(), first_player);

            game.nextTurn();
            checkInvariant(game);

            game.endPlanning();
            checkInvariant(game);
            assertNotNull(game.getCurrentPlayer());

            ArrayList<Island> islands = game.getIslands();

            // action phase
            for (int player_id = 0; player_id < 2; player_id++) {
                assertEquals(7, game.getCurrentPlayer().getSchoolBoard().getEntranceStudents().count());
                // step 1
                for (int i = 0; i < 3; i++) {
                    // move a random student from the player's entrance to a random island
                    Students students = game.getCurrentPlayer().getSchoolBoard().getEntranceStudents();
                    Optional<Color> color = students.entrySet().stream().filter(
                            (key_value) -> {
                                return key_value.getKey() != null // is a valid color
                                        && key_value.getValue() > 0 // is present (at least one element)
                                        && game.getCurrentPlayer().getSchoolBoard().getDiningStudents().get(key_value.getKey()) < Game.MAX_DINING_STUDENTS; // there is enough space in the dining room for that color
                            }).map(Map.Entry::getKey).findFirst();

                    if(color.isPresent()){
                        if(rng.nextBoolean()){
                            game.moveStudent(color.get(), islands.get(rng.nextInt(islands.size())));
                        }else{
                            game.moveStudent(color.get());
                        }
                    }else{
                        /* Very Special Case (and very unlikely)
                           Example:
                            getEntranceStudents: {PINK=0, GREEN=0, RED=5, BLUE=0, YELLOW=0}
                            getDiningStudents: {PINK=0, GREEN=1, RED=8, BLUE=1, YELLOW=1}
                           Only one color in the entrance room and that color is full in the dining room.
                         */
                        // same as before, but without the dining room condition.
                        Optional<Color> color_island = students.entrySet().stream().filter(
                                (key_value) -> {
                                    return key_value.getKey() != null // is a valid color
                                            && key_value.getValue() > 0; // is present (at least one element)
                                }).map(Map.Entry::getKey).findFirst();
                        assertTrue(color_island.isPresent());
                        game.moveStudent(color_island.get(), islands.get(rng.nextInt(islands.size())));
                    }

                }
                assertEquals(4, game.getCurrentPlayer().getSchoolBoard().getEntranceStudents().count());
                checkInvariant(game);

                // step 2 - move mother nature
                Island mother_nature_island = islands.stream().filter(Island::hasMotherNature).findFirst().get();
                int curr_mother_nature_position = islands.indexOf(mother_nature_island);
                int next_mother_nature_position = (curr_mother_nature_position + rng.nextInt(game.getCurrentPlayer().getCurrentAssistant().getSteps()) + 1) % islands.size();

                game.moveMotherNature(islands.get(next_mother_nature_position));
                checkInvariant(game);

                // conquering an island
                game.conquerIsland();
                checkInvariant(game);

                // check victory immediately
                if (game.checkVictory()) {
                    assertNotNull(game.winner());
                    return;
                }

                // step 3 - choose cloud tiles
                ArrayList<Students> clouds = game.getClouds();

                clouds.forEach((x) -> assertTrue(x.count() == 3 || x.count() == 0));

                Students students;
                do {
                    students = clouds.get(rng.nextInt(clouds.size()));
                } while (students.count() <= 0);
                try {
                    game.chooseCloud(students);
                } catch (EmptyCloudException e) {
                    throw new RuntimeException(e);
                }
                checkInvariant(game);

                assertEquals(0, students.count());
                assertEquals(7, game.getCurrentPlayer().getSchoolBoard().getEntranceStudents().count());

                // end phase
                game.nextTurn();
                checkInvariant(game);
            }

            // check victory end game
            if (game.checkEndGame()) {
                assertNotNull(game.winner());
                return ;
            }
            checkInvariant(game);
        }
    }

    /**
     * This test simulates a full 3 players game.
     * @throws FullLobbyException should never happen
     * @throws EmptyMovableException should never happen
     * @throws EmptyBagException should never happen
     * @throws AssistantAlreadyPlayedException should never happen
     * @throws FullDiningRoomException should never happen
     */
    @RepeatedTest(200)
    public void simpleRun3Player() throws FullLobbyException, FullDiningRoomException, EmptyMovableException, EmptyBagException, AssistantAlreadyPlayedException {
        LobbyHandler lobby = new LobbyHandler(0, 3);
        LobbyPlayer player1 = new LobbyPlayer("Player 1");
        LobbyPlayer player2 = new LobbyPlayer("Player 2");
        LobbyPlayer player3 = new LobbyPlayer("Player 3");
        player1.setWizard(1);
        player2.setWizard(2);
        player3.setWizard(3);
        Random rng = new Random();

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);
        lobby.addPlayer(player3);

        Game game = new Game(true, lobby);
        while(true) {
            // planning phase
            if(game.fillClouds()){
                game.getClouds().forEach((x) -> assertEquals(4, x.count()));
            }

            game.beginPlanning();

            // player1
            game.playAssistant(game.getCurrentPlayer().getPlayerHand().get(rng.nextInt(game.getCurrentPlayer().getPlayerHand().size())));
            Player first_player = game.getCurrentPlayer();
            game.nextTurn();

            // player2
            Assistant assistant;
            boolean assistant_already_played;
            int cnt = 0;
            do {
                assistant = game.getCurrentPlayer().getPlayerHand().get(rng.nextInt(game.getCurrentPlayer().getPlayerHand().size()));
                assistant_already_played = false;
                for(Player player : game.getPlayers()){
                    if (player.getCurrentAssistant() != null && player.getCurrentAssistant().equals(assistant)) {
                        assistant_already_played = true;
                        break;
                    }
                }
                cnt++;
            }while(assistant_already_played && cnt < 100000);
            game.playAssistant(assistant);
            assertNotEquals(game.getCurrentPlayer(), first_player);
            game.nextTurn();

            // player3
            cnt = 0;
            do {
                assistant = game.getCurrentPlayer().getPlayerHand().get(rng.nextInt(game.getCurrentPlayer().getPlayerHand().size()));
                assistant_already_played = false;
                for(Player player : game.getPlayers()){
                    if (player.getCurrentAssistant() != null && player.getCurrentAssistant().equals(assistant)) {
                        assistant_already_played = true;
                        break;
                    }
                }
                cnt++;
            }while(assistant_already_played && cnt < 100000);
            game.playAssistant(assistant);
            assertNotEquals(game.getCurrentPlayer(), first_player);
            game.nextTurn();

            game.endPlanning();
            assertNotNull(game.getCurrentPlayer());

            ArrayList<Island> islands = game.getIslands();

            // action phase
            for (int player_id = 0; player_id < 3; player_id++) {
                // step 1
                for (int i = 0; i < game.getGameConfig().NUM_STUDENTS_MOVES; i++) {
                    // move a random student from the player's entrance to a random island
                    Students students = game.getCurrentPlayer().getSchoolBoard().getEntranceStudents();
                    Optional<Color> color = students.entrySet().stream().filter(
                            (key_value) -> {
                                return key_value.getKey() != null // is a valid color
                                        && key_value.getValue() > 0 // is present (at least one element)
                                        && game.getCurrentPlayer().getSchoolBoard().getDiningStudents().get(key_value.getKey()) < Game.MAX_DINING_STUDENTS; // there is enough space in the dining room for that color
                            }).map(Map.Entry::getKey).findFirst();

                    if(color.isPresent()){
                        if(rng.nextBoolean()){
                            game.moveStudent(color.get(), islands.get(rng.nextInt(islands.size())));
                        }else{
                            game.moveStudent(color.get());
                        }
                    }else{
                        /* Very Special Case (and very unlikely)
                           Example:
                            getEntranceStudents: {PINK=0, GREEN=0, RED=5, BLUE=0, YELLOW=0}
                            getDiningStudents: {PINK=0, GREEN=1, RED=8, BLUE=1, YELLOW=1}
                           Only one color in the entrance room and that color is full in the dining room.
                         */
                        // same as before, but without the dining room condition.
                        Optional<Color> color_island = students.entrySet().stream().filter(
                                (key_value) -> {
                                    return key_value.getKey() != null // is a valid color
                                            && key_value.getValue() > 0; // is present (at least one element)
                                }).map(Map.Entry::getKey).findFirst();
                        assertTrue(color_island.isPresent());
                        game.moveStudent(color_island.get(), islands.get(rng.nextInt(islands.size())));
                    }
                }

                // step 2 - move mother nature
                Island mother_nature_island = islands.stream().filter(Island::hasMotherNature).findFirst().get();
                int curr_mother_nature_position = islands.indexOf(mother_nature_island);
                int next_mother_nature_position = (curr_mother_nature_position + rng.nextInt(game.getCurrentPlayer().getCurrentAssistant().getSteps()) + 1) % islands.size();

                game.moveMotherNature(islands.get(next_mother_nature_position));

                // conquering an island
                game.conquerIsland();

                // check victory immediately
                if (game.checkVictory()) {
                    assertNotNull(game.winner());
                    return;
                }

                // step 3 - choose cloud tiles
                ArrayList<Students> clouds = game.getClouds();

                Students students;
                do {
                    students = clouds.get(rng.nextInt(clouds.size()));
                } while (students.count() <= 0);
                try {
                    game.chooseCloud(students);
                } catch (EmptyCloudException e) {
                    throw new RuntimeException(e);
                }

                assertEquals(0, students.count());

                // end phase
                game.nextTurn();
            }

            // check victory end game
            if (game.checkEndGame() || game.getBag().capacity()<12) {
                assertNotNull(game.winner());
                return ;
            }
        }
    }

    /**
     * Test th planning phase playing different characters in different turns
     * @throws FullLobbyException should never happen
     * @throws EmptyBagException should never happen
     * @throws AssistantAlreadyPlayedException should never happen
     */
    @RepeatedTest(100)
    public void PlanningPhase() throws FullLobbyException, EmptyBagException, AssistantAlreadyPlayedException {
        LobbyHandler lobby = new LobbyHandler(0, 3);
        LobbyPlayer player1 = new LobbyPlayer("Player 1");
        LobbyPlayer player2 = new LobbyPlayer("Player 2");
        LobbyPlayer player3 = new LobbyPlayer("Player 3");
        player1.setWizard(1);
        player2.setWizard(2);
        player3.setWizard(3);
        Random rng = new Random();
        lobby.addPlayer(player1);
        lobby.addPlayer(player2);
        lobby.addPlayer(player3);
        Game game = new Game(true, lobby);
        int i=0;
        while(i<7){
            i++;
            game.fillClouds();
            game.beginPlanning();

            // player1
            game.playAssistant(game.getCurrentPlayer().getPlayerHand().get(rng.nextInt(game.getCurrentPlayer().getPlayerHand().size())));
            Player first_player = game.getCurrentPlayer();
            game.nextTurn();

            // player2
            Assistant assistant;
            boolean assistant_already_played;
            int cnt = 0;
            /* Note: the cnt check might return a false positive
               However, this happened with probability (2/3)^-100000, thus, it is impossible.
               The worst case is with 3 players, 2 invalid cards in hand and 1 valid.
             */
            do {
                assistant = game.getCurrentPlayer().getPlayerHand().get(rng.nextInt(game.getCurrentPlayer().getPlayerHand().size()));
                assistant_already_played = false;
                for(Player player : game.getPlayers()){
                    if (player.getCurrentAssistant() != null && player.getCurrentAssistant().equals(assistant)) {
                        assistant_already_played = true;
                        break;
                    }
                }
                cnt++;

            }while(assistant_already_played && cnt < 100000);
            game.playAssistant(assistant);
            assertNotEquals(game.getCurrentPlayer(), first_player);
            game.nextTurn();

            // player3
            cnt = 0;
            do {
                assistant = game.getCurrentPlayer().getPlayerHand().get(rng.nextInt(game.getCurrentPlayer().getPlayerHand().size()));
                assistant_already_played = false;
                for(Player player : game.getPlayers()){
                    if (player.getCurrentAssistant() != null && player.getCurrentAssistant().equals(assistant)) {
                        assistant_already_played = true;
                        break;
                    }
                }
                cnt++;
            }while(assistant_already_played && cnt < 100000);
            game.playAssistant(assistant);
            game.nextTurn();

            game.endPlanning();

            if (game.checkEndGame()) {
                //assertNotNull(game.winner());
                return ;
            }
        }
    }

    /**
     * Check that after 8 rounds the clouds could not be filled
     * @throws FullLobbyException should never happen
     * @throws EmptyBagException should never happen
     * @throws AssistantAlreadyPlayedException should never happen
     */
    @RepeatedTest(100)
    public void PlanningPhaseException() throws FullLobbyException, EmptyBagException, AssistantAlreadyPlayedException {
        LobbyHandler lobby = new LobbyHandler(0, 3);
        LobbyPlayer player1 = new LobbyPlayer("Player 1");
        LobbyPlayer player2 = new LobbyPlayer("Player 2");
        LobbyPlayer player3 = new LobbyPlayer("Player 3");
        player1.setWizard(1);
        player2.setWizard(2);
        player3.setWizard(3);
        Random rng = new Random();
        lobby.addPlayer(player1);
        lobby.addPlayer(player2);
        lobby.addPlayer(player3);
        Game game = new Game(true, lobby);
        int i=0;
        while(i<8){
            i++;
            if(i==8){
                assertFalse(game.fillClouds());
                return;
            }
            game.fillClouds();
            game.beginPlanning();

            // player1
            game.playAssistant(game.getCurrentPlayer().getPlayerHand().get(rng.nextInt(game.getCurrentPlayer().getPlayerHand().size())));
            Player first_player = game.getCurrentPlayer();
            game.nextTurn();

            // player2
            Assistant assistant;
            boolean assistant_already_played;
            int cnt = 0;
            do {
                assistant = game.getCurrentPlayer().getPlayerHand().get(rng.nextInt(game.getCurrentPlayer().getPlayerHand().size()));
                assistant_already_played = false;
                for(Player player : game.getPlayers()){
                    if (player.getCurrentAssistant() != null && player.getCurrentAssistant().equals(assistant)) {
                        assistant_already_played = true;
                        break;
                    }
                }
                cnt++;
            }while(assistant_already_played && cnt < 100000);
            game.playAssistant(assistant);
            assertNotEquals(game.getCurrentPlayer(), first_player);
            game.nextTurn();

            // player3
            cnt = 0;
            do {
                assistant = game.getCurrentPlayer().getPlayerHand().get(rng.nextInt(game.getCurrentPlayer().getPlayerHand().size()));
                assistant_already_played = false;
                for(Player player : game.getPlayers()){
                    if (player.getCurrentAssistant() != null && player.getCurrentAssistant().equals(assistant)) {
                        assistant_already_played = true;
                        break;
                    }
                }
                cnt++;
            }while(assistant_already_played && cnt < 100000);
            game.playAssistant(assistant);
            game.nextTurn();

            game.endPlanning();
        }
    }

    /***
     * Test when a player play the same assistant card of the previous player in the same turn
     * @throws FullLobbyException should never happen
     * @throws EmptyBagException should never happen
     * @throws AssistantAlreadyPlayedException when is played an assistant card that was already played in the same turn by another played
     */
    @Test
    public void AssistantAlreadyPlayedExceptionTest1() throws FullLobbyException, EmptyBagException, AssistantAlreadyPlayedException {
        LobbyHandler lobby = new LobbyHandler(0, 2);
        LobbyPlayer player1 = new LobbyPlayer("Player 1");
        LobbyPlayer player2 = new LobbyPlayer("Player 2");
        player1.setWizard(1);
        player2.setWizard(2);

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game game = new Game(true, lobby);
        game.beginPlanning();

        ArrayList<Assistant> assistants_player1 = game.getPlayers().get(0).getPlayerHand();
        ArrayList<Assistant> assistants_player2 = game.getPlayers().get(1).getPlayerHand();

        game.playAssistant(assistants_player1.get(0));
        game.nextTurn();
        assertThrows(AssistantAlreadyPlayedException.class, () -> game.playAssistant(assistants_player2.get(0)));
    }

    /**
     * Test when a player must play the same assistant card of the previous player because it's the only assistant card that he has
     * @throws FullLobbyException should never happen
     * @throws EmptyBagException should never happen
     * @throws AssistantAlreadyPlayedException should never happen
     */
    @Test
    public void AssistantAlreadyPlayedExceptionTest2() throws FullLobbyException, EmptyBagException, AssistantAlreadyPlayedException {
        LobbyHandler lobby = new LobbyHandler(0, 2);
        LobbyPlayer player1 = new LobbyPlayer("Player 1");
        LobbyPlayer player2 = new LobbyPlayer("Player 2");
        player1.setWizard(1);
        player2.setWizard(2);

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game game = new Game(true, lobby);

        game.beginPlanning();
        ArrayList<Assistant> assistants_player1 = game.getCurrentPlayer().getPlayerHand();
        game.nextTurn();
        ArrayList<Assistant> assistants_player2 = game.getCurrentPlayer().getPlayerHand();
        while(assistants_player1.size() > 1){
            assistants_player1.remove(assistants_player1.get(0));
            assistants_player2.remove(assistants_player2.get(0));
        }
        game.beginPlanning();
        game.playAssistant(assistants_player1.get(0));
        game.nextTurn();
        game.playAssistant(assistants_player2.get(0));
        assertEquals(0, game.getCurrentPlayer().getPlayerHand().size());
    }

    /**
     * Check that the starting player of the current round is the player that played the assistant with
     * the lower power in the previous round
     * @throws FullLobbyException should never happen
     * @throws EmptyBagException should never happen
     * @throws AssistantAlreadyPlayedException should never happen
     */
    @RepeatedTest(100)
    public void EndRoundTest() throws FullLobbyException, EmptyBagException, AssistantAlreadyPlayedException {
        LobbyHandler lobby = new LobbyHandler(0, 2);
        LobbyPlayer player1 = new LobbyPlayer("Player 1");
        LobbyPlayer player2 = new LobbyPlayer("Player 2");
        player1.setWizard(1);
        player2.setWizard(2);

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game game = new Game(true, lobby);
        Random rng = new Random();

        game.beginPlanning();
        ArrayList<Assistant> assistants_player1 = game.getCurrentPlayer().getPlayerHand();
        //choose a random assistant
        int assistant_position1 = rng.nextInt(assistants_player1.size());
        int assistant_power_player1 = assistants_player1.get(assistant_position1).getPower();
        game.playAssistant(assistants_player1.get(assistant_position1));
        Player first_player = game.getCurrentPlayer();
        game.nextTurn();
        ArrayList<Assistant> assistants_player2 = game.getCurrentPlayer().getPlayerHand();
        //choose a random assistant different from the previous one
        int assistant_position2 = rng.nextInt(assistants_player2.size());
        int assistant_power_player2 = assistants_player2.get(assistant_position2).getPower();
        while(assistant_position2 == assistant_position1){
            assistant_position2 = rng.nextInt(assistants_player2.size());
            assistant_power_player2 = assistants_player2.get(assistant_position2).getPower();
        }
        game.playAssistant(assistants_player2.get(assistant_position2));
        Player second_player = game.getCurrentPlayer();
        game.endPlanning();

        //Check that in the next turn the first player is the player that in teh previous round played the assistant
        //whit the lower power
        game.beginPlanning();
        if(assistant_power_player1 < assistant_power_player2){
            assertEquals(first_player, game.getCurrentPlayer());
        }else{
            assertEquals(second_player, game.getCurrentPlayer());
        }
    }

    /**
     * Test different movement from mother nature
     * @throws FullLobbyException should never happen
     * @throws EmptyBagException should never happen
     */
    @RepeatedTest(100)
    public void moveMotherNatureTest() throws FullLobbyException, EmptyBagException {
        LobbyHandler lobby = new LobbyHandler(0, 2);
        LobbyPlayer player1 = new LobbyPlayer("Player 1");
        LobbyPlayer player2 = new LobbyPlayer("Player 2");
        player1.setWizard(1);
        player2.setWizard(2);

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game game = new Game(true, lobby);
        Random rng = new Random();

        ArrayList<Island> islands = game.getIslands();

        for(int i=0; i<5; i++){
            int random = rng.nextInt(2);
            if(random == 0){
                game.moveMotherNature(islands.get(rng.nextInt(islands.size())));
            }else{
                game.conquerIsland();
            }
            int count_mother_nature = 0;
            for(Island island : islands){
                if(island.hasMotherNature()) count_mother_nature++;
            }
            //Check that mothernature is only in one island
            assertEquals(1, count_mother_nature);
        }
    }

    /**
     * Test that the character Witch works in a game simulation
     * @throws FullLobbyException should never happen
     * @throws EmptyBagException should never happen
     * @throws EmptyMovableException should never happen
     * @throws FullDiningRoomException should never happen
     * @throws AssistantAlreadyPlayedException should never happen
     * @throws BadPlayerChoiceException should never happen
     */
    @RepeatedTest(100)
    public void conquerIslandTest() throws FullLobbyException, EmptyBagException, EmptyMovableException, FullDiningRoomException, AssistantAlreadyPlayedException, BadPlayerChoiceException {
        LobbyHandler lobby = new LobbyHandler(0, 2);
        LobbyPlayer player0 = new LobbyPlayer("Player 1");
        LobbyPlayer player1 = new LobbyPlayer("Player 2");
        player0.setWizard(1);
        player1.setWizard(2);
        Random rng = new Random();

        lobby.addPlayer(player0);
        lobby.addPlayer(player1);

        Game game = new Game(true, lobby);

        //simulate five turn
        int turn = 0;
        while(turn != 5){
            turn++;
            // planning phase
            game.fillClouds();
            game.getClouds().forEach((x) -> assertEquals(3, x.count()));
            checkInvariant(game);

            game.beginPlanning();
            checkInvariant(game);

            // player1
            game.playAssistant(game.getCurrentPlayer().getPlayerHand().get(rng.nextInt(game.getCurrentPlayer().getPlayerHand().size())));
            checkInvariant(game);

            Player first_player = game.getCurrentPlayer();
            game.nextTurn();
            checkInvariant(game);

            // player2
            Assistant assistant;
            boolean assistant_already_played;
            int cnt = 0;
            do{
                assistant = game.getCurrentPlayer().getPlayerHand().get(rng.nextInt(game.getCurrentPlayer().getPlayerHand().size()));
                assistant_already_played = false;
                for(Player player : game.getPlayers()){
                    if (player.getCurrentAssistant() != null && player.getCurrentAssistant().equals(assistant)) {
                        assistant_already_played = true;
                        break;
                    }
                }
                cnt++; // it might fail even if it is correct
            }while(assistant_already_played && cnt < 100000);

            game.playAssistant(assistant);
            checkInvariant(game);

            assertNotEquals(game.getCurrentPlayer(), first_player);

            game.nextTurn();
            checkInvariant(game);

            game.endPlanning();
            checkInvariant(game);
            assertNotNull(game.getCurrentPlayer());

            ArrayList<Island> islands = game.getIslands();

            // action phase
            for (int player_id = 0; player_id < 2; player_id++) {
                assertEquals(7, game.getCurrentPlayer().getSchoolBoard().getEntranceStudents().count());
                // step 1
                for (int i = 0; i < 3; i++) {
                    // move a random student from the player's entrance to a random island
                    Students students = game.getCurrentPlayer().getSchoolBoard().getEntranceStudents();
                    Optional<Color> color = students.entrySet().stream().filter(
                            (key_value) -> {
                                return key_value.getKey() != null // is a valid color
                                        && key_value.getValue() > 0 // is present (at least one element)
                                        && game.getCurrentPlayer().getSchoolBoard().getDiningStudents().get(key_value.getKey()) < Game.MAX_DINING_STUDENTS; // there is enough space in the dining room for that color
                            }).map(Map.Entry::getKey).findFirst();

                    if(color.isPresent()){
                        if(rng.nextBoolean()){
                            game.moveStudent(color.get(), islands.get(rng.nextInt(islands.size())));
                        }else{
                            game.moveStudent(color.get());
                        }
                    }else{
                        /* Very Special Case (and very unlikely)
                           Example:
                            getEntranceStudents: {PINK=0, GREEN=0, RED=5, BLUE=0, YELLOW=0}
                            getDiningStudents: {PINK=0, GREEN=1, RED=8, BLUE=1, YELLOW=1}
                           Only one color in the entrance room and that color is full in the dining room.
                         */
                        // same as before, but without the dining room condition.
                        Optional<Color> color_island = students.entrySet().stream().filter(
                                (key_value) -> {
                                    return key_value.getKey() != null // is a valid color
                                            && key_value.getValue() > 0; // is present (at least one element)
                                }).map(Map.Entry::getKey).findFirst();
                        assertTrue(color_island.isPresent());
                        game.moveStudent(color_island.get(), islands.get(rng.nextInt(islands.size())));
                    }

                }
                assertEquals(4, game.getCurrentPlayer().getSchoolBoard().getEntranceStudents().count());
                checkInvariant(game);

                // step 2 - move mother nature
                Island mother_nature_island = islands.stream().filter(Island::hasMotherNature).findFirst().get();
                int curr_mother_nature_position = islands.indexOf(mother_nature_island);
                int next_mother_nature_position = (curr_mother_nature_position + rng.nextInt(game.getCurrentPlayer().getCurrentAssistant().getSteps()) + 1) % islands.size();

                game.moveMotherNature(islands.get(next_mother_nature_position));
                checkInvariant(game);

                // conquering an island
                game.conquerIsland();
                checkInvariant(game);

                // check victory immediately
                if (game.checkVictory()) {
                    assertNotNull(game.winner());
                    return;
                }

                // step 3 - choose cloud tiles
                ArrayList<Students> clouds = game.getClouds();

                clouds.forEach((x) -> assertTrue(x.count() == 3 || x.count() == 0));

                Students students;
                do {
                    students = clouds.get(rng.nextInt(clouds.size()));
                } while (students.count() <= 0);
                try {
                    game.chooseCloud(students);
                } catch (EmptyCloudException e) {
                    throw new RuntimeException(e);
                }
                checkInvariant(game);

                assertEquals(0, students.count());
                assertEquals(7, game.getCurrentPlayer().getSchoolBoard().getEntranceStudents().count());

                // end phase
                game.nextTurn();
            }

            // check victory end game
            if (game.checkEndGame()) {
                assertNotNull(game.winner());
                return ;
            }
        }

        ArrayList<Island> islands = game.getIslands();
        islands.get(game.findMotherNaturePosition()).setMotherNature(false);
        islands.get(rng.nextInt(islands.size())).setMotherNature(true);
        ArrayList<Island> islands_copy = new ArrayList<>(islands);
        Witch witch = new Witch();
        PlayerChoices playerChoices = new PlayerChoices();
        playerChoices.setIsland(islands.get(game.findMotherNaturePosition()));
        witch.activate(game, playerChoices);
        game.conquerIsland();

        //Check that island doesn't change after activate that character
        for(int i=0; i<islands.size(); i++){
            assertEquals(islands_copy.get(i).getOwner(), islands.get(i).getOwner());
            assertEquals(islands_copy.get(i).getNumIslands(), islands.get(i).getNumIslands());
            assertEquals(islands_copy.get(i).getStudents(), islands.get(i).getStudents());
            assertEquals(islands_copy.get(i).getNumTowers(), islands.get(i).getNumTowers());
            assertEquals(islands_copy.get(i).getNoEntryTiles(), islands.get(i).getNoEntryTiles());
            assertEquals(islands_copy.get(i).hasMotherNature(), islands.get(i).hasMotherNature());
        }
    }
}
