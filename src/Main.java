import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public final class Main {
    private Main() {
    }

    private static final Scanner keyboard = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Enter the path of the file you want to compress:");
        var inputFilePath = Paths.get(keyboard.nextLine());

        System.out.println("Enter the path of the file you want to save the compressed file to:");
        var outputFilePath = Paths.get(keyboard.nextLine());

        if (!checkFilesValidity(inputFilePath, outputFilePath)) return;

        Thread thread = compressFile(inputFilePath, outputFilePath);
        monitorProcess(thread);
        System.out.println("Done.");
    }

    private static Thread compressFile(Path inputFilePath, Path outputFilePath) {
        var fileExtension = getFileExtension(inputFilePath);

        return processFileType(inputFilePath, outputFilePath, fileExtension);
    }

    private static Thread processFileType(Path inputFilePath, Path outputFilePath, String fileExtension) {
        return fileExtension.equals("jpg") ?
                compressJpeg(inputFilePath, outputFilePath) :
                compressHuffman(inputFilePath, outputFilePath);
    }

    private static Thread compressJpeg(Path inputFilePath, Path outputFilePath) {
        Thread thread;
        System.out.println("Please enter the quality of the image (0-100):");
        var quality = (int) Double.parseDouble(keyboard.nextLine());
        thread = new Thread(() -> {
            try {
                JpegCompressor.compress(inputFilePath, outputFilePath, quality);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return thread;
    }

    private static String getFileExtension(Path inputFilePath) {
        return inputFilePath.toString().substring(inputFilePath.toString().lastIndexOf(".") + 1);
    }

    private static boolean checkFilesValidity(Path inputFilePath, Path outputFilePath) {
        if (inputFilePath.equals(outputFilePath)) {
            System.out.println("The input file and the output file cannot be the same.");
            return false;
        }

        if (inputFilePath.toFile().isDirectory()) {
            System.out.println("The input file cannot be a directory.");
            return false;
        }

        if (outputFilePath.toFile().isDirectory()) {
            System.out.println("The output file cannot be a directory.");
            return false;
        }

        if (inputFilePath.toFile().length() == 0) {
            System.out.println("The input file is empty.");
            return false;
        }


        return true;
    }

    private static Thread compressHuffman(Path inputFilePath, Path outputFilePath) {
        var thread = new Thread(() -> {
            try {
                HuffmanCompression.compress(inputFilePath, outputFilePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        return thread;
    }

    private static void monitorProcess(Thread thread) {
        try {
            while (thread.isAlive()) {
                //noinspection BusyWait
                Thread.sleep(1000);
                printMessage();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void printMessage() {
        switch (HuffmanCompression.getStatus()) {
            case READING ->
                    System.out.printf("Reading file (%.2f%%)\n", (HuffmanCompression.readBytes / (HuffmanCompression.fileLength * 1.0)) * 100);
            case WRITING ->
                    System.out.printf("Compressing (%.2f%%)\n", (HuffmanCompression.writtenBytes / (HuffmanCompression.fileLength * 1.0)) * 100);
        }
    }


}

