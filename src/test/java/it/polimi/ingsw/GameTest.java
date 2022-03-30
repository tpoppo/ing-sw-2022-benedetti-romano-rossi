package it.polimi.ingsw;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.*;
import it.polimi.ingsw.model.exceptions.*;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


public class GameTest {

    @RepeatedTest(100)
    public void simpleRun() throws FullLobbyException, EmptyMovableException, EmptyBagException, AssistantAlreadyPlayedException, MoveMotherNatureException {
        Lobby lobby = new Lobby(2);
        Player player0 = new Player("Player 1", 1);
        Player player1 = new Player("Player 2", 2);
        Random rng = new Random();

        lobby.addPlayer(player0);
        lobby.addPlayer(player1);

        Game game = new Game(true, lobby);
        while(true) {
            // planning phase
            game.fillClouds();
            game.getClouds().forEach((x) -> assertTrue(x.count() == 3));

            game.beginPlanning();

            // player0
            game.playAssistant(game.getCurrentPlayer().getPlayerHand().get(rng.nextInt(game.getCurrentPlayer().getPlayerHand().size())));
            Player first_player = game.getCurrentPlayer();
            game.nextTurn();

            // player1
            game.playAssistant(game.getCurrentPlayer().getPlayerHand().get(rng.nextInt(game.getCurrentPlayer().getPlayerHand().size())));
            assertNotEquals(game.getCurrentPlayer(), first_player);
            game.nextTurn();


            game.endPlanning();
            assertTrue(game.getCurrentPlayer() != null);

            ArrayList<Island> islands = game.getIslands();

            // action phase
            for (int player_id = 0; player_id < 2; player_id++) {
                // step 1
                for (int i = 0; i < 3; i++) {
                    // move a random student from the player's entrance to a random island
                    Students students = game.getCurrentPlayer().getSchoolBoard().getEntranceStudents();
                    Optional<Color> color = students.entrySet().stream().filter(
                            (key_value) -> {
                                return key_value.getKey() != null && key_value.getValue() > 0;
                            }).map((key_value) -> key_value.getKey()).findFirst();
                    if(rng.nextBoolean()){
                        game.moveStudent(color.get(), islands.get(rng.nextInt(islands.size())));
                    }else{
                        game.moveStudent(color.get());
                    }
                }
                assertEquals(4, game.getCurrentPlayer().getSchoolBoard().getEntranceStudents().count());

                // step 2 - move mother nature
                Island mother_nature_island = islands.stream().filter(Island::hasMotherNature).findFirst().get();
                int curr_mother_nature_position = islands.indexOf(mother_nature_island);
                int next_mother_nature_position = (curr_mother_nature_position + rng.nextInt(game.getCurrentPlayer().getCurrentAssistant().get().getSteps()) + 1) % islands.size();

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

                clouds.forEach((x) -> assertTrue(x.count() == 3 || x.count() == 0));

                Students students = null;
                do {
                    students = clouds.get(rng.nextInt(clouds.size()));
                } while (students.count() <= 0);
                game.chooseCloud(students);

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
    }
}
