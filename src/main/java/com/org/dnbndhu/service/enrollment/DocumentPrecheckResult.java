package com.org.dnbndhu.service.enrollment;

public class DocumentPrecheckResult {

    private boolean passed;
    private String extractedText;
    private double qualityScore;

    private DocumentPrecheckResult(boolean passed, String extractedText, double qualityScore) {
        this.passed = passed;
        this.extractedText = extractedText;
        this.qualityScore = qualityScore;
    }

    public static DocumentPrecheckResult failed() {
        return new DocumentPrecheckResult(false, null, 0);
    }

    public static DocumentPrecheckResult passed(String text, double score) {
        return new DocumentPrecheckResult(true, text, score);
    }

    public boolean isPassed() {
        return passed;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public double getQualityScore() {
        return qualityScore;
    }
}
