package com.org.dnbndhu.service.ocr;

import com.org.dnbndhu.infrastructure.ocr.Tess4JOCRClient;

import java.io.File;

public class OCRService {

    private final Tess4JOCRClient ocrClient;

    public OCRService() {
        this.ocrClient = new Tess4JOCRClient();
    }

    /**
     * Extract and normalize text from image file
     */
    public String extractText(String filePath) {

        File file = new File(filePath);

        if (!file.exists()) {
            throw new RuntimeException("OCR file not found: " + filePath);
        }

        String rawText = ocrClient.extractText(file);

        return normalizeText(rawText);
    }

    /**
     * Clean OCR noise for easier parsing
     */
    private String normalizeText(String text) {

        if (text == null) return "";

        return text
                .replaceAll("\\r", "\n")
                .replaceAll("[|]", " ")
                .replaceAll("[^\\x00-\\x7F]", "") // remove non-ascii
                .trim();
    }
}
