package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.*;

import java.util.Optional;

public class JoinLobbyMessage extends ClientMessage {
    int id;
    public JoinLobbyMessage(int id){
        this.id = id;
        super.message_type = MessageType.MENU;
    }

    public StatusCode handle(ConnectionCEO connectionCEO, LobbyPlayer player) {
        Server server = Server.getInstance();
        Optional<NetworkManager> network_manager = server.joinLobby(id, player);
        if(network_manager.isPresent()){
            connectionCEO.setNetworkManager(network_manager.get());
            MenuManager.getInstance().unsubscribe(connectionCEO);
            network_manager.get().subscribe(connectionCEO);

            return StatusCode.OK;
        }

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
