package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.controller.NetworkManager;
import it.polimi.ingsw.controller.Server;

import java.util.Optional;

public class JoinLobbyMessage extends ClientMessage {
    int id;
    public JoinLobbyMessage(int id){
        this.id = id;
        super.message_type = MessageType.MENU;
    }

    public StatusCode handle(LobbyPlayer player) {
        Server server = Server.getInstance();
        Optional<NetworkManager> network_manager = server.joinLobby(id, player);
        return network_manager.isPresent() ? StatusCode.OK : StatusCode.INVALID_ACTION;
    }
}
