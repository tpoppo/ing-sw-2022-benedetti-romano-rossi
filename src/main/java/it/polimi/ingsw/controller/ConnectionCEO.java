package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.messages.ClientMessage;
import it.polimi.ingsw.view.ViewContent;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionCEO {
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

        if(login()) {
            LOGGER.log(Level.INFO, "Connection established with {0}", player.getUsername());

            MenuManager.getInstance().subscribe(this);
            handleMessages();
        }
        else LOGGER.log(Level.INFO, "Connection failed");
    }
    private boolean login(){
        // The first message must contain the username chosen by the client
        boolean logged_in;
        String username;
        try {
            inputStream = new ObjectInputStream(clientSocket.getInputStream());
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

            do{
                username = (String) inputStream.readObject();
                System.out.println(username);
                logged_in = true;
                if(!server.checkUsername(username)) {
                    logged_in = false;
                    LOGGER.log(Level.INFO, "{0} can logged in. Username already taken", username);
                    outputStream.writeObject("ERROR: Username already taken");
                    outputStream.close();
                }
            }while(!logged_in);

            outputStream.writeObject("OK");
            outputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.WARNING, "Exception while logging in: {0}", e.toString());
            return false;
        }

        // Creates a new lobbyPlayer and adds it to the global player list of the server
        player = new LobbyPlayer(username);
        server.getPlayerList().add(player);

        return true;
    }

    public void handleMessages(){
        try {
            MenuManager menuManager = MenuManager.getInstance();

            // Receiving and handling the messages
            ClientMessage message;
            while((message = (ClientMessage) inputStream.readObject()) != null){
                LOGGER.log(Level.INFO, message.toString());
                MessageEnvelope envelope = new MessageEnvelope(player, message, this);

                switch(message.getMessageType()){
                    case MENU:
                        menuManager.message_queue.add(envelope);
                        break;
                    case GAME:
                        networkManager.getMessageQueue().add(envelope);
                        break;
                }
            }

            outputStream.close();
            inputStream.close();
            clientSocket.close();
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        } finally {
            // Removes the player from the global player list of the server
            server.getPlayerList().remove(player);
        }
    }

    public void sendViewContent(ViewContent viewContent){
        try {
            outputStream.reset();
            outputStream.writeObject(viewContent);
            outputStream.flush();

            LOGGER.log(Level.INFO, viewContent.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public LobbyPlayer getPlayer() {
        return player;
    }

    public void setNetworkManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }
}
