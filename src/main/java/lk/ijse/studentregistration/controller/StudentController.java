package lk.ijse.studentregistration.controller;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.studentregistration.data.StudentDTO;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static lk.ijse.studentregistration.util.UtilProcess.generateId;

@WebServlet(urlPatterns = "/Student")
public class StudentController extends HttpServlet {
    Connection connection;
    static String save_statement = "INSERT INTO student VALUES (?,?,?,?,?)";
    static String getStudent_statement = "SELECT * FROM student WHERE id=?";
    static String updateStudent_statement = "UPDATE student SET name=?, email=?, city=?, age=? WHERE id=?";
    static String deleteStudent_statement = "DELETE FROM student  WHERE id=?";

    @Override
    public void init() throws ServletException {
        try {
            System.out.println("Start");
            var driverClass = getServletContext().getInitParameter("driver-class");
            var dbURl = getServletContext().getInitParameter("dbURL");
            var dbUserName = getServletContext().getInitParameter("dbUserName");
            var dbPassword = getServletContext().getInitParameter("dbPassword");
            System.out.println(" after var");
            System.out.println(driverClass);
            Class.forName(driverClass);
            System.out.println(" after for name");
            // Establish a connection to the database
            this.connection = DriverManager.getConnection(dbURl, dbUserName, dbPassword);
            System.out.println("end");
            // Perform database operations here (e.g., store the connection as a servlet context attribute)

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //getStudent details
        StudentDTO dto = new StudentDTO();
        String id = req.getParameter("id");
        try(PrintWriter writer = resp.getWriter()){// best practice writer ek auto close wenw end unama
            PreparedStatement preparedStatement = connection.prepareStatement(getStudent_statement);
            preparedStatement.setString(1,id);
            ResultSet resultSet= preparedStatement.executeQuery();
            while (resultSet.next()){
                dto.setId(resultSet.getString(1));
                dto.setName(resultSet.getString(2));
                dto.setEmail(resultSet.getString(3));
                dto.setCity(resultSet.getString(4));
                dto.setAge(resultSet.getInt(5));
            }
            resp.setContentType("application/json");// json type response ekk enw kyl kynnn onima ne eth dana eka hodai
            System.out.println(dto);
            Jsonb jsonb = JsonbBuilder.create();// create json object
            jsonb.toJson(dto,resp.getWriter());// convert to json type (object , response eke writer)
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ;






    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //save student

        if (!req.getContentType().toLowerCase().startsWith("application/json") || req.getContentType() == null) {//using headers
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        }
 /*
        BufferedReader reader=req.getReader();
        PrintWriter writer = resp.getWriter();
        StringBuilder stringBuilder = new StringBuilder();
        reader.lines().forEach(line->stringBuilder.append(line+"\n"));
        System.out.println(stringBuilder.toString());
        writer.write(stringBuilder.toString());
        writer.close();*/

        //json manipulation with Parsan

       /* JsonReader reader = Json.createReader(req.getReader());
        //JsonObject jsonObject = reader.readObject();
        JsonArray jsonValues = reader.readArray();
        for (int i=0; i<jsonValues.size();i++){
            System.out.println(jsonValues.getJsonObject(i).getString("age"));
        }*/


        //String id = UUID.randomUUID().toString();  generate ids

        Jsonb jsonb = JsonbBuilder.create();// mulinma jsonBuilder ken jsonb type obeject ekk create krnw
        List<StudentDTO> studentList = jsonb.fromJson(req.getReader(), new ArrayList<StudentDTO>() {
        }.getClass().getGenericSuperclass());// req ewana json ek apita oni type ek deela ek bind krnw

        //studentDTO.setId(id);// anith ithuru id kyn property ekt me dan dena value ek dagannw

        for (StudentDTO student : studentList
        ) {
            String id =generateId();
            student.setId(id);
            System.out.println(student);


            try {
                PreparedStatement preparedStatement = connection.prepareStatement(save_statement);
                preparedStatement.setString(1,student.getId());
                preparedStatement.setString(2,student.getName());
                preparedStatement.setString(3,student.getEmail());
                preparedStatement.setString(4,student.getCity());
                preparedStatement.setInt(5,student.getAge());
                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


        }



    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //update student

        try(PrintWriter writer = resp.getWriter()) {
            StudentDTO dto = new StudentDTO();
            String id = req.getParameter("id");
            PreparedStatement preparedStatement = connection.prepareStatement(updateStudent_statement);
            Jsonb jsonb = JsonbBuilder.create();
            StudentDTO updateStudent = jsonb.fromJson(req.getReader(), StudentDTO.class);


            preparedStatement.setString(5,id);
            preparedStatement.setString(1,updateStudent.getName());
            preparedStatement.setString(2,updateStudent.getEmail());
            preparedStatement.setString(3,updateStudent.getCity());
            preparedStatement.setInt(4,updateStudent.getAge());
            int i = preparedStatement.executeUpdate();
            if(i!=0){
                writer.write("update student");
            }
            else {
                resp.sendError(HttpServletResponse.SC_NO_CONTENT);
            }

        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //delete student
        try(PrintWriter writer = resp.getWriter()) {
            String id = req.getParameter("id");
            PreparedStatement preparedStatement = connection.prepareStatement(deleteStudent_statement);
            Jsonb jsonb = JsonbBuilder.create();
            preparedStatement.setString(1,id);
            int i = preparedStatement.executeUpdate();
            if(i!=0){
               writer.write("delete student");
            }
            else {
                resp.sendError(HttpServletResponse.SC_NO_CONTENT);
            }

        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);

        }


    }
}
