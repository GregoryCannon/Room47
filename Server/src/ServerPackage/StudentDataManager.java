package ServerPackage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Greg on 12/2/18.
 */
public class StudentDataManager {
    private Set<Student> students = new HashSet<>();

    private class Student{
        String fullName;
        String studentId;
    }
    public void readStudentData() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("file.txt"));
        try {
            StringBuilder sb = new StringBuilder();
            String line = "";

            while ((line = br.readLine()) != null) {
                addStudent(line);
            }
        } finally {
            br.close();
        }
    }

    private boolean addStudent(String rawLine){
        String[] chunks = rawLine.split("|");
        if (chunks.length != 2) return false;

        Student newStudent = new Student();
        newStudent.fullName = chunks[0];
        newStudent.studentId = chunks[1];
        students.add(newStudent);
        return true;
    }
}
