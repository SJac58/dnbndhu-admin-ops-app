package com.org.dnbndhu.service.attendance;

import com.org.dnbndhu.repository.AttendanceRepository;
import com.org.dnbndhu.repository.StudentRepository;
import com.org.dnbndhu.service.notification.EmailNotificationService;
import com.org.dnbndhu.domain.model.Student;

import java.time.LocalDate;
import java.util.Map;

public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final EmailNotificationService notificationService;

    public AttendanceService() {
        this.attendanceRepository = new AttendanceRepository();
        this.studentRepository = new StudentRepository();
        this.notificationService = new EmailNotificationService();
    }

    // ================= SAVE ATTENDANCE =================
    public void saveAttendance(Map<Integer, Boolean> attendanceMap, LocalDate date) {

        String attendanceDate = date.toString();

        for (Map.Entry<Integer, Boolean> entry : attendanceMap.entrySet()) {

            int studentId = entry.getKey();
            boolean isAbsent = entry.getValue();

            String status = isAbsent ? "A" : "P";

            attendanceRepository.markAttendance(studentId, attendanceDate, status);

            if ("A".equals(status)) {

                int consecutiveAbsences =
                        attendanceRepository.countConsecutiveAbsences(studentId);

                if (consecutiveAbsences == 3
                        && !attendanceRepository.alreadySentToday(studentId)) {

                    Student student = studentRepository.findById(studentId);

                    if (student != null
                            && student.getEmail() != null
                            && !student.getEmail().isBlank()) {

                        String message = "Dear " + student.getFullName()
                                + ",\n\nYou have been absent for 3 consecutive days."
                                + "\nPlease contact the administration immediately."
                                + "\n\nRegards,\nDEENABANDHU Administration";

                        notificationService.sendEmail(
                                studentId,
                                student.getEmail(),
                                "⚠ Attendance Warning - 3 Consecutive Absences",
                                message
                        );
                    }
                }
            }
        }
    }

    public void saveAttendance(Map<Integer, Boolean> attendanceMap) {
        saveAttendance(attendanceMap, LocalDate.now());
    }

    // ================= ATTENDANCE PERCENTAGE =================
    public double calculateAttendancePercentage(int studentId) {
        return attendanceRepository.calculateAttendancePercentage(studentId);
    }

}