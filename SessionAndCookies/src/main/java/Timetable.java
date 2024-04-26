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
public class Timetable  extends HttpServlet {
    // JDBC URL, username, and password of MariaDB server
    private static final String JDBC_URL = "jdbc:mariadb://localhost:3306/your_database_name";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve data from the form
        String[][] timetable = new String[2][7]; // Adjust the dimensions based on your table size
        for (int week = 0; week < 2; week++) {
            for (int day = 0; day < 7; day++) {
                String name = request.getParameter("name_" + week + "_" + day);
                timetable[week][day] = name != null ? name : "-";
            }
        }
        
        Connection conn = null;
        try {
            // Register JDBC driver
            Class.forName("org.mariadb.jdbc.Driver");
            
            // Open a connection
            conn = DriverManager.getConnection(JDBC_URL);
            
            // Now you can use this connection to execute SQL queries or updates
            // For example, you can insert timetable data into a table
            // Example:
            // PreparedStatement pstmt = conn.prepareStatement("INSERT INTO timetable (week, day, name) VALUES (?, ?, ?)");
            // pstmt.setInt(1, week);
            // pstmt.setInt(2, day);
            // pstmt.setString(3, name);
            // pstmt.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the connection
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
     
        response.sendRedirect("timetable.html");
    }
}
