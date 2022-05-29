package it.polimi.ingsw;

import it.polimi.ingsw.client.ClientConfig;
import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.network.Server;
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

    final int TESTING_PORT = 0xcafe;

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

        Thread.sleep(10);

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

        Thread.sleep(10);

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
    void ClientSocket() throws InterruptedException {
        runThread(() -> {
            Server.setPort(TESTING_PORT);
            Server server = Server.getInstance();
        });

        Thread.sleep(10);

        for(int i=0; i<10; i++) {
            int tmp_i = i;
            runThread(() -> {
                ClientConfig clientConfig = new ClientConfig();
                clientConfig.setPort(TESTING_PORT);
                ClientSocket clientSocket = new ClientSocket(clientConfig);
                clientSocket.login("username_"+tmp_i);
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
