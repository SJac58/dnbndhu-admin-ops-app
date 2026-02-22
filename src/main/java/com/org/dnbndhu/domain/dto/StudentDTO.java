package com.org.dnbndhu.domain.dto;

public class StudentDTO {

    private final int studentId;
    private final String fullName;
    private final String email;
    private final String phone;

    private final double attendancePercentage;
    private final String docsUploaded;

    public StudentDTO(int studentId,
                      String fullName,
                      String email,
                      String phone,
                      double attendancePercentage,
                      String docsUploaded) {

        this.studentId = studentId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.attendancePercentage = attendancePercentage;
        this.docsUploaded = docsUploaded;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public double getAttendance() {
        return attendancePercentage;
    }

    public String getDocsUploaded() {
        return docsUploaded;
    }
}
