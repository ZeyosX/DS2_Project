
package FileBitIO;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public final class BitWriter extends IIOBit {
    private BufferedOutputStream bufferedOutputStream;
    private int currentByteIndex = 0;

    public BitWriter(String fileName) throws Exception {
        super(fileName);
    }

    @Override
    public void loadFile() throws Exception {
        var outputFile = new File(this.fileName);
        var fileOutputStream = new FileOutputStream(outputFile);
        bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        resetCurrentByte();
    }

    public void putBit(int bit) throws Exception {
        bit = bit % 2;
        currentByte.add(currentByteIndex++, (char) (bit + '0'));

        if (currentByteIndex < 8) return;
        var byteValue = getCurrentByteValue();
        bufferedOutputStream.write(byteValue);
        resetCurrentByte();
    }

    private void resetCurrentByte() {
        currentByte.clear();
        currentByteIndex = 0;
    }

    private int getCurrentByteValue() {
        var result = 0;
        for (var i = 7; i >= 0; i--) {
            result += (currentByte.get(i) - '0') * Math.pow(2, 7 - i);
        }
        return result;
    }

    public void putBits(String bits) throws Exception {
        var bitsCharArray = bits.toCharArray();
        for (var c : bitsCharArray) {
            var bit = c - '0';
            putBit(bit);
        }
    }

    @Override
    public void closeFile() throws Exception {
        while (currentByteIndex > 0) {
            putBit(1);
        }
        bufferedOutputStream.close();
    }

}
