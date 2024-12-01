import java.util.*;
import java.util.logging.Logger;

class DepthFirstSearch implements Strategy {
    Logger logger = Logger.getLogger(DepthFirstSearch.class.getName());

    public enum HeuristicType {
        NO,     // No heuristic
        H1B,    // Warnsdorff's method
        H2      // Improved heuristic
    }

    private final HeuristicType selectedHeuristic;

    public DepthFirstSearch(HeuristicType heuristicType) {
        this.selectedHeuristic = heuristicType;
    }

    @Override
    public boolean solve(Node startNode, long startTime) {
        Map<Node, Stack<Node>> frontier = new HashMap<>();
        Stack<Node> path = new Stack<>();
        Set<Node> visited = new HashSet<>();
        path.push(startNode);
        frontier.put(startNode, expand(startNode, path));
        Main.expandedNodes++;

        while (!path.isEmpty()) {
            if (System.currentTimeMillis() - startTime > Main.timeLimit * 60 * 1000L) {
                logger.warning("Timeout: Search exceeded the time limit of " + Main.timeLimit + " minutes.");
                return false;
            }
            Node selectedNode = path.peek();
            if (isGoal(path)) {
                if (Main.expandedNodes == Main.SIZE * Main.SIZE) {
                    logger.info("Solution found without backtracking.");
                } else {
                    logger.info("Solution found with some backtracking.");
                }
                Main.printPath(path);
                return true;
            }
            Stack<Node> children = frontier.getOrDefault(selectedNode, new Stack<>());
            if (!children.isEmpty()) {
                Node child = children.pop();
                if (!visited.contains(child)) {
                    visited.add(child);
                    path.push(child);
                    frontier.put(child, expand(child, path));
                    Main.expandedNodes++;
                }
            } else {
                path.pop();
                visited.remove(selectedNode);
                frontier.remove(selectedNode);
            }
        }
        logger.warning("No solution exists.");
        return false;
    }

    private Stack<Node> expand(Node node, List<Node> path) {
        List<Node> possibleMoves = new ArrayList<>();

        // Find all valid moves
        for (int[] move : Main.MOVES) {
            int x = node.x + move[0];
            int y = node.y + move[1];
            if (isValidMove(path, x, y)) {
                possibleMoves.add(new Node(x, y, node));
            }
        }

        if (selectedHeuristic == HeuristicType.NO) {
            // Convert sorted list to stack (reverse order for stack pop behavior)
            Stack<Node> children = new Stack<>();
            for (Node potentialMove : possibleMoves) {
                children.push(potentialMove);
            }
            return children;
        }
        // Sort moves based on the selected heuristic
        else if (selectedHeuristic == HeuristicType.H1B) {
            // H1B: Warnsdorff method - sort by least number of onward moves
            possibleMoves.sort((a, b) -> {
                int aOnwardMoves = countOnwardMoves(a, path);
                int bOnwardMoves = countOnwardMoves(b, path);
                return Integer.compare(aOnwardMoves, bOnwardMoves);
            });
        } else if (selectedHeuristic == HeuristicType.H2) {
            // H2: Sort by least onward moves, then distance to corners
            possibleMoves.sort((a, b) -> {
                int aOnwardMoves = countOnwardMoves(a, path);
                int bOnwardMoves = countOnwardMoves(b, path);

                // Primary sort: Least number of onward moves
                if (aOnwardMoves != bOnwardMoves) {
                    return Integer.compare(aOnwardMoves, bOnwardMoves);
                }

                // Secondary sort: Distance to corners when onward moves are equal
                return compareDistanceToCorners(a, b);
            });
        }

        // Convert sorted list to stack (reverse order for stack pop behavior)
        Stack<Node> children = new Stack<>();
        for (int i = possibleMoves.size() - 1; i >= 0; i--) {
            children.push(possibleMoves.get(i));
        }
        return children;
    }

    // Helper method to count possible onward moves from a given node
    private int countOnwardMoves(Node node, List<Node> path) {
        int count = 0;
        for (int[] move : Main.MOVES) {
            int x = node.x + move[0];
            int y = node.y + move[1];
            if (isValidMove(path, x, y)) {
                count++;
            }
        }
        return count;
    }

    private static boolean isValidMove(List<Node> path, int x, int y) {
        return x >= 1 && x <= Main.SIZE
                && y >= 1 && y <= Main.SIZE
                && !hasNode(path, x, y);
    }

    // Helper method to compare distance to corners
    private int compareDistanceToCorners(Node a, Node b) {
        // Define corner coordinates for a board of size Main.size
        int[][] corners = {
                {1, 1},           // bottom-left corner
                {1, Main.SIZE},   // top-left corner
                {Main.SIZE, 1},   // bottom-right corner
                {Main.SIZE, Main.SIZE}  // top-right corner
        };

        // Calculate minimum Manhattan distance to any corner
        int minDistanceA = Integer.MAX_VALUE;
        int minDistanceB = Integer.MAX_VALUE;

        for (int[] corner : corners) {
            int distA = Math.abs(a.x - corner[0]) + Math.abs(a.y - corner[1]);
            int distB = Math.abs(b.x - corner[0]) + Math.abs(b.y - corner[1]);

            minDistanceA = Math.min(minDistanceA, distA);
            minDistanceB = Math.min(minDistanceB, distB);
        }

        // Prefer the move closer to a corner
        return Integer.compare(minDistanceA, minDistanceB);
    }

    public static boolean isGoal(Stack<Node> path) {
        return path.size() == Main.SIZE * Main.SIZE;
    }

    public static boolean hasNode(List<Node> path, int x, int y) {
        for (Node n : path) {
            if (n.x == x && n.y == y) {
                return true;
            }
        }
        return false;
    }
}