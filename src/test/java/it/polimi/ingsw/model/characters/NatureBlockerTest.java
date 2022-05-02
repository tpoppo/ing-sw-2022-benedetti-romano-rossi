package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.model.board.Island;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;
import it.polimi.ingsw.utils.exceptions.EmptyBagException;
import it.polimi.ingsw.utils.exceptions.EmptyMovableException;
import it.polimi.ingsw.utils.exceptions.FullLobbyException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NatureBlockerTest {

    @Test
    public void NatureBlocker() throws FullLobbyException, EmptyMovableException, EmptyBagException, BadPlayerChoiceException {
        LobbyHandler lobby = new LobbyHandler(2);
        LobbyPlayer player1 = new LobbyPlayer("Player 1");
        LobbyPlayer player2 = new LobbyPlayer("Player 2");
        player1.setWizard(1);
        player2.setWizard(2);
        Random rng = new Random();

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game game = new Game(true, lobby);

        NatureBlocker natureblocker = new NatureBlocker();
        PlayerChoices playerChoices = new PlayerChoices();

        ArrayList<Island> islands = game.getIslands();
        Island island = islands.get(0);
        island.setNoEntryTiles(2);
        playerChoices.setIsland(island);

        natureblocker.onActivation(game, playerChoices);

        assertEquals(3, islands.get(0).getNoEntryTiles());

        natureblocker.onDeactivation(game, playerChoices);
    }
}
