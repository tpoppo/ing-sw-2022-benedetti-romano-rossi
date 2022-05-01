package it.polimi.ingsw.controller;

import it.polimi.ingsw.view.ViewContent;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class ViewContentCreator extends Thread{
    private final ObjectOutputStream outputStream;
    private NetworkManager networkManager;
    private final LobbyPlayer player;

    public ViewContentCreator(ObjectOutputStream outputStream, NetworkManager networkManager, LobbyPlayer player) {
        this.outputStream = outputStream;
        this.networkManager = networkManager;
        this.player = player;
    }

    @Override
    public void run() {
        while(true){
            ViewContent viewContent = null;
            if(networkManager == null) {
                viewContent = new ViewContent();
            } else {
                viewContent = networkManager.createViewContent(player);
            }

            try {
                outputStream.reset();
                outputStream.writeObject(viewContent);
                outputStream.flush();

                Thread.sleep(250); // FIXME: is this ok?
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setNetworkManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }
}
