package it.polimi.ingsw;

import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.view.CLI;
import it.polimi.ingsw.view.CLIArt;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for simple App.
 */
public class AppTest {
    @Test
    void RunServer() throws InterruptedException {
        runThread(() -> {
            Server server = Server.getInstance();
        });
}

    @Test
    void RunCli() throws InterruptedException {
        runThread(() -> {Server server = Server.getInstance();});
        runThread(() -> {
            ClientSocket client_socket = new ClientSocket();
            CLI cli = new CLI(client_socket);
            cli.run();
        });
    }

    @Test
    void RunCliArt() throws InterruptedException {
        runThread(() -> {Server server = Server.getInstance();});
        runThread(() -> {
            ClientSocket client_socket = new ClientSocket();
            CLIArt cli = new CLIArt(client_socket);
            cli.run();
        });
    }

    void runThread(Runnable runnable) throws InterruptedException {
        // this method is required as an exception in the
        AtomicReference<Exception> exc = new AtomicReference<>();
        new Thread(() -> {
            try{
                runnable.run();
            } catch (Exception e){
                exc.set(e);
            }
        }).start();
        Thread.sleep(300);
        assertNull(exc.get());
    }

}
