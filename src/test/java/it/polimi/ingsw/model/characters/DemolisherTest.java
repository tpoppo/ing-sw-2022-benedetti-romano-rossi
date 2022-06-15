package it.polimi.ingsw.model.characters;


import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameModifiers;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;
import it.polimi.ingsw.utils.exceptions.EmptyBagException;
import it.polimi.ingsw.utils.exceptions.EmptyMovableException;
import it.polimi.ingsw.utils.exceptions.FullLobbyException;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DemolisherTest {

    @Test
    public void Demolisher() throws FullLobbyException, EmptyBagException, BadPlayerChoiceException {
        LobbyHandler lobby = new LobbyHandler(0, 2);
        LobbyPlayer player1 = new LobbyPlayer("Player 1");
        LobbyPlayer player2 = new LobbyPlayer("Player 2");
        player1.setWizard(1);
        player2.setWizard(2);
        Random rng = new Random();

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game game = new Game(true, lobby);

        Demolisher demolisher = new Demolisher();
        PlayerChoices playerchoice = new PlayerChoices();
        playerchoice = null;

        demolisher.activate(game, playerchoice);
        GameModifiers gameModifiers = game.getGameModifiers();
        assertEquals(true, gameModifiers.isInhibitTowers());

        demolisher.deactivate(game);
        assertEquals(false, gameModifiers.isInhibitTowers());
    }
}
