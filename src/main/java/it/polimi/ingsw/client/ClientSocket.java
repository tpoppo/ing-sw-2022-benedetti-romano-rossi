package it.polimi.ingsw.client;

import it.polimi.ingsw.network.messages.ClientMessage;
import it.polimi.ingsw.view.viewcontent.ViewContent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class manage the client side connection to the server
 */
public class ClientSocket {
    private final Logger LOGGER = Logger.getLogger(getClass().getName());
    private Socket clientSocket;
    private ObjectOutputStream output_stream;
    private ObjectInputStream input_stream;
    private String username;
    private ViewContent view;
    final private ClientConfig client_config;

    public final Object mutex = new Object();


    public ClientSocket(ClientConfig client_config) throws IOException {
        this.client_config = client_config;
            setup();
    }

    /**
     * it setups the connection and it creates the socket and input/output streams.
     * @throws IOException if it cannot create the socket or a stream
     */
    private void setup() throws IOException {
        clientSocket = new Socket(client_config.getAddress(), client_config.getPort());
        output_stream = new ObjectOutputStream(clientSocket.getOutputStream());
        input_stream = new ObjectInputStream(clientSocket.getInputStream());
    }

    /**
     * It sends the given message to the server
     * @param message client message to send
     */
    public void send(ClientMessage message) {
        try {
            output_stream.reset();
            output_stream.writeObject(message);
            output_stream.flush();

            LOGGER.log(Level.FINE, "Message sent: {0}", message);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not send message: {0}. Exception: {1}", new Object[]{message, e});
        }
    }

    public void closeConnection() throws IOException {
        clientSocket.close();
    }

    /**
     * @return it returns where the socket is still open
     */
    public boolean isOpened() {
        return !clientSocket.isClosed();
    }

    /**
     * It log in the client with the given username
     * @param username username of the player
     * @return true if the login was successful otherwise it returns false
     */
    public boolean login(String username) {
        try {
            output_stream.reset();
            output_stream.writeObject(username);
            output_stream.flush();
            String response = (String) input_stream.readObject();

            if(response.equals("KO")) return false;
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            LOGGER.log(Level.WARNING, "Invalid string parsing: {0}", new Object[]{e});
            return false;
        }
        this.username = username;

        new Thread(() -> {
            while(isOpened()) {
                try {
                    view = (ViewContent) input_stream.readObject();
                    System.out.println("view:"+view);
                } catch (IOException e) {
                    try {
                        closeConnection();
                    } catch (IOException ex) {
                        LOGGER.log(Level.SEVERE, "Cannot close: {0}", new Object[]{ex});
                    }

                    LOGGER.log(Level.SEVERE, "Server closed. Exception: {0}", new Object[]{e});
                    System.exit(0);
                } catch (ClassNotFoundException e){
                    LOGGER.log(Level.INFO, "Invalid message: {0}", new Object[]{e});
                }

                if(view != null) {
                    synchronized (mutex){
                        mutex.notifyAll();

                        // FIXME:
                        try {
                            mutex.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }).start();

        return true;
    }

    public String getUsername(){
        return username;
    }

    public ViewContent getView() {
        return view;
    }

    public void setView(ViewContent view) {
        this.view = view;
    }
}
