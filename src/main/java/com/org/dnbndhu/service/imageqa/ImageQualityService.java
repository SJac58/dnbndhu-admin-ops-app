package com.org.dnbndhu.service.imageqa;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Core;

public class ImageQualityService {

    static {
        nu.pattern.OpenCV.loadLocally(); // If using openpnp OpenCV
    }

    public double calculateSharpness(String imagePath) {

        Mat image = Imgcodecs.imread(imagePath);

        if (image.empty()) {
            throw new RuntimeException("Failed to load image: " + imagePath);
        }

        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        Mat laplacian = new Mat();
        Imgproc.Laplacian(gray, laplacian, 3);

        Mat stdDev = new Mat();
        Mat mean = new Mat();

        Core.meanStdDev(laplacian, mean, stdDev);

        double sharpness = stdDev.get(0, 0)[0];

        return sharpness;
    }

    public boolean isQualityAcceptable(double sharpness) {
        return sharpness > 50; // threshold (tune later)
    }
}
