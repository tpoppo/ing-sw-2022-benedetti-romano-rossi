package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.characters.Character;

import java.util.Optional;

public class SelectedCharacterMessage extends ClientMessage {
    int character_position;
    public SelectedCharacterMessage(int character_position) {
        this.character_position = character_position;
        super.message_type = MessageType.GAME;
    }

    @Override
    public StatusCode handle(NetworkManager network_manager, LobbyPlayer lobby_player) {
        StatusCode status_code = preamble_game_check(network_manager, lobby_player, GameState.CHOOSE_CLOUD);
        if(status_code != StatusCode.EMPTY) return status_code;

        GameHandler gameHandler = network_manager.getGameHandler();
        Game game = gameHandler.getModel();
        Player player = gameHandler.lobbyPlayerToPlayer(lobby_player);


        // The game must be in expert mode
        if(!game.getExpertMode()){
            network_manager.addErrorMessage(lobby_player, "The game must be in expert mode");
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

        gameHandler.setActionCompleted(true);
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
