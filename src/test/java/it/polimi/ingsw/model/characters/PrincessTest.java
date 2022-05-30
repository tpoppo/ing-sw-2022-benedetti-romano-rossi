package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.model.board.Bag;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PrincessTest {

    @Test
    public void Chef() throws FullLobbyException, EmptyMovableException, EmptyBagException, BadPlayerChoiceException {
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

        Students students = new Students(2, 1, 0, 3, 0);
        game.getCurrentPlayer().getSchoolBoard().setDiningStudents(students);

        Princess princess = new Princess(game);
        princess.require();

        Students Chef_students = princess.getStudents();

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

        princess.onActivation(game, playerChoices);

        Students expected_students = new Students(2, 1, 0, 3, 0);
        expected_students.add(Chefcolor);

        assertEquals(expected_students, game.getCurrentPlayer().getSchoolBoard().getDiningStudents());
    }

    //Test when there are more than one colors in playerchoice
    @Test
    public void BadPlayerChoiceException() throws FullLobbyException, EmptyMovableException, EmptyBagException, BadPlayerChoiceException {
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

        Students students = new Students(2, 3, 2, 0, 1);
        game.getCurrentPlayer().getSchoolBoard().setDiningStudents(students);

        Princess princess = new Princess(game);

        ArrayList<Color> colors = new ArrayList<>();
        colors.add(Color.MAGENTA);
        colors.add(Color.GREEN);

        PlayerChoices playerChoices = new PlayerChoices();
        playerChoices.setStudent(colors);

        assertThrows(BadPlayerChoiceException.class, () -> princess.activate(game, playerChoices));
    }

    //Test when there is not the choosen color in the dining room
    @Test
    public void BadPlayerChoiceException2() throws FullLobbyException, EmptyMovableException, EmptyBagException, BadPlayerChoiceException {
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

        Students students = new Students(0, 1, 2, 2, 1);
        game.getCurrentPlayer().getSchoolBoard().setDiningStudents(students);

        Princess princess = new Princess(game);

        Students Chef_students = princess.getStudents();
        Color Chefcolor = null;
        Boolean choosen = false;

        //choose a color that is not in the princess student's
        for (Color color : Chef_students.keySet()) {
            if(Chef_students.get(color) == 0 && choosen == false){
                Chefcolor = color;
                choosen = true;
            }
        }

        PlayerChoices playerChoices = new PlayerChoices();
        playerChoices.setStudent(Chefcolor);

        assertThrows(BadPlayerChoiceException.class, () -> princess.activate(game, playerChoices));
    }

    //Test when the bag is empty
    @Test
    public void BadPlayerChoiceException3() throws FullLobbyException, EmptyMovableException, EmptyBagException, BadPlayerChoiceException {
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

        Students students = new Students(2, 2, 2, 2, 1);
        game.getCurrentPlayer().getSchoolBoard().setDiningStudents(students);

        Princess princess = new Princess(game);

        Students Chef_students = princess.getStudents();

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

        Bag bag = new Bag();
        bag = game.getBag();

        while(bag.capacity() > 0){
            game.drawStudentFromBag();
        }

        assertThrows(BadPlayerChoiceException.class, () -> princess.activate(game, playerChoices));
    }
}
