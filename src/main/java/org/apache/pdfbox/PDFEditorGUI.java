package org.apache.pdfbox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.*;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.*;

public class PDFEditorGUI extends JFrame {

    private PDDocument document;
    private final JTextField pageNumberField;
    private final JPanel pdfPanel;
    private int currentPageIndex = 0;
    private float zoom = 1.0f;

    public PDFEditorGUI() {
        setTitle("PDF Editor");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Oberes Panel mit Buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton openButton = new JButton("Öffnen");
        JButton saveButton = new JButton("Speichern");
        JButton prevPageButton = new JButton("Vorherige Seite");
        JButton nextPageButton = new JButton("Nächste Seite");
        JButton zoomInButton = new JButton("Vergrößern");
        JButton zoomOutButton = new JButton("Verkleinern");

        pageNumberField = new JTextField("1", 3);
        JLabel pageLabel = new JLabel("Seite:");

        topPanel.add(openButton);
        topPanel.add(saveButton);
        topPanel.add(prevPageButton);
        topPanel.add(nextPageButton);
        topPanel.add(zoomInButton);
        topPanel.add(zoomOutButton);
        topPanel.add(pageLabel);
        topPanel.add(pageNumberField);

        add(topPanel, BorderLayout.NORTH);

        // PDF-Panel
        pdfPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (document != null) {
                    try {
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                        Dimension panelSize = getSize();
                        int x = (panelSize.width - (int) (document.getPage(currentPageIndex).getMediaBox().getWidth() * zoom)) / 2;
                        int y = (panelSize.height - (int) (document.getPage(currentPageIndex).getMediaBox().getHeight() * zoom)) / 2;
                        if (x < 0) x = 0;
                        if (y < 0) y = 0;

                        g2d.translate(x, y);
                        g2d.scale(zoom, zoom);
                        PDFRenderer renderer = new PDFRenderer(document);

                        renderer.renderPageToGraphics(currentPageIndex, g2d);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };


        JScrollPane scrollPane = new JScrollPane(pdfPanel);
        add(scrollPane, BorderLayout.CENTER);

        pdfPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && document != null) {
                    showFullTextEditor();
                }
            }
        });

        openButton.addActionListener(e -> selectAndLoadPDF());
        saveButton.addActionListener(e -> savePDF());
        prevPageButton.addActionListener(e -> showPreviousPage());
        nextPageButton.addActionListener(e -> showNextPage());
        zoomInButton.addActionListener(e -> zoomIn());
        zoomOutButton.addActionListener(e -> zoomOut());

        pageNumberField.addActionListener(e -> goToPage());
    }

    private void showFullTextEditor() {
        if (document != null) {
            try {
                // Gesamten Text der aktuellen Seite extrahieren
                PDFTextStripper textStripper = new PDFTextStripper();
                textStripper.setStartPage(currentPageIndex + 1);
                textStripper.setEndPage(currentPageIndex + 1);
                String text = textStripper.getText(document);

                // Text in einem Textbereich anzeigen
                JTextArea textArea = new JTextArea(text);
                JScrollPane textScrollPane = new JScrollPane(textArea);
                textScrollPane.setPreferredSize(new Dimension(800, 600));

                // Dialog mit OK und Abbrechen Buttons
                int result = JOptionPane.showConfirmDialog(
                        this,
                        textScrollPane,
                        "PDF Text bearbeiten",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

                if (result == JOptionPane.OK_OPTION) {
                    String newText = textArea.getText();
                    replaceTextInPDF(newText);

                    pdfPanel.repaint();
                    JOptionPane.showMessageDialog(this, "Änderungen übernommen.");
                }

            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Fehler beim Extrahieren des Textes.");
            }
        }
    }

    private void replaceTextInPDF(String newText) {
        PDPage page = document.getPage(currentPageIndex);

        // Zeilenumbrüche normalisieren
        newText = newText.replace("\r\n", "\n").replace("\r", "\n");

        // Entfernen von nicht unterstützten Steuerzeichen
        newText = newText.replaceAll("[\\p{Cntrl}&&[^\n\t]]", "");

        // Schriftart und Größe festlegen
        InputStream fontStream = getClass().getResourceAsStream("/fonts/arial.ttf");
        if (fontStream == null) {
            JOptionPane.showMessageDialog(this, "Schriftart nicht gefunden.");
            return;
        }

        try (InputStream fs = fontStream;
             PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, false)) {

            PDType0Font font = PDType0Font.load(document, fs);
            float fontSize = 12;

            // Textfluss vorbereiten
            contentStream.beginText();
            contentStream.setFont(font, fontSize);

            // Startposition (linke obere Ecke)
            PDRectangle mediaBox = page.getMediaBox();
            float margin = 50;
            float width = mediaBox.getWidth() - 2 * margin;
            float startX = mediaBox.getLowerLeftX() + margin;
            float startY = mediaBox.getUpperRightY() - margin;

            contentStream.newLineAtOffset(startX, startY);

            // Text in Zeilen aufteilen
            String[] lines = newText.split("\n");
            for (String line : lines) {
                int lastSpace = -1;
                while (line.length() > 0) {
                    int spaceIndex = line.indexOf(' ', lastSpace + 1);
                    if (spaceIndex < 0)
                        spaceIndex = line.length();
                    String subString = line.substring(0, spaceIndex);
                    float size = fontSize * font.getStringWidth(subString) / 1000;
                    if (size > width) {
                        if (lastSpace == -1)
                            lastSpace = spaceIndex;
                        subString = line.substring(0, lastSpace);

                        contentStream.showText(subString);
                        contentStream.newLineAtOffset(0, -fontSize * 1.2f);

                        line = line.substring(lastSpace).trim();
                        lastSpace = -1;
                    } else if (spaceIndex == line.length()) {
                        contentStream.showText(line);
                        line = "";
                    } else {
                        lastSpace = spaceIndex;
                    }
                }
                contentStream.newLineAtOffset(0, -fontSize * 1.2f);
            }

            contentStream.endText();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Fehler beim Aktualisieren des PDFs.");
        }
    }


    private void selectAndLoadPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("PDF-Datei öffnen");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF-Dokumente", "pdf"));
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                document = PDDocument.load(selectedFile);
                JOptionPane.showMessageDialog(this, "PDF erfolgreich geladen.");
                currentPageIndex = 0;
                zoom = 1.0f;
                pdfPanel.revalidate();
                pdfPanel.repaint();
                pageNumberField.setText("1");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Fehler beim Laden des PDFs.");
            }
        }
    }

    private void savePDF() {
        if (document == null) {
            JOptionPane.showMessageDialog(this, "Kein Dokument geladen.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Speichern unter");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try {
                document.save(fileToSave.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "PDF gespeichert.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Fehler beim Speichern des PDFs.");
            }
        }
    }

    private void showPreviousPage() {
        if (document != null && currentPageIndex > 0) {
            currentPageIndex--;
            pdfPanel.revalidate();
            pdfPanel.repaint();
            pageNumberField.setText(Integer.toString(currentPageIndex + 1));
        }
    }

    private void showNextPage() {
        if (document != null && currentPageIndex < document.getNumberOfPages() - 1) {
            currentPageIndex++;
            pdfPanel.revalidate();
            pdfPanel.repaint();
            pageNumberField.setText(Integer.toString(currentPageIndex + 1));
        }
    }

    private void goToPage() {
        if (document != null) {
            try {
                int pageNum = Integer.parseInt(pageNumberField.getText()) - 1;
                if (pageNum >= 0 && pageNum < document.getNumberOfPages()) {
                    currentPageIndex = pageNum;
                    pdfPanel.revalidate();
                    pdfPanel.repaint();
                } else {
                    JOptionPane.showMessageDialog(this, "Ungültige Seitenzahl.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Bitte geben Sie eine gültige Seitenzahl ein.");
            }
        }
    }

    private void zoomIn() {
        zoom *= 1.25F;
        pdfPanel.revalidate();
        pdfPanel.repaint();
    }

    private void zoomOut() {
        zoom /= 1.25F;
        pdfPanel.revalidate();
        pdfPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PDFEditorGUI frame = new PDFEditorGUI();
            frame.setVisible(true);
        });
    }
}
