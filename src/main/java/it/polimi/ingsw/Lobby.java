package it.polimi.ingsw;

import it.polimi.ingsw.exceptions.FullLobbyException;

import java.util.ArrayList;

public class Lobby {
    static private int count = 0;
    final private int ID;
    private int max_players;
    final private ArrayList<Player> players = new ArrayList<Player>();

    public Lobby(){
        Lobby.count += 1;
        ID = Lobby.count;
    }

    public void addPlayer(Player player) throws FullLobbyException {
        if(players.size() == max_players) throw new FullLobbyException("FullLobbyException: The lobby is full");
        players.add(player);
    }

    public int getID() {
        return ID;
    }

    public int getMax_players() {
        return max_players;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }
}
