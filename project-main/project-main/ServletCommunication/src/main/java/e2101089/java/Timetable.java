package e2101089.java;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/Timetable")
public class Timetable extends HttpServlet {
    private Connection conn;
    private PreparedStatement ps;

    @Override
    public void init() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mariadb://mariadb.vamk.fi/e2101089_tweet", "e2101089", "ZE7ht8ncXaN");
            String sql = "INSERT INTO timetable (week, monday, tuesday, wednesday, thursday, friday, saturday, sunday) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            System.out.println(conn);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve data from the form
        String[][] timetable = new String[2][7]; // Adjust the dimensions based on your table size
        for (int week = 0; week < 2; week++) {
            for (int day = 0; day < 7; day++) {
                String name = request.getParameter("name_" + week + "_" + day);
                timetable[week][day] = name != null ? name : "-";
            }
        }

        // Save the timetable data to the database
        try {
            // Loop through the timetable array and set parameters for the prepared statement
            for (int week = 0; week < 2; week++) {
                for (int day = 0; day < 7; day++) {
                    ps.setString(day + 2, timetable[week][day]); // day + 2 because week starts at index 0 and SQL column indexes start at 1
                }
                // Execute the INSERT statement
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        response.sendRedirect("index.html");
    }

    @Override
    public void destroy() {
        try {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
