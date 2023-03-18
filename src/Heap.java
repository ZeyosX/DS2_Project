import java.util.ArrayList;

public final class Heap {
    private final ArrayList<Node> nodes;

    public Heap() {
        nodes = new ArrayList<>();
    }

    public void addNode(Node node) {
        nodes.add(node);
        heapify();
    }

    private void heapify() {
        for (int i = nodes.size() - 1; i > 0; i--) {
            var parentIndex = (i - 1) >> 1;
            if (nodes.get(i).compareTo(nodes.get(parentIndex)) < 0) {
                swap(i, parentIndex);
            }
        }
    }

    private void swap(int i, int j) {
        var temp = nodes.get(i);
        nodes.set(i, nodes.get(j));
        nodes.set(j, temp);
    }

    public Node remove() {
        var node = nodes.get(0);
        nodes.remove(0);
        return node;
    }

    public Node peek() {
        return nodes.get(0);
    }

    public int size() {
        return nodes.size();
    }

}


