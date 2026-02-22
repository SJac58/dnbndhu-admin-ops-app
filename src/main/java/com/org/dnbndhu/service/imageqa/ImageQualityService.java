package com.org.dnbndhu.service.imageqa;

import com.org.dnbndhu.infrastructure.imageqa.OpenCVImageQAClient;

public class ImageQualityService {

    private final OpenCVImageQAClient qaClient;

    public ImageQualityService() {
        this.qaClient = new OpenCVImageQAClient();
    }

    /**
     * Returns quality score between 0 and 1
     */
    public double evaluateQuality(String imagePath) {

        double sharpness = qaClient.calculateSharpness(imagePath);

        return sharpness;
    }

    /**
     * Returns simple PASS/FAIL status
     */
    public String getQualityStatus(double score) {

        if (score >= 0.7) {
            return "GOOD";
        } else {
            return "POOR";
        }
    }
}
