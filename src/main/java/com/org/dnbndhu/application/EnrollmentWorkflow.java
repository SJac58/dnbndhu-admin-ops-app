package com.org.dnbndhu.application;

import com.org.dnbndhu.domain.model.FamilyDetails;
import com.org.dnbndhu.domain.model.Qualification;
import com.org.dnbndhu.domain.model.Student;
import com.org.dnbndhu.repository.*;

import java.util.List;

public class EnrollmentWorkflow {

    private final StudentRepository studentRepository = new StudentRepository();
    private final DocumentRepository documentRepository = new DocumentRepository();
    private final QualificationRepository qualificationRepository = new QualificationRepository();
    private final FamilyDetailsRepository familyDetailsRepository = new FamilyDetailsRepository();

    public void commitEnrollment(
            Student student,
            List<AcceptedDocument> acceptedDocuments,
            List<Qualification> qualifications,
            List<FamilyDetails> familyDetails
    ) {

        int studentId = studentRepository.save(student);

        for (AcceptedDocument doc : acceptedDocuments) {
            documentRepository.saveDocument(
                    studentId,
                    doc.getDocumentTypeId(),
                    doc.getFilePath(),
                    doc.getQualityScore(),
                    "PASS"
            );
        }

        for (Qualification q : qualifications) {
            q.setStudentId(studentId);
            qualificationRepository.save(q);
        }

        for (FamilyDetails f : familyDetails) {
            f.setStudentId(studentId);
            familyDetailsRepository.save(f);
        }

        System.out.println("✔ Full enrollment committed");
    }
}
