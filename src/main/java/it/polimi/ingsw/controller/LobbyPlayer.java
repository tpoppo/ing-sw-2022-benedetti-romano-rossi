package it.polimi.ingsw.controller;

public class LobbyPlayer {
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
