package javaapplication1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dao {
	// instance fields
	static Connection connect = null;
	Statement statement = null;

	// constructor
	public Dao() {

	}

	public Connection getConnection() {
		// Setup the connection with the DB
		try {
			connect = DriverManager
					.getConnection("jdbc:mysql://www.papademas.net:3307/tickets?autoReconnect=true&useSSL=false"
							+ "&user=fp411&password=411");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connect;
	}

	// CRUD implementation

	public void createTables() {
		// variables for SQL Query table creations
		final String createTicketsTable = "CREATE TABLE ademu_tickets(ticket_id INT AUTO_INCREMENT PRIMARY KEY, ticket_issuer VARCHAR(30),ticket_description VARCHAR(200), start_date DATE, end_date DATE)	";
		final String createUsersTable = "CREATE TABLE ademu_users(uid INT AUTO_INCREMENT PRIMARY KEY, uname VARCHAR(30), upass VARCHAR(30), admin int)	";

		try {

			// execute queries to create tables

			statement = getConnection().createStatement();

			statement.executeUpdate(createTicketsTable);
			statement.executeUpdate(createUsersTable);
			System.out.println("Created tables in given database...");

			// end create table
			// close connection/statement object
			statement.close();
			connect.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// add users to user table
		addUsers();
	}

	public void addUsers() {
		// add list of users from userlist.csv file to users table

		// variables for SQL Query inserts
		String sql;

		Statement statement;
		BufferedReader br;
		List<List<String>> array = new ArrayList<>(); // list to hold (rows & cols)

		// read data from file
		try {
			br = new BufferedReader(new FileReader(new File("./userlist.csv")));

			String line;
			while ((line = br.readLine()) != null) {
				array.add(Arrays.asList(line.split(",")));
			}
		} catch (Exception e) {
			System.out.println("There was a problem loading the file");
		}

		try {

			// Setup the connection with the DB

			statement = getConnection().createStatement();

			// create loop to grab each array index containing a list of values
			// and PASS (insert) that data into your User table
			for (List<String> rowData : array) {

				// Change the sql code below into your users database
				sql = "insert into ademu_users(uname,upass,admin) " + "values('" + rowData.get(0) + "'," + " '"
						+ rowData.get(1) + "','" + rowData.get(2) + "');";
				statement.executeUpdate(sql);
			}
			System.out.println("Inserts completed in the given database...");

			// close statement object
			statement.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public int insertRecords(String ticket_name, String ticket_description) {
		int id = 0;
		try {
			statement = getConnection().createStatement();
			// Change the sql code below into your tickets database
			statement.executeUpdate("Insert into ademu_tickets" + "(ticket_issuer, ticket_description) values(" + " '"
					+ ticket_name + "','" + ticket_description + "')", Statement.RETURN_GENERATED_KEYS);

			// retrieve ticket id number newly auto generated upon record insertion
			ResultSet resultSet = null;
			resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				// retrieve first field in table
				id = resultSet.getInt(1);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;

	}

	public ResultSet readRecords() {
		ResultSet results = null;
		try {
			statement = connect.createStatement();
			// Change the sql code below into your tickets database
			results = statement.executeQuery("SELECT * FROM ademu_tickets");
			// connect.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return results;
	}

	// continue coding for updateRecords implementation
	public void updateRecords(String ticket_id, String ticket_description) {
		ResultSet rs = null;

		try {
			Statement statement = getConnection().createStatement();
			rs = statement.executeQuery("SELECT ticket_description FROM ademu_tickets WHERE ticket_id = " + ticket_id);
			getConnection().close();
			String Results = null;

			while (rs.next()) {
				Results = rs.getString("ticket_description");
			}

			PreparedStatement ps = getConnection()
					.prepareStatement("UPDATE ademu_tickets SET ticket_description = ? WHERE ticket_id = ?");

			String ticket_description2 = ticket_description;

			ps.setString(1, ticket_description2);
			ps.setString(2, ticket_id);
			ps.executeUpdate();
			ps.close();

		} catch (SQLException e2) {
			e2.printStackTrace();
		}
	}

	// continue coding for deleteRecords implementation
	public int deleteRecords(int id) {
		String deletes = null;

		try {
			Statement statement = getConnection().createStatement();
			deletes = "DELETE FROM ademu_tickets WHERE ticket_id= '" + id + "'";
			statement.executeUpdate(deletes);

		} catch (SQLException e3) {
			e3.printStackTrace();
		}
		return id;
	}

	public void closeRecords(String ticket_id) {
		ResultSet rs = null;

		try {
			Statement statement = getConnection().createStatement();
			rs = statement.executeQuery("SELECT ticket_description FROM ademu_tickets WHERE ticket_id = " + ticket_id);
			getConnection().close();
			String Results = null;

			PreparedStatement ps = getConnection()
					.prepareStatement("UPDATE ademu_tickets SET ticket_description = ? WHERE ticket_id = ?");

			String ticket_description = "Closed";

			ps.setString(1, ticket_description);
			ps.setString(2, ticket_id);
			ps.executeUpdate();
			ps.close();

		} catch (SQLException e4) {
			e4.printStackTrace();
		}
	}

}