package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.network.messages.ClientMessage;
import it.polimi.ingsw.network.messages.MessageEnvelope;
import it.polimi.ingsw.view.viewcontent.ViewContent;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class handle the connection between the client and the server.
 * It routes the message to the right manager ({@link MenuManager} or {@link NetworkManager})
 */
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

    /**
     * Manage the login and message routing.
     */
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
            // this assumes that if a networkManager is found, it is in the GAME state,
            // as savefiles are only created by networkmanager is the GAME state

            networkManager.getGameHandler().getModel().getPlayers().stream()
                            .filter(player -> player.getUsername().equals(this.player.getUsername()))
                            .findFirst()
                            .ifPresent(player -> {
                                this.player = player;

                                networkManager.subscribe(this);
                                LOGGER.log(Level.INFO, "Player found and subscribed to networkManager {0}", networkManager);
                            });
        }else {
            MenuManager.getInstance().subscribe(this);
            LOGGER.log(Level.INFO, "New player");
        }

        handleMessages();
    }

    /**
     * Manage the login phase
     * @return true if the login has been successful otherwise false
     * @throws IOException thrown by the ObjectInputStream and ObjectOutputStream.
     */
    private boolean login() throws IOException {
        // The first message must contain the username chosen by the client
        boolean logged_in = false;
        String username = "";
        try {
            inputStream = new ObjectInputStream(clientSocket.getInputStream());
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

            do{
                Object objectUsername = inputStream.readObject();
                if(objectUsername instanceof String){
                    username = (String) objectUsername;
                    logged_in = true;
                    if(!server.checkUsername(username)) {
                        logged_in = false;
                        LOGGER.log(Level.INFO, "{0} tried to connect. Username already taken", username);
                        outputStream.writeObject("KO");
                    }
                } else {
                    LOGGER.log(Level.SEVERE, "Invalid username. Given: {0}", new Object[]{objectUsername});
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

    /**
     * Handle the message received by the client
     */
    private void handleMessages(){
        try {
            MenuManager menuManager = MenuManager.getInstance();

            // Receiving and handling the messages
            Object objectMessage;
            while((objectMessage = inputStream.readObject()) != null){
                if(objectMessage instanceof ClientMessage message) {
                    LOGGER.log(Level.INFO, message.toString());
                    MessageEnvelope envelope = new MessageEnvelope(player, message, this);

                    switch (message.getMessageType()) {
                        case MENU -> menuManager.addMessage(envelope);
                        case GAME -> {
                            if (networkManager != null)
                                networkManager.addMessage(envelope);
                        }
                    }
                } else {
                    LOGGER.log(Level.SEVERE, "Invalid message. Given: {0}", new Object[]{objectMessage});
                }
            }

            menuManager.unsubscribe(this);
            networkManager.unsubscribe(this);

            outputStream.close();
            inputStream.close();
            clientSocket.close();
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.WARNING, "Invalid data ({0})", new Object[]{e.toString()});
        } finally {

            try {
                outputStream.close();
                inputStream.close();
                clientSocket.close();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Cannot close the connection {0}", new Object[]{e});
            }


            // Removes the player from the global player list of the server
            boolean res = server.getPlayerList().remove(player.getUsername());
            MenuManager.getInstance().unsubscribe(this);
            if(networkManager != null) { // destroy the network manager and move all the player
                networkManager.safeDestroy();
                // networkManager.unsubscribe(this); for debugging
            }
            LOGGER.log(Level.INFO, "Player {0} removed? {1}.", new Object[]{player.getUsername(), res});
        }
    }

    /**
     * Helper function used to send {@link ViewContent} to the client
     * @param viewContent view sent to the client
     */
     public void sendViewContent(ViewContent viewContent){
        try {
            outputStream.reset();
            outputStream.writeObject(viewContent);
            outputStream.flush();

            LOGGER.log(Level.FINE, viewContent.toString());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Cannot send: {0}. {1}", new Object[]{viewContent, e});
        }
    }

    /**
     * Reset the ConnectionCEO state to the menu state.
     * It keeps the username information.
     */
    public void clean(){
        this.networkManager = null;
        player.setWizard(null);
    }

    public LobbyPlayer getPlayer() {
        return player;
    }

    public void setNetworkManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    public String toString() {
        return "ConnectionCEO{" +
                "clientSocket=" + clientSocket +
                ", networkManager=" + networkManager +
                ", player=" + player +
                ", inputStream=" + inputStream +
                ", outputStream=" + outputStream +
                '}';
    }
}
