package it.polimi.ingsw.controller;

import java.io.Serializable;

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

    public int getWizard() {
        return wizard;
    }

    public String getUsername() {
        return username;
    }
}
