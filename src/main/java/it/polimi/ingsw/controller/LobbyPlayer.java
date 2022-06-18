package it.polimi.ingsw.controller;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Class LobbyPlayer represents a Player with his basic infos, like the username.
 */
public class LobbyPlayer implements Serializable {
    @Serial
    private static final long serialVersionUID = -8126205440396712999L;
    private final String username;
    private Integer wizard;

    /**
     * Constructor, creates a LobbyPlayer with the given username.
     *
     * @param username the username of the LobbyPlayer.
     */
    public LobbyPlayer(String username){
        this.username = username;
        wizard = null;
    }

    public void setWizard(Integer wizard) {
        this.wizard = wizard;
    }

    public Integer getWizard() {
        return wizard;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LobbyPlayer that = (LobbyPlayer) o;
        return username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "LobbyPlayer{" +
                "username='" + username + '\'' +
                ", wizard=" + wizard +
                '}';
    }
}
