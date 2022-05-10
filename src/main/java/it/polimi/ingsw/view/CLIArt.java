package it.polimi.ingsw.view;

import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.utils.ReducedLobby;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.ansi;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CLIArt extends CLI {
    public CLIArt(ClientSocket client_socket, PrintStream out, InputStream read_stream) {
        super(client_socket, out, read_stream);
    }

    public CLIArt(ClientSocket client_socket) {
        super(client_socket);
    }

    protected void printMenu() {
        print(Constants.ERIANTYS, 1, 10);
        print(ansi().a(">").a(Constants.NEWLINE), 1, 20);
        print(ansi().a(">>").a(Constants.NEWLINE), 1, 20);
        print(ansi().a(">>").a(Constants.NEWLINE), 1, 30);
    }

    @Override
    protected void printGame() {
        print(Constants.ERIANTYS, 0, 0);

        print(ansi().bg(Ansi.Color.WHITE).fg(Ansi.Color.BLACK).a(username).reset(), 0 , 3);

        printState();
        printClouds();
        printIslands();
        printPlayerSchoolBoard();

    }

    private void printState(){

    }

    private void printClouds(){

    }

    private void printIslands(){

    }

    private void printPlayerSchoolBoard(){

    }

    // ------------------------------------------------------------------------ PRINTING HELPER ------------------------------------------------------------------------

    private void print(String s, int x, int y) {
        out.print(ansi().cursor(y, x).a(
                s.replaceAll(
                        Constants.NEWLINE,
                        ansi().a(Constants.NEWLINE).cursorRight(y - 1).toString()
                )
            )
        );
    }
    private void print(Ansi s, int x, int y) {
        print(s.toString(), x, y);
    }

}
