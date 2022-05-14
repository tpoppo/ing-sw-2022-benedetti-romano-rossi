package it.polimi.ingsw.view;

import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.controller.LobbyHandler;
import it.polimi.ingsw.controller.LobbyPlayer;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Island;
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
    private final Pair<Integer, Integer> STD_CURSOR_POSITION = new Pair<>(48, 1);
    private final Pair<Integer, Integer> STD_USERNAME_POSITION = new Pair<>(2, 80);
    private final Pair<Integer, Integer> STD_PLAYERS_POSITION = new Pair<>(4, 80);
    private final Pair<Integer, Integer> STD_BOARD_POSITION = new Pair<>(30, 1);
    private final Pair<Integer, Integer> STD_CHARACTER_POSITION = new Pair<>(22, 50);
    private final Pair<Integer, Integer> STD_STATUS_POSITION = new Pair<>(2, 110);
    private final Pair<Integer, Integer> STD_CLOUDS_POSITION = new Pair<>(10, 1);
    private final Pair<Integer, Integer> STD_ISLANDS_POSITION = new Pair<>(16, 1);
    private final Pair<Integer, Integer> STD_ASSISTANTS_POSITION = new Pair<>(30, 50);
    private final Pair<Integer, Integer> STD_COINS_POSITION = new Pair<>(31, 33);


    public CLIArt(ClientSocket client_socket, PrintStream out, InputStream read_stream) {
        super(client_socket, out, read_stream);
    }

    public CLIArt(ClientSocket client_socket) {
        super(client_socket);
    }


    @Override
    protected void printGame(){
        print(ansi().a(Constants.ERIANTYS), 1, 1);

        // Banner length is 63
        print(ansi().bg(Ansi.Color.WHITE).fg(Ansi.Color.BLACK).a(username).reset(), STD_USERNAME_POSITION);
        print(drawPlayers(), STD_PLAYERS_POSITION);
        print(drawState(), STD_STATUS_POSITION);
        printIslands();
        printClouds();
        // print(drawAssistants(), STD_ASSISTANTS_POSITION);
        // print(drawBoard(schoolBoardPlayerUsername), STD_BOARD_POSITION);

        if(model.getExpertMode()) {
            // print(drawCoins(), STD_COINS_POSITION);
            // print(drawCharacters(), STD_CHARACTER_POSITION);
        }

        print(ansi().eraseLine().a("> ").reset(), STD_CURSOR_POSITION);

        // print server errors
        // if(errorMessage != null) printErrorRelative();
    }

    // NOT USED
    private void printState(){
        final int row_position = STD_STATUS_POSITION.getY() + 1;
        final int columns_position = STD_STATUS_POSITION.getX();

        Player current_player = model.getCurrentPlayer();
        print(ansi().bg(Ansi.Color.DEFAULT).a("Turn: ").reset(), STD_STATUS_POSITION.getY() , STD_STATUS_POSITION.getX());
        print(ansi().bg(Ansi.Color.DEFAULT).a(current_player.getUsername()).reset(), STD_STATUS_POSITION.getY(), STD_STATUS_POSITION.getX());
        GameState gamestate = gameHandler.getCurrentState();

        if(username.equals(current_player.getUsername())){
            switch (gamestate){
                case PLAY_ASSISTANT -> print(ansi().bg(Ansi.Color.DEFAULT).a("It's your turn, play an assistant card...").reset(), row_position, columns_position);
                case CHOOSE_CLOUD -> print(ansi().bg(Ansi.Color.DEFAULT).a("It's your turn, choose a cloud...").reset(), row_position, columns_position);
                case MOVE_MOTHER_NATURE -> print(ansi().bg(Ansi.Color.DEFAULT).a("It's your turn, move mother nature...").reset(), row_position, columns_position);
                case MOVE_STUDENT -> print(ansi().bg(Ansi.Color.DEFAULT).a("It's your turn, move a student...").reset(), row_position, columns_position);
                case ACTIVATE_CHARACTER -> print(ansi().bg(Ansi.Color.DEFAULT).a("It's your turn, activate a character...").reset(), row_position, columns_position);
                case FINISHED -> print(ansi().bg(Ansi.Color.DEFAULT).a("Finished").reset(), row_position, columns_position);
            }
        }else{
            print(ansi().bg(Ansi.Color.DEFAULT).a("It's " + current_player.getUsername() + " turn, wait...").reset(), row_position, columns_position);
        }
        print(ansi().bg(Ansi.Color.DEFAULT), 0, 0);
    }

    private void printClouds(){
        int cnt = 0;
        for(Students students : model.getClouds()){
            String cloudString = drawCloud(students, cnt);
            print("Cloud: "+cnt, STD_CLOUDS_POSITION.getX(), 20*cnt+STD_CLOUDS_POSITION.getY());
            print(cloudString, STD_CLOUDS_POSITION.getX()+1, 20*cnt+STD_CLOUDS_POSITION.getY());
            cnt++;
        }
    }

    private void printIslands(){
        int divisor = model.getIslands().size();
        for(int i=2; i<=model.getIslands().size(); i++) {
            if(model.getIslands().size()%i == 0){
                divisor = model.getIslands().size()/i;
                break;
            }
        }

        int cnt = 0;
        for(Island island : model.getIslands()){
            String islandString = drawIsland(island, 0xdeadcafe^cnt);
            String owner = island.getOwner() == null ? "<free>" : island.getOwner().getUsername();
            print("%d - %s".formatted(cnt, owner),
                    STD_ISLANDS_POSITION.getX() + 6*(cnt/divisor), 16*(cnt%divisor)+1);
            print(islandString, STD_ISLANDS_POSITION.getX() + 6*(cnt/divisor) + 1, 16*(cnt%divisor)+1);
            cnt++;
        }
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
        while(dim > 0){
            Pair<Integer, Integer> current = coast.get(rng.nextInt(coast.size()));
            coast.remove(current);
            if(current.getX() >= 0 && current.getX() < row && current.getY() >= 0 && current.getY() < column){
                mat[current.getX()][current.getY()] = true;
                coast.add(new Pair<>(current.getX()+1, current.getY()));
                coast.add(new Pair<>(current.getX()-1, current.getY()));
                coast.add(new Pair<>(current.getX(), current.getY()+1));
                coast.add(new Pair<>(current.getX(), current.getY()-1));
                dim--;
            }
        }
        return mat;
    }

    private String drawCloud(Students cloud, int seed){
        return drawTile(cloud, 0, false, 30, seed, Ansi.Color.WHITE, 5, 10);
    }

    private String drawIsland(Island island, int seed){
        // dim must be >= 36 (in the worst case)
        return drawTile(island.getStudents(), island.getNumTowers(), island.hasMotherNature(), 36*island.getNumIslands(), seed, Ansi.Color.GREEN, 5, 15);
    }

    private String drawTile(Students students, int num_towers, boolean has_mother_nature, int dim, int seed, Ansi.Color bg_color, int row, int column){
        boolean[][] mask = generateMaskTile(row, column, dim, seed);
        String[][] canvas = new String[mask.length][mask[0].length];
        int position = 0;

        // add students
        if(students != null){
            for(Map.Entry<Color, Integer> entry : students.entrySet()) {
                Color key = entry.getKey();
                int value = entry.getValue();
                while(value > 0){

                    // first available position
                    String s;
                    while(!mask[position/mask.length][position%mask[0].length]) position++;
                    if(value >= 5) {
                        value -= 5;
                        s = ansi().fg(Ansi.Color.valueOf(key.toString())).a(5).reset().toString();
                    } else {
                        value--;
                        s = ansi().fg(Ansi.Color.valueOf(key.toString())).a(1).reset().toString();
                    }
                    canvas[position/mask.length][position%canvas[0].length] = s;
                    position++;
                }
            }
        }

        // add the towers
        for(int i=0; i<num_towers; i++){
            String s;
            // first available position
            while(!mask[position/mask.length][position%mask[0].length]) position++;
            canvas[position/mask.length][position%canvas[0].length] = ansi().fg(Ansi.Color.BLACK).a("T").reset().toString();
            position++;
        }

        // add mother nature
        if(has_mother_nature){
            String s;
            // first available position
            while(!mask[position/mask.length][position%mask[0].length]) position++;
            canvas[position/mask.length][position%canvas[0].length] = ansi().fg(Ansi.Color.RED).a("M").reset().toString();
            position++;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0; i<canvas.length; i++){
            for(int j=0; j<canvas[i].length; j++){
                if(mask[i][j]){ // is it inside the tile mask?
                    if(canvas[i][j] == null) {
                        stringBuilder.append(ansi().bg(bg_color).a(" ").reset());
                    } else {
                        stringBuilder.append(ansi().bg(bg_color).a(canvas[i][j]).reset());
                    }
                } else {
                    stringBuilder.append(" ");
                }
            }
            stringBuilder.append(Constants.NEWLINE);
        }
        return stringBuilder.toString();
    }

}
