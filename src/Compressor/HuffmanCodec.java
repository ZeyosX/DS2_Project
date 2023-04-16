

package Compressor;

public abstract class HuffmanCodec {
    final int MAX_CHARACTERS = 256;
    protected String inputFileName;
    protected String outputFileName;
    protected int distinctCharacters = 0;
    protected long fileLength = 0;
    protected final String[] huffmanCodes = new String[MAX_CHARACTERS];
    protected long outputFileLength;
    protected String summary;

    protected abstract String leftShifter(String text, int n);
    protected abstract void generateSummary();

    public String getSummary() {
        return summary;
    }
}
