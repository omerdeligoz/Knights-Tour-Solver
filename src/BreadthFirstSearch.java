import java.util.*;
import java.util.logging.Logger;

public class BreadthFirstSearch {
    Logger logger = Logger.getLogger(BreadthFirstSearch.class.getName());

    public boolean solve(Node startNode, long startTime) {
        Queue<Node> frontier = new LinkedList<>();
        Set<Node> visited = new HashSet<>();
        frontier.add(startNode);
        startNode.parent = null;
        Main.expandedNodes++;

        while (!frontier.isEmpty()) {
            // Check for timeout
            if (System.currentTimeMillis() - startTime > Main.timeLimit * 60 * 1000L) {
                logger.warning("Timeout: Search exceeded the time limit of " + Main.timeLimit + " minutes.");
                return false;
            }

            Node selectedNode = frontier.poll();
            visited.add(selectedNode);
            Main.expandedNodes++;

            if (isGoal(selectedNode)) {
                logger.info("Solution found.");
                // Reconstruct the path from the start node to the goal node
                Stack<Node> path = new Stack<>();
                for (Node node = selectedNode; node != null; node = node.parent) {
                    path.push(node);
                }
                // Reverse the path to get the correct order from start to goal
                Collections.reverse(path);
                Main.printPath(path);
                return true;
            }

            // Expand the selected node and add its children to the frontier
            Queue<Node> children = expand(selectedNode);
            for (Node child : children) {
                if (!visited.contains(child)) {
                    frontier.add(child);
                    child.parent = selectedNode;
                }
            }
        }

        logger.warning("No solution exists.");
        return false;
    }

    private Queue<Node> expand(Node node) {
        Queue<Node> children = new LinkedList<>();
        for (int[] move : Main.MOVES) {
            int x = node.x + move[0];
            int y = node.y + move[1];
            if (isValidMove(node, x, y)) {
                children.add(new Node(x, y, node));
            }
        }
        return children;
    }

    private boolean isValidMove(Node node, int x, int y) {
        return x >= 1 && x <= Main.SIZE
                && y >= 1 && y <= Main.SIZE
                && !containsNode(x, y, node);
    }

    private boolean isGoal(Node selectedNode) {
        int cnt = 0;
        while (selectedNode != null) {
            cnt++;
            selectedNode = selectedNode.parent;
        }
        return cnt == Main.SIZE * Main.SIZE;
    }

    private boolean containsNode(int x, int y, Node node) {
        while (node != null) {
            if (node.x == x && node.y == y) {
                return true;
            }
            node = node.parent;
        }
        return false;
    }
}
