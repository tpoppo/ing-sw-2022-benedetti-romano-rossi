package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Students;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;
import it.polimi.ingsw.utils.exceptions.EmptyBagException;
import it.polimi.ingsw.utils.exceptions.FullLobbyException;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test of the character Thief
 */
public class ThiefTest {

    /**
     * Test that when a player activate this card every player return 3 students of the chosen color from the dining room
     * @throws FullLobbyException if someone tries to join a lobby but the lobby has already three players
     * @throws EmptyBagException if someone tries to draw a student from the bag but the bag is empty
     * @throws BadPlayerChoiceException if the chosen student is null
     */
    @Test
    public void Thief() throws FullLobbyException, EmptyBagException, BadPlayerChoiceException {
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

        Students student1 = new Students(4, 0, 0, 0, 0);
        Students student2 = new Students(2, 0, 0, 0, 0);

        // player 1 is the current player, while player 2 is the other one.
        Player player1 = game.getCurrentPlayer();
        Player player2 = game.getPlayers().stream().filter((x) -> !x.equals(game.getCurrentPlayer())).findFirst().get();

         
        player1.getSchoolBoard().setDiningStudents(student1);
        player2.getSchoolBoard().setDiningStudents(student2);

        Thief thief = new Thief();
        thief.require();
        PlayerChoices playerchoice = new PlayerChoices();
        playerchoice.setStudent(Color.GREEN);
        thief.activate(game, playerchoice);

        Students expected_student1 = new Students(1, 0, 0, 0, 0);
        Students expected_student2 = new Students(0, 0, 0, 0, 0);
        assertEquals(expected_student1, player1.getSchoolBoard().getDiningStudents());
        assertEquals(expected_student2, player2.getSchoolBoard().getDiningStudents());
    }
}
