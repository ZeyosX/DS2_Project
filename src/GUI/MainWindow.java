package GUI;

import Compressor.HuffmanDecoder;
import Compressor.HuffmanEncoder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public final class MainWindow extends JFrame {
    private final JButton actionButton;
    private final JTextArea textArea;
    private File inputFile;
    private File outputFile;
    private ActionType actionType;

    public MainWindow() {
        super("Omar Iseid Data Structure And Algorithms 2 Project | File Compression & Decompression using Huffman Algorithm");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 600);

        var contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        var leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(200, 0));
        contentPane.add(leftPanel, BorderLayout.WEST);
        leftPanel.setLayout(new GridLayout(4, 3, 0, 10));

        var inputLabel = new JLabel("Input File:");
        var inputButton = new JButton("Choose");
        inputButton.setPreferredSize(new Dimension(80, 20));
        inputButton.addActionListener(this::inputButtonActionListener);
        var inputField = new JTextField();
        inputField.setEditable(false);
        leftPanel.add(inputLabel);
        leftPanel.add(inputField);
        leftPanel.add(inputButton);

        var outputLabel = new JLabel("Output File:");
        var outputButton = new JButton("Choose");
        outputButton.setPreferredSize(new Dimension(80, 20));
        outputButton.addActionListener(this::outputButtonActionListener);

        var outputField = new JTextField();
        outputField.setEditable(false);
        leftPanel.add(outputLabel);
        leftPanel.add(outputField);
        leftPanel.add(outputButton);

        var group = new ButtonGroup();
        var compressButton = new JRadioButton("Compress", true);
        compressButton.addActionListener(this::compressButtonActionListener);
        actionType = ActionType.COMPRESS;
        var decompressButton = new JRadioButton("Decompress");
        decompressButton.addActionListener(this::decompressButtonActionListener);
        compressButton.setPreferredSize(new Dimension(80, 20));
        decompressButton.setPreferredSize(new Dimension(80, 20));
        group.add(compressButton);
        group.add(decompressButton);
        leftPanel.add(new JLabel("Action:"));
        leftPanel.add(compressButton);
        leftPanel.add(decompressButton);

        actionButton = new JButton("Compress");
        actionButton.setPreferredSize(new Dimension(100, 10));
        actionButton.addActionListener(this::actionButtonActionListener);
        leftPanel.add(actionButton);

        this.textArea = new JTextArea();
        this.textArea.setEditable(false);
        var scrollPane = new JScrollPane(textArea);
        contentPane.add(scrollPane, BorderLayout.CENTER);
    }

    private void inputButtonActionListener(ActionEvent actionEvent) {
        var fileChooser = new JFileChooser();
        setFilter(fileChooser, actionType == ActionType.COMPRESS ? ActionType.COMPRESS : ActionType.DECOMPRESS);
        var result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            inputFile = fileChooser.getSelectedFile();
            loadDataInTextArea(inputFile.getPath());
        }
    }

    private void outputButtonActionListener(ActionEvent actionEvent) {
        var fileChooser = new JFileChooser();
        setFilter(fileChooser, actionType == ActionType.COMPRESS ? ActionType.DECOMPRESS : ActionType.COMPRESS);
        var result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            outputFile = new File(fileChooser.getSelectedFile().getPath());
        }
    }

    private void setFilter(JFileChooser fileChooser, ActionType actionType) {
        var description = "";
        var extensions = "";
        if (actionType == ActionType.COMPRESS) {
            description = "Huffman Decompressed File";
            extensions = "txt";

        } else {
            description = "Huffman Compressed File";
            extensions = "huff";
        }
        var filter = new FileNameExtensionFilter(description, extensions);
        fileChooser.setFileFilter(filter);
    }

    private void compressButtonActionListener(ActionEvent actionEvent) {
        actionButton.setText("Compress");
        actionType = ActionType.COMPRESS;
        clearFields();
    }

    private void decompressButtonActionListener(ActionEvent actionEvent) {
        actionButton.setText("Decompress");
        actionType = ActionType.DECOMPRESS;
        clearFields();
    }

    private void actionButtonActionListener(ActionEvent actionEvent) {
        if (checkFields()) return;
        if (actionType == ActionType.COMPRESS) {
            startCompression();
        } else {
            startDecompression();
        }
    }

    private boolean checkFields() {
        var message = "";
        if (inputFile == null) {
            message = "Please choose an input file";
        } else if (outputFile == null) {
            message = "Please choose an output file";
        }
        if (!message.isBlank()) {
            JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        }
        return !message.isBlank();
    }

    private void clearFields() {
        inputFile = null;
        outputFile = null;
        textArea.setText("");
    }

    private void startCompression() {
        try {
            var inputFilePath = inputFile.getPath();
            var outputFilePath = outputFile.getPath() + ".huff";
            var huffmanEncoder = new HuffmanEncoder(inputFilePath, outputFilePath);
            huffmanEncoder.encodeFile();
            loadDataInTextArea(outputFilePath);
            showSummary(huffmanEncoder.getSummary());
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void startDecompression() {
        try {
            var inputFilePath = inputFile.getPath();
            var outputFilePath = outputFile.getPath() + ".txt";
            var huffmanDecoder = new HuffmanDecoder(inputFilePath, outputFilePath);
            huffmanDecoder.decodeFile();
            loadDataInTextArea(outputFilePath);
            showSummary(huffmanDecoder.getSummary());
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void showSummary(String summary) {
        JOptionPane.showMessageDialog(this, summary, "Summary", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadDataInTextArea(String file) {
        textArea.setText("");
        try {
            textArea.read(new FileReader(file), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}

enum ActionType {
    COMPRESS,
    DECOMPRESS
}