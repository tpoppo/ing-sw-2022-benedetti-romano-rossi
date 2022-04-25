package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.NetworkManager;
import it.polimi.ingsw.controller.responses.ServerResponse;
import it.polimi.ingsw.controller.responses.StatusCode;
import it.polimi.ingsw.model.Player;

public class StartGameMessage extends ClientMessage{
    private final boolean expert_mode;
    public StartGameMessage(boolean expert_mode) {
        this.expert_mode = expert_mode;
        super.message_type = MessageType.MENU;
    }

    @Override
    public ServerResponse handle(NetworkManager network_manager, Player player) {
        // FIXME: should anyone be able to start their game?
        network_manager.startGame(expert_mode);
        return new ServerResponse(StatusCode.OK, null);
    }
}
