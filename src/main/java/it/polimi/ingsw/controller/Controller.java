package it.polimi.ingsw.controller;

public class Controller {
    private static Controller instance;
    final private int PORT;

    private MenuManager menuManager;
    private NetworkManager networkManager;

    private Controller(){
        PORT = 1234;
    }

    public static Controller getInstance(){
        if(instance == null) instance = new Controller();
        return instance;
    }

    // TODO:
    //  public void setupConnection(){}
    //  public NetworkManager createLobby(){}
    //  public NetworkManager joinLobby(){}
    //  public NetworkManager reconnect(String username){}
}
