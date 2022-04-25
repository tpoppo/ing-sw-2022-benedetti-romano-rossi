package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameModifiers;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Students;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;
import it.polimi.ingsw.utils.exceptions.EmptyBagException;
import it.polimi.ingsw.utils.exceptions.EmptyMovableException;
import it.polimi.ingsw.utils.exceptions.FullLobbyException;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ThiefTest {

    @Test
    public void Thief() throws FullLobbyException, EmptyMovableException, EmptyBagException, BadPlayerChoiceException {
        LobbyHandler lobby = new LobbyHandler(2);
        LobbyPlayer player1 = new LobbyPlayer("Player 1");
        LobbyPlayer player2 = new LobbyPlayer("Player 2");
        player1.setWizard(1);
        player2.setWizard(2);
        Random rng = new Random();

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game game = new Game(true, lobby);

        Students student1 = new Students(4, 0, 0, 0, 0);
        Students student2 = new Students(2, 0, 0, 0, 0);

        game.getPlayers().get(0).getSchoolBoard().setDiningStudents(student1);
        game.getPlayers().get(1).getSchoolBoard().setDiningStudents(student2);

        Thief thief = new Thief();
        PlayerChoices playerchoice = new PlayerChoices();
        playerchoice.setStudent(Color.GREEN);
        thief.onActivation(game, playerchoice);

        Students expected_student1 = new Students(1, 0, 0, 0, 0);
        Students expected_student2 = new Students(0, 0, 0, 0, 0);
        //assertEquals(expected_student1, game.getPlayers().get(0).getSchoolBoard().getDiningStudents());
        //assertEquals(expected_student2, game.getPlayers().get(1).getSchoolBoard().getDiningStudents());
    }
}
