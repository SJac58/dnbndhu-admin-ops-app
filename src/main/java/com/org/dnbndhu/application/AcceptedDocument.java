package com.org.dnbndhu.application;

public class AcceptedDocument {

    private int documentTypeId;
    private String filePath;
    private double qualityScore;

    public AcceptedDocument(int documentTypeId, String filePath, double qualityScore) {
        this.documentTypeId = documentTypeId;
        this.filePath = filePath;
        this.qualityScore = qualityScore;
    }

    public int getDocumentTypeId() {
        return documentTypeId;
    }

    public String getFilePath() {
        return filePath;
    }

    public double getQualityScore() {
        return qualityScore;
    }
}
