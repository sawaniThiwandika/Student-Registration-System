package lk.ijse.studentregistration.persistance.impl;

import lk.ijse.studentregistration.data.StudentDTO;

import java.sql.Connection;
import java.util.ArrayList;

public interface Data {
    StudentDTO getStudent(String studentID, Connection connection);
    String saveStudent(ArrayList<StudentDTO> studentList, Connection connection);
    boolean updateStudent(StudentDTO studentDTO,Connection connection);
    boolean deleteStudent(String studentID,Connection connection);

}
