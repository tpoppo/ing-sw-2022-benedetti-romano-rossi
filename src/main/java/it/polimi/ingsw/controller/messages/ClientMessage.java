package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.controller.NetworkManager;
import it.polimi.ingsw.controller.responses.ServerResponse;
import it.polimi.ingsw.controller.responses.StatusCode;
import it.polimi.ingsw.model.Player;

import java.io.Serializable;

public abstract class ClientMessage implements Serializable {
    private MessageType message_type;

    public ServerResponse handle(NetworkManager network_manager, Player player){
        return new ServerResponse(StatusCode.BAD_REQUEST, null);
    }

    public ServerResponse handle(LobbyPlayer player){
        return new ServerResponse(StatusCode.BAD_REQUEST, null);
    }

    public MessageType getMessageType() {
        return message_type;
    }
}
