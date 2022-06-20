package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.network.*;

import java.util.Optional;

/**
 * This message is used to join a lobby given an ID and can be used while you are in the menu
 */
public class JoinLobbyMessage extends ClientMessage {
    final int id;
    public JoinLobbyMessage(int id){
        this.id = id;
        super.message_type = MessageType.MENU;
    }

    @Override
    public StatusCode handle(ConnectionCEO connectionCEO, MenuManager menuManager, LobbyPlayer lobby_player) {
        if(!menuManager.isSubscribed(connectionCEO)){
            menuManager.addErrorMessage(lobby_player, "You must be in the Menu");
            return StatusCode.WRONG_STATE;
        }
        Server server = Server.getInstance();
        Optional<NetworkManager> network_manager = server.joinLobby(id, lobby_player);

        if(network_manager.isPresent()){
            connectionCEO.setNetworkManager(network_manager.get());
            MenuManager.getInstance().unsubscribe(connectionCEO);
            network_manager.get().subscribe(connectionCEO);

            return StatusCode.OK;
        }

        menuManager.addErrorMessage(lobby_player, "Please choose a valid lobby");
        return StatusCode.INVALID_ACTION;
    }

    @Override
    public String toString() {
        return "JoinLobbyMessage{" +
                "id=" + id +
                ", message_type=" + message_type +
                '}';
    }
}
