package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.controller.NetworkManager;
import it.polimi.ingsw.controller.responses.ServerResponse;
import it.polimi.ingsw.controller.responses.StatusCode;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.characters.PlayerChoices;
import it.polimi.ingsw.utils.exceptions.BadPlayerChoiceException;

public class ActivateCharacterMessage extends ClientMessage {

    PlayerChoicesSerializable player_choices;
    public ActivateCharacterMessage(PlayerChoicesSerializable player_choices) {
        this.player_choices = player_choices;
        super.message_type = MessageType.GAME;
    }

    @Override
    public ServerResponse handle(NetworkManager network_manager, Player player) {
        GameHandler gameHandler = network_manager.getGameHandler();
        Game game = gameHandler.getModel();

        // Invalid state. It ust be (current_state=ACTIVATE_CHARACTER, action_completed=False)
        if(gameHandler.getCurrentState() != GameState.ACTIVATE_CHARACTER || gameHandler.isActionCompleted()){
            return new ServerResponse(StatusCode.BAD_REQUEST, null); // TODO: viewContent missing
        }

        // Invalid player. Different players (from model and from socket)
        Player current_player = game.getCurrentPlayer();
        if(current_player == null || player != current_player) {
            return new ServerResponse(StatusCode.BAD_REQUEST, null); // TODO: viewContent missing
        }
        Character character = gameHandler.getSelectedCharacter();

        try {
            game.activateCharacter(character, player_choices.toPlayerChoices(game));
        } catch (BadPlayerChoiceException e) {
            return new ServerResponse(StatusCode.OK, null); // TODO: viewContent missing
        }

        gameHandler.setActionCompleted(true);
        return new ServerResponse(StatusCode.OK, null); // TODO: viewContent missing
    }
}
