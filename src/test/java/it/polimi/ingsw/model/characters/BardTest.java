package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Students;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;
import it.polimi.ingsw.utils.exceptions.EmptyBagException;
import it.polimi.ingsw.utils.exceptions.EmptyMovableException;
import it.polimi.ingsw.utils.exceptions.FullLobbyException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BardTest {

    @Test
    public void Bard() throws FullLobbyException, EmptyMovableException, EmptyBagException, BadPlayerChoiceException {
        LobbyHandler lobby = new LobbyHandler(2);
        LobbyPlayer player1 = new LobbyPlayer("Player 1");
        LobbyPlayer player2 = new LobbyPlayer("Player 2");
        player1.setWizard(1);
        player2.setWizard(2);
        Random rng = new Random();

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
        swap_list.add(Color.PINK);
        swap_list.add(Color.GREEN);
        swap_list.add(Color.BLUE);
        swap_list.add(Color.RED);
        playerChoices.setStudent(swap_list);
        bard.activate(game, playerChoices);

        Students expected_diningstudents = new Students(0, 2, 2, 1, 0);
        Students expected_entrancestudents = new Students(3, 0, 0, 1, 1);

        assertEquals(expected_diningstudents, game.getCurrentPlayer().getSchoolBoard().getDiningStudents());
        assertEquals(expected_entrancestudents, game.getCurrentPlayer().getSchoolBoard().getEntranceStudents());

        bard.deactivate(game, playerChoices);
    }
}
