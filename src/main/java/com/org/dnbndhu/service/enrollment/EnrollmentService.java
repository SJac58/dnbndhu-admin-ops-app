package com.org.dnbndhu.service.enrollment;

import com.org.dnbndhu.domain.dto.EnrollmentDraftDTO;
import com.org.dnbndhu.domain.model.FamilyDetails;
import com.org.dnbndhu.domain.model.Qualification;
import com.org.dnbndhu.domain.model.Student;
import com.org.dnbndhu.repository.DocumentRepository;
import com.org.dnbndhu.repository.FamilyDetailsRepository;
import com.org.dnbndhu.repository.QualificationRepository;
import com.org.dnbndhu.repository.StudentRepository;
import com.org.dnbndhu.service.imageqa.ImageQualityService;

import java.util.List;

public class EnrollmentService {

    private final StudentRepository studentRepository;
    private final QualificationRepository qualificationRepository;
    private final FamilyDetailsRepository familyDetailsRepository;
    private final DocumentRepository documentRepository;
    private final ImageQualityService imageQualityService;
    public EnrollmentService() {
        this.studentRepository = new StudentRepository();
        this.qualificationRepository = new QualificationRepository();
        this.familyDetailsRepository = new FamilyDetailsRepository();
        this.documentRepository = new DocumentRepository();
        this.imageQualityService = new ImageQualityService();
    }

    /**
     * Full enrollment workflow
     */
    public int enrollStudent(Student student,
                             List<Qualification> qualifications,
                             List<FamilyDetails> familyDetailsList,
                             EnrollmentDraftDTO draft){
        validateStudent(student);

        // 1️⃣ Save student
        int studentId = studentRepository.save(student);
        if (draft != null) {

            draft.getDocumentPaths().forEach((docType, path) -> {

                try {

                    int documentTypeId = mapDocumentType(docType);

                    // 🔍 Run Image Quality
                    double qualityScore =
                            imageQualityService.evaluateQuality(path);

                    String qualityStatus =
                            imageQualityService.getQualityStatus(qualityScore);

                    documentRepository.saveDocument(
                            studentId,
                            documentTypeId,
                            path,
                            qualityScore,
                            qualityStatus
                    );

                    System.out.println("Saved document: " + docType +
                            " Score: " + qualityScore +
                            " Status: " + qualityStatus);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        // 2️⃣ Save qualifications
        if (qualifications != null && !qualifications.isEmpty()) {
            for (Qualification q : qualifications) {
                q.setStudentId(studentId);
                qualificationRepository.save(q);
            }
        }

        // 3️⃣ Save family details
        if (familyDetailsList != null && !familyDetailsList.isEmpty()) {
            for (FamilyDetails f : familyDetailsList) {
                f.setStudentId(studentId);
                familyDetailsRepository.save(f);
            }
        }

        return studentId;
    }
    private int mapDocumentType(String docType) {

        return switch (docType) {
            case "PHOTO" -> 1;
            case "LIVE_PHOTO" -> 2;
            case "EDUCATION_CERTIFICATE" -> 3;
            case "UDID_CARD" -> 4;
            case "AADHAR_CARD" -> 5;
            case "PAN_CARD" -> 6;
            case "BANK_PASSBOOK" -> 7;
            case "MEDICAL_CERTIFICATE" -> 8;
            default -> throw new IllegalArgumentException("Unknown document type: " + docType);
        };
    }
    // ==========================
    // BASIC VALIDATION
    // ==========================
    private void validateStudent(Student student) {

        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }

        if (student.getFullName() == null || student.getFullName().isBlank()) {
            throw new IllegalArgumentException("Student name is required");
        }

        if (student.getBatchId() <= 0) {
            throw new IllegalArgumentException("Batch must be selected");
        }
    }

}
