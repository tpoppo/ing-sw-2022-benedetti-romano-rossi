package it.polimi.ingsw.utils;

import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.network.NetworkManager;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * This class represent the information sent to the client (via the {@link it.polimi.ingsw.view.viewcontent.MenuContent}).
 * It contains the most important information of the lobby.
 */
public class ReducedLobby implements Serializable {
    @Serial
    private static final long serialVersionUID = 5047237163325726667L;
    private final int ID;
    private final int numPlayer;
    private final int maxPlayers;
    private final ArrayList<String> usernames; // FIXME: this parameters is never used. Should we remove it?

    public ReducedLobby(int id, int numPlayer, int maxPlayers, ArrayList<String> usernames) {
        ID = id;
        this.numPlayer = numPlayer;
        this.maxPlayers = maxPlayers;
        this.usernames = new ArrayList<>(usernames);
    }

    public ReducedLobby(NetworkManager networkManager){
        this(
                networkManager.ID,
                networkManager.getLobbyHandler().getPlayers().size(),
                networkManager.getLobbyHandler().getMaxPlayers(),
                networkManager.getLobbyHandler().getPlayers().stream().map(LobbyPlayer::getUsername).collect(Collectors.toCollection(ArrayList::new))
        );
    }

    public int getID() {
        return ID;
    }

    public int getNumPlayer() {
        return numPlayer;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }
}
