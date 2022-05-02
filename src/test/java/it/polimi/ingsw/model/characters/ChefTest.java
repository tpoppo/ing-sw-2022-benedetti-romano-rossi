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

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChefTest {

    @Test
    public void Chef() throws FullLobbyException, EmptyMovableException, EmptyBagException, BadPlayerChoiceException {
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

        Students students = new Students(2, 1, 0, 3, 0);
        game.getCurrentPlayer().getSchoolBoard().setDiningStudents(students);

        Chef chef = new Chef(game);

        Students Chef_students = chef.getStudents();

        Color Chefcolor = null;
        Boolean choosen = false;
        for (Color color : Chef_students.keySet()) {
            if(Chef_students.get(color) !=0 && choosen == false){
                Chefcolor = color;
                choosen = true;
            }
        }
        PlayerChoices playerChoices = new PlayerChoices();
        playerChoices.setStudent(Chefcolor);

        chef.onActivation(game, playerChoices);

        Students expected_students = new Students(2, 1, 0, 3, 0);
        expected_students.add(Chefcolor);

        assertEquals(expected_students, game.getCurrentPlayer().getSchoolBoard().getDiningStudents());
    }
}
