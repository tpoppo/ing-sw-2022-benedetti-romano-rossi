package it.polimi.ingsw;

import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.view.CLI;

/**
 * Hello world!
 *
 */
public class Eriantys
{
    public static void main(String[] args ) {

        if(args.length == 0) {
            System.out.println("Requires more parameters");
            return ;
        }

        switch (args[0].toLowerCase()) {
            case "server" -> runServer(args); // app server
            case "cli" -> runCli(args); // app cli
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
}
