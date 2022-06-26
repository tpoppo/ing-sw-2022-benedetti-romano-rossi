package it.polimi.ingsw.model.characters;


import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameModifiers;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;
import it.polimi.ingsw.utils.exceptions.EmptyBagException;
import it.polimi.ingsw.utils.exceptions.FullLobbyException;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test of the character Colorblind
 */
public class ColorblindTest {

    /**
     * Test that when the character Colorblind is activated the game will not consider the choosen color for the influence calculation
     * @throws FullLobbyException if someone tries to join a lobby but the lobby has already three players
     * @throws EmptyBagException if someone tries to draw a student from the bag but the bag is empty
     * @throws BadPlayerChoiceException if the choosen student is null
     */
    @Test
    public void Colorblind() throws FullLobbyException, EmptyBagException, BadPlayerChoiceException {
        LobbyHandler lobby = new LobbyHandler(0, 2);
        LobbyPlayer player1 = new LobbyPlayer("Player 1");
        LobbyPlayer player2 = new LobbyPlayer("Player 2");
        player1.setWizard(1);
        player2.setWizard(2);
        Random rng = new Random();

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game game = new Game(true, lobby);

        Colorblind colorblind = new Colorblind();
        colorblind.require();
        PlayerChoices playerchoice = new PlayerChoices();
        playerchoice.setStudent(Color.RED);

        colorblind.activate(game, playerchoice);

        GameModifiers gameModifiers = game.getGameModifiers();
        assertEquals(Color.RED, gameModifiers.getInhibitColor());

        colorblind.deactivate(game);
        assertNull(gameModifiers.getInhibitColor());
    }
}
