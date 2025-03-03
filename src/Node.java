import java.util.BitSet;

public class Node {
    BitSet state;
    Node parent;
    int depth;
    int x;
    int y;

    public Node(int x, int y, Node parent, BitSet state, int depth) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.state = state;
        this.depth = depth;
    }

    @Override
    public String toString() {
        return ("(" + x + "," + y + ")");
    }
}


