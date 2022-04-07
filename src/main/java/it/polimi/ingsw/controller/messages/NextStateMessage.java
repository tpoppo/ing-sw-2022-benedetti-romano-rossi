package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.controller.NetworkManager;
import it.polimi.ingsw.controller.responses.ServerResponse;
import it.polimi.ingsw.controller.responses.StatusCode;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Island;
import it.polimi.ingsw.utils.exceptions.EmptyMovableException;
import it.polimi.ingsw.utils.exceptions.FullDiningRoomException;

public class NextStateMessage extends ClientMessage {


    @Override
    public ServerResponse handle(NetworkManager network_manager, Player player) {
        GameHandler gameHandler = network_manager.getGameHandler();
        Game game = gameHandler.getModel();

        // Invalid state. It must be (action_completed=True)
        if(!gameHandler.isActionCompleted()){
            return new ServerResponse(StatusCode.BAD_REQUEST, null); // TODO: viewContent missing
        }

        // Invalid player. Different players (from model and from socket)
        Player current_player = game.getCurrentPlayer();
        if(current_player == null || player != current_player) {
            return new ServerResponse(StatusCode.BAD_REQUEST, null); // TODO: viewContent missing
        }

        gameHandler.setActionCompleted(false);
        switch(gameHandler.getCurrentState()){
            case ACTIVATE_CHARACTER:
                gameHandler.setCurrentState(gameHandler.getSavedState());
                gameHandler.setSavedState(null);
                break;
            case CHOOSE_CLOUD:

        }




        gameHandler.setActionCompleted(true);
        return new ServerResponse(StatusCode.OK, null); // TODO: viewContent missing
    }

}
