package it.polimi.ingsw.utils;

import it.polimi.ingsw.network.NetworkManager;

import java.io.Serial;
import java.io.Serializable;

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

    public ReducedLobby(int id, int numPlayer, int maxPlayers) {
        ID = id;
        this.numPlayer = numPlayer;
        this.maxPlayers = maxPlayers;
    }

    public ReducedLobby(NetworkManager networkManager){
        this(
                networkManager.ID,
                networkManager.getLobbyHandler().getPlayers().size(),
                networkManager.getLobbyHandler().getMaxPlayers()
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
