package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Island;
import it.polimi.ingsw.model.board.Students;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;
import it.polimi.ingsw.utils.exceptions.EmptyBagException;
import it.polimi.ingsw.utils.exceptions.FullLobbyException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Random;

/**
 * Test the character Monk
 */
public class MonkTest {

    /**
     * Test that when a player activate this card he can move a student on an Island of his choice
     * @throws BadPlayerChoiceException if the students number in input is different from one or the chosen student from the card isn't in the card
     * @throws EmptyBagException if someone tries to draw a student from the bag but the bag is empty
     * @throws FullLobbyException if someone tries to join a lobby but the lobby has already three players
     */
    @Test
    public void Recruiter() throws BadPlayerChoiceException, EmptyBagException, FullLobbyException {
        LobbyHandler lobby = new LobbyHandler(0, 2);
        LobbyPlayer player1 = new LobbyPlayer("Player 1");
        LobbyPlayer player2 = new LobbyPlayer("Player 2");
        player1.setWizard(1);
        player2.setWizard(2);

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game game = new Game(true, lobby);

        ArrayList<Island> islands = game.getIslands();
        Monk monk = (Monk) Character.createCharacter(Characters.MONK, game);
        monk.require();

        PlayerChoices playerchoice = new PlayerChoices();

        Island island = islands.get(3);

        island.setStudents(new Students());

        Color color;
        do{
            color = Color.getRandomColor();
            if(monk.getStudents().getOrDefault(color, 0) == 0) color = null;
        }while(color == null);

        playerchoice.setStudent(color);
        playerchoice.setIsland(island);
        assertEquals(1, playerchoice.getStudent().size());

        monk.activate(game, playerchoice);

        Students expected_student = new Students();
        expected_student.add(color);
        expected_student.forEach((key, value) -> assertEquals(value, islands.get(3).getStudents().getOrDefault(key, -1), "key: "+key));
    }

    /**
     * Test the number of students in input is greater than one the exception BadPlayerChoiceException is called
     * @throws EmptyBagException if someone tries to draw a student from the bag but the bag is empty
     * @throws FullLobbyException if someone tries to join a lobby but the lobby has already three players
     */
    @Test
    public void BadPlayerChoiceException() throws EmptyBagException, FullLobbyException {
        LobbyHandler lobby = new LobbyHandler(0, 2);
        LobbyPlayer player1 = new LobbyPlayer("Player 1");
        LobbyPlayer player2 = new LobbyPlayer("Player 2");
        player1.setWizard(1);
        player2.setWizard(2);

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game game = new Game(true, lobby);

        ArrayList<Island> islands = game.getIslands();
        Monk monk = (Monk) Character.createCharacter(Characters.MONK, game);
        monk.require();

        PlayerChoices playerchoice = new PlayerChoices();

        Island island = islands.get(3);

        island.setStudents(new Students());

        ArrayList<Color> students = new ArrayList<>();
        students.add(Color.GREEN);
        students.add(Color.MAGENTA);

        playerchoice.setStudent(students);
        playerchoice.setIsland(island);

        assertThrows(BadPlayerChoiceException.class, () -> monk.activate(game, playerchoice));
    }

    /**
     * Test that when a player tries to swap a student that isn't in the card the exception BadPlayerChoiceException is called
     * @throws EmptyBagException if someone tries to draw a student from the bag but the bag is empty
     * @throws FullLobbyException if someone tries to join a lobby but the lobby has already three players
     */
    @Test
    public void BadPlayerChoiceException2() throws EmptyBagException, FullLobbyException {
        LobbyHandler lobby = new LobbyHandler(0, 2);
        LobbyPlayer player1 = new LobbyPlayer("Player 1");
        LobbyPlayer player2 = new LobbyPlayer("Player 2");
        player1.setWizard(1);
        player2.setWizard(2);

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game game = new Game(true, lobby);

        ArrayList<Island> islands = game.getIslands();
        Monk monk = (Monk) Character.createCharacter(Characters.MONK, game);
        monk.require();

        PlayerChoices playerchoice = new PlayerChoices();

        Island island = islands.get(3);

        island.setStudents(new Students());

        Color color;
        do{
            color = Color.getRandomColor();
            if(monk.getStudents().getOrDefault(color, 0) != 0) color = null;
        }while(color == null);

        playerchoice.setStudent(color);
        playerchoice.setIsland(island);

        assertThrows(BadPlayerChoiceException.class, () -> monk.activate(game, playerchoice));
    }

}
