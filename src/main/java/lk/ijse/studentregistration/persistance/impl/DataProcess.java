package lk.ijse.studentregistration.persistance.impl;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import lk.ijse.studentregistration.data.StudentDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static lk.ijse.studentregistration.util.UtilProcess.generateId;

public class DataProcess implements Data {
    static String save_statement = "INSERT INTO student VALUES (?,?,?,?,?)";
    static String getStudent_statement = "SELECT * FROM student WHERE id=?";
    static String updateStudent_statement = "UPDATE student SET name=?, email=?, city=?, age=? WHERE id=?";
    static String deleteStudent_statement = "DELETE FROM student  WHERE id=?";

    @Override
    public StudentDTO getStudent(String studentID, Connection connection) {
        StudentDTO dto = new StudentDTO();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getStudent_statement);
            preparedStatement.setString(1, studentID);
            ResultSet resultSet = null;
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                dto.setId(resultSet.getString(1));
                dto.setName(resultSet.getString(2));
                dto.setEmail(resultSet.getString(3));
                dto.setCity(resultSet.getString(4));
                dto.setAge(resultSet.getInt(5));


            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println(dto);
        return dto;
    }

    @Override
    public String saveStudent(ArrayList<StudentDTO> studentList, Connection connection) {
        int i = 0;

        try {
            for (StudentDTO student : studentList) {
                String id = generateId();
                student.setId(id);
                System.out.println(student);

                PreparedStatement preparedStatement = connection.prepareStatement(save_statement);
                preparedStatement.setString(1, student.getId());
                preparedStatement.setString(2, student.getName());
                preparedStatement.setString(3, student.getEmail());
                preparedStatement.setString(4, student.getCity());
                preparedStatement.setInt(5, student.getAge());
                i = preparedStatement.executeUpdate();

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (i > 0) {
           return "Saved";
        }
        else {
            return "error";
        }

    }

    @Override
    public boolean updateStudent(StudentDTO studentDTO, Connection connection) {
        int i=0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(updateStudent_statement);



            preparedStatement.setString(5, studentDTO.getId());
            preparedStatement.setString(1,studentDTO.getName());
            preparedStatement.setString(2, studentDTO.getEmail());
            preparedStatement.setString(3, studentDTO.getCity());
            preparedStatement.setInt(4, studentDTO.getAge());
            i= preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException();
        }
        return i>0;
    }

    @Override
    public boolean deleteStudent(String studentID, Connection connection) {
        int i=0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(deleteStudent_statement);
            preparedStatement.setString(1,studentID);
            i= preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return i>0;

    }
}
