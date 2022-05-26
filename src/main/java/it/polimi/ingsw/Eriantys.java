package it.polimi.ingsw;

import it.polimi.ingsw.client.ClientConfig;
import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.view.CLI;
import it.polimi.ingsw.view.CLIArt;
import it.polimi.ingsw.view.GUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Eriantys {
    public static void main(String[] args ) {

        if(args.length == 0) {
            System.out.println("Missing argument (server - cli - cliart - gui)");
            return ;
        }

        switch (args[0].toLowerCase()) {
            case "server" -> runServer(args); // app server
            case "cli" -> runCli(args); // app cli
            case "cliart" -> runCLIArt(args); // app cli, but fancy
            case "gui" -> runGUI(args); // app gui
        }
        System.out.println("Invalid argument given: "+args[0]);
    }

    private static void runServer(String[] args){
        ClientConfig clientConfig = parseInput(args, true);
        Server.setPort(clientConfig.getPort());
        Server server = Server.getInstance();
    }

    private static void runCli(String[] args){
        ClientConfig clientConfig = parseInput(args, false);
        ClientSocket client_socket = new ClientSocket(clientConfig);
        CLI cli = new CLI(client_socket);
        cli.run();
    }

    private static void runCLIArt(String[] args){
        ClientConfig clientConfig = parseInput(args, false);
        ClientSocket client_socket = new ClientSocket(clientConfig);
        CLIArt cli = new CLIArt(client_socket);
        cli.run();
    }

    private static void runGUI(String[] args){
        System.out.println("Starting the GUI...\n");
        ClientConfig clientConfig = parseInput(args, false);
        GUI.setClientSocket(new ClientSocket(clientConfig));
        GUI.main(args);
    }

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
