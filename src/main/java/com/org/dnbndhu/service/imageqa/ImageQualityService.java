package com.org.dnbndhu.service.imageqa;

public class ImageQualityService {

    public double assessQuality(String imagePath) {

        // STUB for now
        // Later: OpenCV metrics (blur, brightness, resolution)
        return 1.0; // assume perfect quality for now
    }

    public String getQualityStatus(double score) {

        return score >= 0.7 ? "PASS" : "FAIL";
    }
}
