package com.org.dnbndhu.domain.model;

public class Qualification {

    private int studentId;
    private String educationLevel;
    private String institution;
    private String boardUniversity;
    private Integer yearOfPassing;

    // getters & setters
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getEducationLevel() { return educationLevel; }
    public void setEducationLevel(String educationLevel) { this.educationLevel = educationLevel; }

    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }

    public String getBoardUniversity() { return boardUniversity; }
    public void setBoardUniversity(String boardUniversity) { this.boardUniversity = boardUniversity; }

    public Integer getYearOfPassing() { return yearOfPassing; }
    public void setYearOfPassing(Integer yearOfPassing) { this.yearOfPassing = yearOfPassing; }
}
