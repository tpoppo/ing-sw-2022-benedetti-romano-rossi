package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.controller.NetworkManager;
import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.controller.responses.ServerResponse;
import it.polimi.ingsw.controller.responses.StatusCode;
import it.polimi.ingsw.model.Player;

public class JoinLobbyMessage extends ClientMessage {
    int id;

    public ServerResponse handle(LobbyPlayer player) {
        Server server = Server.getInstance();
        // FIXME:
        server.joinLobby(id, player);
        return new ServerResponse(StatusCode.OK, null);
    }
}
