package com.org.dnbndhu.domain.dto;

import java.util.HashMap;
import java.util.Map;

public class EnrollmentDraftDTO {

    private Map<String, String> extractedFields = new HashMap<>();
    private Map<String, String> documentPaths = new HashMap<>();

    public void putField(String key, String value) {
        extractedFields.put(key, value);
    }

    public String getField(String key) {
        return extractedFields.get(key);
    }

    public Map<String, String> getAllFields() {
        return extractedFields;
    }

    public void addDocument(String docType, String path) {
        documentPaths.put(docType, path);
    }

    public Map<String, String> getDocumentPaths() {
        return documentPaths;
    }
}