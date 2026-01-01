package com.org.dnbndhu.repository;

import com.org.dnbndhu.domain.model.Batch;
import com.org.dnbndhu.infrastructure.db.SQLiteConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BatchRepository {

    public void save(Batch batch) {

        String sql = """
            INSERT INTO batches
            (program_id, batch_name, start_date, end_date)
            VALUES (?, ?, ?, ?)
        """;

        try (
                Connection conn = SQLiteConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, batch.getProgramId());
            ps.setString(2, batch.getBatchName());
            ps.setString(3, batch.getStartDate());
            ps.setString(4, batch.getEndDate());

            ps.executeUpdate();

            System.out.println("✔ Batch created");

        } catch (Exception e) {
            throw new RuntimeException("Failed to create batch", e);
        }
    }

    public List<Batch> findAll() {

        List<Batch> batches = new ArrayList<>();

        String sql = "SELECT * FROM batches";

        try (
                Connection conn = SQLiteConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                Batch batch = new Batch();
                batch.setBatchId(rs.getInt("batch_id"));
                batch.setProgramId(rs.getInt("program_id"));
                batch.setBatchName(rs.getString("batch_name"));
                batch.setStartDate(rs.getString("start_date"));
                batch.setEndDate(rs.getString("end_date"));

                batches.add(batch);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch batches", e);
        }

        return batches;
    }
}

