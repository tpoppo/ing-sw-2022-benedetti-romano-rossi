package it.polimi.ingsw.model.characters;


import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameModifiers;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.utils.exceptions.EmptyBagException;
import it.polimi.ingsw.utils.exceptions.EmptyMovableException;
import it.polimi.ingsw.utils.exceptions.FullLobbyException;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NatureMoverTest {

    @Test
    public void NatureMover() throws FullLobbyException, EmptyMovableException, EmptyBagException {
        LobbyHandler lobby = new LobbyHandler(0, 2);
        LobbyPlayer player1 = new LobbyPlayer("Player 1");
        LobbyPlayer player2 = new LobbyPlayer("Player 2");
        player1.setWizard(1);
        player2.setWizard(2);
        Random rng = new Random();

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game game = new Game(true, lobby);

        NatureMover NatureMover = new NatureMover();
        PlayerChoices playerchoice = new PlayerChoices();
        playerchoice = null;
        NatureMover.onActivation(game, playerchoice);

        GameModifiers gameModifiers = game.getGameModifiers();
        assertEquals(2, gameModifiers.getExtraSteps());

        NatureMover.onDeactivation(game);
        assertEquals(0, gameModifiers.getExtraSteps());
    }
}
