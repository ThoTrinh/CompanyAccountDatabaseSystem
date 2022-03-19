package jsp_azure_test;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class DataHandler {
	private Connection conn;

	// Initialize and save the database connection

	final static String url = "jdbc:sqlserver://trin0003-sql-server.database.windows.net:1433;database=cs-dsa-4513-sql-db;user=trin0003@trin0003-sql-server;password=****;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";

	private void getDBConnection() throws SQLException {
		if (conn != null) {
			return;
		}
		this.conn = DriverManager.getConnection(url);
	}

	// Return the result of selecting everything from the Customer table
	public ResultSet getAllCustomers() throws SQLException {
		getDBConnection(); // Prepare the database connection
		// Prepare the SQL statement
		final String sqlQuery = "SELECT * FROM Customer;";
		final PreparedStatement stmt = conn.prepareStatement(sqlQuery);
		// Execute the query
		return stmt.executeQuery();
	}

	// Return the result of selecting Customers with their category in the given range from Customer table
	public ResultSet retrieveCustomers(int lower_b, int upper_b) throws SQLException {
			getDBConnection(); // Prepare the database connection
			// Prepare the SQL statement
			final String sqlQuery = "SELECT name, category AS name FROM Customer" +
					"    WHERE category >= ? AND category <= ? ORDER BY 1 ;";
			final PreparedStatement stmt = conn.prepareStatement(sqlQuery);
			// Replace the '?' in the above statement with the given attribute values
			stmt.setInt(1, lower_b);
			stmt.setInt(2, upper_b);
			// Execute the query
			return stmt.executeQuery();
		}
	// Inserts a record into the Customer table with the given attribute values
	public boolean addCustomer( String name, String address, int category) throws SQLException {
		getDBConnection(); // Prepare the database connection
		// Prepare the SQL statement
		final String sqlQuery =
							"INSERT INTO Customer " +
									"(name, address, category) " +
										"VALUES " +
										"(?, ?, ?)";
		final PreparedStatement stmt = conn.prepareStatement(sqlQuery);
		// Replace the '?' in the above statement with the given attribute values
		stmt.setString(1, name);
		stmt.setString(2, address);
		stmt.setInt(3, category);
		// Execute the query; if only one record is updated, then we indicate success by returning true
		return stmt.executeUpdate() == 1;
	}
}
