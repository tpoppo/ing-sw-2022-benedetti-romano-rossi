package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameModifiers;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;
import it.polimi.ingsw.utils.exceptions.EmptyBagException;
import it.polimi.ingsw.utils.exceptions.FullLobbyException;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test the character Knight
 */
public class KnightTest {

    /**
     * Test that when a player activate this character his influence is increased by two
     * @throws FullLobbyException if someone tries to join a lobby but the lobby has already three players
     * @throws EmptyBagException if someone tries to draw a student from the bag but the bag is empty
     * @throws BadPlayerChoiceException if game is null
     */
    @Test
    public void Influencer() throws FullLobbyException, EmptyBagException, BadPlayerChoiceException {
        LobbyHandler lobby = new LobbyHandler(0, 2);
        LobbyPlayer player1 = new LobbyPlayer("Player 1");
        LobbyPlayer player2 = new LobbyPlayer("Player 2");
        player1.setWizard(1);
        player2.setWizard(2);
        Random rng = new Random();

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game game = new Game(true, lobby);

        Knight knight = new Knight();
        PlayerChoices playerchoice = new PlayerChoices();
        knight.activate(game, playerchoice);

        GameModifiers gameModifiers = game.getGameModifiers();
        assertEquals(2, gameModifiers.getBuffInfluence());

        knight.deactivate(game);
        assertEquals(0, gameModifiers.getBuffInfluence());
    }
}
