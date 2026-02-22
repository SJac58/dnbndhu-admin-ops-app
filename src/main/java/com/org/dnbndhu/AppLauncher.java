package com.org.dnbndhu;
import com.org.dnbndhu.infrastructure.db.SchemaInitializer;
import com.org.dnbndhu.domain.model.Student;
import com.org.dnbndhu.application.EnrollmentWorkflow;
import com.org.dnbndhu.service.attendance.AttendanceService;
import com.org.dnbndhu.service.enrollment.DocumentPrecheckResult;
import com.org.dnbndhu.service.enrollment.DocumentProcessingService;
import com.org.dnbndhu.ui.MainApp;
import javafx.application.Application;

import java.util.HashMap;
import java.util.Map;

public class AppLauncher {

    public static void main(String[] args) {

       SchemaInitializer.init();

        Application.launch(MainApp.class, args);

    }
}
