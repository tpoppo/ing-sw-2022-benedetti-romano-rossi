package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Island;
import it.polimi.ingsw.model.board.Students;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;
import it.polimi.ingsw.utils.exceptions.EmptyBagException;
import it.polimi.ingsw.utils.exceptions.EmptyMovableException;
import it.polimi.ingsw.utils.exceptions.FullLobbyException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Random;

public class RecruiterTest {

    @Test
    public void Recruiter() throws BadPlayerChoiceException, EmptyMovableException, EmptyBagException, FullLobbyException {
        LobbyHandler lobby = new LobbyHandler(2);
        LobbyPlayer player1 = new LobbyPlayer("Player 1");
        LobbyPlayer player2 = new LobbyPlayer("Player 2");
        player1.setWizard(1);
        player2.setWizard(2);
        Random rng = new Random();

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game game = new Game(true, lobby);

        ArrayList<Island> islands = game.getIslands();
        Recruiter recruiter = (Recruiter) Character.createCharacter(Characters.RECRUITER, game);

        PlayerChoices playerchoice = new PlayerChoices();

        Students student = new Students();
        Island island = islands.get(3);

        island.setStudents(new Students());

        Color color = null;
        do{
            color = Color.getRandomColor();
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
