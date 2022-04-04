package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Island;
import it.polimi.ingsw.model.board.Students;
import it.polimi.ingsw.model.exceptions.BadPlayerChoiceException;
import it.polimi.ingsw.model.exceptions.EmptyBagException;
import it.polimi.ingsw.model.exceptions.EmptyMovableException;
import it.polimi.ingsw.model.exceptions.FullLobbyException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

public class RecruiterTest {

    @Test
    public void Recruiter() throws BadPlayerChoiceException, EmptyMovableException, EmptyBagException, FullLobbyException {
        Lobby lobby = new Lobby(2);
        Player player1 = new Player("Player 1", 1);
        Player player2 = new Player("Player 2", 2);
        Random rng = new Random();

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game game = new Game(true, lobby);

        ArrayList<Island> islands = game.getIslands();
        Recruiter recruiter = new Recruiter(game);

        PlayerChoices playerchoice = new PlayerChoices();

        Students student = new Students();
        Island island = islands.get(3);

        island.setStudents(new Students());

        Color color = null;
        do{
            color = Color.getColors().get((new Random()).nextInt(Color.getColors().size()));
            if(recruiter.getStudents().getOrDefault(color, 0) == 0) color = null;
        }while(color == null);

        playerchoice.setStudent(color);
        playerchoice.setIsland(island);
        assertEquals(1, playerchoice.getStudent().size());

        recruiter.onActivation(game, playerchoice);

        Students expected_student = new Students();
        expected_student.add(color);
        expected_student.forEach((key, value) -> assertEquals(value, islands.get(3).getStudents().getOrDefault(key, -1), "key: "+key));
    }
}
