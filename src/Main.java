import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class Main {
    private Main() {
    }
    public static void main(String[] args) {
        var inputFilePath = Paths.get("C:\\Users\\ZeyosPC\\Desktop\\New Text Document.txt");
        var outputFilePath = Paths.get("C:\\Users\\ZeyosPC\\Desktop\\tetol.txt");
        var thread = beginCompression(inputFilePath, outputFilePath);
        monitorProcess(thread);
        System.out.println("Done.");
    }

    private static Thread beginCompression(Path inputFilePath, Path outputFilePath) {
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
                var status = HuffmanCompression.getStatus();
                System.out.printf("Compressed (%s): %.2f%%\n",
                        status,
                        ((status.equals("Reading")? HuffmanCompression.readBytes : HuffmanCompression.writtenBytes) / (HuffmanCompression.fileLength * 1.0)) * 100);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}