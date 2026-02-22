package com.org.dnbndhu.infrastructure.imageqa;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class OpenCVImageQAClient {

    /**
     * Simple sharpness detection using variance of pixel intensity.
     * Returns value between 0.0 and 1.0
     */
    public double calculateSharpness(String imagePath) {

        try {

            BufferedImage image = ImageIO.read(new File(imagePath));

            if (image == null) return 0.0;

            double variance = calculateVariance(image);

            // Normalize score (empirical scaling)
            double score = Math.min(variance / 1000.0, 1.0);

            return score;

        } catch (Exception e) {
            return 0.0;
        }
    }

    private double calculateVariance(BufferedImage image) {

        int width = image.getWidth();
        int height = image.getHeight();

        double sum = 0;
        double sumSq = 0;
        int count = 0;

        for (int x = 0; x < width; x += 5) {
            for (int y = 0; y < height; y += 5) {

                int rgb = image.getRGB(x, y);
                int gray = (rgb >> 16) & 0xff;

                sum += gray;
                sumSq += gray * gray;
                count++;
            }
        }

        double mean = sum / count;

        return (sumSq / count) - (mean * mean);
    }
}
