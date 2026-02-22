package com.org.dnbndhu.service.ocr;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OCRFieldExtractorService {

    public Map<String, String> extractFields(String documentType, String text) {

        Map<String, String> fields = new HashMap<>();

        if (text == null || text.isBlank()) return fields;

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

        // Aadhaar number
        Matcher aadhaar = Pattern.compile("\\b\\d{4}\\s?\\d{4}\\s?\\d{4}\\b")
                .matcher(text);
        if (aadhaar.find()) {
            map.put("aadhaarNo", aadhaar.group().replaceAll("\\s", ""));
        }

        // DOB
        Matcher dob = Pattern.compile("\\b\\d{2}/\\d{2}/\\d{4}\\b")
                .matcher(text);
        if (dob.find()) {

            String dobStr = dob.group();
            map.put("dateOfBirth", dobStr);

            try {
                LocalDate birthDate =
                        LocalDate.parse(dobStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                int age = Period.between(birthDate, LocalDate.now()).getYears();
                map.put("age", String.valueOf(age));

            } catch (Exception ignored) {}
        }

        // Gender
        if (text.toLowerCase().contains("male")) {
            map.put("gender", "Male");
        } else if (text.toLowerCase().contains("female")) {
            map.put("gender", "Female");
        }

        // Name (very basic heuristic — usually first line without keywords)
        String[] lines = text.split("\n");
        for (String line : lines) {
            if (!line.toLowerCase().contains("government")
                    && !line.toLowerCase().contains("dob")
                    && !line.toLowerCase().contains("male")
                    && !line.toLowerCase().contains("female")
                    && line.trim().length() > 3) {

                map.putIfAbsent("fullName", line.trim());
                break;
            }
        }

        // Address
        for (String line : lines) {
            if (line.toLowerCase().contains("address")) {
                map.put("address",
                        line.replaceAll("(?i)address", "").trim());
            }
        }
    }

    // ================= PAN =================

    private void extractPan(Map<String, String> map, String text) {

        // PAN number
        Matcher pan = Pattern.compile("\\b[A-Z]{5}[0-9]{4}[A-Z]\\b")
                .matcher(text);
        if (pan.find()) {
            map.put("panNo", pan.group());
        }

        // DOB
        Matcher dob = Pattern.compile("\\b\\d{2}/\\d{2}/\\d{4}\\b")
                .matcher(text);
        if (dob.find()) {
            map.put("dateOfBirth", dob.group());
        }

        // Name
        String[] lines = text.split("\n");
        for (String line : lines) {
            if (!line.toLowerCase().contains("income")
                    && !line.toLowerCase().contains("tax")
                    && line.trim().length() > 3
                    && line.matches("^[A-Za-z ]+$")) {

                map.putIfAbsent("fullName", line.trim());
                break;
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

        String[] lines = text.split("\n");

        for (String line : lines) {

            if (line.toLowerCase().contains("disability")) {
                map.put("disabilityType",
                        line.replaceAll("(?i)disability|:", "").trim());
            }

            Matcher percent = Pattern.compile("\\b\\d{1,3}%\\b")
                    .matcher(line);

            if (percent.find()) {
                map.put("disabilityPercentage",
                        percent.group().replace("%", ""));
            }
        }
    }
}
