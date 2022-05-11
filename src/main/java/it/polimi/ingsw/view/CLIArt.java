package it.polimi.ingsw.view;

import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.model.Player;
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
        Player current_player = model.getCurrentPlayer();
        print(ansi().bg(Ansi.Color.WHITE).a("Turn: "), 0 , 0);
        print(ansi().bg(Ansi.Color.WHITE).a(current_player.getUsername()), 0 , 7);
        GameState gamestate = gameHandler.getCurrentState();

        if(username == current_player.getUsername()){
            if(gamestate == GameState.PLAY_ASSISTANT){
                print(ansi().bg(Ansi.Color.WHITE).a("It's your turn, play an assistant card..."), 2 , 0);
            }
            if(gamestate == GameState.CHOOSE_CLOUD){
                print(ansi().bg(Ansi.Color.WHITE).a("It's your turn, choose a cloud..."), 2 , 0);
            }
            if(gamestate == GameState.MOVE_MOTHER_NATURE){
                print(ansi().bg(Ansi.Color.WHITE).a("It's your turn, move mother nature..."), 2 , 0);
            }
            if(gamestate == GameState.MOVE_STUDENT){
                print(ansi().bg(Ansi.Color.WHITE).a("It's your turn, move a student..."), 2 , 0);
            }
            if(gamestate == GameState.ACTIVATE_CHARACTER){
                print(ansi().bg(Ansi.Color.WHITE).a("It's your turn, activate a character..."), 2 , 0);
            }
        }else{
            print(ansi().bg(Ansi.Color.WHITE).a("It's " + current_player + " turn, wait..."), 2 , 0);
        }
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
