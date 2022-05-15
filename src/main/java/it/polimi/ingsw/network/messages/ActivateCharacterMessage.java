package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.network.*;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;

public class ActivateCharacterMessage extends ClientMessage {
    PlayerChoicesSerializable player_choices;

    public ActivateCharacterMessage(PlayerChoicesSerializable player_choices) {
        this.player_choices = player_choices;
        super.message_type = MessageType.GAME;
    }

    @Override
    public StatusCode handle(NetworkManager network_manager, LobbyPlayer lobby_player) {
        StatusCode status_code = preambleGameCheck(network_manager, lobby_player, GameState.ACTIVATE_CHARACTER, false);
        if(status_code != StatusCode.EMPTY) return status_code;

        GameHandler gameHandler = network_manager.getGameHandler();
        Game game = gameHandler.getModel();

        Character character = gameHandler.getSelectedCharacter();

        try {
            game.activateCharacter(character, player_choices.toPlayerChoices(game));
        } catch (BadPlayerChoiceException e) {
            // TODO: add error message for client
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
