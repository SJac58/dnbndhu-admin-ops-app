package com.org.dnbndhu.service.enrollment;

import com.org.dnbndhu.service.imageqa.ImageQualityService;
import com.org.dnbndhu.service.ocr.OCRService;

import java.io.File;

public class DocumentProcessingService {

    private final ImageQualityService qualityService =
            new ImageQualityService();

    private final OCRService ocrService =
            new OCRService();

    public String processAndExtractText(File file) {

        String path = file.getAbsolutePath();

        double sharpness =
                qualityService.calculateSharpness(path);

        if (!qualityService.isQualityAcceptable(sharpness)) {
            throw new RuntimeException(
                    "Image quality too low. Please upload clearer image."
            );
        }

        return ocrService.extractText(path);
    }
}
