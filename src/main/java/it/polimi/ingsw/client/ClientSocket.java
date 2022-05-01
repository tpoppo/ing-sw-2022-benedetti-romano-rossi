package it.polimi.ingsw.client;

import it.polimi.ingsw.controller.messages.ClientMessage;
import it.polimi.ingsw.utils.Consts;
import it.polimi.ingsw.view.ViewContent;

import javax.swing.text.View;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientSocket {
    private final String SERVER_ADDR = Consts.SERVER_ADDR;
    private final int SERVER_PORT = Consts.SERVER_PORT;

    private Socket clientSocket;
    private ObjectOutputStream output_stream;
    private ObjectInputStream input_stream;

    private String username;
    private ViewContent view;

    public ClientSocket(){
        try {
            setup();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setup() throws IOException {
        clientSocket = new Socket(SERVER_ADDR, SERVER_PORT);
        output_stream = new ObjectOutputStream(clientSocket.getOutputStream());
        input_stream = new ObjectInputStream(clientSocket.getInputStream());

        new Thread(() -> {
            while(true) {
                try {
                    while((view = (ViewContent) input_stream.readObject()) != null);
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void send(ClientMessage message) {
        try {
            output_stream.reset();
            output_stream.writeObject(message);
            output_stream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection() throws IOException {
        clientSocket.close();
    }

    public void login(String username) {
        try {
            output_stream.reset();
            output_stream.writeObject(username);
            output_stream.flush();
            // FIXME: how do we check whether the user has logged in?
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.username = username;
    }

    public String getUsername(){
        return username;
    }

    public ViewContent getView(){
        return view;
    }

}
