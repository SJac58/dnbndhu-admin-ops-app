package com.org.dnbndhu.infrastructure.ocr;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

public class Tess4JOCRClient {

    private final ITesseract tesseract;

    public Tess4JOCRClient() {

        tesseract = new Tesseract();

        // IMPORTANT: Must point to tessdata folder directly
        tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");

        tesseract.setLanguage("eng");
    }

    public String extractText(File imageFile) {

        try {
            return tesseract.doOCR(imageFile);

        } catch (TesseractException e) {
            throw new RuntimeException(
                    "OCR failed for file: " + imageFile.getAbsolutePath(), e);
        }
    }
}
