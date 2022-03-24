package it.polimi.ingsw;

import it.polimi.ingsw.board.*;
import it.polimi.ingsw.exceptions.AssistantAlreadyPlayedException;
import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.exceptions.EmptyMovableException;
import it.polimi.ingsw.exceptions.FullLobbyException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class GameTest {

    @Test
    public void simpleRun() throws FullLobbyException, EmptyMovableException, EmptyBagException, AssistantAlreadyPlayedException {
        Lobby lobby = new Lobby(2);
        Player player0 = new Player("Player 1", 1);
        Player player1 = new Player("Player 2", 2);
        Random rng = new Random();

        lobby.addPlayer(player0);
        lobby.addPlayer(player1);

        Game game = new Game(true, lobby);

        game.fillClouds();
        game.beginPlanning();
        System.out.println(player0 + " " + player1 + " => " + game.getCurrentPlayer());

        game.playAssistant(game.getCurrentPlayer().getPlayerHand().get(rng.nextInt(game.getCurrentPlayer().getPlayerHand().size())));
        Player first_player = game.getCurrentPlayer();
        game.nextTurn();
        game.playAssistant(game.getCurrentPlayer().getPlayerHand().get(rng.nextInt(game.getCurrentPlayer().getPlayerHand().size())));
        assertNotEquals(game.getCurrentPlayer(), first_player);

        game.endPlanning();

        ArrayList<Island> islands = game.getIslands();

        // player 0
        for(int i=0; i<3; i++){
            // move a random student from the player's entrance to a random island
            Students students = game.getCurrentPlayer().getSchoolBoard().getEntranceStudents();
            Optional<Color> color = students.entrySet().stream().filter(
                    (key_value) -> {
                        return key_value.getKey() != null && key_value.getValue() > 0;
                    }).map((key_value) -> key_value.getKey()).findFirst();

            game.moveStudent(color.get(), islands.get(rng.nextInt(islands.size())));
        }
        game.nextTurn();
        // player 1
        for(int i=0; i<3; i++){
            // move a random student from the player's entrance to a random island
            Students students = game.getCurrentPlayer().getSchoolBoard().getEntranceStudents();
            Optional<Color> color = students.entrySet().stream().filter(
                    (key_value) -> {
                        return key_value.getKey() != null && key_value.getValue() > 0;
                    }).map((key_value) -> key_value.getKey()).findFirst();

            game.moveStudent(color.get());
        }
        game.nextTurn();

        // TODO: not finished
    }

    @Test
    public void Islandtest(){
        Island island1 = new Island();
        Island island2 = new Island();

        Students student1 = new Students(2, 3, 1, 0, 2);
        Students student2 = new Students(1, 0, 2, 3, 0);

        Player player1 = new Player("Player1", 0);
        Player player2 = new Player("Player2", 1);

        island1.setMotherNature(true);
        island1.setStudents(student1);
        island1.setNumTowers(1);
        island1.setOwner(player1);
        island1.setNoEntryTiles(0);

        island2.setMotherNature(false);
        island2.setStudents(student2);
        island2.setNumTowers(2);
        island2.setOwner(player2);
        island2.setNoEntryTiles(0);

        Students sum_student = new Students(3, 3, 3, 3, 2);

        island1.merge(island2);

        assertEquals(true, island1.hasMotherNature());
        assertEquals(sum_student, island1.getStudents());
        assertEquals(3, island1.getNumTowers());
        assertEquals(player1, island1.getOwner());
        assertEquals(0, island1.getNoEntryTiles());
    }

    @Test
    public void SchoolBoard_test(){
        Professors professor = new Professors();
        Students dining_students = new Students(4, 5, 2, 6, 1);
        Students entrance_students = new Students(2, 2, 1, 3, 1);

        SchoolBoard schoolboard = new SchoolBoard(3, professor, dining_students, entrance_students);

        assertEquals(3, schoolboard.getNumTowers());
        assertEquals(professor, schoolboard.getProfessors());
        assertEquals(dining_students, schoolboard.getDiningStudents());
        assertEquals(entrance_students, schoolboard.getEntranceStudents());
    }

}
