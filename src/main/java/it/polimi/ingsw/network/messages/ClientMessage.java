package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.network.*;
import it.polimi.ingsw.model.Player;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class represents the message sent by the client to execute the various actions
 */
public abstract class ClientMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 657653770765716348L;
    protected MessageType message_type;

    /**
     * This method is called when the client message is received in the lobby or in the game
     * @param network_manager the NetworkManager in the game and in the lobby
     * @param lobby_player the LobbyPlayer that sent the message
     * @return a status code depending on the result (e.g. OK valid, INVALID_ACTION invalid parameters)
     */
    public StatusCode handle(NetworkManager network_manager, LobbyPlayer lobby_player) {
        return StatusCode.NOT_IMPLEMENTED;
    }

    /**
     * This method is called when the client message is received in the menu
     * @param connectionCEO the ConnectionCEO of the sender
     * @param menuManager the used MenuManager
     * @param lobby_player the LobbyPlayer that sent the message
     * @return a status code depending on the result (e.g. OK valid, INVALID_ACTION invalid parameters)
     */
    public StatusCode handle(ConnectionCEO connectionCEO, MenuManager menuManager, LobbyPlayer lobby_player) {
        return StatusCode.NOT_IMPLEMENTED;
    }

    public MessageType getMessageType() {
        return message_type;
    }

    /**
     * Checks whether the game state is required_state and the player is valid (same player from the game and from the socket)
     *
     * @param network_manager current game
     * @param lobby_player    current player (from the socket)
     * @param required_state  state required to take the action
     * @return empty if it is valid otherwise WRONG_PLAYER|WRONG_STATE
     */
    protected StatusCode preambleGameCheck(NetworkManager network_manager, LobbyPlayer lobby_player, GameState required_state, boolean action_completed) {
        GameHandler gameHandler = network_manager.getGameHandler();

        // You must be in game (check current_handler)
        if (gameHandler == null || network_manager.getCurrentHandler() != HandlerType.GAME) {
            network_manager.addErrorMessage(lobby_player, "You are in the lobby, not in the game");
            return StatusCode.WRONG_HANDLER;
        }

        Game game = gameHandler.getModel();

        // Invalid player. Different players (from model and from socket)
        Player current_player = game.getCurrentPlayer();
        Player player = network_manager.getGameHandler().lobbyPlayerToPlayer(lobby_player);
        if (current_player == null || !current_player.equals(player)) {
            network_manager.addErrorMessage(lobby_player, "It is not your turn");
            return StatusCode.WRONG_PLAYER;
        }

        // Invalid state. It must be (current_state=required_state, action_completed=False)
        if ((required_state != null && gameHandler.getCurrentState() != required_state) || gameHandler.isActionCompleted() != action_completed) {
            System.err.println(required_state);

            // String err = "Required: %s %s. Given: %s %s.".formatted(required_state, action_completed, gameHandler.getCurrentState(), gameHandler.isActionCompleted());
            String err = "You cannot do this action at the moment";
            network_manager.addErrorMessage(lobby_player, "Wrong state." + err);
            return StatusCode.WRONG_STATE;
        }

        return StatusCode.EMPTY;
    }

    protected StatusCode preambleLobbyCheck(NetworkManager network_manager, LobbyPlayer lobby_player) {
        LobbyHandler lobby_handler = network_manager.getLobbyHandler();

        // You must be in game (check current_handler)
        if (lobby_handler == null || network_manager.getCurrentHandler() != HandlerType.LOBBY) {
            network_manager.addErrorMessage(lobby_player, "You are in game, not in the lobby");
            return StatusCode.WRONG_HANDLER;
        }

        return StatusCode.EMPTY;
    }

    protected StatusCode getStatusCode(NetworkManager network_manager, LobbyPlayer lobby_player, GameHandler gameHandler, Game game) {
        gameHandler.setActionCompleted(true);

        boolean isACharacterActive = game.getCharacters().stream().anyMatch(Character::isActivated);
        boolean canAffordCharacter = gameHandler.lobbyPlayerToPlayer(lobby_player).getCoins() >= game.minCharacterCost();

        if(!game.getExpertMode() || isACharacterActive || !canAffordCharacter)
            return new NextStateMessage().handle(network_manager, lobby_player);

        return StatusCode.OK;
    }

    @Override
    public String toString() {
        return "ClientMessage{" +
                "message_type=" + message_type +
                '}';
    }
}
