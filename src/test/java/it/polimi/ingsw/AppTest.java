package it.polimi.ingsw;

import it.polimi.ingsw.client.ClientConfig;
import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.network.MenuManager;
import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.network.messages.CreateLobbyMessage;
import it.polimi.ingsw.network.messages.JoinLobbyMessage;
import it.polimi.ingsw.network.messages.StartGameMessage;
import it.polimi.ingsw.view.CLI;
import it.polimi.ingsw.view.CLIArt;
import it.polimi.ingsw.view.GUI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for simple App.
 */
public class AppTest {

    final int TESTING_PORT = 0xcafd;

    @Test
    void RunServer() throws InterruptedException {
        runThread(() -> {
            Server.setPort(TESTING_PORT);
            Server server = Server.getInstance();
        });
}

    @Test
    @EnabledOnOs(OS.LINUX)
    void RunCli() throws InterruptedException {
        runThread(() -> {
            Server.setPort(TESTING_PORT);
            Server server = Server.getInstance();
        });

        Thread.sleep(20);

        runThread(() -> {
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.setPort(TESTING_PORT);
            ClientSocket client_socket = new ClientSocket(clientConfig);
            CLI cli = new CLI(client_socket);
            cli.run();
        });
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    void RunCliArt() throws InterruptedException {
        runThread(() -> {
            Server.setPort(TESTING_PORT);
            Server server = Server.getInstance();
        });

        Thread.sleep(20);

        runThread(() -> {
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.setPort(TESTING_PORT);
            ClientSocket client_socket = new ClientSocket(clientConfig);
            CLIArt cliArt = new CLIArt(client_socket);
            cliArt.run();
        });
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    void ClientSocketCliCliArtMenuCheck() throws InterruptedException {
        runThread(() -> {
            Server.setPort(TESTING_PORT);
            Server server = Server.getInstance();
        });

        Thread.sleep(20);

        for(int i=0; i<10; i++) {
            final int final_i = i;
            runThread(() -> {
                ClientConfig clientConfig = new ClientConfig();
                CLI cli;
                clientConfig.setPort(TESTING_PORT);
                try {
                    Thread.sleep(5*final_i);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                ClientSocket clientSocket = new ClientSocket(clientConfig);
                if(final_i % 2 == 0){
                    cli = new CLIArt(clientSocket);
                } else {
                    cli = new CLI(clientSocket);
                }
                String username = "username_"+final_i;
                clientSocket.login(username);
                try {
                    Thread.sleep(5*final_i);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                assertTrue(Server.getInstance().getPlayerList().contains(username));

                clientSocket.send(new JoinLobbyMessage(final_i / 5));
                clientSocket.send(new CreateLobbyMessage(final_i % 3));
                clientSocket.send(new StartGameMessage(final_i % 2 == 1));

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                assertEquals(0, MenuManager.getInstance().countSubscriber());
            });
        }
    }

    void runThread(Runnable runnable) throws InterruptedException {
        // this method is required as an exception might not stop the run
        AtomicReference<Exception> exc = new AtomicReference<>();

        Thread t = new Thread(() -> {
            try{
                runnable.run();
            } catch (Exception e){
                exc.set(e);
            }
        });
        t.start();
        Thread.sleep(100);
        t.interrupt();
        assertNull(exc.get());
    }
}
