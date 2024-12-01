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
            // THIS ORDER CAN FIND A SOLUTION FOR ALL N
            //{2, -1}, {2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2},

            //THIS ORDER CAN NOT FIND A SOLUTION FOR N = 41,52,66,74,79
            {1, -2}, {2, -1}, {2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}

    };
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    public static int[][] board;    // indices are [size - i][j + 1]
    public static int SIZE;
    public static int timeLimit;
    public static int expandedNodes = 0;
    public static int startX = 1;
    public static int startY = 1;

    public static void main(String[] args) {
//        args = new String[]{"8", "d", "5"}; // For testing purposes

        configureLogging();
        SearchStrategy strategyChoice;
        // Assume that args[0], args[1], args[2] are correctly given
        // Parse board size
        SIZE = Integer.parseInt(args[0]);

        // Parse search strategy
        char choice = args[1].toLowerCase().charAt(0);
        strategyChoice = switch (choice) {
            case 'a' -> SearchStrategy.BFS;
            case 'b' -> SearchStrategy.DFS;
            case 'c' -> SearchStrategy.DFS_H1B;
            case 'd' -> SearchStrategy.DFS_H2;
            default ->
                // Assuming correct input, this block should not be reached
                    throw new IllegalArgumentException("Invalid search strategy: " + args[1]);
        };

        // Parse time limit
        timeLimit = Integer.parseInt(args[2]);

        board = new int[SIZE][SIZE];
        logger.info("Starting search with board size " + SIZE + ", initial position (" + startX + ", " + startY + "), and strategy " + strategyChoice + " with time limit " + timeLimit + " minutes.");
        treeSearch(strategyChoice);
    }

    /*
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
    */
    public static void treeSearch(SearchStrategy strategyChoice) {
        Strategy strategy = switch (strategyChoice) {
            case BFS -> new BreadthFirstSearch();
            case DFS -> new DepthFirstSearch(DepthFirstSearch.HeuristicType.NO);
            case DFS_H1B -> new DepthFirstSearch(DepthFirstSearch.HeuristicType.H1B);
            case DFS_H2 -> new DepthFirstSearch(DepthFirstSearch.HeuristicType.H2);
        };
        long startTime = System.currentTimeMillis();
        try {
            // Start the search from the root node
            Node root = new Node(startX, startY, null);
            strategy.solve(root, startTime);
        } catch (OutOfMemoryError e) {
            logger.warning("Out of memory error occurred: " + e.getMessage());
        } catch (Exception e) {
            logger.warning("An error occurred: " + e.getMessage());
        } finally {
            long endTime = System.currentTimeMillis();
            long timeSpent = endTime - startTime;
            long minutes = (timeSpent / 1000) / 60;
            long seconds = (timeSpent / 1000) % 60;
            long milliseconds = timeSpent % 1000;
            String formattedTime = String.format("%d.%02d.%03d", minutes, seconds, milliseconds);
            logger.info("Nodes Expanded: " + String.format("%,d", expandedNodes));
            logger.info("Time spent: " + formattedTime);
        }
    }

    public static void printPath(Stack<Node> path) {
        File file = new File("path.txt");
        List<String> pathList = new ArrayList<>();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Node node : path) {
                pathList.add("(" + node.x + "," + node.y + ")");
            }
            writer.write(pathList.toString());
            logger.info("Path found: " + pathList);
        } catch (IOException e) {
            logger.warning("Failed to write path to file." + e);
        }
    }

    public static void configureLogging() {
        try {
            // Get the root logger
            Logger rootLogger = LogManager.getLogManager().getLogger("");
            Level logLevel = Level.INFO; // Change to Level.INFO for less detailed logs
            // Remove default handlers (optional, to avoid duplicate logs)
            for (Handler handler : rootLogger.getHandlers()) {
                rootLogger.removeHandler(handler);
            }
            String format = "%1$tF %1$tT %2$-25s %4$-7s: %5$s%6$s%n";
            System.setProperty("java.util.logging.SimpleFormatter.format", format);
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
}