package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.controller.responses.ServerResponse;
import it.polimi.ingsw.controller.responses.StatusCode;

public class JoinLobbyMessage extends ClientMessage {
    int id;
    public JoinLobbyMessage(int id){
        this.id = id;
        super.message_type = MessageType.MENU;
    }

    public ServerResponse handle(LobbyPlayer player) {
        Server server = Server.getInstance();
        server.joinLobby(id, player);

        return new ServerResponse(StatusCode.OK, null);
    }
}
