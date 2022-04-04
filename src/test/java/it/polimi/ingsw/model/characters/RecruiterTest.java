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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

public class RecruiterTest {

    @Test
    public void Recruiter() throws BadPlayerChoiceException, EmptyMovableException, EmptyBagException {
        Lobby lobby = new Lobby(2);
        Game game = new Game(true, lobby);

        ArrayList<Island> islands = game.getIslands();
        Recruiter recruiter = new Recruiter(game);

        PlayerChoices playerchoice = new PlayerChoices();

        Students student = new Students();
        Island island = new Island();
        island = islands.get(3);

        playerchoice.setStudent(Color.RED);
        playerchoice.setIsland(island);

        recruiter.onActivation(game, playerchoice);

        Students expected_student = new Students(0, 0, 0, 0, 1);
        assertEquals(expected_student, islands.get(3).getStudents());
    }
}
