package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.network.*;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.utils.exceptions.AssistantAlreadyPlayedException;
/**
 * This message is used to select an assistant.
 * It can be used while you are in game.
 */
public class PlayAssistantMessage extends ClientMessage {
    final int card_position;

    public PlayAssistantMessage(int card_position) {
        this.card_position = card_position;
        super.message_type = MessageType.GAME;
    }

    @Override
    public StatusCode handle(NetworkManager network_manager, LobbyPlayer lobby_player) {
        StatusCode status_code = preambleGameCheck(network_manager, lobby_player, GameState.PLAY_ASSISTANT, false);
        if(status_code != StatusCode.EMPTY) return status_code;

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
            network_manager.addErrorMessage(lobby_player, "Assistant already in play");
            return StatusCode.INVALID_ACTION;
        }

        gameHandler.setActionCompleted(true);

        return new NextStateMessage().handle(network_manager, lobby_player);
    }

    @Override
    public String toString() {
        return "PlayAssistantMessage{" +
                "card_position=" + card_position +
                ", message_type=" + message_type +
                '}';
    }
}
