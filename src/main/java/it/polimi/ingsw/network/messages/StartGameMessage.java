package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.network.NetworkManager;

/**
 * This message is used in the lobby to start the game given the selected mode
 */
public class StartGameMessage extends ClientMessage{
    private final boolean expert_mode;
    public StartGameMessage(boolean expert_mode) {
        this.expert_mode = expert_mode;
        super.message_type = MessageType.GAME;
    }

    @Override
    public StatusCode handle(NetworkManager network_manager, LobbyPlayer lobby_player) {
        StatusCode status_code = preambleLobbyCheck(network_manager, lobby_player);
        if(status_code != StatusCode.EMPTY) return status_code;

        LobbyHandler lobby_handler = network_manager.getLobbyHandler();
        if(lobby_handler.getPlayers().size() <= 1){
            network_manager.addErrorMessage(lobby_player, "Not enough player. There must be "+lobby_handler.getMaxPlayers()+" players. Current: "+ lobby_handler.getPlayers().size());
            return StatusCode.INVALID_ACTION;
        }

        for(LobbyPlayer current_lobby_player : lobby_handler.getPlayers()){
            if(current_lobby_player.getWizard() == null){
                network_manager.addErrorMessage(lobby_player, "Not all player have selected a wizard");
                return StatusCode.INVALID_ACTION;
            }
        }

        network_manager.startGame(expert_mode);
        return StatusCode.OK;
    }

    @Override
    public String toString() {
        return "StartGameMessage{" +
                "expert_mode=" + expert_mode +
                ", message_type=" + message_type +
                '}';
    }
}
