package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.NetworkManager;
import it.polimi.ingsw.controller.responses.ServerResponse;
import it.polimi.ingsw.model.Player;

import java.io.Serializable;

public interface ClientMessage extends Serializable {
    public ServerResponse handle(NetworkManager network_manager, Player player);
}
