package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.*;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.utils.exceptions.*;
import org.junit.jupiter.api.RepeatedTest;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


public class GameTest {

    /**
     * Check whether the invariants of the game are respected
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
        assertEquals(islands_tiles_counter, 12);

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
    }

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
            Assistant assistant = null;
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

                Students students = null;
                do {
                    students = clouds.get(rng.nextInt(clouds.size()));
                } while (students.count() <= 0);
                game.chooseCloud(students);
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
    
    @RepeatedTest(200)
    public void simpleRun3Player() throws FullLobbyException, FullDiningRoomException, EmptyMovableException, EmptyBagException, AssistantAlreadyPlayedException, MoveMotherNatureException {
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

                Students students = null;
                do {
                    students = clouds.get(rng.nextInt(clouds.size()));
                } while (students.count() <= 0);
                game.chooseCloud(students);

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

    @RepeatedTest(100)
    public void PlanningPhase() throws FullLobbyException, EmptyMovableException, EmptyBagException, AssistantAlreadyPlayedException {
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

    @RepeatedTest(100)
    public void PlanningPhaseException() throws FullLobbyException, EmptyBagException, AssistantAlreadyPlayedException, EmptyMovableException {
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

    /*
    These tests were done when game.mergeisland was public
    // Test with all the owner equals
    @Test
    public void Merge() throws FullLobbyException, EmptyMovableException, EmptyBagException {
        Lobby lobby = new Lobby(2);
        Player player1 = new Player("Player 1", 1);
        Player player2 = new Player("Player 2", 2);
        Random rng = new Random();

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game game = new Game(true, lobby);
        ArrayList<Island> islands = game.getIslands();

        //Set all the islands with the same Owner
        for(Island island : islands){
            island.setOwner(player1);
        }
        ArrayList<Island> expectedislands = new ArrayList<Island>();
        expectedislands.add(islands.get(0));
        game.mergeIslands();
        assertEquals(expectedislands, islands);
    }

    //Test with first and last Owner equals
    @Test
    public void Merge2() throws FullLobbyException, EmptyMovableException, EmptyBagException {
        Lobby lobby = new Lobby(2);
        Player player1 = new Player("Player 1", 1);
        Player player2 = new Player("Player 2", 2);
        Random rng = new Random();

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game game = new Game(true, lobby);
        ArrayList<Island> islands = game.getIslands();

        //Set player2 as Owner of the first and last island, player1 as Owner of all the other islands
        int position = 0;
        for(Island island : islands){
            if(position == 0){
                island.setOwner(player2);
                position++;
                continue;
            }
            if(position == islands.size()-1){
                island.setOwner(player2);
                position++;
                continue;
            }
            island.setOwner(player1);
            position++;
        }
        game.mergeIslands();
        assertEquals(2, islands.size());
        assertEquals(player1, islands.get(0).getOwner());
        assertEquals(player2, islands.get(islands.size() - 1).getOwner());
    }*/
}
