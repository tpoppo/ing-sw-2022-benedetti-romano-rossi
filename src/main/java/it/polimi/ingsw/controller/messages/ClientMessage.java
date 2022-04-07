package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.NetworkManager;
import it.polimi.ingsw.controller.responses.ServerResponse;
import it.polimi.ingsw.model.Player;

import java.io.Serializable;

public abstract class ClientMessage implements Serializable {
    private MessageType message_type;

    public abstract ServerResponse handle(NetworkManager network_manager, Player player);

    public MessageType getMessageType() {
        return message_type;
    }
}
