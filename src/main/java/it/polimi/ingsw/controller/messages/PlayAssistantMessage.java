package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.controller.NetworkManager;
import it.polimi.ingsw.controller.responses.ServerResponse;
import it.polimi.ingsw.controller.responses.StatusCode;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.utils.exceptions.AssistantAlreadyPlayedException;

public class PlayAssistantMessage implements ClientMessage{
    int card_position;

    public PlayAssistantMessage(int card_position) {
        this.card_position = card_position;
    }

    @Override
    public ServerResponse handle(NetworkManager network_manager, Player player) {
        GameHandler gameHandler = network_manager.getGameHandler();
        Game game = gameHandler.getModel();

        // Invalid state. It ust be (current_state=ACTIVATE_CHARACTER, action_completed=False)
        if(gameHandler.getCurrentState() != GameState.ACTIVATE_CHARACTER && !gameHandler.isActionCompleted()){
            return new ServerResponse(StatusCode.BAD_REQUEST, null); // TODO: viewContent missing
        }

        // Invalid player. Different players (from model and from socket)
        Player current_player = game.getCurrentPlayer();
        if(current_player == null || player != current_player) {
            return new ServerResponse(StatusCode.BAD_REQUEST, null); // TODO: viewContent missing
        }

        // Invalid card_position value
        if(card_position < 0 || card_position >= current_player.getPlayerHand().size()){
            return new ServerResponse(StatusCode.BAD_REQUEST, null); // TODO: viewContent missing
        }

        try {
            game.playAssistant(current_player.getPlayerHand().get(card_position));
        } catch (AssistantAlreadyPlayedException e) {
            return new ServerResponse(StatusCode.BAD_REQUEST, null); // TODO: viewContent missing
        }

        gameHandler.setActionCompleted(true);
        return new ServerResponse(StatusCode.OK, null); // TODO: viewContent missing
    }
}
