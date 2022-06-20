package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.model.board.Island;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;
import it.polimi.ingsw.utils.exceptions.EmptyBagException;
import it.polimi.ingsw.utils.exceptions.FullLobbyException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test of the character Witch
 */
public class WitchTest {

    /**
     * Test that when a player activate this card a No entry tile is placed on the chosen island
     * @throws FullLobbyException if someone tries to join a lobby but the lobby has already three players
     * @throws EmptyBagException if someone tries to draw a student from the bag but the bag is empty
     * @throws BadPlayerChoiceException if tiles are equals to zero
     */
    @Test
    public void NatureBlocker() throws FullLobbyException, EmptyBagException, BadPlayerChoiceException {
        LobbyHandler lobby = new LobbyHandler(0, 2);
        LobbyPlayer player1 = new LobbyPlayer("Player 1");
        LobbyPlayer player2 = new LobbyPlayer("Player 2");
        player1.setWizard(1);
        player2.setWizard(2);
        Random rng = new Random();

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game game = new Game(true, lobby);

        Witch natureblocker = new Witch();
        PlayerChoices playerChoices = new PlayerChoices();
        natureblocker.require();

        ArrayList<Island> islands = game.getIslands();
        Island island = islands.get(0);
        island.setNoEntryTiles(2);
        playerChoices.setIsland(island);

        natureblocker.activate(game, playerChoices);

        assertEquals(3, islands.get(0).getNoEntryTiles());
        assertEquals(3, natureblocker.getNoEntryTiles());

        natureblocker.deactivate(game);
    }
}
