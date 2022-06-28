package it.polimi.ingsw.controller;

import it.polimi.ingsw.utils.exceptions.FullLobbyException;
import it.polimi.ingsw.utils.exceptions.WizardNotAvailableException;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class LobbyHandler represents a lobby.
 * It manages the lobby's evolution and functionalities.
 */
public class LobbyHandler implements Serializable {
    @Serial
    private static final long serialVersionUID = 3855855398364694695L;
    public final int ID;
    private final int max_players;
    private final ArrayList<LobbyPlayer> players;
    private final ArrayList<Integer> available_wizards;

    /**
     * Constructor that specifies both the lobby ID and the maximum number of players.
     *
     * @param ID the lobby ID.
     * @param max_players the maximum number of players that can join this lobby
     */
    public LobbyHandler(int ID, int max_players){
        this.ID = ID;
        this.max_players = max_players;
        players = new ArrayList<>();
        available_wizards = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
    }

    /**
     * Constructor that only require the lobby ID. Sets the maximum number of players to 3.
     *
     * @param ID the lobby ID.
     */
    public LobbyHandler(int ID){
        this(ID, 3);
    }

    /**
     * Binds the desired wizard to the given player.
     *
     * @param desired_wizard the wizard to be chosen.
     * @param player the player which the wizard will be associated with.
     * @throws WizardNotAvailableException if the desired wizard is not available.
     */
    public void chooseWizard(int desired_wizard, LobbyPlayer player) throws WizardNotAvailableException {
        if(!available_wizards.contains(desired_wizard)) throw new WizardNotAvailableException();

        player.setWizard(desired_wizard);

        // NOTE: remove(int) remove by index and remove(Object) remove by value
        available_wizards.remove(Integer.valueOf(desired_wizard));
    }

    /**
     * Adds a player to the lobby.
     *
     * @param player the player to be added.
     * @throws FullLobbyException if the lobby already contains its maximum number of players.
     */
    public void addPlayer(LobbyPlayer player) throws FullLobbyException {
        if(players.size() == max_players) throw new FullLobbyException();
        players.add(player);
    }

    public ArrayList<LobbyPlayer> getPlayers() {
        return players;
    }

    public int getMaxPlayers() {
        return max_players;
    }

    public ArrayList<Integer> getAvailableWizards() {
        return new ArrayList<>(available_wizards);
    }
}
