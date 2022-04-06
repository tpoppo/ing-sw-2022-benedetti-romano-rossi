package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.controller.NetworkManager;
import it.polimi.ingsw.controller.responses.ServerResponse;
import it.polimi.ingsw.controller.responses.StatusCode;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.characters.Character;

public class ActivateCharacterMessage implements ClientMessage {

    public ActivateCharacterMessage() {

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

        // The game must be in expert mode
        if(!game.getExpertMode()){
            return new ServerResponse(StatusCode.BAD_REQUEST, null); // TODO: viewContent missing
        }

        // no other characters active
        for(Character character : game.getCharacters()){
            if(character.isActivated()){
                return new ServerResponse(StatusCode.BAD_REQUEST, null); // TODO: viewContent missing
            }
        }

        // TODO: not finished

        gameHandler.setActionCompleted(true);
        return new ServerResponse(StatusCode.OK, null); // TODO: viewContent missing
    }
}
