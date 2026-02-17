package com.org.dnbndhu.domain.model;

public class FamilyDetails {

    private int studentId;
    private String memberName;
    private String relationship;
    private Double income;
    private String phone;

    // getters & setters
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }

    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }

    public Double getIncome() { return income; }
    public void setIncome(Double income) { this.income = income; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
