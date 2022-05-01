package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.messages.MoveMotherNatureMessage;
import it.polimi.ingsw.model.board.Island;
import it.polimi.ingsw.utils.exceptions.EmptyBagException;
import it.polimi.ingsw.utils.exceptions.EmptyMovableException;
import it.polimi.ingsw.utils.exceptions.FullLobbyException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoveMotherNatureMessageTest {

    @Test
    public void MoveMotherNatureMessage() throws FullLobbyException, EmptyMovableException, EmptyBagException {
        LobbyHandler lobby = new LobbyHandler(2);
        LobbyPlayer player1 = new LobbyPlayer("Player 1");
        LobbyPlayer player2 = new LobbyPlayer("Player 2");
        player1.setWizard(1);
        player2.setWizard(2);
        Random rng = new Random();

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        Game game = new Game(true, lobby);

        ArrayList<Island> islands = game.getIslands();
        int pos = 0;

        for(Island island : islands){
            if(pos == 0){
                island.setMotherNature(true);
            }else{
                island.setMotherNature(false);
            }
            pos++;
        }

        LobbyHandler lobbyhandler = new LobbyHandler(2);
        GameHandler gamehandler = new GameHandler(true, lobbyhandler);
        gamehandler.setModel(game);
        MoveMotherNatureMessage movemothernaturemessage = new MoveMotherNatureMessage(1);
        NetworkManager networkmanager = NetworkManager.createNetworkManager(2);

        movemothernaturemessage.handle(networkmanager, game.getPlayers().get(0));

        assertEquals(true, islands.get(1));

    }
}
