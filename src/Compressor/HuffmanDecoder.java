
package Compressor;

import java.io.*;
import java.util.Arrays;

import FileBitIO.*;


public final class HuffmanDecoder extends HuffmanCodec {

    public HuffmanDecoder(String inputFileName, String outputFileName) {
        loadFile(inputFileName, outputFileName);
    }

    public void loadFile(String inputFileName, String outputFileName) {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        summary = "";
    }

    public void decodeFile() throws Exception {
        if (inputFileName.length() == 0) return;
        Arrays.fill(huffmanCodes, "");
        var bitReader = new BitReader(inputFileName);
        fileLength = bitReader.available();

        outputFileLength = Long.parseLong(bitReader.getBytes(4), 2);
        distinctCharacters = Integer.parseInt(bitReader.getByte(), 2) + 1;
        generateSummary();
        extractCodeTable(bitReader);
        writeDecompressedData(bitReader);
    }

    void writeDecompressedData(BitReader bitReader) throws Exception {
        try (var fileOutputStream = new FileOutputStream(outputFileName)) {
            var i = 0L;
            while (i < outputFileLength) {
                i = writeCharacter(bitReader, fileOutputStream, i);
            }
        }
    }

    private long writeCharacter(BitReader bitReader, FileOutputStream fileOutputStream, long i) throws Exception {
        var buffer = "";
        int k;
        for (k = 0; k < 32; k++) {
            buffer += bitReader.getBit();
            var character = findCharacter(buffer);
            if (character <= -1) continue;
            fileOutputStream.write(character);
            i++;
            break;
        }
        if (k >= 32) throw new Exception("Corrupted File!");
        return i;
    }

    private void extractCodeTable(BitReader bitReader) throws Exception {
        for (var i = 0L; i < distinctCharacters; i++) {
            var ch = Integer.parseInt(bitReader.getByte(), 2);
            var length = Integer.parseInt(leftShifter(bitReader.getBits(5), 8), 2);
            huffmanCodes[ch] = bitReader.getBits(length);
        }
    }

    @Override
    protected void generateSummary() {
        summary = String.format("""
                Compressed File Size: %d
                Original   File Size: %d
                Distinct   Characters: %d
                """, fileLength, outputFileLength, distinctCharacters);
    }

    private int findCharacter(String characterCode) {
        var character = -1;
        for (var i = 0; i < MAX_CHARACTERS; i++) {
            if (!isCodeExists(characterCode, i)) continue;
            character = i;
            break;
        }
        return character;
    }

    private boolean isCodeExists(String characterCode, int i) {
        return !huffmanCodes[i].equals("") && huffmanCodes[i].equals(characterCode);
    }


    protected String leftShifter(String txt, int n) {
        var txtBuilder = new StringBuilder(txt);
        while (txtBuilder.length() < n) {
            txtBuilder.insert(0, "0");
        }
        txt = txtBuilder.toString();
        return txt;
    }

}



