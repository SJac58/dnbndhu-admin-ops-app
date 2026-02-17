package com.org.dnbndhu.service.ocr;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OCRFieldExtractorService {

    public Map<String, String> extractFields(String documentType, String text) {

        Map<String, String> fields = new HashMap<>();

        switch (documentType.toUpperCase()) {

            case "AADHAR_CARD" -> extractAadhaar(fields, text);
            case "PAN_CARD" -> extractPan(fields, text);
            case "BANK_PASSBOOK" -> extractBank(fields, text);
            case "UDID_CARD" -> extractUdid(fields, text);
        }

        return fields;
    }

    // ================= AADHAAR =================

    private void extractAadhaar(Map<String, String> map, String text) {

        // Aadhaar number (xxxx xxxx xxxx)
        Matcher aadhaar = Pattern.compile("\\d{4}\\s?\\d{4}\\s?\\d{4}")
                .matcher(text);
        if (aadhaar.find()) {
            map.put("aadhaarNo", aadhaar.group());
        }

        // DOB
        Matcher dob = Pattern.compile("\\d{2}/\\d{2}/\\d{4}")
                .matcher(text);
        if (dob.find()) {
            map.put("dateOfBirth", dob.group());
        }

        // Address (very simplified)
        if (text.toLowerCase().contains("address")) {
            String[] lines = text.split("\n");
            for (String line : lines) {
                if (line.toLowerCase().contains("address")) {
                    map.put("address", line.replaceAll("(?i)address", "").trim());
                }
            }
        }
    }

    // ================= PAN =================

    private void extractPan(Map<String, String> map, String text) {

        // PAN format: ABCDE1234F
        Matcher pan = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]")
                .matcher(text);
        if (pan.find()) {
            map.put("panNo", pan.group());
        }

        String[] lines = text.split("\n");

        for (String line : lines) {

            if (line.toLowerCase().contains("name")) {
                map.put("fullName", cleanValue(line));
            }

            if (line.toLowerCase().contains("father")) {
                map.put("fatherName", cleanValue(line));
            }

            if (line.matches(".*\\d{2}/\\d{2}/\\d{4}.*")) {
                map.put("dateOfBirth", line.trim());
            }
        }
    }

    // ================= BANK =================

    private void extractBank(Map<String, String> map, String text) {

        Matcher pincode = Pattern.compile("\\b\\d{6}\\b")
                .matcher(text);

        if (pincode.find()) {
            map.put("pinCode", pincode.group());
        }
    }

    // ================= UDID =================

    private void extractUdid(Map<String, String> map, String text) {

        if (text.toLowerCase().contains("disability")) {

            String[] lines = text.split("\n");

            for (String line : lines) {

                if (line.toLowerCase().contains("disability")) {
                    map.put("disabilityType", cleanValue(line));
                }

                if (line.matches(".*\\d+%.*")) {
                    map.put("disabilityPercentage",
                            line.replaceAll("[^0-9]", ""));
                }
            }
        }
    }

    private String cleanValue(String line) {
        return line.replaceAll("(?i)name|father|disability|:", "")
                .trim();
    }
}
