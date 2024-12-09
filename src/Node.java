
public class Node {
    Node parent;
    boolean[][] board;
    int depth;
    int x;
    int y;

    public Node(int x, int y, Node parent, boolean[][] board, int depth) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.board = board;
        this.depth = depth;
    }

    @Override
    public String toString() {
        return ("(" + x + "," + y + ")");
    }
}


