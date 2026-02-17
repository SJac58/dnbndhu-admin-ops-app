package com.org.dnbndhu.service.ocr;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

public class OCRService {

    private final ITesseract tesseract;

    public OCRService() {

        tesseract = new Tesseract();

        tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
        tesseract.setLanguage("eng");
    }


    public String extractText(String filePath) {

        try {
            return tesseract.doOCR(new File(filePath));
        } catch (TesseractException e) {
            throw new RuntimeException("OCR failed", e);
        }
    }
}
