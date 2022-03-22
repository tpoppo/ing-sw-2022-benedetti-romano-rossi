package it.polimi.ingsw;

import it.polimi.ingsw.board.Color;
import it.polimi.ingsw.board.Island;
import it.polimi.ingsw.board.Students;
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
}
