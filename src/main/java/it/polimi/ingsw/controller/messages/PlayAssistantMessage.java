package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.utils.exceptions.AssistantAlreadyPlayedException;

import java.util.Optional;

public class PlayAssistantMessage extends ClientMessage {
    int card_position;

    public PlayAssistantMessage(int card_position) {
        this.card_position = card_position;
        super.message_type = MessageType.GAME;
    }

    @Override
    public StatusCode handle(NetworkManager network_manager, LobbyPlayer lobby_player) {
        Optional<StatusCode> status_code = preamble_game_check(network_manager, lobby_player, GameState.ACTIVATE_CHARACTER);
        if(status_code.isPresent()) return status_code.get();

        GameHandler gameHandler = network_manager.getGameHandler();
        Game game = gameHandler.getModel();
        Player player = gameHandler.lobbyPlayerToPlayer(lobby_player);


        // Invalid card_position value
        if(card_position < 0 || card_position >= player.getPlayerHand().size()){
            network_manager.addErrorMessage(lobby_player, "Invalid card position. It must be in range [0, "+ player.getPlayerHand().size()+"). Given: "+ card_position);
            return StatusCode.INVALID_ACTION;
        }

        try {
            game.playAssistant(player.getPlayerHand().get(card_position));
        } catch (AssistantAlreadyPlayedException e) {
            network_manager.addErrorMessage(lobby_player, "Assistant already in play.");
            return StatusCode.INVALID_ACTION;
        }

        game.nextTurn();
        gameHandler.setActionCompleted(true);
        return StatusCode.OK;
    }
}
