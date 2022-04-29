package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.model.Player;

import java.io.Serializable;
import java.util.Optional;

public abstract class ClientMessage implements Serializable {
    protected MessageType message_type;

    public StatusCode handle(NetworkManager network_manager, LobbyPlayer lobby_player) {
        return StatusCode.NOT_IMPLEMENTED;
    }

    public StatusCode handle(LobbyPlayer player) {
        return StatusCode.NOT_IMPLEMENTED;
    }

    public MessageType getMessageType() {
        return message_type;
    }


    /**
     * Check whether the game state is required_state and the player is valid (same player from the game and from the socket)
     *
     * @param network_manager current game
     * @param lobby_player    current player (from the socket)
     * @param required_state  state required to take the action
     * @return empty if it is valid otherwise WRONG_PLAYER|WRONG_STATE
     */
    protected Optional<StatusCode> preamble_game_check(NetworkManager network_manager, LobbyPlayer lobby_player, GameState required_state) {
        GameHandler gameHandler = network_manager.getGameHandler();

        // You must be in game (check current_handler)
        if (gameHandler == null || network_manager.getCurrentHandler() != HandlerType.GAME) {
            network_manager.addErrorMessage(lobby_player, "You are in the lobby, not in the game.");
            return Optional.of(StatusCode.WRONG_HANDLER);
        }

        Game game = gameHandler.getModel();

        // Invalid state. It must be (current_state=required_state, action_completed=False)
        if (gameHandler.getCurrentState() != required_state || gameHandler.isActionCompleted()) {
            network_manager.addErrorMessage(lobby_player, "There's a time and place for everything but not now!");
            return Optional.of(StatusCode.WRONG_STATE);
        }

        // Invalid player. Different players (from model and from socket)
        Player current_player = game.getCurrentPlayer();
        Player player = network_manager.getGameHandler().lobbyPlayerToPlayer(lobby_player);
        if (current_player == null || !current_player.equals(player)) {
            network_manager.addErrorMessage(player, "It is not your turn.");
            return Optional.of(StatusCode.WRONG_PLAYER);
        }

        return Optional.empty();
    }

    protected Optional<StatusCode> preamble_lobby_check(NetworkManager network_manager, LobbyPlayer lobby_player) {
        LobbyHandler lobby_handler = network_manager.getLobbyHandler();

        // You must be in game (check current_handler)
        if (lobby_handler == null || network_manager.getCurrentHandler() != HandlerType.LOBBY) {
            network_manager.addErrorMessage(lobby_player, "You are in game, not in the lobby.");
            return Optional.of(StatusCode.WRONG_HANDLER);
        }

        return Optional.empty();
    }

}
