import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public final class Main {
    private Main() {
    }

    private static final Scanner keyboard = new Scanner(System.in);

    public static void main(String[] args) {
        showMenu();
    }

    private static void showMenu() {
        //noinspection InfiniteLoopStatement
        while (true) {
            printMenu();
            handleUserChoice();
        }
    }

    private static void handleUserChoice() {
        boolean invalidChoice = false;
        //noinspection ReassignedVariable
        while (!invalidChoice) {
            System.out.print("Enter your choice: ");
            var choice = Integer.parseInt(keyboard.nextLine());

            switch (choice) {
                case 1 -> handleFileCompression();
                case 2 -> handleFileDecompression();
                case 3 -> System.exit(0);
                default -> {
                    System.out.println("Invalid choice. Try again.");
                    invalidChoice = true;
                }
            }
        }


    }

    private static void printMenu() {
        System.out.println("""
                1. Compress a file
                2. Decompress a file
                3. Exit
                """);
    }

    private static void handleFileDecompression() {
        System.out.println("Enter the path of the file you want to decompress:");
        var inputFilePath = Paths.get(keyboard.nextLine());

        System.out.println("Enter the path of the file you want to save the decompressed file to:");
        var outputFilePath = Paths.get(keyboard.nextLine());

        if (!checkFilesValidity(inputFilePath, outputFilePath)) return;

        Thread thread = new Thread(() -> {
            try {
                HuffmanCompression.decompress(inputFilePath, outputFilePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        monitorDecompressionProcess(thread);
        System.out.println("Done.");
    }

    private static void monitorDecompressionProcess(Thread thread) {
        thread.start();
        while (thread.isAlive()) {
            if (HuffmanCompression.getStatus() == IOStatus.READING) {
                System.out.printf("Reading: %.2f%%\r", (HuffmanCompression.readBytes / (HuffmanCompression.fileLength * 1.0)) * 100);
            } else if (HuffmanCompression.getStatus() == IOStatus.WRITING) {
                System.out.printf("Writing: %.2f%%\r", (HuffmanCompression.writtenBytes / (HuffmanCompression.fileLength * 1.0)) * 100);
            }
        }
    }

    private static void handleFileCompression() {
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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
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
        return new Thread(() -> {
            try {
                HuffmanCompression.compress(inputFilePath, outputFilePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void monitorProcess(Thread thread) {
        thread.start();
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

