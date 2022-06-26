package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.network.*;

import java.io.Serial;

/**
 * This message is used to close the game after it has ended
 */
public class EndingMessage extends ClientMessage {
    @Serial
    private static final long serialVersionUID = -5172580025871070512L;

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
            network_manager.addErrorMessage(lobby_player, "Your game is not finished");
            return StatusCode.WRONG_STATE;
        }
        network_manager.safeDestroy();

        return StatusCode.OK;
    }
}
