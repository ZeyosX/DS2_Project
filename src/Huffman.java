import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public final class Huffman {
    private final ArrayList<Node> nodes = new ArrayList<>();

    public Huffman() {

    }

    public void addNodes(String text) {
        for (int i = 0; i < text.length(); i++) {
            addNode(text.charAt(i));
        }
    }

    public void addNodes(char[] text) {
        for (char c : text) {
            if (c == '\u0000') break;
            addNode(c);
        }
    }

    public void addNodes(File file) {

        FileReader fileReader;
        try {
            fileReader = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        var buffer = new char[1024 * 1024];
        Arrays.fill(buffer, '\u0000');
        var offset = 0;
        while (true) {
            try {
                var read = fileReader.read(buffer, offset, buffer.length - offset);
                if (read == -1) {
                    break;
                }
                offset += read;
                addNodes(buffer);
                Arrays.fill(buffer, '\u0000');
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }


    private void addNode(char c) {
        int index = findNode(c);
        if (index == -1) {
            nodes.add(new Node(1, c));
        } else {
            nodes.get(index).setFrequency(nodes.get(index).getFrequency() + 1);
        }
    }

    private int findNode(char character) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).getCharacter() == character) {
                return i;
            }
        }
        return -1;
    }

    public String toString() {
        return nodes.toString();
    }

    public static void main(String[] args) {
        Huffman h = new Huffman();

        h.addNodes(new File("C:\\Users\\ZeyosPC\\Desktop\\New Text Document.txt"));
        System.out.println(h);
    }

}

final class Node {
    private int frequency;
    private final char character;
    private final Node left;
    private final Node right;

    public Node(int frequency, char character, Node left, Node right) {
        this.frequency = frequency;
        this.character = character;
        this.left = left;
        this.right = right;
    }

    public Node(int frequency, char character) {
        this(frequency, character, null, null);
    }

    public Node(int frequency, Node left, Node right) {
        this(frequency, '\0', left, right);
    }

    public int getFrequency() {
        return frequency;
    }

    public char getCharacter() {
        return character;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }

    public String toString() {
        return "Node(" + frequency + ", " + character + ")";
    }

    public void setFrequency(int i) {
        frequency = i;
    }
}
