package it.polimi.ingsw;

import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.view.CLI;
import it.polimi.ingsw.view.CLIArt;
import it.polimi.ingsw.view.GUI;

/**
 * Hello world!
 *
 */
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
    }

    static void runServer(String[] args){
        Server server = Server.getInstance();
    }

    static void runCli(String[] args){
        ClientSocket client_socket = new ClientSocket();
        CLI cli = new CLI(client_socket);
        cli.run();
    }

    static void runCLIArt(String[] args){
        ClientSocket client_socket = new ClientSocket();
        CLIArt cli = new CLIArt(client_socket);
        cli.run();
    }

    static void runGUI(String[] args){
        System.out.println("Starting the GUI...\n");
        GUI.main(args);
    }
}
