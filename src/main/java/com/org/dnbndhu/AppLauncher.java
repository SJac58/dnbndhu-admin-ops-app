package com.org.dnbndhu;
import com.org.dnbndhu.infrastructure.db.SchemaInitializer;
import com.org.dnbndhu.domain.model.Student;
import com.org.dnbndhu.repository.StudentRepository;

public class AppLauncher {

    public static void main(String[] args) {

        SchemaInitializer.init();

        Student student = new Student();
        student.setFullName("Test Student");
        student.setPhone("9999999999");
        student.setGender("M");
        student.setBatchId(1);

        new StudentRepository().save(student);

        System.out.println("Test insert done.");
    }
}
