package Compressor;

public final class MinHeap {

    private final HuffmanNode[] nodes;
    private final int capacity;
    private int total;

    public MinHeap(int max) {
        capacity = max;
        total = 0;
        nodes = new HuffmanNode[capacity];
    }

    public synchronized void add(HuffmanNode node) {
        if (isFull()) return;
        if (isEmpty()) {
            nodes[total++] = node;
            return;
        }
        var i = total - 1;
        int pos;
        while (i >= 0) {
            if (nodes[i].frequency < node.frequency) break;
            i--;
        }
        pos = total - 1;
        while (pos >= i + 1) {
            nodes[pos + 1] = nodes[pos];
            pos--;
        }
        nodes[i + 1] = node;
        total++;
    }

    public synchronized HuffmanNode remove() {
        if (isEmpty()) return null;
        HuffmanNode ret = nodes[0];
        total--;
        for (var i = 0; i < total; i++) {
            nodes[i] = nodes[i + 1];
        }
        return ret;
    }

    public boolean isEmpty() {
        return total == 0;
    }

    public boolean isFull() {
        return total == capacity;
    }

    public int totalNodes() {
        return total;
    }

}

