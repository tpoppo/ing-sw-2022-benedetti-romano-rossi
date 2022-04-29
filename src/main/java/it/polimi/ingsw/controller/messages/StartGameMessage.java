package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.controller.NetworkManager;

import java.util.Optional;

public class StartGameMessage extends ClientMessage{
    private final boolean expert_mode;
    public StartGameMessage(boolean expert_mode) {
        this.expert_mode = expert_mode;
        super.message_type = MessageType.MENU;
    }

    @Override
    public StatusCode handle(NetworkManager network_manager, LobbyPlayer lobby_player) {
        Optional<StatusCode> status_code = preamble_lobby_check(network_manager, lobby_player);
        if(status_code.isPresent()) return status_code.get();

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
}
