package com.org.dnbndhu.service.enrollment;

import com.org.dnbndhu.service.imageqa.ImageQualityService;
import com.org.dnbndhu.service.ocr.OCRService;

public class DocumentProcessingService {

    private final ImageQualityService imageQualityService = new ImageQualityService();
    private final OCRService ocrService = new OCRService();

    /**
     * PHASE 1: Pre-check only (NO DB)
     */
    public DocumentPrecheckResult precheckDocument(String filePath) {

        double qualityScore = imageQualityService.assessQuality(filePath);
        String qualityStatus = imageQualityService.getQualityStatus(qualityScore);

        if (!"PASS".equalsIgnoreCase(qualityStatus)) {
            return DocumentPrecheckResult.failed();
        }

        String extractedText = ocrService.extractText(filePath);

        return DocumentPrecheckResult.passed(extractedText, qualityScore);
    }
}
