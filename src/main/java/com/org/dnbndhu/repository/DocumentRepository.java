package com.org.dnbndhu.repository;
import com.org.dnbndhu.infrastructure.db.SQLiteConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class DocumentRepository {

    public void saveDocument(
            int studentId,
            int documentTypeId,
            String filePath,
            double qualityScore,
            String qualityStatus
    ) {

        String sql = """
            INSERT INTO student_documents
            (student_id, document_type_id, file_path, quality_score, quality_status)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (
                Connection conn = SQLiteConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, studentId);
            ps.setInt(2, documentTypeId);
            ps.setString(3, filePath);
            ps.setDouble(4, qualityScore);
            ps.setString(5, qualityStatus);

            ps.executeUpdate();

            System.out.println("✔ Document saved");

        } catch (Exception e) {
            throw new RuntimeException("Failed to save document", e);
        }
    }
}
