package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.network.messages.ClientMessage;
import it.polimi.ingsw.network.messages.MessageEnvelope;
import it.polimi.ingsw.view.ViewContent;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionCEO extends Thread {
    private final Logger LOGGER = Logger.getLogger(getClass().getName());
    private final Socket clientSocket;
    private NetworkManager networkManager;
    private final Server server;
    private LobbyPlayer player;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    public ConnectionCEO(Socket clientSocket){
        this.server = Server.getInstance();
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try{
            while(!login()) {
                LOGGER.log(Level.INFO, "Connection failed");
            }
        }catch (IOException e){

            try {
                clientSocket.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            LOGGER.log(Level.INFO, "User disconnected during login");
            return;
        }


        LOGGER.log(Level.INFO, "Connection established with {0}", player.getUsername());

        if((networkManager = server.findPlayerLocation(player)) != null) {
            networkManager.getGameHandler().getModel().getPlayers().stream()
                            .filter(player -> player.getUsername().equals(this.player.getUsername()))
                            .findFirst()
                            .ifPresent(player -> {
                                this.player = player; // FIXME: is this ok?

                                networkManager.subscribe(this);
                                LOGGER.log(Level.INFO, "Player found and subscribed to networkManager {}", networkManager);
                            });
        }else {
            MenuManager.getInstance().subscribe(this);
            LOGGER.log(Level.INFO, "New player");
        }

        handleMessages();
    }

    private boolean login() throws IOException {
        // The first message must contain the username chosen by the client
        boolean logged_in;
        String username;
        try {
            inputStream = new ObjectInputStream(clientSocket.getInputStream());
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

            do{
                username = (String) inputStream.readObject();
                logged_in = true;
                if(!server.checkUsername(username)) {
                    logged_in = false;
                    LOGGER.log(Level.INFO, "{0} tried to connect. Username already taken", username);
                    outputStream.writeObject("KO");
                }
            }while(!logged_in);

            outputStream.writeObject("OK");
            outputStream.flush();
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.WARNING, "Exception while logging in: {0}", e.toString());
            return false;
        }

        // Creates a new lobbyPlayer and adds it to the global player list of the server
        player = new LobbyPlayer(username);
        server.getPlayerList().add(player.getUsername());

        return true;
    }

    private void handleMessages(){
        try {
            MenuManager menuManager = MenuManager.getInstance();

            // Receiving and handling the messages
            ClientMessage message;
            while((message = (ClientMessage) inputStream.readObject()) != null){
                LOGGER.log(Level.INFO, message.toString());
                MessageEnvelope envelope = new MessageEnvelope(player, message, this);

                switch (message.getMessageType()) {
                    case MENU -> menuManager.addMessage(envelope);
                    case GAME -> {
                        if(networkManager != null)
                            networkManager.addMessage(envelope);
                    }
                }
            }

            outputStream.close();
            inputStream.close();
            clientSocket.close();
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        } finally {
            // Removes the player from the global player list of the server
            boolean res = server.getPlayerList().remove(player.getUsername());
            LOGGER.log(Level.SEVERE, "Player {0} removed? {1}.", new Object[]{player.getUsername(), res});

        }
    }

    public void sendViewContent(ViewContent viewContent){
        try {
            outputStream.reset();
            outputStream.writeObject(viewContent);
            outputStream.flush();

            LOGGER.log(Level.FINE, viewContent.toString());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Cannot send: {0}. {1}", new Object[]{viewContent, e});
            // throw new RuntimeException(e);
        }
    }

    public LobbyPlayer getPlayer() {
        return player;
    }

    public void setNetworkManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }
}
