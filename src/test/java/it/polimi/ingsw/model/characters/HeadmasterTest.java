package it.polimi.ingsw.model.characters;


import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameModifiers;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;
import it.polimi.ingsw.utils.exceptions.EmptyBagException;
import it.polimi.ingsw.utils.exceptions.FullLobbyException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test of the character Headmaster
 */
public class HeadmasterTest {

    /**
     * Test that when a player activate the character Headmaster this player can take control of any number of Professors
     * even if he has the same number of Students as the player who currently controls them
     * @throws FullLobbyException if someone tries to join a lobby but the lobby has already three players
     * @throws EmptyBagException if someone tries to draw a student from the bag but the bag is empty
     * @throws BadPlayerChoiceException if game is null
     */
    @Test
    public void Headmaster() throws FullLobbyException, EmptyBagException, BadPlayerChoiceException {
        LobbyHandler lobby = new LobbyHandler(0, 2);
        LobbyPlayer player1 = new LobbyPlayer("Player 1");
        LobbyPlayer player2 = new LobbyPlayer("Player 2");
        player1.setWizard(1);
        player2.setWizard(2);

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game game = new Game(true, lobby);

        Headmaster Headmaster = new Headmaster();
        PlayerChoices playerchoice = new PlayerChoices();
        Headmaster.activate(game, playerchoice);
        GameModifiers gameModifiers = game.getGameModifiers();
        assertEquals(1, gameModifiers.getProfessorModifier());

        Headmaster.deactivate(game);
        assertEquals(0, gameModifiers.getProfessorModifier());
    }
}
