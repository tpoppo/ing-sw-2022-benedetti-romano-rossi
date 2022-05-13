package it.polimi.ingsw.view;

import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Students;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.ReducedLobby;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.ansi;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CLIArt extends CLI {
    public CLIArt(ClientSocket client_socket, PrintStream out, InputStream read_stream) {
        super(client_socket, out, read_stream);
    }

    public CLIArt(ClientSocket client_socket) {
        super(client_socket);
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
        final int row_position = 10;
        final int columns_position = 2;

        Player current_player = model.getCurrentPlayer();
        print(ansi().bg(Ansi.Color.WHITE).a("Turn: ").reset(), 0 , 0);
        print(ansi().bg(Ansi.Color.WHITE).a(current_player.getUsername()).reset(), 0 , 7);
        GameState gamestate = gameHandler.getCurrentState();

        if(username.equals(current_player.getUsername())){
            switch (gamestate){
                case PLAY_ASSISTANT -> print(ansi().bg(Ansi.Color.WHITE).a("It's your turn, play an assistant card...").reset(), row_position, columns_position);
                case CHOOSE_CLOUD -> print(ansi().bg(Ansi.Color.WHITE).a("It's your turn, choose a cloud...").reset(), row_position, columns_position);
                case MOVE_MOTHER_NATURE -> print(ansi().bg(Ansi.Color.WHITE).a("It's your turn, move mother nature...").reset(), row_position, columns_position);
                case MOVE_STUDENT -> print(ansi().bg(Ansi.Color.WHITE).a("It's your turn, move a student...").reset(), row_position, columns_position);
                case ACTIVATE_CHARACTER -> print(ansi().bg(Ansi.Color.WHITE).a("It's your turn, activate a character...").reset(), row_position, columns_position);
                case FINISHED -> print(ansi().bg(Ansi.Color.WHITE).a("Finished").reset(), row_position, columns_position);
            }
        }else{
            print(ansi().bg(Ansi.Color.WHITE).a("It's " + current_player + " turn, wait...").reset(), row_position, columns_position);
        }
    }

    private void printClouds(){
        int cnt = 0;
        for(Students students : model.getClouds()){
            String cloudString = drawTile(students, 30, cnt, Ansi.Color.CYAN);
            print(cloudString, 15, 15*cnt+1);
            cnt++;
        }
    }

    private void printIslands(){

    }

    private void printPlayerSchoolBoard(){

    }

    private boolean[][] generateMaskTile(int row, int column, int dim, int seed){
        Random rng = new Random(seed);
        dim = Math.min(row*column, dim);
        boolean[][] mat = new boolean[row][column];
        ArrayList<Pair<Integer, Integer>> coast = new ArrayList<Pair<Integer, Integer>>();
        coast.add(new Pair<>(row/2, column/2));
        mat[row/2][column/2] = true;
        for(int i=0; i<dim; i++){
            Pair<Integer, Integer> current = coast.get(rng.nextInt(coast.size()));
            coast.remove(current);
            if(current.getX() >= 0 && current.getX() < row && current.getY() >= 0 && current.getY() < column){
                mat[current.getX()][current.getY()] = true;
                coast.add(new Pair<>(current.getX()+1, current.getY()));
                coast.add(new Pair<>(current.getX()-1, current.getY()));
                coast.add(new Pair<>(current.getX(), current.getY()+1));
                coast.add(new Pair<>(current.getX(), current.getY()-1));
            }
        }
        return mat;
    }

    private String drawTile(Students students, int dim, int seed, Ansi.Color bg_color){
        boolean[][] mask = generateMaskTile(5, 10, dim, seed);
        String[][] canvas = new String[5][10];
        int position = 0;
        print(students.toString(), 40, 20);
        for(Map.Entry<Color, Integer> entry : students.entrySet()) {
            Color key = entry.getKey();
            int value = entry.getValue();
            while(value >= 0){

                // first available position
                String s;
                while(!mask[position/mask.length][position%mask[0].length]) position++;
                if(value >= 5){
                    value -= 5;
                     s = ansi().fg(Ansi.Color.valueOf(key.toString())).a(5).reset().toString();
                }else{
                    value--;
                    s = ansi().fg(Ansi.Color.valueOf(key.toString())).a(1).reset().toString();
                }
                canvas[position/mask.length][position%canvas[0].length] = s;
                position++;
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0; i<canvas.length; i++){
            for(int j=0; j<canvas[i].length; j++){
                if(mask[i][j]){ // is inside the tile
                    if(canvas[i][j] == null){
                        stringBuilder.append(ansi().bg(bg_color).a(" ").reset());
                    } else {
                        stringBuilder.append(ansi().bg(bg_color).a(canvas[i][j]).reset());
                    }
                } else{
                    stringBuilder.append(" ");
                }
            }
            stringBuilder.append(Constants.NEWLINE);
        }
        return stringBuilder.toString();
    }


}
