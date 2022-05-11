package it.polimi.ingsw.utils;

public class Constants {
    // match constants
    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 3;
    public static final String ANSI_RESET = "\033[0m";

    // Style
    public static final String ANSI_BOLD = "\033[1m";
    public static final String ANSI_ITALIC = "\033[3m";
    public static final String ANSI_UNDERLINE = "\033[4m";

    // Color
    public static final String ANSI_BLACK = "\033[0;30m";   // BLACK
    public static final String ANSI_RED = "\033[0;31m";     // RED
    public static final String ANSI_GREEN = "\033[0;32m";   // GREEN
    public static final String ANSI_YELLOW = "\033[0;33m";  // YELLOW
    public static final String ANSI_BLUE = "\033[0;34m";    // BLUE
    public static final String ANSI_PURPLE = "\033[0;35m";  // PURPLE
    public static final String ANSI_CYAN = "\033[0;36m";    // CYAN
    public static final String ANSI_WHITE = "\033[0;37m";   // WHITE

    // Background
    public static final String ANSI_BLACK_BACKGROUND = "\033[40m";  // BLACK
    public static final String ANSI_RED_BACKGROUND = "\033[41m";    // RED
    public static final String ANSI_GREEN_BACKGROUND = "\033[42m";  // GREEN
    public static final String ANSI_YELLOW_BACKGROUND = "\033[43m"; // YELLOW
    public static final String ANSI_BLUE_BACKGROUND = "\033[44m";   // BLUE
    public static final String ANSI_PURPLE_BACKGROUND = "\033[45m"; // PURPLE
    public static final String ANSI_CYAN_BACKGROUND = "\033[46m";   // CYAN
    public static final String ANSI_WHITE_BACKGROUND = "\033[47m";  // WHITE

    public static final String NEWLINE = String.format("%n");


    public static String ERIANTYS = """

            ███████╗██████╗ ██╗ █████╗ ███╗   ██╗████████╗██╗   ██╗███████╗
            ██╔════╝██╔══██╗██║██╔══██╗████╗  ██║╚══██╔══╝╚██╗ ██╔╝██╔════╝
            █████╗  ██████╔╝██║███████║██╔██╗ ██║   ██║    ╚████╔╝ ███████╗
            ██╔══╝  ██╔══██╗██║██╔══██║██║╚██╗██║   ██║     ╚██╔╝  ╚════██║
            ███████╗██║  ██║██║██║  ██║██║ ╚████║   ██║      ██║   ███████║
            ╚══════╝╚═╝  ╚═╝╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝   ╚══════╝
                                                                          \s
            """;

    public static String MENU = """

            ███╗   ███╗███████╗███╗   ██╗██╗   ██╗
            ████╗ ████║██╔════╝████╗  ██║██║   ██║
            ██╔████╔██║█████╗  ██╔██╗ ██║██║   ██║
            ██║╚██╔╝██║██╔══╝  ██║╚██╗██║██║   ██║
            ██║ ╚═╝ ██║███████╗██║ ╚████║╚██████╔╝
            ╚═╝     ╚═╝╚══════╝╚═╝  ╚═══╝ ╚═════╝\s
                                                 \s
            """;

    public static String LOBBY = """

            ██╗      ██████╗ ██████╗ ██████╗ ██╗   ██╗
            ██║     ██╔═══██╗██╔══██╗██╔══██╗╚██╗ ██╔╝
            ██║     ██║   ██║██████╔╝██████╔╝ ╚████╔╝\s
            ██║     ██║   ██║██╔══██╗██╔══██╗  ╚██╔╝ \s
            ███████╗╚██████╔╝██████╔╝██████╔╝   ██║  \s
            ╚══════╝ ╚═════╝ ╚═════╝ ╚═════╝    ╚═╝  \s
                                                     \s
            """;

    public static final String AUTHORS =
            "\nby "
                    + ANSI_PURPLE
                    + "Arturo Benedetti"
                    + ANSI_RESET
                    + ", "
                    + ANSI_RED
                    + "Lorenzo Rossi"
                    + ANSI_RESET
                    + ", "
                    + ANSI_YELLOW
                    + "Luca Romanò"
                    + ANSI_RESET;

    // Server info
    public static final String SERVER_ADDR = "127.0.0.1";

    public static final int SERVER_PORT = 42069; // nice

    // Path for saves
    public static final String PATH_SAVES = "src/main/SavedGames";
}
