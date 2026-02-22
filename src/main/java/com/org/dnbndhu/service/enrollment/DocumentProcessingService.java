package com.org.dnbndhu.service.enrollment;

import com.org.dnbndhu.service.imageqa.ImageQualityService;
import com.org.dnbndhu.service.ocr.OCRFieldExtractorService;
import com.org.dnbndhu.service.ocr.OCRService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentProcessingService {

    private final ImageQualityService imageQualityService;
    private final OCRService ocrService;
    private final OCRFieldExtractorService fieldExtractorService;

    public DocumentProcessingService() {
        this.imageQualityService = new ImageQualityService();
        this.ocrService = new OCRService();
        this.fieldExtractorService = new OCRFieldExtractorService();
    }

    /**
     * Processes uploaded documents and returns extracted fields
     */
    public Map<String, String> processDocuments(
            Map<String, String> documentTypeToPathMap) {

        Map<String, String> combinedFields = new HashMap<>();

        for (Map.Entry<String, String> entry : documentTypeToPathMap.entrySet()) {

            String documentType = entry.getKey();
            String filePath = entry.getValue();

            // 1️⃣ Image Quality Check
            double qualityScore = imageQualityService.evaluateQuality(filePath);
            String qualityStatus =
                    imageQualityService.getQualityStatus(qualityScore);

            if (!"GOOD".equals(qualityStatus)) {
                System.out.println("⚠ Skipping low quality document: " + documentType);
                continue;
            }

            // 2️⃣ OCR Extraction
            String extractedText = ocrService.extractText(filePath);

            // 3️⃣ Field Extraction
            Map<String, String> extractedFields =
                    fieldExtractorService.extractFields(documentType, extractedText);

            // 4️⃣ Merge fields (first value wins)
            extractedFields.forEach(combinedFields::putIfAbsent);
        }

        return combinedFields;
    }
}
