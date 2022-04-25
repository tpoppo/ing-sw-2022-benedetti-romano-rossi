package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameModifiers;
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

public class JugglerTest {

    @Test
    public void Juggler() throws FullLobbyException, EmptyMovableException, EmptyBagException, BadPlayerChoiceException {
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

        Students student = new Students(3, 2, 0, 4, 2);
        game.getPlayers().get(0).getSchoolBoard().setEntranceStudents(student);

        PlayerChoices playerchoice= new PlayerChoices();
        ArrayList<Color> change = new ArrayList<Color>();

        Juggler juggler = new Juggler(game);
        Students Jugglerstudents = juggler.getStudents();

        Color Jugglercolor = null;
        boolean choosen = false;
        for (Color color : Jugglerstudents.keySet()) {
            if(Jugglerstudents.get(color) !=0 && choosen == false){
                Jugglercolor = color;
                choosen = true;
            }
        }
        change.add(Color.GREEN);
        change.add(Jugglercolor);
        playerchoice.setStudent(change);

        juggler.onActivation(game, playerchoice);

        Students expected_student = new Students(2, 2, 0, 4, 2);
        expected_student.add(Jugglercolor);

        //assertEquals(expected_student, game.getPlayers().get(0).getSchoolBoard().getEntranceStudents());
    }
}
