package com.org.dnbndhu.domain.dto;

public class StudentDTO {

    private int studentId;
    private String fullName;
    private String email;
    private String phone;

    private double attendancePercentage;
    private String docsUploaded;

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
