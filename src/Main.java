import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

public class Main {
    // possible moves for a knight in chess
    public static final int[][] MOVES = new int[][]{
            {2, -1},
            {2, 1},      // THIS ORDER CAN FIND A SOLUTION FOR ALL N
            {1, 2},
            {-1, 2},
            {-2, 1},
            {-2, -1},
            {-1, -2},
            {1, -2},

//            {1, -2},    //THIS ORDER CAN NOT FIND A SOLUTION FOR N = 41,52,66,74,79
//            {2, -1},
//            {2, 1},
//            {1, 2},
//            {-1, 2},
//            {-2, 1},
//            {-2, -1},
//            {-1, -2}

    };
    public static int[][] board;    // indices are [size - i][j + 1]
    public static int SIZE;
    public static int timeLimit;
    public static int expandedNodes = 0;


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        char strategyChoice;
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
        while (true) {
            System.out.println("a. Breadth First Search");
            System.out.println("b. Depth First Search");
            System.out.println("c. Depth First Search with Node Selection Heuristic h1b");
            System.out.println("d. Depth First Search with Node Selection Heuristic h2");
            System.out.print("Please enter the search strategy: ");
            strategyChoice = scanner.next().charAt(0);
            if (strategyChoice == 'a' || strategyChoice == 'b' || strategyChoice == 'c' || strategyChoice == 'd') {
                break;
            }
            System.out.println("Invalid input. Please enter a valid strategy (a, b, c, or d).");
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
        treeSearch(strategyChoice);
    }

    public static void treeSearch(char strategyChoice) {
        Strategy strategy = null;
        switch (strategyChoice) {
            case 'a':
                strategy = new BreadthFirstSearch();
                break;
            case 'b':
                strategy = new DepthFirstSearch(DepthFirstSearch.HeuristicType.NO);
                break;
            case 'c':
                strategy = new DepthFirstSearch(DepthFirstSearch.HeuristicType.H1B);
                break;
            case 'd':
                strategy = new DepthFirstSearch(DepthFirstSearch.HeuristicType.H2);
                break;
        }
        Node root = new Node(1, 1, null);


        // Create a ScheduledExecutorService to handle the timeout
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Strategy finalStrategy = strategy;
        System.out.println("Searching for a solution...");
        System.out.println("Started at: " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
        System.out.println("Time limit: " + timeLimit + " minutes");
        System.out.println("End time: " + new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis() + (long) timeLimit * 60 * 1000)));
        Future<?> future = scheduler.submit(() -> finalStrategy.solve(root));

        try {
            // Wait for the task to complete within the time limit
            future.get(timeLimit, TimeUnit.MINUTES);
        } catch (TimeoutException e) {
            future.cancel(true); // Cancel the task
            System.out.println("Timeout error: Execution time exceeded the time limit.");
            System.exit(-2);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            scheduler.shutdown();
        }
    }





    public static void printPath(Stack<Node> path) {
        int cnt = 1;
        File file = new File("C:\\Users\\Cyber\\PycharmProjects\\PythonProject\\input.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
//            for (String s : traversed) {
//                writer.write(s);
//                writer.newLine();
//            }
            for (Node node : path) {
                writer.write(node.x + "," + node.y);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        for (Node node : path) {
//            System.out.print("->(" + node.x + "," + node.y + ")");
//        }
//        System.out.println();
    }
}