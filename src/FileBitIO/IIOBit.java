package FileBitIO;

import java.util.ArrayList;

public abstract class IIOBit implements AutoCloseable {
    protected String fileName;
    protected final ArrayList<Character> currentByte;

    public IIOBit(String fileName) throws Exception {
        this.fileName = fileName;
        this.currentByte = new ArrayList<>();
        loadFile();
    }

    public abstract void loadFile() throws Exception;

    public abstract void closeFile() throws Exception;

    @Override
    public final void close() throws Exception {
        this.closeFile();
    }

}
