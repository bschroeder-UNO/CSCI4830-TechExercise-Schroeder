import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class WebPage
 */
@WebServlet("/WebPage")
public class WebPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static String url = "jdbc:mysql://ec2-52-14-191-24.us-east-2.compute.amazonaws.com:3306/TechExercise";
	static String user = "bschroeder_remote";
	static String password = "csci4830";
	static Connection connection = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public WebPage() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String SQLStatement = "SELECT * FROM books";
		ArrayList<Object[]> tableData = new ArrayList<>();
		setConnection();
		response.setContentType("text/html;charset=UTF-8");
		try {
			ResultSet rs = SQLStatement(SQLStatement, connection);
			int columnCount = rs.getMetaData().getColumnCount();
			//System.out.printf("Column Count is %d\n", columnCount);
			while (rs.next()) {
				Object[] row = new Object[columnCount];
				for (int i = 0; i < columnCount; i++) {
					row[i] = rs.getObject(i + 1); // Note: ResultSet columns are 1-indexed
				}
				tableData.add(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String out = buildHtmlTable(tableData);
		response.getWriter().append(out);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	private void setConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver"); // old:com.mysql.jdbc.Driver
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your MySQL JDBC Driver?");
			e.printStackTrace();
		}
		// System.out.println("MySQL JDBC Driver Registered!");
		connection = null;
		try {
			connection = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
		}
		if (connection != null) {
			// System.out.println("You made it, take control your database now!");
		} else {
			System.out.println("Failed to make connection!");
		}
	}

	private static ResultSet SQLStatement(String str, Connection c) {
		ResultSet rs = null;
		try {
			PreparedStatement statement = c.prepareStatement(str);
			rs = statement.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	private String buildHtmlTable(ArrayList<Object[]> tableData) {
		StringBuilder html = new StringBuilder();
		html.append("<style>");
		html.append("table { border-collapse: collapse; }");
		html.append("th, td { border: 1px solid black; padding: 8px; text-align: center; }</style><table>");

		// Add table headers
		html.append("<tr>");
		html.append("<th>").append("Item ID").append("</th>");
		html.append("<th>").append("Book Name").append("</th>");
		html.append("<th>").append("Author").append("</th>");
		html.append("<th>").append("Item Status").append("</th>");
		html.append("<th>").append("Checked Out By").append("</th>");
		html.append("</tr>");

		// Add table rows
		for (Object[] row : tableData) {
	        html.append("<tr>");
	        for (int i = 0; i < row.length; i++) {
	            Object value = row[i];
	            if (i == row.length - 1 && value == null) {
	                html.append("<td>").append("N/A").append("</td>");
	            } else if (i == row.length - 2 && value instanceof Integer) {
	                html.append("<td>").append((Integer) value == 1 ? "Checked Out" : "Available").append("</td>");
	            } else {
	                html.append("<td>").append(value).append("</td>");
	            }
	        }
	        html.append("</tr>");
	    }

	    html.append("</table><br>");
	    // Add checkout form
	    html.append("<form method='post' action='checkout'>"); // Assuming 'checkout' is the servlet URL to handle the form submission
	    html.append("User Name: <input type='text' name='user_name'><br>");
	    html.append("Item ID: <input type='text' name='item_id'><br>");
	    html.append("<input type='submit' value='Check Out'>");
	    html.append("</form>");
	    return html.toString();
	}
}
