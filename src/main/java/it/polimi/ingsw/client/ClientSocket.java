package it.polimi.ingsw.client;

import it.polimi.ingsw.network.messages.ClientMessage;
import it.polimi.ingsw.utils.Consts;
import it.polimi.ingsw.view.ViewContent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientSocket {
    private final Logger LOGGER = Logger.getLogger(getClass().getName());
    private Socket clientSocket;
    private ObjectOutputStream output_stream;
    private ObjectInputStream input_stream;
    private String username;
    private ViewContent view;

    public final Object mutex = new Object();

    public ClientSocket(){
        try {
            setup();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setup() throws IOException {
        clientSocket = new Socket(Consts.SERVER_ADDR, Consts.SERVER_PORT);
        output_stream = new ObjectOutputStream(clientSocket.getOutputStream());
        input_stream = new ObjectInputStream(clientSocket.getInputStream());
    }

    public void send(ClientMessage message) {
        try {
            output_stream.reset();
            output_stream.writeObject(message);
            output_stream.flush();

            LOGGER.log(Level.INFO, "Message sent: {0}", message);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Couldn't send message: {0}", message);
            throw new RuntimeException(e);
        }
    }

    public void closeConnection() throws IOException {
        clientSocket.close();
    }

    public boolean login(String username) {
        try {
            output_stream.reset();
            output_stream.writeObject(username);
            output_stream.flush();
            String s = (String) input_stream.readObject();

            if(s.charAt(0) == 'E') return false;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.username = username;

        new Thread(() -> {
            while(true) {
                try {
                    view = (ViewContent) input_stream.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
 //               LOGGER.log(Level.INFO, "Received view: {0}", view);
                if(view != null) {
                    synchronized (mutex){
                        mutex.notifyAll();

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
