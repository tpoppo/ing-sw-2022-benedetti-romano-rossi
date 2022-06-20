package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.GameHandler;
import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.network.*;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Assistant;
/**
 * This message is used to move mother nature and can be used while the sender is in game
 */
public class MoveMotherNatureMessage extends ClientMessage {

    final int mother_nature_position;

    public MoveMotherNatureMessage(int mother_nature_position) {
        this.mother_nature_position = mother_nature_position;
        super.message_type = MessageType.GAME;
    }

    @Override
    public StatusCode handle(NetworkManager network_manager, LobbyPlayer lobby_player) {
        StatusCode status_code = preambleGameCheck(network_manager, lobby_player, GameState.MOVE_MOTHER_NATURE, false);
        if(status_code != StatusCode.EMPTY) return status_code;

        GameHandler gameHandler = network_manager.getGameHandler();
        Game game = gameHandler.getModel();
        Player player = gameHandler.lobbyPlayerToPlayer(lobby_player);

        // Invalid current assistant. Checks whether the current assistant is present
        Assistant assistant = player.getCurrentAssistant();
        if(assistant == null){
            network_manager.addErrorMessage(lobby_player, "The assistant has not been selected");
            return StatusCode.INVALID_ACTION;
        }

        // Invalid mother_nature_position value
        if(mother_nature_position < 0 || mother_nature_position >= game.getIslands().size()){
            network_manager.addErrorMessage(lobby_player, "The mother nature position must be in range [0, "+ game.getIslands().size()+")");
            return StatusCode.INVALID_ACTION;
        }

        // check the distance between mother_nature_position and the current mother nature position
        int distance = (mother_nature_position-game.findMotherNaturePosition()) % game.getIslands().size();
        // ensure that distance remain positive.
        // (x+MOD) % MOD = x if x > 0
        distance += game.getIslands().size();
        distance %= game.getIslands().size();
        int max_distance = assistant.getSteps()+game.getGameModifiers().getExtraSteps();

        //NOTE: distance == 0 means that mother nature would go through all the islands (game.getIslands().size() steps).
        if(distance > max_distance || (distance == 0 && max_distance < game.getIslands().size())){
            network_manager.addErrorMessage(lobby_player, "The island chosen is too far. Distance: "+distance+ " Max Distance:"+max_distance);
            return StatusCode.INVALID_ACTION;
        }

        game.moveMotherNature(game.getIslands().get(mother_nature_position));
        game.conquerIsland();

        return getStatusCode(network_manager, lobby_player, gameHandler, game);
    }

    @Override
    public String toString() {
        return "MoveMotherNatureMessage{" +
                "mother_nature_position=" + mother_nature_position +
                ", message_type=" + message_type +
                '}';
    }
}
