import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class HuffmanCompression {

    private HuffmanCompression() {
    }

    public static long writtenBytes;
    public static long readBytes;
    public static long fileLength = 1;
    private static IOStatus status = IOStatus.DONE;

    public static void compress(Path inputFilePath, Path outputFilePath) throws IOException {
        var inputStream = new FileInputStream(inputFilePath.toFile());
        var outputStream = new FileOutputStream(outputFilePath.toFile());

        var startTime = System.currentTimeMillis();
        var fileSizeBefore = inputStream.getChannel().size();
        System.out.printf("File size before compression: %d bytes\n", fileSizeBefore);

        status = IOStatus.READING;
        fileLength = inputStream.getChannel().size();

        var frequencyMap = new HashMap<Character, Integer>();
        getFrequencies(inputStream, frequencyMap);

        var heap = makeHuffmanHeap(frequencyMap);

        buildHuffmanHeapFromBottom(heap);

        var codeMap = new HashMap<Character, String>();
        assignHuffmanCodes(codeMap, heap.peek(), "");

        var bitOutputStream = new BitOutputStream(outputStream);
        inputStream = new FileInputStream(inputFilePath.toFile());

        status = IOStatus.WRITING;
        writeCompressedData(inputStream, codeMap, bitOutputStream);

        var endTime = System.currentTimeMillis();
        endCompressing(inputStream, outputStream, bitOutputStream);
        status = IOStatus.DONE;

        var fileSizeAfter = outputFilePath.toFile().length();
        System.out.printf("Size after compression: %d bytes\n", fileSizeAfter);
        System.out.printf("Compression ratio: %.2f%%\n", (fileSizeAfter / (fileSizeBefore * 1.0)) * 100);
        System.out.printf("Time taken: %d ms\n", endTime - startTime);
    }

    private static void endCompressing(FileInputStream inputStream, FileOutputStream outputStream, BitOutputStream bitOutputStream) throws IOException {
        bitOutputStream.close();
        outputStream.close();
        inputStream.close();
    }

    private static void writeCompressedData(FileInputStream inputStream, HashMap<Character, String> codeMap, BitOutputStream bitOutputStream) throws IOException {
        int data;
        while ((data = inputStream.read()) != -1) {
            var c = (char) data;
            var code = codeMap.get(c);
            for (var i = 0; i < code.length(); i++) {
                bitOutputStream.writeBit(code.charAt(i) == '1');
            }
            writtenBytes++;
        }
    }

    private static void buildHuffmanHeapFromBottom(Heap heap) {
        while (heap.size() > 1) {
            var left = heap.remove();
            var right = heap.remove();
            var parent = new Node(left.data + right.data, '\0', left, right);
            heap.addNode(parent);
        }
    }

    private static Heap makeHuffmanHeap(HashMap<Character, Integer> frequencyMap) {
        Heap heap;
        heap = new Heap();
        for (var entry : frequencyMap.entrySet()) {
            heap.addNode(new Node(entry.getValue(), entry.getKey()));
        }
        return heap;
    }

    private static void getFrequencies(FileInputStream inputStream, HashMap<Character, Integer> frequencyMap) throws IOException {
        int data = inputStream.read();
        while (data != -1) {
            var c = (char) data;
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
            data = inputStream.read();
            readBytes++;
        }
    }

    public static void decompress(Path inputFilePath, Path outputFilePath) throws IOException {
        var inputStream = new FileInputStream(inputFilePath.toFile());
        var outputStream = new FileOutputStream(outputFilePath.toFile());

        var root = readTree(inputStream);
        writeDecompressedText(inputStream, outputStream, root, root);

        endDecompressing(inputStream, outputStream);
    }

    private static void endDecompressing(FileInputStream inputStream, FileOutputStream outputStream) throws IOException {
        outputStream.close();
        inputStream.close();
    }

    private static void writeDecompressedText(FileInputStream inputStream,
                                              FileOutputStream outputStream,
                                              Node root, Node current) throws IOException {
        int bit = inputStream.read();
        while (bit != -1) {
            for (var i = 7; i >= 0; i--) {
                current = isOne(current, bit, i);
                if (!current.isLeaf()) continue;
                outputStream.write(current.character);
                current = root;
            }
            bit = inputStream.read();
        }
    }

    private static Node isOne(Node node, int bit, int i) {
        return ((bit >> i) & 1) == 1 ? node.right : node.left;
    }

    private static Node readTree(FileInputStream inputStream) throws IOException {
        var bit = inputStream.read();
        if (bit == 1) {
            var character = (char) inputStream.read();
            return new Node(0, character);
        }
        var left = readTree(inputStream);
        var right = readTree(inputStream);
        return new Node(0, '\0', left, right);
    }

    private static void assignHuffmanCodes(Map<Character, String> codeMap, Node node, String code) {
        if (node == null) return;
        if (node.isLeaf()) {
            codeMap.put(node.character, code);
        } else {
            assignHuffmanCodes(codeMap, node.left, code + "0");
            assignHuffmanCodes(codeMap, node.right, code + "1");
        }
    }

    public static IOStatus getStatus() {
        return status;
    }
}

