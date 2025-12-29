PRAGMA foreign_keys = ON;

-- =========================
-- PROGRAMS & BATCHES
-- =========================
CREATE TABLE IF NOT EXISTS programs (
                                        program_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                        program_name TEXT NOT NULL,
                                        description TEXT,
                                        duration_months INTEGER,
                                        total_required_documents INTEGER
);

CREATE TABLE IF NOT EXISTS batches (
                                       batch_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                       program_id INTEGER NOT NULL,
                                       batch_name TEXT,
                                       start_date TEXT,
                                       end_date TEXT,
                                       FOREIGN KEY (program_id) REFERENCES programs(program_id)
    );

-- =========================
-- STUDENTS
-- =========================
CREATE TABLE IF NOT EXISTS students (
                                        student_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                        full_name TEXT NOT NULL,
                                        date_of_birth TEXT,
                                        age INTEGER,
                                        gender TEXT,

                                        disability_type TEXT,
                                        disability_percentage INTEGER,

                                        marital_status TEXT,
                                        religion TEXT,
                                        caste TEXT,
                                        sub_caste TEXT,

                                        aadhaar_no TEXT UNIQUE,
                                        pan_no TEXT UNIQUE,

                                        email TEXT,
                                        phone TEXT,

                                        address TEXT,
                                        district TEXT,
                                        taluq TEXT,
                                        village TEXT,
                                        pin_code TEXT,

                                        referral_source TEXT,
                                        prior_training INTEGER CHECK (prior_training IN (0,1)),

                                        enrollment_timestamp TEXT NOT NULL DEFAULT (CURRENT_TIMESTAMP),
                                        batch_id INTEGER NOT NULL,

    FOREIGN KEY (batch_id) REFERENCES batches(batch_id)
    );

-- =========================
-- QUALIFICATIONS
-- =========================
CREATE TABLE IF NOT EXISTS qualifications (
                                              qualification_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                              student_id INTEGER NOT NULL,
                                              education_level TEXT,
                                              institution TEXT,
                                              board TEXT,
                                              year_of_passing INTEGER,
                                              FOREIGN KEY (student_id) REFERENCES students(student_id)
    );

-- =========================
-- FAMILY DETAILS
-- =========================
CREATE TABLE IF NOT EXISTS family_details (
                                              family_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                              student_id INTEGER NOT NULL,
                                              member_name TEXT,
                                              relationship TEXT,
                                              income REAL,
                                              phone TEXT,
                                              FOREIGN KEY (student_id) REFERENCES students(student_id)
    );

-- =========================
-- DOCUMENT MANAGEMENT
-- =========================
CREATE TABLE IF NOT EXISTS document_types (
                                              document_type_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                              document_name TEXT NOT NULL,
                                              mandatory INTEGER CHECK (mandatory IN (0,1))
    );

CREATE TABLE IF NOT EXISTS student_documents (
                                                 document_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                                 student_id INTEGER NOT NULL,
                                                 enrollment_id INTEGER,

                                                 document_type_id INTEGER NOT NULL,

                                                 file_name TEXT NOT NULL,
                                                 file_path TEXT NOT NULL,

                                                 uploaded_at TEXT DEFAULT CURRENT_TIMESTAMP,

                                                 quality_score REAL,
                                                 quality_status TEXT,
                                                 verified INTEGER DEFAULT 0 CHECK (verified IN (0,1)),
                                                 verification_date TEXT,

                                                 FOREIGN KEY (student_id)
                                                     REFERENCES students(student_id)
                                                     ON DELETE CASCADE,

                                                 FOREIGN KEY (enrollment_id)
                                                     REFERENCES enrollment(enrollment_id)
                                                     ON DELETE SET NULL,

                                                 FOREIGN KEY (document_type_id)
                                                     REFERENCES document_types(document_type_id)
                                                     ON DELETE RESTRICT
);

-- =========================
-- ATTENDANCE
-- =========================
CREATE TABLE IF NOT EXISTS attendance (
                                          attendance_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                          student_id INTEGER NOT NULL,
                                          attendance_date TEXT NOT NULL,
                                          status TEXT CHECK(status IN ('P','A')),
    UNIQUE(student_id, attendance_date),
    FOREIGN KEY (student_id) REFERENCES students(student_id)
    );

-- =========================
-- PLACEMENT & NOTIFICATIONS
-- =========================
CREATE TABLE IF NOT EXISTS companies (
                                         company_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                         company_name TEXT,
                                         contact_person TEXT,
                                         contact_phone TEXT
);

CREATE TABLE IF NOT EXISTS placements (
                                          placement_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                          company_id INTEGER,
                                          job_role TEXT,
                                          description TEXT,
                                          posted_date TEXT,
                                          FOREIGN KEY (company_id) REFERENCES companies(company_id)
    );

CREATE TABLE IF NOT EXISTS notifications (
                                             notification_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                             placement_id INTEGER,
                                             student_id INTEGER,
                                             message TEXT,
                                             sent_date TEXT,
                                             status TEXT,
                                             FOREIGN KEY (placement_id) REFERENCES placements(placement_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id)
    );

-- =========================
-- ANALYTICS VIEWS
-- =========================
CREATE VIEW IF NOT EXISTS student_document_status AS
SELECT
    s.student_id,
    COUNT(sd.document_id) AS submitted_docs,
    SUM(CASE WHEN sd.verified = 1 THEN 1 ELSE 0 END) AS verified_docs
FROM students s
         LEFT JOIN student_documents sd ON s.student_id = sd.student_id
GROUP BY s.student_id;

CREATE VIEW IF NOT EXISTS enrollment_stats AS
SELECT
    strftime('%Y-%m', enrollment_date) AS month,
    COUNT(*) AS enrolled_students
FROM students
GROUP BY month;
