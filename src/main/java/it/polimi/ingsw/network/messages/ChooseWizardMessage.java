package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.network.*;
import it.polimi.ingsw.utils.exceptions.WizardNotAvailableException;

import java.io.Serial;

/**
 * This message is used to select the wizard in the lobby.
 */
public class ChooseWizardMessage extends ClientMessage{
    @Serial
    private static final long serialVersionUID = 4553028616933916085L;
    private final int wizard;

    public ChooseWizardMessage(int wizard){
        this.wizard = wizard;
        super.message_type = MessageType.GAME;
    }

    public StatusCode handle(NetworkManager network_manager, LobbyPlayer lobby_player) {
        StatusCode status_code = preambleLobbyCheck(network_manager, lobby_player);
        if(status_code != StatusCode.EMPTY) return status_code;

        if(lobby_player.getWizard() != null) {
            network_manager.addErrorMessage(lobby_player, "You have already chosen a wizard");
            return StatusCode.INVALID_ACTION;
        }

        if(wizard < 1 || wizard > 4){
            network_manager.addErrorMessage(lobby_player, "The value must be between 1 and 4. Given: "+lobby_player.getWizard());
            return StatusCode.INVALID_ACTION;
        }

        try {
            network_manager.getLobbyHandler().chooseWizard(wizard, lobby_player);
        } catch (WizardNotAvailableException e) {
            network_manager.addErrorMessage(lobby_player, "Wizard already chosen");
            return StatusCode.INVALID_ACTION;
        }

        return StatusCode.OK;
    }

    @Override
    public String toString() {
        return "ChooseWizardMessage{" +
                "wizard=" + wizard +
                ", message_type=" + message_type +
                '}';
    }
}