package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.network.*;
import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.utils.exceptions.FullLobbyException;

import java.io.Serial;

/**
 * This message is used to create and join a new lobby
 */
public class CreateLobbyMessage extends ClientMessage {
    @Serial
    private static final long serialVersionUID = 703942620318630480L;
    final int max_players;

    public CreateLobbyMessage(int max_players) {
        this.max_players = max_players;
        super.message_type = MessageType.MENU;
    }

    /**
     * It creates the lobby and joins it.
     *
     * @param lobby_player The caller (the current lobby player)
     * @return the status response.
     */
    @Override
    public StatusCode handle(ConnectionCEO connectionCEO, MenuManager menuManager, LobbyPlayer lobby_player) {
        if(!menuManager.isSubscribed(connectionCEO)){
            menuManager.addErrorMessage(lobby_player, "You must be in the Menu");
            return StatusCode.WRONG_STATE;
        }

        if(max_players < Constants.MIN_PLAYERS || max_players > Constants.MAX_PLAYERS) {
            menuManager.addErrorMessage(lobby_player, "Lobby size can be only 2 or 3! Given : " + max_players);
            return StatusCode.INVALID_ACTION;
        }


        Server server = Server.getInstance();
        NetworkManager network_manager = server.createLobby(max_players);
        LobbyHandler lobby_handler = network_manager.getLobbyHandler();
        try {
            lobby_handler.addPlayer(lobby_player);
        } catch (FullLobbyException e) { // It means that the lobby is full. It should be impossible.
            menuManager.addErrorMessage(lobby_player, "Lobby is full (this shouldn't have happened) :/");
            return StatusCode.INVALID_ACTION;
        }
        connectionCEO.setNetworkManager(network_manager);
        MenuManager.getInstance().unsubscribe(connectionCEO);
        network_manager.subscribe(connectionCEO);

        return StatusCode.OK;
    }

    @Override
    public String toString() {
        return "CreateLobbyMessage{" +
                "max_players=" + max_players +
                ", message_type=" + message_type +
                '}';
    }
}
