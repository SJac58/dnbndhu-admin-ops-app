package com.org.dnbndhu.repository;

import com.org.dnbndhu.domain.dto.StudentDTO;
import com.org.dnbndhu.domain.model.Student;
import com.org.dnbndhu.infrastructure.db.SQLiteConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentRepository {

    // ===============================
    // SAVE STUDENT
    // ===============================
    public int save(Student student) {

        String sql = """
            INSERT INTO students (
                full_name,
                date_of_birth,
                age,
                gender,
                disability_type,
                disability_percentage,
                marital_status,
                religion,
                caste,
                sub_caste,
                aadhaar_no,
                pan_no,
                email,
                phone,
                address,
                district,
                taluq,
                village,
                pin_code,
                referral_source,
                prior_training,
                batch_id
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = SQLiteConnectionManager.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, student.getFullName());
            ps.setString(2, student.getDateOfBirth());
            ps.setObject(3, student.getAge());
            ps.setString(4, student.getGender());
            ps.setString(5, student.getDisabilityType());
            ps.setObject(6, student.getDisabilityPercentage());
            ps.setString(7, student.getMaritalStatus());
            ps.setString(8, student.getReligion());
            ps.setString(9, student.getCaste());
            ps.setString(10, student.getSubCaste());
            ps.setString(11, student.getAadhaarNo());
            ps.setString(12, student.getPanNo());
            ps.setString(13, student.getEmail());
            ps.setString(14, student.getPhone());
            ps.setString(15, student.getAddress());
            ps.setString(16, student.getDistrict());
            ps.setString(17, student.getTaluq());
            ps.setString(18, student.getVillage());
            ps.setString(19, student.getPinCode());
            ps.setString(20, student.getReferralSource());
            ps.setObject(21, student.getPriorTraining());
            ps.setInt(22, student.getBatchId());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

            throw new RuntimeException("Student saved but ID not generated");

        } catch (Exception e) {
            throw new RuntimeException("Failed to save student", e);
        }
    }

    // ===============================
    // DASHBOARD VIEW
    // ===============================
    public List<StudentDTO> findAllWithStats(int batchId) {

        String sql = """
            SELECT s.student_id,
                   s.full_name,
                   s.email,
                   s.phone,
                   
                   IFNULL((
                       SELECT 
                           (SUM(CASE WHEN a.status='P' THEN 1 ELSE 0 END) * 100.0) /
                           COUNT(*)
                       FROM attendance a
                       WHERE a.student_id = s.student_id
                   ), 0) AS attendance_percentage,

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

    // ===============================
    // FIND BY ID
    // ===============================
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
                s.setEnrollmentDate(rs.getString("enrollment_timestamp"));
                s.setBatchId(rs.getInt("batch_id"));

                return s;
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch student", e);
        }

        return null;
    }

    // ===============================
    // ATTENDANCE %
    // ===============================
    public double calculateAttendancePercentage(int studentId) {

        String sql = """
            SELECT 
                CASE 
                    WHEN COUNT(*) = 0 THEN 0
                    ELSE (SUM(CASE WHEN status = 'P' THEN 1 ELSE 0 END) * 100.0) / COUNT(*)
                END AS attendance_percent
            FROM attendance
            WHERE student_id = ?
        """;

        try (Connection conn = SQLiteConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("attendance_percent");
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate attendance percentage", e);
        }

        return 0;
    }

    // ===============================
    // FIND BY BATCH
    // ===============================
    public List<Student> findByBatchId(int batchId) {

        String sql = "SELECT student_id, full_name FROM students WHERE batch_id = ?";

        List<Student> list = new ArrayList<>();

        try (Connection conn = SQLiteConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, batchId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Student s = new Student();
                s.setStudentId(rs.getInt("student_id"));
                s.setFullName(rs.getString("full_name"));
                list.add(s);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch students", e);
        }

        return list;
    }
}
