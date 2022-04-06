package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.controller.NetworkManager;
import it.polimi.ingsw.controller.responses.ServerResponse;
import it.polimi.ingsw.controller.responses.StatusCode;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Island;
import it.polimi.ingsw.utils.exceptions.EmptyMovableException;
import it.polimi.ingsw.utils.exceptions.FullDiningRoomException;

import java.util.Optional;

public class MoveStudentMessage implements ClientMessage {

    private final Color color;
    private final Integer island_position;

    public MoveStudentMessage(Color color, Integer island_position) {
        this.color = color;
        this.island_position = island_position;
    }

    @Override
    public ServerResponse handle(NetworkManager network_manager, Player player) {
        GameHandler gameHandler = network_manager.getGameHandler();
        Game game = gameHandler.getModel();

        // Invalid state. It must be (current_state=MOVE_STUDENT, action_completed=False)
        if(gameHandler.getCurrentState() != GameState.MOVE_STUDENT || gameHandler.isActionCompleted()){
            return new ServerResponse(StatusCode.BAD_REQUEST, null); // TODO: viewContent missing
        }

        // Invalid player. Different players (from model and from socket)
        Player current_player = game.getCurrentPlayer();
        if(current_player == null || player != current_player) {
            return new ServerResponse(StatusCode.BAD_REQUEST, null); // TODO: viewContent missing
        }

        if(island_position != null) {

            // Invalid mother_nature_position value
            if(island_position < 0 || island_position >= game.getIslands().size()){
                return new ServerResponse(StatusCode.BAD_REQUEST, null); // TODO: viewContent missing
            }

            Island island = game.getIslands().get(island_position);

            try {
                game.moveStudent(color, island);
            } catch (EmptyMovableException e) { // not enough student of color "color" at the entrance
                return new ServerResponse(StatusCode.BAD_REQUEST, null); // TODO: viewContent missing
            }

        }else{
            try {
                game.moveStudent(color);
            } catch (FullDiningRoomException | EmptyMovableException e) { // not enough student of color "color" at the entrance
                return new ServerResponse(StatusCode.BAD_REQUEST, null); // TODO: viewContent missing
            }
        }

        gameHandler.setActionCompleted(true);
        return new ServerResponse(StatusCode.OK, null); // TODO: viewContent missing
    }
}
