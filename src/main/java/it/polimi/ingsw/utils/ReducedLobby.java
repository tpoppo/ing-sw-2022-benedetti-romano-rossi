package it.polimi.ingsw.utils;

import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.controller.NetworkManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ReducedLobby implements Serializable {
    private final int ID;
    private final int numPlayer;
    private final int maxPlayers;
    private final ArrayList<String> usernames;

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
