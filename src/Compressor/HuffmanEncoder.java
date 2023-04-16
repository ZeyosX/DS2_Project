
package Compressor;

import FileBitIO.BitWriter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;


public final class HuffmanEncoder extends HuffmanCodec {

    private final long[] frequencies = new long[MAX_CHARACTERS];

    void resetFrequency() {
        Arrays.fill(frequencies, 0);
        distinctCharacters = 0;
        fileLength = 0;
        summary = "";
    }

    public HuffmanEncoder(String inputFileName, String outPutFileName) {
        loadFile(inputFileName, outPutFileName);
    }

    public void loadFile(String inputFileName, String outputFileName) {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        resetFrequency();
    }

    public void encodeFile() throws Exception {
        if (inputFileName.length() == 0) return;
        var fileInputStream = new FileInputStream(inputFileName);
        var bufferedInputStream = new BufferedInputStream(fileInputStream);

        fileLength = bufferedInputStream.available();
        if (fileLength == 0) throw new Exception("File is Empty!");

        countCharactersOccurrence(bufferedInputStream);

        bufferedInputStream.reset();

        var minHeap = getMinHeap();

        buildHuffmanTree(minHeap);

        var root = minHeap.remove();
        assert root != null;
        buildHuffmanCodes(root, "");

        Arrays.fill(huffmanCodes, "");
        getHuffmanCodes(root);

        try (var bitWriter = new BitWriter(outputFileName)) {
            writeHeader(bitWriter);
            writeCompressedData(bufferedInputStream, bitWriter);
        }

        generateSummary();
    }

    @Override
    protected void generateSummary() {
        outputFileLength = new File(outputFileName).length();
        var compressionRatio = ((outputFileLength) * 100) / (fileLength * 1.0f);

        summary = String.format("""
                Distinct Characters: %d
                Original File Size: %d
                Compressed File Size: %d
                Compression Ratio: %.2f%%
                """, distinctCharacters, fileLength, outputFileLength, compressionRatio);
    }

    private void writeCompressedData(BufferedInputStream bufferedInputStream, BitWriter bitWriter) throws Exception {
        var pos = 0L;
        while (pos < fileLength) {
            var character = bufferedInputStream.read();
            bitWriter.putBits(huffmanCodes[character]);
            pos++;
        }
    }

    private void writeHeader(BitWriter bitWriter) throws Exception {
        String buffer;
        buffer = leftShifter(Long.toString(fileLength, 2), 32);
        bitWriter.putBits(buffer);
        buffer = leftShifter(Integer.toString(distinctCharacters - 1, 2), 8);
        bitWriter.putBits(buffer);

        for (var i = 0; i < MAX_CHARACTERS; i++) {
            if (huffmanCodes[i].length() == 0) continue;
            buffer = leftShifter(Integer.toString(i, 2), 8);
            bitWriter.putBits(buffer);
            buffer = leftShifter(Integer.toString(huffmanCodes[i].length(), 2), 5);
            bitWriter.putBits(buffer);
            bitWriter.putBits(huffmanCodes[i]);
        }
    }

    private void countCharactersOccurrence(BufferedInputStream bufferedInputStream) throws IOException {
        var read_byte = 0L;
        bufferedInputStream.mark((int) fileLength);
        distinctCharacters = 0;

        while (read_byte < fileLength) {
            var character = bufferedInputStream.read();
            read_byte++;
            if (frequencies[character] == 0) distinctCharacters++;
            frequencies[character]++;
        }
    }

    private MinHeap getMinHeap() {
        var minHeap = new MinHeap(distinctCharacters + 1);

        for (var i = 0; i < MAX_CHARACTERS; i++) {
            if (frequencies[i] <= 0) continue;
            var node = new HuffmanNode(frequencies[i], (char) i, null, null);
            minHeap.add(node);
        }
        return minHeap;
    }

    private static void buildHuffmanTree(MinHeap minHeap) throws Exception {
        HuffmanNode low1;
        HuffmanNode low2;
        while (minHeap.totalNodes() > 1) {
            low1 = minHeap.remove();
            low2 = minHeap.remove();
            if (low1 == null || low2 == null) throw new Exception("Min heap Error!");
            HuffmanNode intermediate = new HuffmanNode((low1.frequency + low2.frequency), (char) 0, low1, low2);
            minHeap.add(intermediate);
        }
    }

    void buildHuffmanCodes(HuffmanNode parentNode, String parentCode) {
        parentNode.huffCode = parentCode;
        if (parentNode.left != null) {
            buildHuffmanCodes(parentNode.left, parentCode + "0");
        }

        if (parentNode.right != null) {
            buildHuffmanCodes(parentNode.right, parentCode + "1");
        }
    }

    void getHuffmanCodes(HuffmanNode parentNode) {
        if (parentNode == null) return;

        var asciiCode = parentNode.character;
        if (parentNode.left == null || parentNode.right == null) {
            huffmanCodes[asciiCode] = parentNode.huffCode;
        }

        if (parentNode.left != null) {
            getHuffmanCodes(parentNode.left);
        }

        if (parentNode.right != null) {
            getHuffmanCodes(parentNode.right);
        }
    }

    protected String leftShifter(String txt, int numberOfZeros) {
        var txtBuilder = new StringBuilder(txt);
        while (txtBuilder.length() < numberOfZeros)
            txtBuilder.insert(0, '0');
        return txtBuilder.toString();
    }

}


