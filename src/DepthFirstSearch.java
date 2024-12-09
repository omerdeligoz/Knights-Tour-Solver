//import java.util.*;
//import java.util.logging.Logger;
//
//class DepthFirstSearch {
//    public enum HeuristicType {
//        NO,     // No heuristic
//        H1B,    // Warnsdorff's method
//        H2      // Improved heuristic
//    }
//
//    private final HeuristicType selectedHeuristic;
//    Logger logger = Logger.getLogger(DepthFirstSearch.class.getName());
//
//    public DepthFirstSearch(HeuristicType heuristicType) {
//        this.selectedHeuristic = heuristicType;
//    }
//
//    public boolean solve(Node startNode, long startTime) {
//        Map<Node, Stack<Node>> frontier = new HashMap<>();
//        Stack<Node> path = new Stack<>();
//        Set<Node> visited = new HashSet<>();
//        path.push(startNode);
//        frontier.put(startNode, expand(startNode, path));
//        Main.expandedNodes++;
//
//        while (!path.isEmpty()) {
//            // Check for timeout
//            if (System.currentTimeMillis() - startTime > Main.timeLimit * 60 * 1000L) {
//                logger.warning("Timeout: Search exceeded the time limit of " + Main.timeLimit + " minutes.");
//                return false;
//            }
//            // Select the current node
//            Node selectedNode = path.peek();
//
//            // Check if the current node is the goal
//            if (isGoal(path)) {
//                if (Main.expandedNodes == Main.SIZE * Main.SIZE) {
//                    logger.info("Solution found without backtracking.");
//                } else {
//                    logger.info("Solution found with some backtracking.");
//                }
//                Main.printPath(path);
//                return true;
//            }
//
//            // Get the children of the current node
//            Stack<Node> children = frontier.getOrDefault(selectedNode, new Stack<>());
//
//            if (!children.isEmpty()) {
//                // Get the next child from the stack
//                Node nextNode = children.pop();
//
//                // Add the child to the path and frontier
//                if (!visited.contains(nextNode)) {
//                    visited.add(nextNode);
//                    path.push(nextNode);
//
//                    // Add the children of the next node to the frontier
//                    frontier.put(nextNode, expand(nextNode, path));
//                    Main.expandedNodes++;
//                }
//            }
//            // Backtrack if no children are available (dead-end)
//            else {
//                path.pop();
//                visited.remove(selectedNode);
//                frontier.remove(selectedNode);
//            }
//        }
//        logger.warning("No solution exists.");
//        return false;
//    }
//
//    private Stack<Node> expand(Node node, List<Node> path) {
//        List<Node> possibleMoves = new ArrayList<>();
//
//        // Try all possible moves from the current node
//        for (int[] move : Main.MOVES) {
//            int x = node.x + move[0];
//            int y = node.y + move[1];
//
//            // Check if the move is valid
//            if (isValidMove(path, x, y)) {
//                possibleMoves.add(new Node(x, y, node, node.board, node.depth + 1));
//            }
//        }
//
//        // If no heuristic is selected, return the moves as is
//        if (selectedHeuristic == HeuristicType.NO) {
//            return convertToStack(possibleMoves);
//        }
//        // Sort moves based on the selected heuristic
//        else if (selectedHeuristic == HeuristicType.H1B) {
//            // H1B: Warnsdorff method - sort by least number of options
//            possibleMoves.sort(Comparator.comparingInt(a -> countPossibleMoves(a, path)));
//        } else if (selectedHeuristic == HeuristicType.H2) {
//            // H2: Sort by least options, then distance to corners
//            possibleMoves.sort(Comparator.comparingInt((Node a) -> countPossibleMoves(a, path)).thenComparing(this::compareDistanceToCorners));
//        }
//        return convertToStack(possibleMoves);
//    }
//
//    private static Stack<Node> convertToStack(List<Node> possibleMoves) {
//        // Convert sorted list to stack (reverse order for stack pop behavior)
//        Stack<Node> children = new Stack<>();
//        for (int i = possibleMoves.size() - 1; i >= 0; i--) {
//            children.push(possibleMoves.get(i));
//        }
//        return children;
//    }
//
//    // Helper method to count possible moves from the given node
//    private int countPossibleMoves(Node node, List<Node> path) {
//        int count = 0;
//        for (int[] move : Main.MOVES) {
//            int x = node.x + move[0];
//            int y = node.y + move[1];
//            if (isValidMove(path, x, y)) {
//                count++;
//            }
//        }
//        return count;
//    }
//
//    private static boolean isValidMove(List<Node> path, int x, int y) {
//        return x >= 1 && x <= Main.SIZE
//                && y >= 1 && y <= Main.SIZE
//                && !containsNode(path, x, y);
//    }
//
//    // Helper method to compare distance to corners
//    private int compareDistanceToCorners(Node a, Node b) {
//        // Define corner coordinates
//        int[][] corners = {
//                {1, 1},                 // bottom-left corner
//                {1, Main.SIZE},         // top-left corner
//                {Main.SIZE, 1},         // bottom-right corner
//                {Main.SIZE, Main.SIZE}  // top-right corner
//        };
//
//        // Calculate minimum Manhattan distance to any corner
//        int minDistanceA = Integer.MAX_VALUE;
//        int minDistanceB = Integer.MAX_VALUE;
//
//        for (int[] corner : corners) {
//            int distA = Math.abs(a.x - corner[0]) + Math.abs(a.y - corner[1]);
//            int distB = Math.abs(b.x - corner[0]) + Math.abs(b.y - corner[1]);
//
//            minDistanceA = Math.min(minDistanceA, distA);
//            minDistanceB = Math.min(minDistanceB, distB);
//        }
//
//        return Integer.compare(minDistanceA, minDistanceB);
//    }
//
//    public static boolean isGoal(Stack<Node> path) {
//        return path.size() == Main.SIZE * Main.SIZE;
//    }
//
//    public static boolean containsNode(List<Node> path, int x, int y) {
//        for (Node n : path) {
//            if (n.x == x && n.y == y) {
//                return true;
//            }
//        }
//        return false;
//    }
//}