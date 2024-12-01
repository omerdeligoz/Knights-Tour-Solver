import java.util.*;
import java.util.logging.Logger;

public class BreadthFirstSearch implements Strategy {
    Logger logger = Logger.getLogger(BreadthFirstSearch.class.getName());
    @Override
    public boolean solve(Node startNode) {
        Queue<Node> frontier = new LinkedList<>();
        Set<Node> visited = new HashSet<>();
        frontier.add(startNode);
        startNode.parent = null;
        Main.expandedNodes++;

        while (!frontier.isEmpty()) {
            Node selectedNode = frontier.poll();
            visited.add(selectedNode);

            if (isGoal(selectedNode)) {
                // Reconstruct the path from the start node to the goal node
                Stack<Node> path = new Stack<>();
                for (Node node = selectedNode; node != null; node = node.parent) {
                    path.push(node);
                }
                // Reverse the path to get the correct order from start to goal
                Collections.reverse(path);

                logger.info("Solution found. Nodes Expanded: " + Main.expandedNodes);
                Main.printPath(path);
                return true;
            }

            Queue<Node> children = expand(selectedNode); // Adjust expand to return List<Node>
            for (Node child : children) {
                if (!visited.contains(child)) {
                    frontier.add(child);
                    child.parent = selectedNode;
                    Main.expandedNodes++;
                    if (Main.expandedNodes % 10000 == 0) {
                        logger.info("Nodes Expanded: " + Main.expandedNodes);
                    }
                }
            }
        }

        logger.info("No solution found. Nodes Expanded: " + Main.expandedNodes);
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
        return x >= 1 && x <= Main.SIZE && y >= 1 && y <= Main.SIZE && !hasNode(x, y, node);
    }

    private boolean isGoal(Node selectedNode) {
        int cnt = 0;
        while (selectedNode != null) {
            cnt++;
            selectedNode = selectedNode.parent;
        }
        return cnt == Main.SIZE * Main.SIZE;
    }

    private boolean hasNode(int x, int y, Node node) {
        while (node != null) {
            if (node.x == x && node.y == y) {
                return true;
            }
            node = node.parent;
        }
        return false;
    }
}
