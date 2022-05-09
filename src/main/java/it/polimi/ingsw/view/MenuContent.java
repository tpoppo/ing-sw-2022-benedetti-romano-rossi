package it.polimi.ingsw.view;

import it.polimi.ingsw.network.HandlerType;
import it.polimi.ingsw.network.NetworkManager;
import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.utils.ReducedLobby;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MenuContent extends ViewContent {
    private final ArrayList<ReducedLobby> lobbies;

    public MenuContent(){
        this.lobbies = new ArrayList<>();

        List<NetworkManager> lobbies = Server.getInstance().getLobbies();
        lobbies = lobbies.stream().filter(x -> x.getCurrentHandler().equals(HandlerType.LOBBY)).collect(Collectors.toList());

        for(NetworkManager lobby : lobbies) {
            ReducedLobby reducedLobby = new ReducedLobby(lobby);
            this.lobbies.add(reducedLobby);
        }
    }

    @Override
    public ArrayList<ReducedLobby> getLobbies() {
        return lobbies;
    }
}