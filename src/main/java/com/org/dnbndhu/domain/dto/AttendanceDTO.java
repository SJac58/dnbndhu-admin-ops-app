package com.org.dnbndhu.domain.dto;

public class AttendanceDTO {

    private final int studentId;
    private final String fullName;
    private boolean absent;

    public AttendanceDTO(int studentId, String fullName) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.absent = false;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getFullName() {
        return fullName;
    }

    public boolean isAbsent() {
        return absent;
    }

    public void setAbsent(boolean absent) {
        this.absent = absent;
    }
}
