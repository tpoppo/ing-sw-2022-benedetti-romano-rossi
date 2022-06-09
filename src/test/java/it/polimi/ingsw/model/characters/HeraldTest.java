package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Island;
import it.polimi.ingsw.model.board.Professors;
import it.polimi.ingsw.model.board.Students;
import it.polimi.ingsw.utils.exceptions.AssistantAlreadyPlayedException;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;
import it.polimi.ingsw.utils.exceptions.EmptyBagException;
import it.polimi.ingsw.utils.exceptions.FullLobbyException;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class HeraldTest {
    @RepeatedTest(100)
    public void Herald() throws FullLobbyException, EmptyBagException, BadPlayerChoiceException, AssistantAlreadyPlayedException {
        LobbyHandler lobby = new LobbyHandler(0, 2);
        LobbyPlayer lobby_player1 = new LobbyPlayer("Player 1");
        LobbyPlayer lobby_player2 = new LobbyPlayer("Player 2");
        lobby_player1.setWizard(1);
        lobby_player2.setWizard(2);
        Random rng = new Random();

        lobby.addPlayer(lobby_player1);
        lobby.addPlayer(lobby_player2);

        Game game = new Game(true, lobby);
        game.beginPlanning();

        Students dining_student1 = new Students(0, 5, 0, 0, 0);
        Students dining_student2 = new Students(0, 2, 0, 0, 0);

        // player 1 is the current player, while player 2 is the other one.
        Player player1 = game.getCurrentPlayer();
        Player player2 = game.getPlayers().stream().filter((x) -> !x.equals(game.getCurrentPlayer())).findFirst().get();
        Professors professors = new Professors();
        professors.add(Color.CYAN);

        player1.getSchoolBoard().setDiningStudents(dining_student1);
        player2.getSchoolBoard().setDiningStudents(dining_student2);
        player1.getSchoolBoard().setProfessors(professors);

        game.playAssistant(player1.getPlayerHand().get(0));
        game.nextTurn();
        game.playAssistant(player2.getPlayerHand().get(1));
        game.endPlanning();

        Students null_students = new Students(0, 0, 0, 0, 0);

        ArrayList<Island> islands = game.getIslands();
        for(Island island : islands){
            island.setOwner(null);
            island.setStudents(null_students);
        }

        int position_herald = rng.nextInt(islands.size());

        Students students = new Students(0, 3, 0, 0, 0);
        islands.get(position_herald).setStudents(students);

        PlayerChoices playerChoices = new PlayerChoices();
        playerChoices.setIsland(islands.get(position_herald));

        Herald herald = new Herald();
        herald.activate(game, playerChoices);
        herald.deactivate(game);
        assertEquals(player1, islands.get(position_herald).getOwner());
    }
}
