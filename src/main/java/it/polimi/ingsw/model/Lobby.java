package it.polimi.ingsw.model;

import it.polimi.ingsw.model.exceptions.FullLobbyException;
import it.polimi.ingsw.model.exceptions.WizardNotAvailableException;

import java.util.ArrayList;
import java.util.Arrays;

public class Lobby {
    static private int count = 0;
    final private int ID;
    private int max_players;
    final private ArrayList<Player> players = new ArrayList<Player>();
    private final ArrayList<Integer> available_wizards;   // FIXME: I don't really think this belongs to the Lobby (?)

    public Lobby(){
        Lobby.count += 1;
        ID = Lobby.count;

        this.max_players = 3;

        available_wizards = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));  // FIXME: This scares me
    }

    public Lobby(int max_players){
        Lobby.count += 1;
        ID = Lobby.count;

        this.max_players = max_players;

        available_wizards = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));  // FIXME: This scares me
    }

    public void addPlayer(Player player) throws FullLobbyException {
        if(players.size() == max_players) throw new FullLobbyException();
        players.add(player);
    }

    // FIXME: Maybe it's better to have this in the Game class
    public void chooseWizard(int desired_wizard, Player player) throws WizardNotAvailableException {
        if(!available_wizards.contains(desired_wizard)) throw new WizardNotAvailableException();

        player.setWizard(desired_wizard);
        available_wizards.remove(desired_wizard);
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
