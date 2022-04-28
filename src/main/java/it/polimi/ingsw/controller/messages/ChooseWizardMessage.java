package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.controller.responses.StatusCode;
import it.polimi.ingsw.utils.exceptions.WizardNotAvailableException;

import java.util.Optional;

public class ChooseWizardMessage extends ClientMessage{
    private int wizard;

    public ChooseWizardMessage(int wizard){
        this.wizard = wizard;
        super.message_type = MessageType.GAME;
    }

    public StatusCode handle(NetworkManager network_manager, LobbyPlayer lobby_player) {
        Optional<StatusCode> status_code = preamble_lobby_check(network_manager, lobby_player);
        if(status_code.isPresent()) return status_code.get();

        try {
            network_manager.getLobbyHandler().chooseWizard(wizard, lobby_player);
        } catch (WizardNotAvailableException e) {
            network_manager.addErrorMessage(lobby_player, "Wizard already chosen.");
            return StatusCode.INVALID_ACTION;
        }

        return StatusCode.OK;
    }

}