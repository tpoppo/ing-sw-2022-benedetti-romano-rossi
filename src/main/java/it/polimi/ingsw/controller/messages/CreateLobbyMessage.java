package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.utils.exceptions.FullLobbyException;


public class CreateLobbyMessage extends ClientMessage {
    int max_players;

    public CreateLobbyMessage(int max_players) {
        this.max_players = max_players;
        super.message_type = MessageType.MENU;
    }

    /**
     * It creates the lobby and joins it.
     *
     * @param player The caller (the current lobby player)
     * @return the status response.
     */
    public StatusCode handle(LobbyPlayer player) {
        // We check whether max_players is 2 or 3?
        if(max_players != 3 && max_players != 2) {
            return StatusCode.INVALID_ACTION;
        }

        Server server = Server.getInstance();
        NetworkManager network_manager = server.createLobby(max_players);
        LobbyHandler lobby_handler = network_manager.getLobbyHandler();
        try {
            lobby_handler.addPlayer(player);
        } catch (FullLobbyException e) { // It means that the lobby is full. It should be impossible.
            return StatusCode.INVALID_ACTION;
        }

        return StatusCode.OK;
    }

    @Override
    public String toString() {
        return "CreateLobbyMessage{" +
                "max_players=" + max_players +
                ", message_type=" + message_type +
                '}';
    }
}
