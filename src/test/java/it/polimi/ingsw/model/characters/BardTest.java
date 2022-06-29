package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Students;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;
import it.polimi.ingsw.utils.exceptions.EmptyBagException;
import it.polimi.ingsw.utils.exceptions.FullLobbyException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test of the class Bard
 */
public class BardTest {

    /**
     * Test the character Bard swapping two students between the entrance room and dining room
     * @throws FullLobbyException if someone tries to join a lobby but the lobby has already three players
     * @throws EmptyBagException if someone tries to draw a student from the bag but the bag is empty
     * @throws BadPlayerChoiceException if there aren't the chosen students in the entrance and dining room or the number of students in input are odd
     * or greater than four
     */
    @Test
    public void Bard() throws FullLobbyException, EmptyBagException, BadPlayerChoiceException {
        LobbyHandler lobby = new LobbyHandler(0, 2);
        LobbyPlayer player1 = new LobbyPlayer("Player 1");
        LobbyPlayer player2 = new LobbyPlayer("Player 2");
        player1.setWizard(1);
        player2.setWizard(2);

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game game = new Game(true, lobby);
        game.beginPlanning();

        Students diningstudents = new Students(1, 1, 2, 0, 1);
        game.getCurrentPlayer().getSchoolBoard().setDiningStudents(diningstudents);

        Students entrancestudents = new Students(2, 1, 0, 2, 0);
        game.getCurrentPlayer().getSchoolBoard().setEntranceStudents(entrancestudents);

        Bard bard = new Bard();

        PlayerChoices playerChoices = new PlayerChoices();
        ArrayList<Color> swap_list = new ArrayList<>();
        swap_list.add(Color.MAGENTA);
        swap_list.add(Color.GREEN);
        swap_list.add(Color.CYAN);
        swap_list.add(Color.RED);
        playerChoices.setStudent(swap_list);
        bard.activate(game, playerChoices);
        bard.require();

        Students expected_diningstudents = new Students(0, 2, 2, 1, 0);
        Students expected_entrancestudents = new Students(3, 0, 0, 1, 1);

        assertEquals(expected_diningstudents, game.getCurrentPlayer().getSchoolBoard().getDiningStudents());
        assertEquals(expected_entrancestudents, game.getCurrentPlayer().getSchoolBoard().getEntranceStudents());

        bard.deactivate(game);
    }

    /**
     * Test that when the number of students in input is odd the exception BadPlayerChoiceException is called
     * @throws FullLobbyException if someone tries to join a lobby but the lobby has already three players
     * @throws EmptyBagException if someone tries to draw a student from the bag but the bag is empty
     */
    @Test
    public void BadPlayerChoiceException1() throws FullLobbyException, EmptyBagException {
        LobbyHandler lobby = new LobbyHandler(0, 2);
        LobbyPlayer player1 = new LobbyPlayer("Player 1");
        LobbyPlayer player2 = new LobbyPlayer("Player 2");
        player1.setWizard(1);
        player2.setWizard(2);

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game game = new Game(true, lobby);
        game.beginPlanning();

        Students diningstudents = new Students(1, 1, 2, 0, 1);
        game.getCurrentPlayer().getSchoolBoard().setDiningStudents(diningstudents);

        Students entrancestudents = new Students(2, 1, 0, 2, 0);
        game.getCurrentPlayer().getSchoolBoard().setEntranceStudents(entrancestudents);

        Bard bard = new Bard();

        PlayerChoices playerChoices = new PlayerChoices();
        ArrayList<Color> swap_list = new ArrayList<>();
        swap_list.add(Color.MAGENTA);
        swap_list.add(Color.GREEN);
        swap_list.add(Color.GREEN);
        playerChoices.setStudent(swap_list);
        assertThrows(BadPlayerChoiceException.class, () -> bard.activate(game, playerChoices));
        bard.deactivate(game);
    }

    /**
     * Test that when the students in input aren't in the entrance or dining room the exception BadPlayerChoiceException is called
     * @throws FullLobbyException if someone tries to join a lobby but the lobby has already three players
     * @throws EmptyBagException if someone tries to draw a student from the bag but the bag is empty
     */
    @Test
    public void BadPlayerChoiceException2() throws FullLobbyException, EmptyBagException {
        LobbyHandler lobby = new LobbyHandler(0, 2);
        LobbyPlayer player1 = new LobbyPlayer("Player 1");
        LobbyPlayer player2 = new LobbyPlayer("Player 2");
        player1.setWizard(1);
        player2.setWizard(2);

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game game = new Game(true, lobby);
        game.beginPlanning();

        Students diningstudents = new Students(0, 1, 2, 0, 1);
        game.getCurrentPlayer().getSchoolBoard().setDiningStudents(diningstudents);

        Students entrancestudents = new Students(0, 1, 0, 2, 0);
        game.getCurrentPlayer().getSchoolBoard().setEntranceStudents(entrancestudents);

        Bard bard = new Bard();

        PlayerChoices playerChoices = new PlayerChoices();
        ArrayList<Color> swap_list = new ArrayList<>();
        swap_list.add(Color.GREEN);
        swap_list.add(Color.GREEN);
        swap_list.add(Color.GREEN);
        swap_list.add(Color.GREEN);
        playerChoices.setStudent(swap_list);
        assertThrows(BadPlayerChoiceException.class, () -> bard.activate(game, playerChoices));
        bard.deactivate(game);
    }
}
