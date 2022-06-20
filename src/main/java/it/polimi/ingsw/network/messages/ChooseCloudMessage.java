package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.network.*;
import it.polimi.ingsw.utils.exceptions.EmptyCloudException;

import java.io.Serial;

/**
 * This message is used to select the cloud
 */
public class ChooseCloudMessage extends ClientMessage {
    @Serial
    private static final long serialVersionUID = -6567465369692086533L;
    final int cloud_position;

    public ChooseCloudMessage(int cloud_position) {
        super.message_type = MessageType.GAME;
        this.cloud_position = cloud_position;
    }

    @Override
    public StatusCode handle(NetworkManager network_manager, LobbyPlayer lobby_player) {

        StatusCode status_code = preambleGameCheck(network_manager, lobby_player, GameState.CHOOSE_CLOUD, false);
        if(status_code != StatusCode.EMPTY) return status_code;

        GameHandler gameHandler = network_manager.getGameHandler();
        Game game = gameHandler.getModel();

        // Invalid cloud_position value
        if(cloud_position < 0 || cloud_position >= game.getClouds().size()){
            network_manager.addErrorMessage(lobby_player, "The cloud position must be in the valid range [0, "+game.getClouds().size()+")");
            return StatusCode.INVALID_ACTION;
        }

        try {
            game.chooseCloud(game.getClouds().get(cloud_position));
        } catch (EmptyCloudException e) {
            network_manager.addErrorMessage(lobby_player, "The chosen cloud is empty");
            return StatusCode.INVALID_ACTION;
        }

        return getStatusCode(network_manager, lobby_player, gameHandler, game);
    }

    @Override
    public String toString() {
        return "ChooseCloudMessage{" +
                "cloud_position=" + cloud_position +
                ", message_type=" + message_type +
                '}';
    }
}
