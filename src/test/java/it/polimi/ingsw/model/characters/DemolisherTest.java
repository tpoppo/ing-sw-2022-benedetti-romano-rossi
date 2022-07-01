package it.polimi.ingsw.model.characters;


import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameModifiers;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;
import it.polimi.ingsw.utils.exceptions.EmptyBagException;
import it.polimi.ingsw.utils.exceptions.FullLobbyException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of the character Demolisher
 */
public class DemolisherTest {

    /**
     * Test that when the character Demolisher is activated towers do not count towards influence
     * @throws FullLobbyException if someone tries to join a lobby but the lobby has already three players
     * @throws EmptyBagException if someone tries to draw a student from the bag but the bag is empty
     * @throws BadPlayerChoiceException if the chosen island is null
     */
    @Test
    public void Demolisher() throws FullLobbyException, EmptyBagException, BadPlayerChoiceException {
        LobbyHandler lobby = new LobbyHandler(0, 2);
        LobbyPlayer player1 = new LobbyPlayer("Player 1");
        LobbyPlayer player2 = new LobbyPlayer("Player 2");
        player1.setWizard(1);
        player2.setWizard(2);

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game game = new Game(true, lobby);

        Demolisher demolisher = new Demolisher();
        PlayerChoices playerchoice = new PlayerChoices();

        demolisher.activate(game, playerchoice);
        GameModifiers gameModifiers = game.getGameModifiers();
        assertTrue(gameModifiers.isInhibitTowers());

        demolisher.deactivate(game);
        assertFalse(gameModifiers.isInhibitTowers());
    }
}
