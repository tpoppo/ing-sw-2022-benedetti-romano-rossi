package it.polimi.ingsw;

import it.polimi.ingsw.client.ClientConfig;
import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.view.CLI;
import it.polimi.ingsw.view.CLIArt;
import it.polimi.ingsw.view.GUI;

import java.io.IOException;
import java.util.*;

/**
 * This class parses the command line argument and starts the right mode.
 */
public class Eriantys {
    public static void main(String[] args ) {

        if(args.length == 0) {
            System.out.println("Missing argument (server - cli - cliart - gui)");
        } else {
            try {
                switch (args[0].toLowerCase()) {
                    case "server" -> runServer(args); // app server
                    case "cli" -> runCli(args); // app cli
                    case "cliart" -> runCLIArt(args); // app cli, but fancy
                    case "gui" -> runGUI(args); // app gui
                    default -> System.out.println("Invalid argument given: " + args[0]);
                }
            } catch (IOException e){
                System.out.println("Cannot connect to server");
            }
        }
    }

    /**
     * Start the server
     * @param args the command line arguments
     */
    private static void runServer(String[] args){
        ClientConfig clientConfig = parseInput(args, true);
        Server.setPort(clientConfig.getPort());
        Server.getInstance();
    }

    /**
     * Start the client cli
     * @param args the command line arguments
     */
    private static void runCli(String[] args) throws IOException {
        ClientConfig clientConfig = parseInput(args, false);
        ClientSocket client_socket = new ClientSocket(clientConfig);
        CLI cli = new CLI(client_socket);
        cli.run();
    }

    /**
     * Start the client cli art
     * @param args the command line arguments
     */
    private static void runCLIArt(String[] args) throws IOException {
        ClientConfig clientConfig = parseInput(args, false);
        ClientSocket client_socket = new ClientSocket(clientConfig);
        CLIArt cli = new CLIArt(client_socket);
        cli.run();
    }

    /**
     * Start the client gui
     * @param args the command line arguments
     */
    private static void runGUI(String[] args) throws IOException {
        System.out.println("Starting the GUI...\n");
        ClientConfig clientConfig = parseInput(args, false);
        System.out.println("Server Address: " + clientConfig.getAddress());
        System.out.println("Server Port: " + clientConfig.getPort());
        GUI.setClientSocket(new ClientSocket(clientConfig));
        GUI.main(args);
    }

    /**
     * Parse the command line arguments and convert them into an object.
     * @param args the command line arguments
     * @param is_server used by the server
     * @return the {@link ClientConfig} used by {@link CLI}, {@link GUI} and {@link CLIArt}.
     */
    static ClientConfig parseInput(String[] args, boolean is_server){
        ClientConfig client_config = new ClientConfig();
        ArrayList<String> largs = new ArrayList<>(Arrays.asList(args));

        // set the port <port> argument
        int port = largs.indexOf("port");
        if(port != -1){
            if(port+1<largs.size()){
                try{
                    client_config.setPort(Integer.parseInt(largs.get(port+1)));
                } catch (NumberFormatException e) {
                    System.out.println("the ip parameters must be an integer given: "+largs.get(port+1));
                }
            } else {
                System.out.println("The port value is missing");
            }
        }

        // set the ip <ip> argument
        int ip = largs.indexOf("ip");
        if(ip != -1) {
            if(is_server){
                System.out.println("It is running in server mode: the ip argument has been ignored");
            } else {
                if (ip + 1 < largs.size()) {
                    client_config.setAddress(largs.get(ip + 1));
                } else {
                    System.out.println("The ip value is missing");
                }
            }
        }
        return client_config;
    }
}
