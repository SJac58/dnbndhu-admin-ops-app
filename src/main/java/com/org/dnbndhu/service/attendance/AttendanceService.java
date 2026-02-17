package com.org.dnbndhu.service.attendance;

import com.org.dnbndhu.repository.AttendanceRepository;

import java.time.LocalDate;
import java.util.Map;

public class AttendanceService {

    private final AttendanceRepository repository = new AttendanceRepository();

    public void saveAttendance(Map<Integer, Boolean> attendanceMap) {

        String today = LocalDate.now().toString();

        for (Map.Entry<Integer, Boolean> entry : attendanceMap.entrySet()) {

            int studentId = entry.getKey();
            boolean isAbsent = entry.getValue();

            String status = isAbsent ? "A" : "P";

            repository.markAttendance(studentId, today, status);

            // Check 3 consecutive absences
            if (repository.countConsecutiveAbsences(studentId) >= 3) {
                System.out.println("⚠ Student ID " + studentId + " has 3 consecutive absences.");
                // Later to be done: trigger email notification here
            }
        }
    }
}
