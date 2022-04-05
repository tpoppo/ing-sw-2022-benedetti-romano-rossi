package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.controller.NetworkManager;
import it.polimi.ingsw.controller.responses.ServerResponse;
import it.polimi.ingsw.controller.responses.StatusCode;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Assistant;

import java.util.Optional;

public class MoveMotherNatureMessage implements ClientMessage{

    int mother_nature_position;

    public MoveMotherNatureMessage(int mother_nature_position) {
        this.mother_nature_position = mother_nature_position;
    }

    @Override
    public ServerResponse handle(NetworkManager network_manager, Player player) {
        GameHandler gameHandler = network_manager.getGameHandler();
        Game game = gameHandler.getModel();

        // Invalid state. It ust be (current_state=MOVE_MOTHER_NATURE, action_completed=False)
        if(gameHandler.getCurrentState() != GameState.MOVE_MOTHER_NATURE && !gameHandler.isActionCompleted()){
            return new ServerResponse(StatusCode.BAD_REQUEST, null); // TODO: viewContent missing
        }

        // Invalid player. Different players (from model and from socket)
        Player current_player = game.getCurrentPlayer();
        if(current_player == null || player != current_player) {
            return new ServerResponse(StatusCode.BAD_REQUEST, null); // TODO: viewContent missing
        }

        // Invalid current assistant. Check whether the current assistant is present
        Optional<Assistant> optional_assistant = current_player.getCurrentAssistant();
        if(!optional_assistant.isPresent()){
            return new ServerResponse(StatusCode.BAD_REQUEST, null); // TODO: viewContent missing
        }
        Assistant assistant = optional_assistant.get();

        // Invalid mother_nature_position value
        if(mother_nature_position < 0 || mother_nature_position >= game.getIslands().size()){
            return new ServerResponse(StatusCode.BAD_REQUEST, null); // TODO: viewContent missing
        }

        // check the distance between mother_nature_position and the current mother nature position
        int distance = (mother_nature_position-game.findMotherNaturePosition()) % game.getIslands().size();
        distance = (distance + game.getIslands().size()) % game.getIslands().size();
        int max_distance = assistant.getSteps()+game.getGameModifiers().getExtraSteps();

        //NOTE: distance == 0 means that mother nature would go through all the islands (game.getIslands().size() steps).
        if(distance > max_distance || (distance == 0 && max_distance < game.getIslands().size())){
            return new ServerResponse(StatusCode.BAD_REQUEST, null); // TODO: viewContent missing
        }

        game.moveMotherNature(game.getIslands().get(mother_nature_position));
        game.conquerIsland();

        gameHandler.setActionCompleted(true);
        return new ServerResponse(StatusCode.OK, null); // TODO: viewContent missing
    }
}
