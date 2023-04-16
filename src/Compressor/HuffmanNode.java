
package Compressor;

public final class HuffmanNode {
    HuffmanNode left;
    HuffmanNode right;
    public long frequency;
    public char character;
    public String huffCode;

    public HuffmanNode(long frequency, char character, HuffmanNode left, HuffmanNode right) {
        this.frequency = frequency;
        this.character = character;
        this.left = left;
        this.right = right;
        huffCode = "";
    }
}
