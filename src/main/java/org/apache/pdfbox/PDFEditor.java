package org.apache.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationText;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationSquareCircle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

public class PDFEditor {

    // Methode zum Laden eines PDFs
    public PDDocument loadPDF(String filePath) {
        PDDocument document = null;
        try {
            document = PDDocument.load(new File(filePath));
            System.out.println("PDF erfolgreich geladen: " + filePath);
        } catch (IOException e) {
            System.err.println("Fehler beim Laden des PDFs: " + e.getMessage());
        }
        return document;
    }

    // Methode zum Extrahieren von Text aus dem PDF
    public void extractText(PDDocument document) {
        try {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            System.out.println("Extrahierter Text:\n" + text);
        } catch (IOException e) {
            System.err.println("Fehler beim Extrahieren des Texts: " + e.getMessage());
        }
    }

    // Methode zum Hinzufügen einer Text-Annotation
    public void addTextAnnotation(PDDocument document, int pageNumber, String text, float x, float y) {
        try {
            PDPage page = document.getPage(pageNumber - 1); // Seiten sind nullbasiert

            PDAnnotationText textAnnotation = new PDAnnotationText();
            textAnnotation.setContents(text);
            textAnnotation.setTitlePopup("Kommentar");
            textAnnotation.setRectangle(new org.apache.pdfbox.pdmodel.common.PDRectangle(x, y, 50, 50));

            // Gelbe Farbe als PDColor setzen
            PDColor yellow = new PDColor(new float[]{1, 1, 0}, PDDeviceRGB.INSTANCE);
            textAnnotation.setColor(yellow);

            page.getAnnotations().add(textAnnotation);
            System.out.println("Annotation hinzugefügt: " + text);
        } catch (IOException e) {
            System.err.println("Fehler beim Hinzufügen der Annotation: " + e.getMessage());
        }
    }

    // Methode zum Schließen des Dokuments
    public void closePDF(PDDocument document) {
        try {
            if (document != null) {
                document.close();
                System.out.println("PDF geschlossen.");
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Schließen des PDFs: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        PDFEditor pdfEditor = new PDFEditor();

        // Pfad zu Ihrem PDF-Dokument
        String filePath = "C:\\Users\\Muhammad\\Desktop\\Motivation.pdf";

        // PDF laden und Text extrahieren
        PDDocument document = pdfEditor.loadPDF(filePath);
        if (document != null) {
            pdfEditor.extractText(document);

            // Annotation hinzufügen
            pdfEditor.addTextAnnotation(document, 1, "Dies ist eine Text-Annotation", 100, 700);

            // PDF speichern, um die Annotation zu behalten
            try {
                document.save("C:\\Users\\Muhammad\\Desktop\\Motivation_with_annotation.pdf");
                System.out.println("PDF gespeichert mit Annotationen.");
            } catch (IOException e) {
                System.err.println("Fehler beim Speichern des PDFs: " + e.getMessage());
            }

            pdfEditor.closePDF(document);
        }
    }
}
