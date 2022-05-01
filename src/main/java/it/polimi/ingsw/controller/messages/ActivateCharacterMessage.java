package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;

import java.util.Optional;

public class ActivateCharacterMessage extends ClientMessage {

    PlayerChoicesSerializable player_choices;
    public ActivateCharacterMessage(PlayerChoicesSerializable player_choices) {
        this.player_choices = player_choices;
        super.message_type = MessageType.GAME;
    }

    @Override
    public StatusCode handle(NetworkManager network_manager, LobbyPlayer lobby_player) {
        StatusCode status_code = preamble_game_check(network_manager, lobby_player, GameState.CHOOSE_CLOUD);
        if(status_code != StatusCode.EMPTY) return status_code;

        GameHandler gameHandler = network_manager.getGameHandler();
        Game game = gameHandler.getModel();

        Character character = gameHandler.getSelectedCharacter();

        try {
            game.activateCharacter(character, player_choices.toPlayerChoices(game));
        } catch (BadPlayerChoiceException e) {
            return StatusCode.INVALID_ACTION;
        }

        gameHandler.setActionCompleted(true);
        return StatusCode.OK;
    }

    @Override
    public String toString() {
        return "ActivateCharacterMessage{" +
                "player_choices=" + player_choices +
                ", message_type=" + message_type +
                '}';
    }
}
