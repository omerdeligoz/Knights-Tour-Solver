import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.*;

public class Main {
    public enum SearchStrategy {
        BFS, DFS, DFS_H1B, DFS_H2
    }

    // possible moves for a knight in chess
    public static final int[][] MOVES = new int[][]{
//            {2, -1},
//            {2, 1},      // THIS ORDER CAN FIND A SOLUTION FOR ALL N
//            {1, 2},
//            {-1, 2},
//            {-2, 1},
//            {-2, -1},
//            {-1, -2},
//            {1, -2},
//
            {1, -2},    //THIS ORDER CAN NOT FIND A SOLUTION FOR N = 41,52,66,74,79
            {2, -1},
            {2, 1},
            {1, 2},
            {-1, 2},
            {-2, 1},
            {-2, -1},
            {-1, -2}

    };
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    public static int[][] board;    // indices are [size - i][j + 1]
    public static int SIZE;
    public static int timeLimit;
    public static int expandedNodes = 0;
    public static int startX = 1;
    public static int startY = 1;

    public static void configureLogging() {
        try {
            // Get the root logger
            Logger rootLogger = LogManager.getLogManager().getLogger("");
            Level logLevel = Level.INFO; // Change to Level.INFO for less detailed logs
            // Remove default handlers (optional, to avoid duplicate logs)
            for (Handler handler : rootLogger.getHandlers()) {
                rootLogger.removeHandler(handler);
            }

            // Create a ConsoleHandler for console output
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(logLevel);
            consoleHandler.setFormatter(new SimpleFormatter());
            rootLogger.addHandler(consoleHandler);


            // Ensure the logs directory exists
            new java.io.File("logs").mkdirs();

            // Add a separator in the full logs file for a new run
            String separator = "\n========== New Run: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " ==========\n";
            try (FileWriter writer = new FileWriter("logs/full_logs.log", true)) { // Append mode
                writer.write(separator);
            }
            // Create another FileHandler for appending to full_logs.log
            FileHandler fullLogHandler = new FileHandler("logs/full_logs.log", true); // Append mode
            fullLogHandler.setLevel(logLevel);
            fullLogHandler.setFormatter(new SimpleFormatter());
            rootLogger.addHandler(fullLogHandler);

//            // Create a timestamp for the log filename
//            String timestamp = new SimpleDateFormat("dd.MM.yyyy_HH.mm.ss").format(new Date());
//            // Create a FileHandler for file logging
//            String logFileName = "logs/log_" + timestamp + ".log";
//            FileHandler fileHandler = new FileHandler(logFileName, 5000000, 1, false); // Limit: 5 MB, 1 file, no append
//            fileHandler.setLevel(logLevel);
//            fileHandler.setFormatter(new SimpleFormatter());
//            rootLogger.addHandler(fileHandler);

            rootLogger.setLevel(logLevel);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to initialize logging setup.");
        }
    }

    public static void main(String[] args) {
        configureLogging();
        Scanner scanner = new Scanner(System.in);
        SearchStrategy strategyChoice = null;

        // Loop to get valid board size
        while (true) {
            System.out.print("Please enter the size of the board: ");
            if (scanner.hasNextInt()) {
                SIZE = scanner.nextInt();
                if (SIZE > 0) break;
            } else {
                scanner.next(); // clear invalid input
            }
            System.out.println("Invalid input. Please enter a positive integer.");
        }

        // Loop to get valid search strategy
        while (strategyChoice == null) {
            System.out.println("a. Breadth First Search");
            System.out.println("b. Depth First Search");
            System.out.println("c. Depth First Search with Node Selection Heuristic h1b");
            System.out.println("d. Depth First Search with Node Selection Heuristic h2");
            System.out.print("Please enter the search strategy: ");
            char choice = scanner.next().toLowerCase().charAt(0);

            switch (choice) {
                case 'a' -> strategyChoice = SearchStrategy.BFS;
                case 'b' -> strategyChoice = SearchStrategy.DFS;
                case 'c' -> strategyChoice = SearchStrategy.DFS_H1B;
                case 'd' -> strategyChoice = SearchStrategy.DFS_H2;
                default -> System.out.println("Invalid input. Please enter a valid strategy (a, b, c, or d).");
            }

        }

        // Loop to get valid time limit
        while (true) {
            System.out.print("Please enter the time limit in minutes: ");
            if (scanner.hasNextInt()) {
                timeLimit = scanner.nextInt();
                if (timeLimit > 0) break;
            } else {
                scanner.next(); // clear invalid input
            }
            System.out.println("Invalid input. Please enter a positive integer.");
        }

        board = new int[SIZE][SIZE];
        logger.info("Starting search with board size " + SIZE + ", initial position (" + startX + ", " + startY + "), and strategy " + strategyChoice + " with time limit " + timeLimit + " minutes.");
        treeSearch(strategyChoice);
    }

    public static void treeSearch(SearchStrategy strategyChoice) {
        Strategy strategy = switch (strategyChoice) {
            case BFS -> new BreadthFirstSearch();
            case DFS -> new DepthFirstSearch(DepthFirstSearch.HeuristicType.NO);
            case DFS_H1B -> new DepthFirstSearch(DepthFirstSearch.HeuristicType.H1B);
            case DFS_H2 -> new DepthFirstSearch(DepthFirstSearch.HeuristicType.H2);
        };

        // Start the search from the root node
        Node root = new Node(startX, startY, null);

        // Create a thread for searching
        Thread searchThread = new Thread(() -> strategy.solve(root));

        // Start the search thread
        searchThread.start();
        try {
            // Wait for the specified time limit
            searchThread.join(timeLimit * 60 * 1000L); // Convert minutes to milliseconds

            // If thread is still alive after timeout, interrupt it
            if (searchThread.isAlive()) {
                searchThread.interrupt();
                logger.warning("Timeout: Search exceeded the time limit of " + timeLimit + " minutes.");
            }
        } catch (InterruptedException e) {
            logger.warning("Search was interrupted.");
        }
    }

    public static void printPath(Stack<Node> path) {
        File file = new File("C:\\Users\\Cyber\\PycharmProjects\\PythonProject\\input.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Node node : path) {
                writer.write(node.x + "," + node.y);
                writer.newLine();
            }
        } catch (IOException e) {
            logger.warning("Failed to write path to file." + e);
        }
    }
}