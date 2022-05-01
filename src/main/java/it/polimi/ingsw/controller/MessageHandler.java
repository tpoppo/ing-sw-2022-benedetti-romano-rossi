package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.messages.ClientMessage;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageHandler extends Thread{
    private final Logger LOGGER = Logger.getLogger(getClass().getName());
    private final Socket clientSocket;
    private final MenuManager menuManager;
    private NetworkManager networkManager;
    private ViewContentCreator viewContentCreator;
    private final Server server;
    private LobbyPlayer player;

    public MessageHandler(Socket clientSocket){
        this.clientSocket = clientSocket;
        menuManager = MenuManager.getInstance();
        server = Server.getInstance();
    }

    @Override
    public void run(){
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());

            // The first message must contain the username chosen by the client
            boolean logged_in = false;
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
            String username;
            do{
                username = (String) inputStream.readObject();
                logged_in = true;
                if(!server.checkUsername(username)) {
                    logged_in = false;
                    LOGGER.log(Level.INFO, "{0} can logged in. Username already taken", username);
                    out.println("ERROR: Username already taken");
                    out.close();
                }
            }while(!logged_in);

            out.println("OK");
            out.flush();
            LOGGER.log(Level.INFO, "{0} logged in", username);


            // Creates a new lobbyPlayer and adds it to the global player list of the server
            player = new LobbyPlayer(username);
            server.getPlayerList().add(player);

            // Creates and starts the viewContentCreator
            viewContentCreator = new ViewContentCreator(outputStream, null, player);
            viewContentCreator.start();

            // Receiving and handling the messages
            ClientMessage message;
            while((message = (ClientMessage) inputStream.readObject()) != null){
                MessageEnvelope envelope = new MessageEnvelope(player, message);

                switch(message.getMessageType()){
                    case MENU:
                        menuManager.message_queue.add(envelope);
                        break;
                    case GAME:
                        if(networkManager == null){
                            networkManager = server.findPlayerLocation(player);
                            viewContentCreator.setNetworkManager(networkManager);
                        }

                        networkManager.getMessageQueue().add(envelope);
                        break;
                }
            }

            outputStream.close();
            inputStream.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            // Removes the player from the global player list of the server
            server.getPlayerList().remove(player);
        }
    }
}
