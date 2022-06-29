package it.polimi.ingsw;

import it.polimi.ingsw.client.ClientConfig;
import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.network.messages.ChooseWizardMessage;
import it.polimi.ingsw.network.messages.CreateLobbyMessage;
import it.polimi.ingsw.network.messages.JoinLobbyMessage;
import it.polimi.ingsw.network.messages.StartGameMessage;
import it.polimi.ingsw.view.CLI;
import it.polimi.ingsw.view.CLIArt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.IOException;
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
            Server.getInstance();
        });
}

    @Test
    @EnabledOnOs(OS.LINUX)
    void RunCli() throws InterruptedException {
        runThread(() -> {
            Server.setPort(TESTING_PORT);
            Server.getInstance();
        });

        Thread.sleep(20);

        runThread(() -> {
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.setPort(TESTING_PORT);
            ClientSocket client_socket;
            try {
                client_socket = new ClientSocket(clientConfig);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            CLI cli = new CLI(client_socket);
            cli.run();
        });
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    void RunCliArt() throws InterruptedException {
        runThread(() -> {
            Server.setPort(TESTING_PORT);
            Server.getInstance();
        });

        Thread.sleep(20);

        runThread(() -> {
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.setPort(TESTING_PORT);
            ClientSocket client_socket;
            try {
                client_socket = new ClientSocket(clientConfig);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            CLIArt cliArt = new CLIArt(client_socket);
            cliArt.run();
        });
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    void ClientSocketCliCliArtMenuCheck() throws InterruptedException {
        runThread(() -> {
            Server.setPort(TESTING_PORT);
            Server.getInstance();
        });

        Thread.sleep(20);

        for(int i=0; i<10; i++) {
            final int final_i = i;
            runThread(() -> {
                ClientConfig clientConfig = new ClientConfig();
                clientConfig.setPort(TESTING_PORT);
                ClientSocket clientSocket;
                try {
                    clientSocket = new ClientSocket(clientConfig);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if (final_i % 2 == 0) {
                    new CLIArt(clientSocket);
                } else {
                    new CLI(clientSocket);
                }
                String username = "username_" + Math.random();

                if (final_i != 0)
                    assertTrue(clientSocket.login(username));

                // final_i has not logged in
                assertEquals(Server.getInstance().getPlayerList().contains(username), final_i != 0);

                clientSocket.send(new JoinLobbyMessage(final_i / 3));
                clientSocket.send(new CreateLobbyMessage(final_i % 5 + 2));
                clientSocket.send(new CreateLobbyMessage(2));
                for (int j = -1; j < 4; j++) {
                    clientSocket.send(new ChooseWizardMessage(j));
                }
                clientSocket.send(new StartGameMessage(final_i % 2 == 1));
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
                e.printStackTrace(System.err);
                exc.set(e);
            }
        });
        t.start();
        Thread.sleep(200);
        t.interrupt();
        assertNull(exc.get());
    }
}
