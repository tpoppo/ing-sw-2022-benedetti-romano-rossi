package it.polimi.ingsw.controller;

import java.io.Serializable;
import java.util.Objects;

public class LobbyPlayer implements Serializable {

    private final String username;
    private Integer wizard;

    public LobbyPlayer(String username){
        this.username = username;
        wizard = null;
    }

    public void setWizard(int wizard) {
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
        return Objects.equals(username, that.username) && Objects.equals(wizard, that.wizard);
    }


    @Override
    public int hashCode() {
        return Objects.hash(username, wizard);
    }
}
