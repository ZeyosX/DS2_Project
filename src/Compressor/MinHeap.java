package Compressor;

public final class MinHeap {

    private final HuffmanNode[] nodes;
    private final int capacity;
    private int size;

    public MinHeap(int max) {
        capacity = max;
        size = 0;
        nodes = new HuffmanNode[capacity];
    }

    public synchronized void add(HuffmanNode node) {
        if (isFull()) return;
        if (isEmpty()) {
            nodes[size++] = node;
            return;
        }
        var i = size - 1;
        int pos;
        while (i >= 0) {
            if (nodes[i].frequency < node.frequency) break;
            i--;
        }
        pos = size - 1;
        while (pos >= i + 1) {
            nodes[pos + 1] = nodes[pos];
            pos--;
        }
        nodes[i + 1] = node;
        size++;
    }

    public synchronized HuffmanNode remove() {
        if (isEmpty()) return null;
        HuffmanNode ret = nodes[0];
        size--;
        for (var i = 0; i < size; i++) {
            nodes[i] = nodes[i + 1];
        }
        return ret;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public int size() {
        return size;
    }

}

