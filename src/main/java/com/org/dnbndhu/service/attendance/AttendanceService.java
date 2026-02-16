package com.org.dnbndhu.service.attendance;

import com.org.dnbndhu.repository.AttendanceRepository;

public class AttendanceService {

    private final AttendanceRepository attendanceRepository =
            new AttendanceRepository();

    public void markAttendance(int studentId, String date, String status) {

        // Step 1: Save attendance
        attendanceRepository.markAttendance(studentId, date, status);

        // Step 2: If absent, check absence streak
        if ("A".equals(status)) {
            int absences =
                    attendanceRepository.countConsecutiveAbsences(studentId);

            if (absences >= 3) {
                notifyStudent(studentId);
            }
        }
    }

    /**
     * Stub for now — WhatsApp later
     */
    private void notifyStudent(int studentId) {
        System.out.println(
                "⚠ Student " + studentId +
                        " absent for 3 consecutive days. Notification should be sent."
        );
    }
}
