package it.polimi.ingsw.view;

import it.polimi.ingsw.client.ClientSocket;
import it.polimi.ingsw.client.Command;
import it.polimi.ingsw.client.CommandHandler;
import it.polimi.ingsw.client.CommandType;
import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.*;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.ReducedLobby;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiColors;
import org.fusesource.jansi.AnsiConsole;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.fusesource.jansi.Ansi.ansi;

public class CLI {
    final protected Logger LOGGER = Logger.getLogger(getClass().getName());
    final protected ClientSocket client_socket;
    final protected PrintStream out;
    final protected Scanner read_stream;
    protected ViewContent view;
    protected String username;
    protected String schoolBoardPlayerUsername;
    protected String errorMessage;

    private final Pair<Integer, Integer> STD_CURSOR_POSITION = new Pair<>(48, 1);
    private final Pair<Integer, Integer> STD_USERNAME_POSITION = new Pair<>(2, 80);
    private final Pair<Integer, Integer> STD_PLAYERS_POSITION = new Pair<>(4, 80);
    private final Pair<Integer, Integer> STD_BOARD_POSITION = new Pair<>(30, 1);
    private final Pair<Integer, Integer> STD_CHARACTER_POSITION = new Pair<>(22, 50);
    private final Pair<Integer, Integer> STD_STATUS_POSITION = new Pair<>(10, 1);
    private final Pair<Integer, Integer> STD_CLOUDS_POSITION = new Pair<>(15, 50);
    private final Pair<Integer, Integer> STD_ISLANDS_POSITION = new Pair<>(15, 1);
    private final Pair<Integer, Integer> STD_ASSISTANTS_POSITION = new Pair<>(30, 50);
    private final Pair<Integer, Integer> STD_COINS_POSITION = new Pair<>(31, 33);

    private final Map<GameState, List<Command>> COMMAND_MAP = Map.of(
            GameState.PLAY_ASSISTANT, List.of(CommandHandler.get("assistant"), CommandHandler.get("pass")),
            GameState.CHOOSE_CLOUD, List.of(CommandHandler.get("cloud"), CommandHandler.get("pass")),
            GameState.MOVE_MOTHER_NATURE, List.of(CommandHandler.get("mm"), CommandHandler.get("pass"))
            // TODO: finish
    );

    public CLI(ClientSocket client_socket, PrintStream out, InputStream read_stream) {
        this.client_socket = client_socket;
        this.out = out;
        this.read_stream = new Scanner(new BufferedInputStream(read_stream));
    }
    public CLI(ClientSocket client_socket) {
        this(client_socket, System.out, System.in);
    }

    public void run(){
        AnsiConsole.systemInstall();

        String username;

        clearScreen();
        out.println(Constants.ERIANTYS);
        out.println(Constants.AUTHORS);
        out.println();

        out.println(ansi().fg(Ansi.Color.GREEN).a("LOGIN").reset());
        out.print("Username: ");

        username = read_stream.nextLine();
        while(!client_socket.login(username)) {
            // TODO: better error handling
            out.print(ansi().fgBrightRed().a("Username already taken").reset());
            out.print(ansi().cursorUpLine().eraseLine().a("Username: "));
            username = read_stream.nextLine();
        }
        out.println("Logged in");

        this.username = username;
        this.schoolBoardPlayerUsername = username; // sets own username for later displaying of board

        startRendering();

        while(true){
            getInput();
        }
    }

    private void getInput(){
        read_stream.reset();
        String input = read_stream.nextLine();
        this.errorMessage = CommandHandler.sendInput(input, client_socket, this);

        if(errorMessage != null) printClientError(); // for clientside errors
    }

    /**
     * Clears the screen.
     */
    protected void clearScreen(){
        // clear the console
        out.println(ansi().cursor(0, 0).eraseScreen());
        out.flush();
    }

