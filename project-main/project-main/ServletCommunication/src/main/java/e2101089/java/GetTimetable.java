package e2101089.java;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@WebServlet("/GetTimetable")
public class GetTimetable extends HttpServlet {
    private Connection conn;
    private PreparedStatement ps;

    @Override
    public void init() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mariadb://mariadb.vamk.fi/e2101089_tweet", "e2101089", "xe6F9JEXT5j");
            System.out.println(conn);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
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

            HashMap<Integer, List<String>> timetable = new HashMap<Integer, List<String>>();



            while (rs.next()) {
                int week = rs.getInt("week");
                String name = rs.getString(day);
                List<String> children =
                        timetable.containsKey(week) ?
                            timetable.get(week) != null ?
                                timetable.get(week)
                                : new ArrayList<String>()
                            : new ArrayList<String>()
                        ;
                children.add(name);
                timetable.put(week, children);

                //out.println("<tr><td>" + week + "</td><td>" + name + "</td></tr>");
            }

            // Process the result set
            PrintWriter out = response.getWriter();
            out.println("<table style=\"width: 80%; margin: 20px auto; border-collapse: collapse; border: 2px solid #4CAF50; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\">");
            out.println("<tr><th style=\"padding: 10px; border-bottom: 2px solid #4CAF50;\">Week</th><th style=\"padding: 10px; border-bottom: 2px solid #4CAF50;\">" + day.substring(0, 1).toUpperCase() + day.substring(1) + "</th></tr>");

            for (int week : timetable.keySet()) {
                out.println("<tr><td style=\"padding: 10px; border-bottom: 2px solid #4CAF50;\">" + week + "</td>");
                out.println("<td style=\"padding: 10px; border-bottom: 2px solid #4CAF50;\">");
                for (String s : timetable.get(week)) {
                    out.println(s + "</br>");
                }
                out.println("</td></tr>");

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
