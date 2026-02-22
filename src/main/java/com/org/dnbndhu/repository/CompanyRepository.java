package com.org.dnbndhu.repository;

import com.org.dnbndhu.infrastructure.db.SQLiteConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyRepository {

    // =========================
    // SAVE COMPANY
    // =========================
    public int save(String companyName) {

        String sql = """
            INSERT INTO companies (company_name)
            VALUES (?)
        """;

        try (
                Connection conn = SQLiteConnectionManager.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {

            ps.setString(1, companyName);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save company", e);
        }

        return -1;
    }

    // =========================
    // FIND ALL COMPANIES
    // =========================
    public List<String> findAllNames() {

        String sql = "SELECT company_name FROM companies ORDER BY company_name";

        List<String> list = new ArrayList<>();

        try (
                Connection conn = SQLiteConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(rs.getString("company_name"));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch companies", e);
        }

        return list;
    }
}
