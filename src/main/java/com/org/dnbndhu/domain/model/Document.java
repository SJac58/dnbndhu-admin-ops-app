package com.org.dnbndhu.domain.model;
//double check at time of ocr model
public class Document {

    private int documentId;
    private int studentId;
    private int documentTypeId;

    private String filePath;
    private String ocrExtractedText; //not in db...make new table for this?? or not since one time extraction

    private Double qualityScore;
    private String qualityStatus;

    private Integer verified; // 0 or 1
    private String verificationDate;

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(int documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getOcrExtractedText() {
        return ocrExtractedText;
    }

    public void setOcrExtractedText(String ocrExtractedText) {
        this.ocrExtractedText = ocrExtractedText;
    }

    public Double getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(Double qualityScore) {
        this.qualityScore = qualityScore;
    }

    public String getQualityStatus() {
        return qualityStatus;
    }

    public void setQualityStatus(String qualityStatus) {
        this.qualityStatus = qualityStatus;
    }

    public Integer getVerified() {
        return verified;
    }

    public void setVerified(Integer verified) {
        this.verified = verified;
    }

    public String getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(String verificationDate) {
        this.verificationDate = verificationDate;
    }
}
