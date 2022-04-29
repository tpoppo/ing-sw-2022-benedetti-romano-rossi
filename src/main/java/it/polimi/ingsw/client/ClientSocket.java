package it.polimi.ingsw.client;

import it.polimi.ingsw.controller.messages.ClientMessage;
import it.polimi.ingsw.utils.Consts;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientSocket {
    private final String SERVER_ADDR = Consts.SERVER_ADDR;
    private final int SERVER_PORT = Consts.SERVER_PORT;

    private Socket clientSocket;
    private ObjectOutputStream outputStream;

    public ClientSocket(){
        try {
            setup();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setup() throws IOException {
        clientSocket = new Socket(SERVER_ADDR, SERVER_PORT);
        outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
    }

    public void send(ClientMessage message) {
        try {
            outputStream.reset();
            outputStream.writeObject(message);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection() throws IOException {
        clientSocket.close();
    }
}
