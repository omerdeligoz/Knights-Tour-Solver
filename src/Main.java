import java.text.SimpleDateFormat;
import java.io.IOException;
import java.util.logging.*;
import java.io.FileWriter;
import java.util.*;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static TreeSearch.Strategy strategy;
    public static Problem problem = new Problem();


    public static void main(String[] args) {
        // Configure the logging settings
        configureLogging();
        // Get user inputs for problem configuration
        getInputs();

        // Log the problem configuration details
        logger.info("Problem configuration:\n" +
                "                                                  Board size: " + problem.size + "\n" +
                "                                                  Initial position (" + problem.startX + ", " + problem.startY + ")\n" +
                "                                                  Strategy " + strategy + "\n" +
                "                                                  Time limit " + problem.timeLimit + " minutes.");

        // Create a TreeSearch instance with the problem configuration
        TreeSearch treeSearch = new TreeSearch(problem);
        // Record the start time of the search
        long startTime = System.currentTimeMillis();
        problem.startTime = startTime;
        try {
            // Attempt to solve the problem using the specified strategy
            treeSearch.solve(problem, strategy);
        } catch (OutOfMemoryError e) {
            // Log a warning if an OutOfMemoryError occurs
            logger.warning("Out of memory error occurred: " + e.getMessage());
        } catch (Exception e) {
            // Log a warning if any other exception occurs
            logger.warning("An error occurred: " + e.getMessage());
        } finally {
            // Record the end time of the search
            long endTime = System.currentTimeMillis();
            long timeSpent = endTime - startTime;
            long minutes = (timeSpent / 1000) / 60;
            long seconds = (timeSpent / 1000) % 60;
            long milliseconds = timeSpent % 1000;
            String formattedTime = String.format("%d.%02d.%03d", minutes, seconds, milliseconds);

            // Log the results of the search
            logger.info("Nodes Created  -> " + String.format("%,d", problem.createdNodes));
            logger.info("Nodes Expanded -> " + String.format("%,d", problem.expandedNodes));
            logger.info("Time spent     -> " + formattedTime);
        }
    }

    // Method to get user inputs for problem configuration
    public static void getInputs() {
        Scanner scanner = new Scanner(System.in);
        // Loop to get a valid board size from the user
        while (true) {
            System.out.print("Please enter the size of the board: ");
            if (scanner.hasNextInt()) {
                problem.size = scanner.nextInt();
                if (problem.size > 0) break;    // Ensure the board size is positive
            } else {
                scanner.next(); // Clear invalid input
            }
            System.out.println("\nInvalid input. Please enter a positive integer.\n");
        }

        // Loop to get a valid search strategy from the user
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
                scanner.next(); // Clear invalid input
            }
        }

        // Loop to get a valid time limit from the user
        while (true) {
            System.out.print("Please enter the time limit in minutes: ");
            if (scanner.hasNextInt()) {
                problem.timeLimit = scanner.nextInt();
                if (problem.timeLimit > 0) break;   // Ensure the time limit is positive
            } else {
                scanner.next(); // Clear invalid input
            }
            System.out.println("\nInvalid input. Please enter a positive integer.\n");
        }
    }

    // Method to configure logging settings
    public static void configureLogging() {
        try {
            // Get the root logger
            Logger rootLogger = LogManager.getLogManager().getLogger("");
            Level logLevel = Level.INFO;

            // Remove all existing handlers from the root logger
            for (Handler handler : rootLogger.getHandlers()) {
                rootLogger.removeHandler(handler);
            }

            // Set the log format
            String format = "%1$tF %1$tT %2$-20s %4$-7s: %5$s%6$s%n";
            System.setProperty("java.util.logging.SimpleFormatter.format", format);
            // Create a ConsoleHandler for console output
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(logLevel);
            consoleHandler.setFormatter(new SimpleFormatter());
            rootLogger.addHandler(consoleHandler);

            // Ensure the logs directory exists
            new java.io.File("logs").mkdirs();

            // Add a separator in the logs file for a new run
            String separator = "\n========== New Run: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " ==========\n";
            try (FileWriter writer = new FileWriter("logs/logs.log", true)) { // Append mode
                writer.write(separator);
            }
            // Create FileHandler
            FileHandler fullLogHandler = new FileHandler("logs/logs.log", true); // Append mode
            fullLogHandler.setLevel(logLevel);
            fullLogHandler.setFormatter(new SimpleFormatter());
            rootLogger.addHandler(fullLogHandler);

            // Set the root logger level
            rootLogger.setLevel(logLevel);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to initialize logging setup.");
        }
    }
}