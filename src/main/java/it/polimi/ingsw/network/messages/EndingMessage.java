package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.network.*;

public class EndingMessage extends ClientMessage {
    public EndingMessage() {
        super.message_type = MessageType.GAME;
    }

    @Override
    public StatusCode handle(NetworkManager network_manager, LobbyPlayer lobby_player) {
        GameHandler gameHandler = network_manager.getGameHandler();

        // You must be in game (check current_handler)
        if (gameHandler == null || network_manager.getCurrentHandler() != HandlerType.GAME) {
            network_manager.addErrorMessage(lobby_player, "You are in the lobby, not in the game");
            return StatusCode.WRONG_HANDLER;
        }

        if(gameHandler.getCurrentState() != GameState.ENDING){
            network_manager.addErrorMessage(lobby_player, "You game is not finished");
            return StatusCode.WRONG_STATE;
        }

        MenuManager menuManager = MenuManager.getInstance();

        for (ConnectionCEO subscriber : network_manager.getSubscribers()) {
            menuManager.subscribe(subscriber);
            subscriber.clean();
        }
        network_manager.getSubscribers().clear();
        network_manager.destroy();
        Server.getInstance().deleteNetworkManager(network_manager);

        return StatusCode.OK;
    }
}
