package it.polimi.ingsw.view.viewcontent;

import it.polimi.ingsw.network.HandlerType;
import it.polimi.ingsw.network.NetworkManager;
import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.utils.ReducedLobby;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MenuContent extends ViewContent {
    private final ArrayList<ReducedLobby> lobbies;
    private final String errorMessage;

    public MenuContent(){
        this(null);
    }

    public MenuContent(String errorMessage){
        this.lobbies = new ArrayList<>();

        this.errorMessage = errorMessage;

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

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "MenuContent{" +
                "lobbies=" + lobbies +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
