import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class JpegCompressor {
    private JpegCompressor() {
    }

    public static void compress(String sourceImagePath, String compressedImagePath, float quality) throws IOException {


        InputStream inputStream = null;
        try {
            inputStream = Files.newInputStream(Paths.get(sourceImagePath));
            var imageInputStream = ImageIO.createImageInputStream(inputStream);
            var readers = ImageIO.getImageReaders(imageInputStream);

            if (!readers.hasNext()) throw new IllegalArgumentException("No reader available for image type");

            var reader = readers.next();
            reader.setInput(imageInputStream);

            doLossyCompression(compressedImagePath, quality, reader);

        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private static void doLossyCompression(String compressedImagePath, float quality, ImageReader reader) throws IOException {
        ImageOutputStream outputStream;
        ImageWriter writer = null;
        try {
            outputStream = ImageIO.createImageOutputStream(new File(compressedImagePath));
            writer = ImageIO.getImageWriter(reader);
            writer.setOutput(outputStream);

            var params = writer.getDefaultWriteParam();
            params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            params.setCompressionQuality(quality);

            var image = reader.readAll(0, null);
            writer.write(null, image, params);

        } finally {
            if (writer != null) {
                writer.dispose();
            }
            reader.dispose();
        }
    }
}