package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.utils.exceptions.FullLobbyException;
import it.polimi.ingsw.utils.exceptions.WizardNotAvailableException;

import java.util.ArrayList;
import java.util.Arrays;

public class LobbyHandler {
    private int max_players;
    private ArrayList<Player> players;
    private ArrayList<Integer> available_wizards;

    public LobbyHandler(int max_players){
        this.max_players = max_players;
        players = new ArrayList<>();
        available_wizards = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
    }

    public LobbyHandler(){
        new LobbyHandler(3);
    }

    public void chooseWizard(int desired_wizard, Player player) throws WizardNotAvailableException {
        if(!available_wizards.contains(desired_wizard)) throw new WizardNotAvailableException();

        player.setWizard(desired_wizard);
        available_wizards.remove(desired_wizard);
    }

    public void addPlayer(Player player) throws FullLobbyException {
        if(players.size() == max_players) throw new FullLobbyException();
        players.add(player);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }
}
