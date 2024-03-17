
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CheckoutItem
 */
@WebServlet("/checkout")
public class CheckoutItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static String url = "jdbc:mysql://ec2-52-14-191-24.us-east-2.compute.amazonaws.com:3306/TechExercise";
	static String user = "bschroeder_remote";
	static String password = "csci4830";
	static Connection connection = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CheckoutItem() {
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
		response.getWriter().append("Button worked.");
		// System.out.println("Button worked.");
		response.getWriter().append("Served at: ").append(request.getContextPath());

		// System.out.printf("Item ID: %s\nName: %s\n", itemID, userName);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// Retrieve form data
		String itemID = request.getParameter("item_id");
		String username = request.getParameter("user_name");
		//Establish database connection:
		SQLConnection();
		// Process checkout (e.g., update database)
		String checkoutMessage = SQLRequest(itemID, username);
		
		// Redirect back to the main page or display a confirmation message
		//doGet(request, response);
		// Write the response message
        PrintWriter out = response.getWriter();
        out.println("<html><head><title>Checkout Response</title></head><body>");
        out.println("<h2>Checkout Response</h2>");
        out.println("<p>" + checkoutMessage + "</p>");
        out.println("</body></html>");
        out.println("<br><button onclick=\"window.location.href='WebPage'\">Return to WebPage</button>");
	}

	private static String SQLRequest(String item, String name) {
		String query = "SELECT * FROM books WHERE book_id= ? AND is_checked_out = 0";
		String ret;
		try (PreparedStatement statement = connection.prepareStatement(query)) {
	        statement.setString(1, item);
	        ResultSet resultSet = statement.executeQuery();

	        if (resultSet.next()) {
	            // Item is not checked out, update the row
	            String updateQuery = "UPDATE books SET is_checked_out = 1, checked_out_by = ? WHERE book_id = ?";
	            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
	                updateStatement.setString(1, name);
	                updateStatement.setString(2, item);
	                updateStatement.executeUpdate();
	                ret = "Item checked out successfully.";
	            }
	        } else {
	            // Item is already checked out
	            // You can handle this case as needed
	        	ret = "Book is already checked out.";
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        ret = "An error occurred, check if your input was valid.";
	    }
		return ret;
	}

	private static void SQLConnection() {
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

}
