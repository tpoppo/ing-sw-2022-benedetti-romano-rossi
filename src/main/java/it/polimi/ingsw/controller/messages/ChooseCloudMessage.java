package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.controller.responses.StatusCode;
import it.polimi.ingsw.model.Player;

import java.util.Optional;

public class ChooseCloudMessage extends ClientMessage {

    int cloud_position;

    public ChooseCloudMessage(int cloud_position) {
        super.message_type = MessageType.GAME;
        this.cloud_position = cloud_position;
    }

    @Override
    public StatusCode handle(NetworkManager network_manager, LobbyPlayer lobby_player) {

        Optional<StatusCode> status_code = preamble_game_check(network_manager, lobby_player, GameState.CHOOSE_CLOUD);
        if(status_code.isPresent()) return status_code.get();

        GameHandler gameHandler = network_manager.getGameHandler();
        Game game = gameHandler.getModel();

        // Invalid cloud_position value
        if(cloud_position < 0 || cloud_position >= game.getClouds().size()){
            network_manager.addErrorMessage(lobby_player, "The cloud position must be in the valid range [0, "+game.getClouds().size()+")");
            return StatusCode.INVALID_ACTION;
        }

        game.chooseCloud(game.getClouds().get(cloud_position));
        game.nextTurn();

        gameHandler.setActionCompleted(true);
        return StatusCode.INVALID_ACTION;
    }
}
