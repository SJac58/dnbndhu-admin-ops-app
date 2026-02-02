package com.org.dnbndhu;
import com.org.dnbndhu.infrastructure.db.SchemaInitializer;
import com.org.dnbndhu.domain.model.Student;
import com.org.dnbndhu.application.EnrollmentWorkflow;
import com.org.dnbndhu.service.enrollment.DocumentPrecheckResult;
import com.org.dnbndhu.service.enrollment.DocumentProcessingService;

import java.util.HashMap;
import java.util.Map;

public class AppLauncher {

    public static void main(String[] args) {

       // SchemaInitializer.init();

        DocumentProcessingService service = new DocumentProcessingService();

        DocumentPrecheckResult result =
                service.precheckDocument("C:/Users/saraj/Downloads/sample.jpg");

        if (!result.isPassed()) {
            System.out.println("Upload different document");
        } else {
            System.out.println("OCR Text: " + result.getExtractedText());
        }

    }
}
