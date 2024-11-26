public class Node {
    Node parent;
    int x;
    int y;

    public Node(int x, int y, Node parent) {
        this.x = x;
        this.y = y;
        this.parent = parent;
    }

    @Override
    public String toString() {
        return ("(" + x + "," + y + ")");
    }
}


