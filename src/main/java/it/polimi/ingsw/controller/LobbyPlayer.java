package it.polimi.ingsw.controller;

import java.util.Optional;

public class LobbyPlayer {
    private final String username;
    private Optional<Integer> wizard;

    public LobbyPlayer(String username){
        this.username = username;
        wizard = Optional.empty();
    }

    public void setWizard(int wizard) {
        this.wizard = Optional.of(wizard);
    }

    public Optional<Integer> getWizard() {
        return wizard;
    }

    public String getUsername() {
        return username;
    }
}
