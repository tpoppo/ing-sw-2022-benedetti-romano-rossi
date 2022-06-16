package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.network.*;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Island;
import it.polimi.ingsw.utils.exceptions.EmptyMovableException;
import it.polimi.ingsw.utils.exceptions.FullDiningRoomException;

/**
 * This message is used to move a student from the entrance to the dining room or a given island and can be used while you are in game
 */
public class MoveStudentMessage extends ClientMessage {

    private final Color color;
    private final Integer island_position;

    public MoveStudentMessage(Color color, Integer island_position) {
        this.color = color;
        this.island_position = island_position;
        super.message_type = MessageType.GAME;
    }

    public MoveStudentMessage(Color color) {
        this(color, null);
    }

    @Override
    public StatusCode handle(NetworkManager network_manager, LobbyPlayer lobby_player) {
        StatusCode status_code = preambleGameCheck(network_manager, lobby_player, GameState.MOVE_STUDENT, false);
        if(status_code != StatusCode.EMPTY) return status_code;

        GameHandler gameHandler = network_manager.getGameHandler();
        Game game = gameHandler.getModel();

        if(color == null){
            network_manager.addErrorMessage(lobby_player, "Missing color");
            return StatusCode.INVALID_ACTION;
        }

        if(island_position != null) {
            // Invalid mother_nature_position value
            if(island_position < 0 || island_position >= game.getIslands().size()){
                network_manager.addErrorMessage(lobby_player, "Invalid mother nature position. It must be in [0, "+game.getIslands().size()+"). Given: "+island_position);
                return StatusCode.INVALID_ACTION;
            }

            Island island = game.getIslands().get(island_position);

            try {
                game.moveStudent(color, island);
            } catch (EmptyMovableException e) { // not enough student of color "color" at the entrance
                network_manager.addErrorMessage(lobby_player, "Not enough student of color \""+color+"\" at the entrance");
                return StatusCode.INVALID_ACTION;
            }

        }else{

            try {
                game.moveStudent(color);
            } catch (FullDiningRoomException e) {
                network_manager.addErrorMessage(lobby_player, "The dining room is full for the color "+color);
                return StatusCode.INVALID_ACTION;
            } catch (EmptyMovableException e) { // not enough student of color "color" at the entrance
                network_manager.addErrorMessage(lobby_player, "Not enough student of color \""+color+"\" at the entrance");
                return StatusCode.INVALID_ACTION;

            }
        }

        gameHandler.setActionCompleted(true);

        return new NextStateMessage().handle(network_manager, lobby_player);
    }

    @Override
    public String toString() {
        return "MoveStudentMessage{" +
                "color=" + color +
                ", island_position=" + island_position +
                ", message_type=" + message_type +
                '}';
    }
}
