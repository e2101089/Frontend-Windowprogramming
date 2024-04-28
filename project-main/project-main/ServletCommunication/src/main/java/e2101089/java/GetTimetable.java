package e2101089.java;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/GetTimetable")
public class GetTimetable extends HttpServlet {
    private Connection conn;
    private PreparedStatement ps;

    @Override
    public void init() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mariadb://mariadb.vamk.fi/e2101089_tweet", "e2101089", "hgMbbeTWqVC");
            System.out.println(conn);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the day parameter from the request
        String day = request.getParameter("day");

        // Validate the day parameter (optional)
        if (day == null || !day.matches("monday|tuesday|wednesday|thursday|friday|saturday|sunday")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid day parameter");
            return;
        }

        // Prepare and execute the SQL query
        try {
            String sql = "SELECT week, " + day + " FROM timetable";
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            // Process the result set
            PrintWriter out = response.getWriter();
            out.println("<table>");
            out.println("<tr><th>Week</th><th>" + day.substring(0, 1).toUpperCase() + day.substring(1) + "</th></tr>");
            while (rs.next()) {
                int week = rs.getInt("week");
                String name = rs.getString(day);
                out.println("<tr><td>" + week + "</td><td>" + name + "</td></tr>");
            }
            out.println("</table>");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
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