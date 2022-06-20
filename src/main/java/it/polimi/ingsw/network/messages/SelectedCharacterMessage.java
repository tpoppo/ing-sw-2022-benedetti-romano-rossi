package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.network.*;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.characters.Character;

import java.util.Set;

/**
 * This message is used to select a character given its position.
 * It can be sent by the player during their turn in the game.
 */
public class SelectedCharacterMessage extends ClientMessage {
    final int character_position;
    public SelectedCharacterMessage(int character_position) {
        this.character_position = character_position;
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

        // you cannot be in the activated character state
        if(gameHandler.getCurrentState() == GameState.ACTIVATE_CHARACTER){
            network_manager.addErrorMessage(lobby_player, "At most one character can be activated each turn");
            return StatusCode.WRONG_STATE;
        }

        Game game = gameHandler.getModel();

        // Invalid player. Different players (from model and from socket)
        Player current_player = game.getCurrentPlayer();
        Player player = network_manager.getGameHandler().lobbyPlayerToPlayer(lobby_player);
        if (current_player == null || !current_player.equals(player)) {
            network_manager.addErrorMessage(lobby_player, "It is not your turn");
            return StatusCode.WRONG_PLAYER;
        }

        // The game must be in expert mode
        if(!game.getExpertMode()){
            network_manager.addErrorMessage(lobby_player, "The game must be in expert mode");
            return StatusCode.INVALID_ACTION;
        }

        // You must be in the action phase
        if(!Set.of(GameState.MOVE_MOTHER_NATURE, GameState.MOVE_STUDENT, GameState.CHOOSE_CLOUD).contains(gameHandler.getCurrentState())){
            network_manager.addErrorMessage(lobby_player, "You must be in the action phase");
            return StatusCode.INVALID_ACTION;
        }

        // no other characters active
        for(Character character : game.getCharacters()){
            if(character.isActivated()){
                network_manager.addErrorMessage(lobby_player, "At most one character can be activated each turn");
                return StatusCode.INVALID_ACTION;
            }
        }

        // Invalid character_position value
        if(character_position < 0 || character_position >= game.getCharacters().size()){
            network_manager.addErrorMessage(lobby_player, "Invalid character position. It must be in range [0, "+game.getCharacters().size()+"). Given: "+ character_position);
            return StatusCode.INVALID_ACTION;
        }
        Character character = game.getCharacters().get(character_position);

        // check if the player has enough money
        if(character.getCost() > player.getCoins()){
            network_manager.addErrorMessage(lobby_player, "Not enough money. Required: "+ character.getCost() + ". Available: "+ player.getCoins());
            return StatusCode.INVALID_ACTION;
        }

        gameHandler.setSelectedCharacter(character);

        gameHandler.setSavedState(gameHandler.getCurrentState());
        gameHandler.setCurrentState(GameState.ACTIVATE_CHARACTER);

        gameHandler.setSavedActionCompleted(gameHandler.isActionCompleted());
        gameHandler.setActionCompleted(false);

        return StatusCode.OK;
    }

    @Override
    public String toString() {
        return "SelectedCharacterMessage{" +
                "character_position=" + character_position +
                ", message_type=" + message_type +
                '}';
    }
}
