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
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JugglerTest {

    @RepeatedTest(50)
    public void Juggler() throws FullLobbyException, EmptyBagException, BadPlayerChoiceException {
        LobbyHandler lobby = new LobbyHandler(0, 2);
        LobbyPlayer player1 = new LobbyPlayer("Player 1");
        LobbyPlayer player2 = new LobbyPlayer("Player 2");
        player1.setWizard(1);
        player2.setWizard(2);
        Random rng = new Random();

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game game = new Game(true, lobby);
        game.beginPlanning();

        Students student = new Students(3, 2, 0, 4, 2);
        game.getCurrentPlayer().getSchoolBoard().setEntranceStudents(student);

        PlayerChoices playerchoice= new PlayerChoices();
        ArrayList<Color> change = new ArrayList<Color>();

        Juggler juggler = new Juggler(game);
        juggler.require();
        Students Jugglerstudents = juggler.getStudents();

        Color Jugglercolor = null;
        boolean choosen = false;
        for (Color color : Jugglerstudents.keySet()) {
            if(Jugglerstudents.get(color) !=0 && !choosen){
                Jugglercolor = color;
                choosen = true;
            }
        }

        change.add(Color.GREEN);
        change.add(Jugglercolor);
        playerchoice.setStudent(change);

        juggler.activate(game, playerchoice);

        Students expected_student = new Students(2, 2, 0, 4, 2);
        expected_student.add(Jugglercolor);

        assertEquals(expected_student, game.getCurrentPlayer().getSchoolBoard().getEntranceStudents());
    }

    //Test when the number of students is odd
    @Test
    public void BadPlayerChoiceException() throws FullLobbyException, EmptyBagException {
        LobbyHandler lobby = new LobbyHandler(0, 2);
        LobbyPlayer player1 = new LobbyPlayer("Player 1");
        LobbyPlayer player2 = new LobbyPlayer("Player 2");
        player1.setWizard(1);
        player2.setWizard(2);
        Random rng = new Random();

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game game = new Game(true, lobby);
        game.beginPlanning();

        Students student = new Students(3, 2, 0, 4, 2);
        game.getCurrentPlayer().getSchoolBoard().setEntranceStudents(student);

        PlayerChoices playerchoice= new PlayerChoices();
        ArrayList<Color> change = new ArrayList<Color>();

        Juggler juggler = new Juggler(game);
        juggler.require();
        Students Jugglerstudents = juggler.getStudents();

        Color Jugglercolor = null;
        boolean choosen = false;
        for (Color color : Jugglerstudents.keySet()) {
            if(Jugglerstudents.get(color) !=0 && !choosen){
                Jugglercolor = color;
                choosen = true;
            }
        }

        change.add(Color.GREEN);
        playerchoice.setStudent(change);

        assertThrows(BadPlayerChoiceException.class, () -> juggler.activate(game, playerchoice));
    }

    @Test
    public void BadPlayerChoiceException2() throws FullLobbyException, EmptyBagException {
        LobbyHandler lobby = new LobbyHandler(0, 2);
        LobbyPlayer player1 = new LobbyPlayer("Player 1");
        LobbyPlayer player2 = new LobbyPlayer("Player 2");
        player1.setWizard(1);
        player2.setWizard(2);
        Random rng = new Random();

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game game = new Game(true, lobby);
        game.beginPlanning();

        Students student = new Students(0, 2, 0, 4, 2);
        game.getCurrentPlayer().getSchoolBoard().setEntranceStudents(student);

        PlayerChoices playerchoice= new PlayerChoices();
        ArrayList<Color> change = new ArrayList<Color>();

        Juggler juggler = new Juggler(game);
        juggler.require();
        Students Jugglerstudents = juggler.getStudents();

        Color Jugglercolor = null;
        boolean choosen = false;
        for (Color color : Jugglerstudents.keySet()) {
            if(Jugglerstudents.get(color) !=0 && !choosen){
                Jugglercolor = color;
                choosen = true;
            }
        }

        change.add(Color.GREEN);
        change.add(Jugglercolor);
        playerchoice.setStudent(change);

        assertThrows(BadPlayerChoiceException.class, () -> juggler.activate(game, playerchoice));
    }
}
