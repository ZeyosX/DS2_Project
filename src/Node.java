public final class Node implements Comparable<Node> {
    int data;
    char character;
    Node left;
    Node right;

    public Node(int data, char character, Node left, Node right) {
        this.data = data;
        this.character = character;
        this.left = left;
        this.right = right;
    }

    public Node(int data, char character) {
        this.data = data;
        this.character = character;
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }

    @Override
    public int compareTo(Node node) {
        return this.data - node.data;
    }
}
