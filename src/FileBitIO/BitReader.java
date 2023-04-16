
package FileBitIO;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

public final class BitReader extends IIOBit {
    private BufferedInputStream bufferedInputStream;

    public BitReader(String fileName) throws Exception {
        super(fileName);
    }

    @Override
    public void loadFile() throws Exception {
        var inputFile = new File(this.fileName);
        var fileInputStream = new FileInputStream(inputFile);
        bufferedInputStream = new BufferedInputStream(fileInputStream);
    }

    void leftShift() {
        while (currentByte.size() < 8) {
            currentByte.add(0, '0');
        }
    }

    public char getBit() throws Exception {
        if (currentByte.isEmpty() && bufferedInputStream.available() >= 1) {
            var read_byte = bufferedInputStream.read();
            addCollectionToCurrentByte(Integer.toString(read_byte, 2));
            leftShift();
        }
        return !currentByte.isEmpty() ? currentByte.remove(0) : '\0';
    }

    private void addCollectionToCurrentByte(String bits) {
        for (var bit : bits.toCharArray()) {
            currentByte.add(bit);
        }
    }

    public String getBits(int n) throws Exception {
        var bits = new StringBuilder();
        for (var i = 0; i < n; i++) {
            bits.append(getBit());
        }
        return bits.toString();
    }

    public String getByte() throws Exception {
        return getBits(8);
    }

    public String getBytes(int numberOfBytes) throws Exception {
        var bytes = new StringBuilder();
        for (var i = 0; i < numberOfBytes; i++) {
            bytes.append(getByte());
        }
        return bytes.toString();
    }

    public long available() throws Exception {
        return bufferedInputStream.available();
    }

    @Override
    public void closeFile() throws Exception {
        bufferedInputStream.close();
    }
}
