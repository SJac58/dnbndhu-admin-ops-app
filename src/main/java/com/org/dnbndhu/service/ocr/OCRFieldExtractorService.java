package com.org.dnbndhu.service.ocr;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OCRFieldExtractorService {

    public Map<String, String> extractFields(String documentType, String rawText) {

        Map<String, String> fields = new HashMap<>();

        if (rawText == null || rawText.isBlank()) return fields;

        String text = normalize(rawText);
        String[] lines = text.split("\n");

        switch (documentType.toUpperCase()) {

            case "AADHAR_CARD" -> extractAadhaar(fields, text, lines);
            case "PAN_CARD" -> extractPan(fields, text, lines);
            case "BANK_PASSBOOK" -> extractBank(fields, text);
            case "UDID_CARD" -> extractUdid(fields, text, lines);
        }
        if (fields.containsKey("dateOfBirth")) {
            calculateAge(fields, fields.get("dateOfBirth"));
        }
        return fields;
    }

    // =========================================================
    // NORMALIZATION
    // =========================================================

    private String normalize(String text) {

        text = text.replaceAll("\\r", "");
        text = text.replaceAll("\\t", " ");
        text = text.replaceAll(" +", " ");
        text = text.replace("D0B", "DOB");
        text = text.replace("D O B", "DOB");
        text = text.replace("0f", "of");

        return text.trim();
    }

    // =========================================================
    // AADHAAR CARD
    // =========================================================

    private void extractAadhaar(Map<String, String> map, String text, String[] lines) {

        // Aadhaar Number
        Matcher aadhaar = Pattern.compile("\\b\\d{4}\\s?\\d{4}\\s?\\d{4}\\b")
                .matcher(text);

        if (aadhaar.find()) {
            map.put("aadhaarNo", aadhaar.group().replaceAll("\\s", ""));
        }

        // DOB (Flexible)
        Matcher dob = Pattern.compile("(?i)DOB\\s*:?\\s*(\\d{2}/\\d{2}/\\d{4})")
                .matcher(text);

        if (dob.find()) {

            String dobStr = dob.group(1);
            map.put("dateOfBirth", dobStr);

            calculateAge(map, dobStr);
        }

        // Gender
        Matcher gender = Pattern.compile("\\b(MALE|FEMALE)\\b", Pattern.CASE_INSENSITIVE)
                .matcher(text);

        if (gender.find()) {
            map.put("gender", capitalize(gender.group()));
        }

        // -------- FIXED NAME LOGIC --------
        // Look above DOB line for probable name

        for (int i = 0; i < lines.length; i++) {

            if (lines[i].toLowerCase().contains("dob")) {

                for (int j = i - 1; j >= 0; j--) {

                    String candidate = lines[j]
                            .replaceAll("[^A-Za-z ]", "")
                            .trim();

                    if (candidate.length() >= 3
                            && !candidate.toLowerCase().contains("female")
                            && !candidate.toLowerCase().contains("male")) {

                        // Remove 1-letter garbage prefix like "g Anjali"
                        if (candidate.matches("^[A-Za-z]\\s+[A-Za-z]+.*")) {
                            candidate = candidate.substring(2);
                        }

                        map.put("fullName", candidate.trim());
                        break;
                    }
                }
                break;
            }
        }

        // Address
        boolean capture = false;
        StringBuilder addressBuilder = new StringBuilder();

        for (String line : lines) {

            if (line.toLowerCase().contains("address")) {
                capture = true;
                continue;
            }

            if (capture) {

                addressBuilder.append(line.trim()).append(" ");

                if (line.matches(".*\\b\\d{6}\\b.*")) {
                    break;
                }
            }
        }

        if (!addressBuilder.isEmpty()) {
            map.put("address", addressBuilder.toString().trim());
        }
    }

    // =========================================================
    // PAN CARD
    // =========================================================

    private void extractPan(Map<String, String> map, String text, String[] lines) {

        // PAN Number (Allow last char OCR mistake)
        Matcher pan = Pattern.compile("\\b[A-Z]{5}[0-9]{4}[A-Z0-9]\\b")
                .matcher(text);

        if (pan.find()) {
            map.put("panNo", pan.group());
        }

        // DOB
        Matcher dob = Pattern.compile("\\b\\d{2}/\\d{2}/\\d{4}\\b")
                .matcher(text);

        if (dob.find()) {
            String dobStr = dob.group();
            map.put("dateOfBirth", dobStr);
            calculateAge(map, dobStr);
        }

        // -------- FIXED NAME LOGIC --------
        for (int i = 0; i < lines.length; i++) {

            if (lines[i].matches("\\b[A-Z]{5}[0-9]{4}[A-Z0-9]\\b")) {

                for (int j = i + 1; j < lines.length; j++) {

                    String candidate = lines[j].trim();

                    if (candidate.isBlank()) continue;

                    if (!candidate.toLowerCase().contains("name")
                            && candidate.matches("^[A-Z ]{5,}$")) {

                        map.put("fullName", candidate);
                        break;
                    }
                }
                break;
            }
        }

        // Father's Name
        for (int i = 0; i < lines.length; i++) {

            if (lines[i].toLowerCase().contains("father")) {

                for (int j = i + 1; j < lines.length; j++) {

                    String candidate = lines[j].trim();

                    if (candidate.isBlank()) continue;

                    if (candidate.matches("^[A-Z ]{5,}$")) {
                        map.put("fatherName", candidate);
                        break;
                    }
                }
            }
        }
    }

    // =========================================================
    // BANK
    // =========================================================

    private void extractBank(Map<String, String> map, String text) {

        Matcher pincode = Pattern.compile("\\b\\d{6}\\b")
                .matcher(text);

        if (pincode.find()) {
            map.put("pinCode", pincode.group());
        }
    }

    // =========================================================
    // UDID CARD
    // =========================================================

    private void extractUdid(Map<String, String> map, String text, String[] lines) {

        // UDID Number
        Matcher udid = Pattern.compile("\\b[A-Z]{2}[0-9]{14}\\b")
                .matcher(text);

        if (udid.find()) {
            map.put("udidNo", udid.group());
        }

        // Disability Percentage (More flexible)
        Matcher percent = Pattern.compile("(\\d{1,3})\\s*%")
                .matcher(text);

        if (percent.find()) {
            map.put("disabilityPercentage", percent.group(1));
        }

        // Year of Birth
        Matcher yob = Pattern.compile("\\b(19|20)\\d{2}\\b")
                .matcher(text);

        if (yob.find()) {
            map.put("yearOfBirth", yob.group());
        }

        // Disability Type
        for (int i = 0; i < lines.length; i++) {

            if (lines[i].toLowerCase().contains("disability type")) {

                if (i + 1 < lines.length) {
                    map.put("disabilityType", lines[i + 1].trim());
                }
            }
        }

        // Name (More tolerant)
        for (int i = 0; i < lines.length; i++) {

            if (lines[i].toLowerCase().contains("name")) {

                for (int j = i + 1; j < lines.length; j++) {

                    String candidate = lines[j]
                            .replaceAll("[^A-Za-z ]", "")
                            .trim();

                    if (candidate.length() >= 3) {
                        map.put("fullName", candidate);
                        break;
                    }
                }
            }
        }
    }

    // =========================================================
    // AGE CALCULATION
    // =========================================================

    private void calculateAge(Map<String, String> map, String dobStr) {

        try {

            dobStr = dobStr.trim().replaceAll("[^0-9/]", "");

            LocalDate birthDate =
                    LocalDate.parse(dobStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            int age = Period.between(birthDate, LocalDate.now()).getYears();

            map.put("age", String.valueOf(age));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================================

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() +
                input.substring(1).toLowerCase();
    }
}