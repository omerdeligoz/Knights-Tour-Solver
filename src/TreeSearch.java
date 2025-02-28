import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class TreeSearch {
    Logger logger = Logger.getLogger(TreeSearch.class.getName());
    Problem problem;

    // Enum for different search strategies
    public enum Strategy {
        BFS,        // Breadth-First Search
        DFS,        // Depth-First Search (No heuristic)
        DFS_H1B,    // Depth-First Search with Warnsdorff's method heuristic
        DFS_H2      // Depth-First Search with improved heuristic
    }

    // Possible knight moves in a chessboard
    private final int[][] directions = new int[][]{
            {1, 2},
            {2, 1},
            {2, -1},
            {1, -2},
            {-1, -2},
            {-2, -1},
            {-2, 1},
            {-1, 2},
    };

    // Constructor to initialize the TreeSearch with a problem instance
    public TreeSearch(Problem problem) {
        this.problem = problem;
    }

    // Method to solve the problem using the specified strategy
    public void solve(Problem problem, Strategy strategy) {
        // Initialize the frontier with the root node
        Deque<Node> frontier = new ArrayDeque<>();
        Node root = new Node(problem.startX, problem.startY, null, new BitSet(problem.size * problem.size), 0);
        setState(root.state, problem.startX, problem.startY);
        frontier.add(root);

        // Search until the frontier is empty
        while (!frontier.isEmpty()) {
            // Check for timeout
            if (System.currentTimeMillis() - problem.startTime > problem.timeLimit * 60 * 1000L) {
                logger.warning("Timeout: Search exceeded the time limit of " + problem.timeLimit + " minutes.");
                return;
            }

            Node selectedNode = null;
            // Select the current node based on the search strategy
            switch (strategy) {
                case BFS -> selectedNode = frontier.pollFirst();                 // Select the first node in the queue
                case DFS, DFS_H1B, DFS_H2 -> selectedNode = frontier.pollLast(); // Select the last node in the stack
            }
            assert selectedNode != null;
            problem.expandedNodes++;

            // Check if the current node is the goal
            if (isGoalState(selectedNode)) {
                printPath(selectedNode);
                logger.info("Solution found!");
                return;
            }

            // Expand the selected node and add its children to the frontier
            List<Node> children = expand(selectedNode, strategy);
            frontier.addAll(children);
        }

        logger.info("No solution exists.");
    }


    // Method to expand a node and generate its children based on the strategy
    private List<Node> expand(Node node, Strategy strategy) {
        List<Node> possibleMoves = new ArrayList<>();

        // Try all possible moves from the current node
        for (int[] direction : directions) {
            int x = node.x + direction[0];
            int y = node.y + direction[1];

            // Check if the move is valid
            if (isValidMove(node.state, x, y)) {
                BitSet newState = (BitSet) node.state.clone();
                setState(newState, x, y);
                possibleMoves.add(new Node(x, y, node, newState, node.depth + 1));
                problem.createdNodes++;
            }
        }

        // Sort moves based on the selected heuristic
        switch (strategy) {
            case BFS, DFS -> {
                return possibleMoves; // No sorting needed for BFS and DFS
            }
            case DFS_H1B -> {
                possibleMoves.sort(Comparator.comparingInt(this::countPossibleMoves).reversed());
                return possibleMoves;
            }
            case DFS_H2 -> {
                possibleMoves.sort((Comparator.comparingInt(this::countPossibleMoves)
                        .thenComparing(this::compareDistanceToCorners)).reversed()
                );
                return possibleMoves;
            }
        }
        return possibleMoves;
    }

    // Method to compare the distance of two nodes to the nearest corner
    private int compareDistanceToCorners(Node a, Node b) {
        // Define corner coordinates
        int[][] corners = {
                {1, 1},                       // bottom-left corner
                {1, problem.size},            // bottom-right corner
                {problem.size, 1},            // top-left corner
                {problem.size, problem.size}  // top-right corner
        };

        // Calculate minimum Manhattan distance to any corner
        int minDistanceA = Integer.MAX_VALUE;
        int minDistanceB = Integer.MAX_VALUE;

        for (int[] corner : corners) {
            int distanceA = Math.abs(a.x - corner[0]) + Math.abs(a.y - corner[1]);
            int distanceB = Math.abs(b.x - corner[0]) + Math.abs(b.y - corner[1]);

            minDistanceA = Math.min(minDistanceA, distanceA);
            minDistanceB = Math.min(minDistanceB, distanceB);
        }

        return Integer.compare(minDistanceA, minDistanceB);
    }

    // Method to count the number of possible moves from a node
    private int countPossibleMoves(Node node) {
        int count = 0;
        for (int[] direction : directions) {
            int x = node.x + direction[0];
            int y = node.y + direction[1];
            if (isValidMove(node.state, x, y)) {
                count++;
            }
        }
        return count;
    }

    // Method to check if a move is valid
    private boolean isValidMove(BitSet state, int x, int y) {
        return x >= 1 && x <= problem.size
                && y >= 1 && y <= problem.size
                && !state.get((x - 1) * problem.size + (y - 1));
    }

    // Method to check if the current node is the goal state
    private boolean isGoalState(Node selectedNode) {
        return selectedNode.depth == problem.size * problem.size - 1;
    }

    // Method to set the state of a node
    private void setState(BitSet state, int x, int y) {
        state.set((x - 1) * problem.size + (y - 1));
    }

    // Method to print the path from the start node to the goal node
    private void printPath(Node node) {
        File file = new File("path.txt");
        List<String> pathList = new ArrayList<>();
        // Reconstruct the path from the start node to the goal node
        while (node != null) {
            pathList.addFirst(node.toString());
            node = node.parent;
        }

        // Write the path to a file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(pathList.toString());
            logger.info("Path: " + pathList);
        } catch (IOException e) {
            logger.warning("Failed to write path to file." + e);
        }
    }
}
