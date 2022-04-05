package it.polimi.ingsw.controller;

public class Server {
    private static Server instance;
    final private int PORT;

    private MenuManager menuManager;
    private NetworkManager networkManager;

    private Server(){
        PORT = 1234;
    }

    public static Server getInstance(){
        if(instance == null) instance = new Server();
        return instance;
    }

    // TODO:
    //  public void setupConnection(){}
    //  public NetworkManager createLobby(){}
    //  public NetworkManager joinLobby(){}
    //  public NetworkManager reconnect(String username){}
}
