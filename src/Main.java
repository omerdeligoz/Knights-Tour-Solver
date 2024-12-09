import java.text.SimpleDateFormat;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.*;
import java.io.FileWriter;
import java.io.File;
import java.util.*;


public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());


    public static long expandedNodes = 0;
    public static long createdNodes = 1;

    public static int startX = 1;
    public static int startY = 1;

    public static int SIZE;
    public static TreeSearch.Strategy strategy;
    public static int timeLimit;


    public static void main(String[] args) {
        configureLogging();
        if (args.length == 0) {
            getInputs();
        } else {
            // Assume that args[0], args[1], args[2] are correctly given
            SIZE = Integer.parseInt(args[0]);
            timeLimit = Integer.parseInt(args[2]);

            // Parse search strategy
            int choice = Integer.parseInt(args[1]);
            strategy = switch (choice) {
                case 1 -> TreeSearch.Strategy.BFS;
                case 2 -> TreeSearch.Strategy.DFS;
                case 3 -> TreeSearch.Strategy.DFS_H1B;
                case 4 -> TreeSearch.Strategy.DFS_H2;
                default -> throw new IllegalArgumentException("Invalid search strategy: " + args[1]);
            };
        }
        logger.info("Starting search with board size " + SIZE + ", initial position (" + startX + ", " + startY + "), and strategy " + strategy + " with time limit " + timeLimit + " minutes.");
        TreeSearch treeSearch = new TreeSearch(strategy);
        long startTime = System.currentTimeMillis();
        try {
            treeSearch.solve(strategy, startX, startY, startTime);
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
            logger.info("Nodes Created: " + String.format("%,d", createdNodes));
            logger.info("Time spent: " + formattedTime);
        }
    }


    public static void getInputs() {
        Scanner scanner = new Scanner(System.in);
        // Loop to get valid board size
        while (true) {
            System.out.print("Please enter the size of the board: ");
            if (scanner.hasNextInt()) {
                SIZE = scanner.nextInt();
                if (SIZE > 0) break;
            } else {
                scanner.next(); // clear invalid input
            }
            System.out.println("\nInvalid input. Please enter a positive integer.\n");
        }

        // Loop to get valid search strategy
        while (strategy == null) {
            System.out.println("1. Breadth First Search");
            System.out.println("2. Depth First Search");
            System.out.println("3. Depth First Search with Node Selection Heuristic h1b");
            System.out.println("4. Depth First Search with Node Selection Heuristic h2");
            System.out.print("Please enter the search strategy: ");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1 -> strategy = TreeSearch.Strategy.BFS;
                    case 2 -> strategy = TreeSearch.Strategy.DFS;
                    case 3 -> strategy = TreeSearch.Strategy.DFS_H1B;
                    case 4 -> strategy = TreeSearch.Strategy.DFS_H2;
                    default -> System.out.println("\nInvalid input. Please enter a valid strategy (1, 2, 3 or 4).\n");
                }
            } else {
                scanner.next(); // clear invalid input
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
            System.out.println("\nInvalid input. Please enter a positive integer.\n");
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
            String format = "%1$tF %1$tT %2$-20s %4$-7s: %5$s%6$s%n";
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

            rootLogger.setLevel(logLevel);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to initialize logging setup.");
        }
    }
}