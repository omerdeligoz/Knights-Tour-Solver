import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class TreeSearch {
    Logger logger = Logger.getLogger(TreeSearch.class.getName());

    public enum Strategy {
        BFS,
        DFS,        // No heuristic
        DFS_H1B,    // Warnsdorff's method
        DFS_H2      // Improved heuristic

    }

    public static final int[][] MOVES = new int[][]{
            {-1, -2},
            {-2, -1},
            {-2, 1},
            {-1, 2},
            {1, 2},
            {2, 1},
            {2, -1},
            {1, -2},
    };
    Strategy strategy;

    public TreeSearch(Strategy strategy) {
        this.strategy = strategy;
    }

    public void solve(Strategy strategy, int startX, int startY, long startTime) {
        Deque<Node> frontier = new ArrayDeque<>();
        boolean[][] emptyBoard = new boolean[Main.SIZE][Main.SIZE];
        Node root = new Node(startX, startY, null, emptyBoard, 0);
        root.board[Main.SIZE - startX][startY - 1] = true;
        frontier.add(root);

        // Search until the frontier is empty
        while (!frontier.isEmpty()) {
            // Check for timeout
            if (System.currentTimeMillis() - startTime > Main.timeLimit * 60 * 1000L) {
                logger.warning("Timeout: Search exceeded the time limit of " + Main.timeLimit + " minutes.");
                return;
            }

            Node selectedNode = null;
            // Select the current node based on the search strategy
            switch (strategy) {
                case BFS -> selectedNode = frontier.pollFirst();                 // Select the first node in the queue
                case DFS, DFS_H1B, DFS_H2 -> selectedNode = frontier.pollLast(); // Select the last node in the stack
            }
            assert selectedNode != null;
            Main.expandedNodes++;

            // Check if the current node is the goal
            if (isGoalState(selectedNode)) {
                printPath(selectedNode);
                logger.info("Solution found!");
                return;
            }

            // Expand the selected node and add its children to the frontier
            List<Node> children = expand(selectedNode);
            frontier.addAll(children);
        }

        logger.info("No solution exists.");
    }

    private List<Node> expand(Node node) {
        List<Node> possibleMoves = new ArrayList<>();

        // Try all possible moves from the current node
        for (int[] move : MOVES) {
            int x = node.x + move[0];
            int y = node.y + move[1];

            // Check if the move is valid
            if (isValidMove(node.board, x, y)) {
                boolean[][] newBoard = deepCopy(node.board);
                newBoard[Main.SIZE - x][y - 1] = true;
                possibleMoves.add(new Node(x, y, node, newBoard, node.depth + 1));
                Main.createdNodes++;
            }
        }

        switch (strategy) {
            // If no heuristic is selected, return the moves as is
            case BFS, DFS -> {
                return possibleMoves;
            }
            // Sort moves based on the selected heuristic
            case DFS_H1B -> {
                possibleMoves.sort(
                        Comparator.comparingInt(this::countPossibleMoves)
                                .reversed()
                );
                return possibleMoves;
            }
            case DFS_H2 -> {
                possibleMoves.sort(
                        (Comparator.comparingInt(this::countPossibleMoves)
                                .thenComparing(this::compareDistanceToCorners))
                                .reversed()
                );
                return possibleMoves;
            }
        }
        return possibleMoves;
    }

    public static boolean[][] deepCopy(boolean[][] original) {
        if (original == null) {
            return null;
        }
        return Arrays.stream(original).map(boolean[]::clone).toArray(boolean[][]::new);
    }


    private int compareDistanceToCorners(Node a, Node b) {
        // Define corner coordinates
        int[][] corners = {
                {1, 1},                 // bottom-left corner
                {1, Main.SIZE},         // bottom-right corner
                {Main.SIZE, 1},         // top-left corner
                {Main.SIZE, Main.SIZE}  // top-right corner
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

    private int countPossibleMoves(Node node) {
        int count = 0;
        for (int[] move : MOVES) {
            int x = node.x + move[0];
            int y = node.y + move[1];
            if (isValidMove(node.board, x, y)) {
                count++;
            }
        }
        return count;
    }

    private static boolean isValidMove(boolean[][] board, int x, int y) {
        return x >= 1 && x <= Main.SIZE
                && y >= 1 && y <= Main.SIZE
                && !board[Main.SIZE - x][y - 1];
    }

    private void printPath(Node node) {
        File file = new File("path.txt");
        List<String> pathList = new ArrayList<>();
        // Reconstruct the path from the start node to the goal node
        while (node != null) {
            pathList.addFirst(node.toString());
            node = node.parent;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(pathList.toString());
            logger.info("Path: " + pathList);
        } catch (IOException e) {
            logger.warning("Failed to write path to file." + e);
        }
    }

    private boolean isGoalState(Node selectedNode) {
        return selectedNode.depth == Main.SIZE * Main.SIZE - 1;
    }
}
