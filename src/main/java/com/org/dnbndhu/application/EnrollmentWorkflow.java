package com.org.dnbndhu.application;

import com.org.dnbndhu.domain.model.Student;
import com.org.dnbndhu.repository.DocumentRepository;
import com.org.dnbndhu.repository.StudentRepository;

import java.util.List;

public class EnrollmentWorkflow {

    private final StudentRepository studentRepository = new StudentRepository();
    private final DocumentRepository documentRepository = new DocumentRepository();

    /**
     * FINAL COMMIT STEP
     */
    public void commitEnrollment(
            Student student,
            List<AcceptedDocument> acceptedDocuments
    ) {
        // 1. Save student
        int studentId = studentRepository.save(student);

        // 2. Save documents
        for (AcceptedDocument doc : acceptedDocuments) {
            documentRepository.saveDocument(
                    studentId,
                    doc.getDocumentTypeId(),
                    doc.getFilePath(),
                    doc.getQualityScore(),
                    "PASS"
            );
        }

        System.out.println("✔ Enrollment committed successfully");
    }
}
