import java.io.IOException;
import java.io.OutputStream;

public final class BitOutputStream {
    private OutputStream outputStream;
    private final int bufferSize;
    private int buffer;
    private int count;

    public BitOutputStream(OutputStream outputStream) {
        if (outputStream == null) throw new IllegalArgumentException("OutputStream cannot be null");
        this.outputStream = outputStream;
        this.bufferSize = 8;
    }

    public void writeBit(boolean bit) throws IOException {
        if (outputStream == null) throw new IOException("Stream is closed");
        buffer <<= 1;
        if (bit) {
            buffer |= 1;
        }
        count++;
        if (count == bufferSize) {
            flushBuffer();
        }

    }

    public void close() throws IOException {
        if (outputStream == null) return;
        if (count > 0) {
            buffer <<= bufferSize - count;
            flushBuffer();
        }
        outputStream.close();
        outputStream = null;
    }

    private void flushBuffer() throws IOException {
        outputStream.write(buffer);
        buffer = 0;
        count = 0;
    }
}