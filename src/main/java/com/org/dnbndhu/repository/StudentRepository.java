package com.org.dnbndhu.repository;

import com.org.dnbndhu.domain.dto.StudentDTO;
import com.org.dnbndhu.domain.model.Student;
import com.org.dnbndhu.infrastructure.db.SQLiteConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StudentRepository {

    public int save(Student student) {

        String sql = """
            INSERT INTO students
            (full_name, phone, gender, batch_id)
            VALUES (?, ?, ?, ?)
        """;

        try (
                Connection conn = SQLiteConnectionManager.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, student.getFullName());
            ps.setString(2, student.getPhone());
            ps.setString(3, student.getGender());
            ps.setInt(4, student.getBatchId());

            ps.executeUpdate();

            // 🔑 Get generated student_id
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int studentId = rs.getInt(1);
                System.out.println("✔ Student saved with ID: " + studentId);
                return studentId;
            }

            throw new RuntimeException("Student saved but ID not generated");

        } catch (Exception e) {
            throw new RuntimeException("Failed to save student", e);
        }
    }

    //for dashboard
    public List<StudentDTO> findAllWithStats(int batchId) {

        String sql = """
        SELECT s.student_id,
               s.full_name,
               s.email,
               s.phone,
               
               -- Attendance %
               IFNULL((
                   SELECT 
                       (SUM(CASE WHEN a.status='P' THEN 1 ELSE 0 END) * 100.0) /
                       COUNT(*)
                   FROM attendance a
                   WHERE a.student_id = s.student_id
               ), 0) AS attendance_percentage,

               -- Documents uploaded count
               (
                   SELECT COUNT(*)
                   FROM student_documents sd
                   WHERE sd.student_id = s.student_id
               ) || ' / ' || p.total_required_documents AS docs_uploaded

        FROM students s
        JOIN batches b ON s.batch_id = b.batch_id
        JOIN programs p ON b.program_id = p.program_id
        WHERE s.batch_id = ?
    """;

        List<StudentDTO> list = new ArrayList<>();

        try (Connection conn = SQLiteConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, batchId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new StudentDTO(
                        rs.getInt("student_id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getDouble("attendance_percentage"),
                        rs.getString("docs_uploaded")
                ));
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch students", e);
        }

        return list;
    }

    //for student profile
    public Student findById(int studentId) {

        String sql = "SELECT * FROM students WHERE student_id = ?";

        try (Connection conn = SQLiteConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Student s = new Student();

                s.setStudentId(rs.getInt("student_id"));
                s.setFullName(rs.getString("full_name"));
                s.setDateOfBirth(rs.getString("date_of_birth"));
                s.setAge(rs.getObject("age") != null ? rs.getInt("age") : null);
                s.setGender(rs.getString("gender"));
                s.setDisabilityType(rs.getString("disability_type"));
                s.setDisabilityPercentage(
                        rs.getObject("disability_percentage") != null
                                ? rs.getInt("disability_percentage")
                                : null
                );
                s.setMaritalStatus(rs.getString("marital_status"));
                s.setReligion(rs.getString("religion"));
                s.setCaste(rs.getString("caste"));
                s.setSubCaste(rs.getString("sub_caste"));
                s.setAadhaarNo(rs.getString("aadhaar_no"));
                s.setPanNo(rs.getString("pan_no"));
                s.setEmail(rs.getString("email"));
                s.setPhone(rs.getString("phone"));
                s.setAddress(rs.getString("address"));
                s.setDistrict(rs.getString("district"));
                s.setTaluq(rs.getString("taluq"));
                s.setVillage(rs.getString("village"));
                s.setPinCode(rs.getString("pin_code"));
                s.setReferralSource(rs.getString("referral_source"));
                s.setPriorTraining(
                        rs.getObject("prior_training") != null
                                ? rs.getInt("prior_training")
                                : null
                );
                s.setEnrollmentDate(rs.getString("enrollment_date"));
                s.setBatchId(rs.getInt("batch_id"));

                return s;
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch student", e);
        }

        return null;
    }


}