    private void startRendering(){
        new Thread(() -> {
            while(true){
                synchronized (client_socket.mutex){
                    while(client_socket.getView() == null) {
                        try {
                            client_socket.mutex.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                this.view = client_socket.getView();
                // FIXME: synchronized for view usage?
                // if there's an error message, it overwrites the current one
                if(view.getErrorMessage() != null)
                    this.errorMessage = view.getErrorMessage();

                LOGGER.log(Level.FINE, "Rendered view: {0}", view);

                clearScreen();

                if(view.getCurrentHandler() == null){ // we are in the menu
                    printMenu();
                } else{
                    switch (view.getCurrentHandler()) {
                        case LOBBY -> // we are in the lobby
                                printLobby();
                        case GAME -> { // we are in the game
                            printGame();
                        }
                    }
                }

                synchronized (client_socket.mutex){
                    client_socket.setView(null);
                    client_socket.mutex.notifyAll();
                }
            }
        }).start();
    }

    protected void printMenu() {
        out.println(Constants.MENU);
        ArrayList<ReducedLobby> reduced_lobbies = view.getLobbies();

        if(!reduced_lobbies.isEmpty())
            out.println(ansi().bold().a("Available lobbies:").reset());
        else out.println(ansi().fgRed().a("No lobbies available!").reset());

        for(ReducedLobby reduced_lobby : reduced_lobbies){
            out.printf("Lobby %d: Slots: %d/%d\n", reduced_lobby.getID(), reduced_lobby.getNumPlayer(), reduced_lobby.getMaxPlayers());
        }
        out.println();
        out.println("create <max players> - create a new lobby");
        out.println("join <lobby id> - join the lobby with ID <lobby id>");

        out.print(ansi().cursorDown(2).a("> "));

        if(errorMessage != null) printErrorRelative();
    }

    protected void printLobby(){
        out.println(Constants.LOBBY);

        LobbyHandler lobby_handler = view.getLobbyHandler();
        ArrayList<LobbyPlayer> players = lobby_handler.getPlayers();

        // prints the lobby id
        out.println(ansi().bg(Ansi.Color.WHITE).fg(Ansi.Color.BLACK).a("Lobby ID: " + lobby_handler.ID).reset());
        out.println();
        out.println();

        // print the list of the players in the lobby
        out.printf("Players: %d/%d\n", players.size(), lobby_handler.getMaxPlayers());
        for(LobbyPlayer lobby_player : players){
            Integer wizard = lobby_player.getWizard();
            out.printf("%s (Wizard: %s)\n", lobby_player.getUsername().equals(username) ?
                    ansi().bg(Ansi.Color.WHITE).fg(Ansi.Color.BLACK).a(username).reset() : lobby_player.getUsername(),
                    wizard
            );
        }
        out.println();
        out.println("start <expert mode> - start the game");
        out.println("wizard <id> - choose wizard");

        out.print(ansi().cursorDownLine(2).a("> "));

        if(errorMessage != null) printErrorRelative();
    }

    protected void printGame(){
        print(ansi().a(Constants.ERIANTYS), 1, 1);

        Game model = view.getGameHandler().getModel();

        // Banner length is 63
        print(ansi().bg(Ansi.Color.WHITE).fg(Ansi.Color.BLACK).a(username).reset(), STD_USERNAME_POSITION);
        print(drawPlayers(), STD_PLAYERS_POSITION);
        print(drawState(), STD_STATUS_POSITION);
        print(drawIslands(), STD_ISLANDS_POSITION);
        print(drawClouds(), STD_CLOUDS_POSITION);
        print(drawAssistants(), STD_ASSISTANTS_POSITION);
        print(drawBoard(schoolBoardPlayerUsername), STD_BOARD_POSITION);

        if(model.getExpertMode()) {
            print(drawCharacters(), STD_CHARACTER_POSITION);
        }

        print(ansi().eraseLine().a("> ").reset(), STD_CURSOR_POSITION);

        // print server errors
        if(errorMessage != null) printErrorRelative();
    }

    private void printErrorRelative(){
        out.print(ansi().cursorDownLine(2).eraseLine());
        out.print(ansi().fgBrightRed().a("ERROR: " + errorMessage).reset());
        out.print(ansi().cursorUpLine(2).cursorRight(2).eraseLine());
    }

    private void printClientError(){
        out.print(ansi().cursorDownLine(1).eraseLine());
        out.print(ansi().fgBrightRed().a("ERROR: " + errorMessage).reset());
        out.print(ansi().cursorUpLine(2).cursorRight(2).eraseLine());
    }

    protected String drawPlayers(){
        StringBuilder playersStr = new StringBuilder();

        Game model = view.getGameHandler().getModel();

        playersStr.append(ansi().bold().a("PLAYERS").reset()).append(Constants.NEWLINE);

        int count = 0;
        ArrayList<Player> players = model.getPlayers();
        for(Player player : players) {
            playersStr.append(count).append(": ").append(player.getUsername()).append(Constants.NEWLINE);

            count++;
        }

        return playersStr.toString();
    }

    protected String drawState(){
        StringBuilder stateText = new StringBuilder();

        GameHandler gameHandler = view.getGameHandler();
        Game model = gameHandler.getModel();

        if(model.getCurrentPlayer().getUsername().equals(username))
            stateText.append(ansi().bold().fg(Ansi.Color.GREEN).a("IT'S YOUR TURN!").reset());
        else
            stateText.append(ansi()
                    .bold()
                    .a("TURN: ")
                    .reset()
                    .a(model.getCurrentPlayer().getUsername())
            );
        stateText.append(Constants.NEWLINE);

        String instruction = "null";
        String availableCommands = "no commands :(";

        switch (gameHandler.getCurrentState()){
            // TODO:
            case PLAY_ASSISTANT -> {
                instruction = "Play an assistant: ";
                availableCommands = "assistant <number>";
            }
            case CHOOSE_CLOUD -> {
                instruction = "Choose a cloud: ";
                availableCommands = "cloud <number>";
            }
            case MOVE_MOTHER_NATURE -> {
                instruction = "Move mother nature: ";
                availableCommands = "mm <island>";
            }
            case MOVE_STUDENT -> {
                instruction = "Move a student (" + gameHandler.getStudentMoves() + " left): ";
                availableCommands = "ms <color>" +
                                    Constants.NEWLINE +
                                    "ms <color> <island>";
            }
            case ACTIVATE_CHARACTER -> {
                instruction = "Activate a character: ";
                availableCommands = ""; // TODO
            }
            case FINISHED -> {
                instruction = "Finished :";
            }
        }

        stateText.append(ansi().bold());
        stateText.append(instruction).append(ansi().reset());
        if(gameHandler.isActionCompleted())
            stateText.append(ansi().fgBrightGreen().a("DONE!").reset());
        else
            stateText.append(ansi().fgBrightYellow().a("IN PROGRESS...").reset());
        stateText.append(Constants.NEWLINE);
        stateText.append(availableCommands);

        return stateText.toString();
    }

    private String drawClouds(){
        StringBuilder cloudsText = new StringBuilder();

        Game model = view.getGameHandler().getModel();

        cloudsText.append(ansi().bold().a("CLOUDS").reset()).append(Constants.NEWLINE);

        int count = 0;
        for(Students cloud : model.getClouds()){
            if(cloud.count() == 0)
                cloudsText.append(ansi().bgBrightRed().a("Cloud " + count + ": -").reset());
            else {
                cloudsText.append("Cloud ").append(count).append(": ");

                for(Color key : cloud.keySet()){
                    if(cloud.get(key) > 0)
                        cloudsText.append(ansi().fgBright(Ansi.Color.valueOf(key.toString())).a(cloud.get(key) + " ").reset());
                }
            }

            cloudsText.append(Constants.NEWLINE);
            count++;
        }

        return cloudsText.toString();
    }

    private String drawIslands(){
        StringBuilder islandStr = new StringBuilder();

        Game model = view.getGameHandler().getModel();

        islandStr.append(ansi().bold().a("ISLANDS").reset()).append(Constants.NEWLINE);

        for(int i=0; i<model.getIslands().size(); i++){
            Island island = model.getIslands().get(i);
            Students islandStudents = island.getStudents();

            islandStr.append(i).append(" (").append(island.getNumIslands()).append("):\t");
            if(island.hasMotherNature())
                islandStr.append(ansi().bgBrightYellow().fg(Ansi.Color.BLACK).a("M").reset().a(" "));
            else islandStr.append("  ");
            if(island.getNoEntryTiles() > 0)
                islandStr.append(ansi().bg(Ansi.Color.RED).fg(Ansi.Color.BLACK).a(island.getNoEntryTiles()).reset());
            else islandStr.append(" ");
            islandStr.append("  ");

            for(Color studentColor : island.getStudents().keySet()){
                if(islandStudents.get(studentColor) > 0)
                    islandStr.append(ansi().fgBright(Ansi.Color.valueOf(studentColor.toString())).a(islandStudents.get(studentColor) + " ").reset());
            }
            islandStr.append(" ");
            islandStr.append(island.getOwner() == null ?
                    "" :
                    island.getOwner().getUsername() + " - Towers: " + island.getNumTowers()
            );
            islandStr.append(Constants.NEWLINE);
        }

        return islandStr.toString();
    }

    private String drawBoard(String username){
        StringBuilder boardStr = new StringBuilder();

        Game model = view.getGameHandler().getModel();

        Player player = model.usernameToPlayer(username);

        boardStr.append(ansi().bold().a("SCHOOLBOARD").reset());

        // displays the username of the owner if it's not the user's
        if(!schoolBoardPlayerUsername.equals(username))
            boardStr.append(" (").append(schoolBoardPlayerUsername).append(")");

        boardStr.append(Constants.NEWLINE);

        Students entranceStudents = player.getSchoolBoard().getEntranceStudents();
        Students diningStudents = player.getSchoolBoard().getDiningStudents();
        int numTowers = player.getSchoolBoard().getNumTowers();
        Professors professors = player.getProfessors();

        boardStr.append("Towers: ").append(numTowers).append(Constants.NEWLINE);
        boardStr.append("Entrance: ");
        for(Color studentColor : entranceStudents.keySet()){
            if(entranceStudents.get(studentColor) > 0)
                boardStr.append(ansi().fgBright(Ansi.Color.valueOf(studentColor.toString())).a(entranceStudents.get(studentColor) + " ").reset());
        }
        boardStr.append(Constants.NEWLINE).append(Constants.NEWLINE);

        boardStr.append("Dining room:").append(Constants.NEWLINE).append(Constants.NEWLINE);
        for(Color studentColor : diningStudents.keySet()){
            boardStr.append(ansi().bg(Ansi.Color.valueOf(studentColor.toString())).fg(Ansi.Color.BLACK));

            int numOfStudents = diningStudents.get(studentColor);

            for(int i = 0; i< Game.MAX_DINING_STUDENTS; i++){
                if(numOfStudents > 0)
                    boardStr.append("S");
                else if(i > 0 && (i - 2) % 3 == 0) {
                    boardStr.append("C");
                }else boardStr.append("_");
                boardStr.append("   ");

                numOfStudents--;
            }

            if(professors.contains(studentColor))
                boardStr.append(" P");
            else boardStr.append("  ");

            boardStr.append(ansi().a(Constants.NEWLINE + Constants.NEWLINE).reset());
        }

        if(model.getExpertMode())
            boardStr.append(ansi().cursor(STD_COINS_POSITION.getFirst(), STD_COINS_POSITION.getSecond()).a(drawCoins(player)));

        return boardStr.toString();
    }

    private String drawAssistants(){
        StringBuilder assistantStr = new StringBuilder();

        Game model = view.getGameHandler().getModel();

        ArrayList<Assistant> assistants = model.usernameToPlayer(username).getPlayerHand();
        Assistant currentAssistant = model.usernameToPlayer(username).getCurrentAssistant();
        Map<String, Assistant> playedAssistantsMap = model.getPlayers().stream()
                .filter(player -> player.getCurrentAssistant() != null)
                .collect(Collectors.toMap(LobbyPlayer::getUsername, Player::getCurrentAssistant));

        assistantStr.append(ansi().bold().a("ASSISTANTS").reset()).append(Constants.NEWLINE);
        int count = 0;
        // iterating on all assistants
        for(Assistant assistant : Assistant.getAssistants(1)){
            // current assistant is green
            if(assistant.equals(currentAssistant))
                assistantStr.append(ansi().bgBrightGreen().fgBlack());
                // assistants played by other players are yellow
            else if(playedAssistantsMap.containsValue(assistant))
                assistantStr.append(ansi().bgBrightYellow().fgBlack());
                // already played assistants are red
            else if(!assistants.contains(assistant) && !assistant.equals(currentAssistant))
                assistantStr.append(ansi().bgRed().fgBrightDefault());

            // if the player has this assistant, the index is displayed (otherwise -)
            if(assistants.contains(assistant)) {
                assistantStr.append(count).append(": ");
                count++;
            }else assistantStr.append("-: ");

            assistantStr.append("P ").append(assistant.getPower()).append(" - ").append("M ").append(assistant.getSteps());
            assistantStr.append(ansi().reset());
            if(playedAssistantsMap.containsValue(assistant)) {
                List<String> usernames = playedAssistantsMap.keySet().stream()
                        .filter(key -> playedAssistantsMap.get(key).equals(assistant))
                        .toList();
                assistantStr.append(" ").append(usernames);
            }
            assistantStr.append(Constants.NEWLINE);
        }

        return assistantStr.toString();
    }

    private String drawCoins(Player player){
        StringBuilder coinsStr = new StringBuilder();

        int coins = player.getCoins();

        coinsStr.append(ansi().bold().a("COINS: ").reset());
        coinsStr.append(ansi().fgBrightYellow().a(coins));

        return coinsStr.toString();
    }

    private String drawCharacters(){
        StringBuilder charStr = new StringBuilder();

        Game model = view.getGameHandler().getModel();

        ArrayList<Character> characters = model.getCharacters();
        charStr.append(ansi().bold().a("CHARACTERS").reset()).append(Constants.NEWLINE);

        int count = 0;
        for(Character character : characters){
            if(character.equals(view.getGameHandler().getSelectedCharacter()))
                charStr.append(ansi().bgBrightGreen().fgBlack());
            charStr.append(count).append(" - ").append(character.getClass().getSimpleName()).append(" - Cost: ").append(character.getCost()).append(" ");
            charStr.append(ansi().reset());

            // print card's specific students
            // FIXME: is null check useful?
            if(character.getStudents() != null && character.getStudents().count() > 0){
                for(Color studentColor : character.getStudents().keySet()) {
                    int numOfStudents = character.getStudents().get(studentColor);

                    if (numOfStudents > 0)
                        charStr.append(ansi().fg(Ansi.Color.valueOf(studentColor.toString())).a(numOfStudents).reset().a(" "));
                }
            }

            // print card's specific noEntryTiles
            if(character.getNoEntryTiles() > 0)
                charStr.append(ansi().bg(Ansi.Color.RED).fg(Ansi.Color.BLACK).a(character.getNoEntryTiles()).reset());

            charStr.append(Constants.NEWLINE);
            count++;
        }

        return charStr.toString();
    }

    public void printHelpScreen(){
        clearScreen();

        StringBuilder helpText = new StringBuilder();
        List<Command> commands = CommandHandler.getCommands();

        helpText.append(ansi().bold().a(Constants.HELP).reset());
        helpText.append(Constants.NEWLINE).append(Constants.NEWLINE);

        for(CommandType commandType : CommandType.values()){
            helpText.append(ansi().bold().a(commandType.toString()).reset()).append(Constants.NEWLINE);
            commands.stream()
                    .filter(command -> command.getCommandType().equals(commandType))
                    .forEach(command -> {
                        helpText.append(command.getName()).append(" ");
                        command.getArguments().forEach(argument -> helpText.append("[" + argument + "] "));
                        helpText.append(ansi().cursorToColumn(42).a(command.getDescription()));
                        helpText.append(Constants.NEWLINE);
                    });
            helpText.append(Constants.NEWLINE);
            helpText.append(Constants.NEWLINE);
        }

        print(helpText.toString(), 1, 1);
    }

    public void cleanErrorMessage(){
        this.errorMessage = null;
        out.print(ansi().cursorDownLine(1).eraseLine());
        out.print(ansi().cursorUpLine(2).cursorRight(2).eraseLine());
    }

    public void printRequestedBoard(){
        int rowFrom = STD_BOARD_POSITION.getFirst();
        int columnFrom = STD_BOARD_POSITION.getSecond();
        int rowTo = rowFrom + 14;
        int columnTo = columnFrom + 35;

        eraseBox(rowFrom, columnFrom, rowTo, columnTo);
        print(drawBoard(schoolBoardPlayerUsername), STD_BOARD_POSITION);
        print(ansi().eraseLine().a("> ").reset(), STD_CURSOR_POSITION);
    }

    public void refresh(){
        clearScreen();

        if(view.getCurrentHandler() != null){
            switch (view.getCurrentHandler()){
                case LOBBY -> printLobby();
                case GAME -> printGame();
            }
        }else printMenu();
    }

    public void setSchoolboardPlayerUsername(String schoolboardPlayerID) {
        this.schoolBoardPlayerUsername = schoolboardPlayerID;
    }

    public ViewContent getView() {
        return view;
    }

    // ---------------------------- PRINTING HELPER ---------------------------- //

    protected void print(Ansi s, int row, int column) {
        print(s.toString(), row, column);
    }

    protected void print(Ansi s, Pair<Integer, Integer> coordinates){
        print(s, coordinates.getFirst(), coordinates.getSecond());
    }

    protected void print(String s, int row, int column) {
        out.print(ansi().cursor(row, column).a(
                        s.replaceAll(
                                Constants.NEWLINE,
                                ansi().a(Constants.NEWLINE).cursorRight(column - 1).toString()
                        )
                )
        );
        out.flush();
    }

    protected void print(String s, Pair<Integer, Integer> coordinates){
        print(s, coordinates.getFirst(), coordinates.getSecond());
    }

    protected void drawBox(Ansi.Color color, int rowFrom, int columnFrom, int rowTo, int columnTo){
        out.print(ansi().bg(color));

        for(int i=rowFrom; i<rowTo; i++) {
            out.print(ansi().cursor(i, columnFrom));
            for (int k = columnFrom; k < columnTo; k++)
                out.print(ansi().a(" "));
        }

        out.print(ansi().reset());
    }

    protected void eraseBox(int rowFrom, int columnFrom, int rowTo, int columnTo){
        for(int i=rowFrom; i<rowTo; i++) {
            out.print(ansi().cursor(i, columnFrom));
            for (int k = columnFrom; k < columnTo; k++)
                out.print(ansi().reset().a(" "));
        }
    }
}
