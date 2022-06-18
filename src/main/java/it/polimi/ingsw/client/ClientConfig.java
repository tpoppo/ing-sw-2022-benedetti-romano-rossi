package it.polimi.ingsw.client;

import it.polimi.ingsw.utils.Constants;

/**
 * Contains the config used by the ClientSocket to connect to the server.
 */
public class ClientConfig {
    String address;
    int port;

    /**
     * Creates an object with the default values
     */
    public ClientConfig(){
        port = Constants.SERVER_PORT;
        address = Constants.SERVER_ADDR;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "ClientConfig{" +
                "address='" + address + '\'' +
                ", port=" + port +
                '}';
    }
}
