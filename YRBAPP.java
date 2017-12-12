import java.util.*;
import java.util.Date;
import java.net.*;
import java.text.*;
import java.lang.*;
import java.io.*;
import java.sql.*;


public class YRBAPP {

	Calendar calendar = Calendar.getInstance();
	Date now = calendar.getTime();
	Timestamp currentTimestamp = new Timestamp(now.getTime());

	TreeMap<Integer, String> c = new TreeMap<Integer, String>();
	TreeMap<Integer, String> b = new TreeMap<Integer, String>();
	TreeMap<Integer, String> d = new TreeMap<Integer, String>();
	Scanner in = new Scanner(System.in);
	private Connection conDB; // Connection to the database system.
	private String url; // URL: Which database?

	private Integer custID; // Who are we tallying?
	private String custName; // Name of that customer.
	private String chosencat;
	private String chosenstr;

	private int chosentitle;
	private String chosentitlestr;
	private String title;
	private double price;
	private int chosen;
	private int q;
	private String club;
	private int year;
	private String newName;
	private String newCity;

	// Constructor
	public YRBAPP() throws NumberFormatException {
		// Set up the DB connection.

		try {
			// Register the driver with DriverManager.
			Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (InstantiationException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.exit(0);
		}

		// URL: Which database?
		url = "jdbc:db2:c3421a";

		// Initialize the connection.
		try {
			// Connect with a fall-thru id & password
			conDB = DriverManager.getConnection(url);
		} catch (SQLException e) {
			System.out.print("\nSQL: database connection error.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Let's have autocommit turned off. No particular reason here.
		try {
			conDB.setAutoCommit(false);
		} catch (SQLException e) {
			System.out.print("\nFailed trying to turn autocommit off.\n");
			e.printStackTrace();
			System.exit(0);
		}

		// Who are we tallying?
		try {
			System.out.println("Enter your Customer ID: ");

			String custIDString = in.nextLine();
			while (!NumberCheck(custIDString)) {
				System.out.println("Input must be a number, try again");
				custIDString = in.nextLine();

			}
			custID = Integer.parseInt(custIDString);
		} catch (NumberFormatException e) {

			System.out.println("Provide an INT for the cust#.");

		}

		// Is this custID for real?
		while (!customerCheck()) {
			System.out.print("There is no customer id #");
			System.out.print(custID);
			System.out.println(" in the database. Try again");
			String custIDString2 = in.nextLine();
			while (!NumberCheck(custIDString2)) {
				System.out.println("Input must be a number, try again");
				custIDString2 = in.nextLine();

			}

			custID = Integer.parseInt(custIDString2);

		}

		System.out.println("Here is your info: ");
		CustInfo();

		System.out.println("\nDo you want to update your information ?" + " [yes|no]");
		String updateAnswer = in.nextLine();
		while (!YesOrNo(updateAnswer)) {
			System.out.println("Invalid input, either enter yes or no... Try again");
			System.out.println("\nDo you want to update your information ?" + " [yes|no]");

			updateAnswer = in.nextLine();

		}

		if (updateAnswer.equals("yes")) {

			System.out.println("\nEnter new name");
			newName = in.nextLine();
			System.out.println("\nEnter new city");
			newCity = in.nextLine();
			updateInfo();
		}

		System.out.println("\nSelect a Category # of your choice: ");

		// List of Categories
		Categories();

		try {
			System.out.println("Enter category #: ");
			chosenstr = in.nextLine();
			while (!NumberCheck(chosenstr)) {
				System.out.println("Input must be a number, try again");
				chosenstr = in.nextLine();

			}
			chosen = Integer.parseInt(chosenstr);
			chosencat = c.get(chosen);

		} catch (NumberFormatException e) {

			System.out.println("Provide an INT for the cust#.");

		}

		while (!CategoryNumberCheck()) {

			System.out.println("That category number is invalid, try again");
			chosenstr = in.nextLine();
			while (!NumberCheck(chosenstr)) {
				System.out.println("Input must be a number, try again");
				chosenstr = in.nextLine();

			}

			chosen = Integer.parseInt(chosenstr);
			chosencat = c.get(chosen);
		}

		try {
			System.out.println("Enter the title of the book you want to purchase: ");
			title = in.nextLine();
		} catch (NullPointerException e) {
			System.out.println("Invalid input!");

		}

		while (!bookCheck()) {

			System.out.println("\nThe title: ");
			System.out.print("'" + title + "'");
			System.out.println(" doesn't exist, Try again");
			try {
				System.out.println("Enter category #: ");
				chosenstr = in.nextLine();
				while (!NumberCheck(chosenstr)) {
					System.out.println("Input must be a number, try again");
					chosenstr = in.nextLine();

				}
				chosen = Integer.parseInt(chosenstr);
				chosencat = c.get(chosen);

			} catch (NumberFormatException e) {

				System.out.println("Provide an INT for the cust#.");

			}

			try {
				System.out.println("Enter the title of the book you want to purchase: ");
				title = in.nextLine();
			} catch (NullPointerException e) {
				System.out.println("Invalid input!");

			}

		}

		SelectBooks();

		try {
			System.out.println("Enter the Book # of your choice: ");
			chosentitlestr = in.nextLine();
			while (!NumberCheck(chosentitlestr)) {
				System.out.println("\nInput must be a number, try again\n");
				chosentitlestr = in.nextLine();

			}
			chosentitle = Integer.parseInt(chosentitlestr);
			title = b.get(chosentitle);

		} catch (NumberFormatException e) {

			System.out.println("Provide an INT for the cust#.");

		}

		while (!TitleNumberCheck()) {
			System.out.println("\nThat Book # doesn't exist, try again");
			try {
				System.out.println("\nEnter the Book # of your choice: ");
				chosentitlestr = in.nextLine();
				while (!NumberCheck(chosentitlestr)) {
					System.out.println("\nInput must be a number, try again\n");
					chosentitlestr = in.nextLine();

				}
				chosentitle = Integer.parseInt(chosentitlestr);
				title = b.get(chosentitle);

			} catch (NumberFormatException e) {

				System.out.println("Provide an INT for the cust#.");

			}

		}

		MinimumPrice();

		System.out.println("How many copies do you want to buy ?");
		String qstr = in.nextLine();
		while (!NumberCheck(qstr)) {
			System.out.println("Input must be a number, try again");
			qstr = in.nextLine();

		}
		q = Integer.parseInt(qstr);

		TotalPrice();

		System.out.println("\n Do you want to make this purchase ?" + " [yes|no]");
		String purchaseAnswer = in.nextLine();
		while (!YesOrNo(purchaseAnswer)) {
			System.out.println("Invalid input, either enter yes or no... Try again");
			System.out.println("\nDo you want to update your information ?" + " [yes|no]");

			purchaseAnswer = in.nextLine();

		}

		if (purchaseAnswer.equals("yes")) {

			InsertPurchase();
		}

		try {
			conDB.commit();
		} catch (SQLException e) {
			System.out.print("\nFailed trying to commit.\n");
			e.printStackTrace();
			System.exit(0);
		}
		// Close the connection.
		try {
			conDB.close();
		} catch (SQLException e) {
			System.out.print("\nFailed trying to close the connection.\n");
			e.printStackTrace();
			System.exit(0);
		}

	}

	public boolean NumberCheck(String str) {

		return str.matches("^[0-9]*$");
	}

	public boolean CategoryNumberCheck() {

		if (chosen > 12) {

			return false;

		}

		return true;

	}

	public boolean TitleNumberCheck() {

		if (!b.containsKey(chosentitle)) {

			return false;

		}

		return true;

	}

	public boolean customerCheck() {
		String queryText = ""; // The SQL text.
		PreparedStatement querySt = null; // The query handle.
		ResultSet answers = null; // A cursor.

		boolean inDB = false; // Return.

		queryText = "SELECT name       " + "FROM yrb_customer " + "WHERE cid = ?     ";

		// Prepare the query.
		try {
			querySt = conDB.prepareStatement(queryText);
		} catch (SQLException e) {
			System.out.println("Cust Check failed in prepare");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Execute the query.
		try {
			querySt.setInt(1, custID.intValue());
			answers = querySt.executeQuery();
		} catch (SQLException e) {
			System.out.println("Cust Check failed in execute");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Any answer?
		try {
			if (answers.next()) {
				inDB = true;
				custName = answers.getString("name");
			} else {
				inDB = false;
				custName = null;
			}
		} catch (SQLException e) {
			System.out.println("Cust Check failed in cursor.");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Close the cursor.
		try {
			answers.close();
		} catch (SQLException e) {
			System.out.print("Cust Check failed closing cursor.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

		// We're done with the handle.
		try {
			querySt.close();
		} catch (SQLException e) {
			System.out.print("Cust Check failed closing the handle.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

		return inDB;
	}

	public void CustInfo() {

		String queryText = ""; // The SQL text.
		PreparedStatement querySt = null; // The query handle.
		ResultSet answers = null; // A cursor.

		queryText = "SELECT cid, name, city" + "  FROM yrb_customer" + "  WHERE cid = ?";

		// Prepare the query.
		try {
			querySt = conDB.prepareStatement(queryText);
		} catch (SQLException e) {
			System.out.println("Cust info failed in prepare");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Execute the query.
		try {

			querySt.setInt(1, custID.intValue());

			answers = querySt.executeQuery();
		} catch (SQLException e) {
			System.out.println("Cust info failed in execute");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Variables to hold the column value(s).

		int cid;

		String name;
		String city;

		// Walk through the results and present them.
		try {

			if (answers.next()) {

				cid = answers.getInt("cid");
				name = answers.getString("name");
				city = answers.getString("city");

				System.out.print("\n" + "Customer ID: " + cid + "\n" + "Name: " + name + "\n" + "City: " + city);

			}

		} catch (SQLException e) {
			System.out.println("Cust info failed in cursor.");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Close the cursor.
		try {
			answers.close();
		} catch (SQLException e) {
			System.out.print("Cust info failed closing cursor.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

		// We're done with the handle.
		try {
			querySt.close();
		} catch (SQLException e) {
			System.out.print("Cust info failed closing the handle.\n");
			System.out.println(e.toString());
			System.exit(0);
		}
	}

	public void Categories() {
		String queryText = ""; // The SQL text.
		PreparedStatement querySt = null; // The query handle.
		ResultSet answers = null; // A cursor.

		queryText = "SELECT cat " + " FROM yrb_category";

		// Prepare the query.
		try {
			querySt = conDB.prepareStatement(queryText);
		} catch (SQLException e) {
			System.out.println("Cat list failed in prepare");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Execute the query.
		try {

			answers = querySt.executeQuery();
		} catch (SQLException e) {
			System.out.println("Cat list failed in execute");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Variables to hold the column value(s).
		String cats;

		// Walk through the results and present them.
		try {

			int count = 1;
			while (answers.next()) {
				cats = answers.getString("cat");
				c.put(count, cats);

				System.out.print("\n" + count + ". " + cats);

				count++;
			}
			System.out.println("\n");

		} catch (SQLException e) {
			System.out.println("Cat list failed in cursor.");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Close the cursor.
		try {
			answers.close();
		} catch (SQLException e) {
			System.out.print("Cat list failed closing cursor.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

		// We're done with the handle.
		try {
			querySt.close();
		} catch (SQLException e) {
			System.out.print("Cat list failed closing the handle.\n");
			System.out.println(e.toString());
			System.exit(0);
		}
	}

	public boolean YesOrNo(String str) {

		return str.matches("^(?:yes|no)$");

	}

	public boolean bookCheck() {
		String queryText = ""; // The SQL text.
		PreparedStatement querySt = null; // The query handle.
		ResultSet answers = null; // A cursor.

		boolean inDB = false; // Return.

		queryText = "SELECT b.title as title       " + "FROM yrb_book a, yrb_offer b "
				+ "WHERE b.title = ? and a.year = b.year";

		// Prepare the query.
		try {
			querySt = conDB.prepareStatement(queryText);
		} catch (SQLException e) {
			System.out.println("Book Check failed in prepare");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Execute the query.
		try {
			querySt.setString(1, title);
			answers = querySt.executeQuery();
		} catch (SQLException e) {
			System.out.println("Book Check failed in execute");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Any answer?
		try {
			if (answers.next()) {
				inDB = true;
				title = answers.getString("title");
			} else {
				inDB = false;
				custName = null;
			}
		} catch (SQLException e) {
			System.out.println("Book Check failed in cursor.");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Close the cursor.
		try {
			answers.close();
		} catch (SQLException e) {
			System.out.print("Book Check failed closing cursor.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

		// We're done with the handle.
		try {
			querySt.close();
		} catch (SQLException e) {
			System.out.print("Book Check failed closing the handle.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

		return inDB;
	}

	public void SelectBooks() {
		String queryText = ""; // The SQL text.
		PreparedStatement querySt = null; // The query handle.
		ResultSet answers = null; // A cursor.

		queryText = "SELECT distinct a.title, a.year, a.language, a.weight" + "  FROM yrb_book a, yrb_offer b"
				+ "  WHERE a.title = b.title and b.title = ? and a.year = b.year and a.cat = ? ";

		// Prepare the query.
		try {
			querySt = conDB.prepareStatement(queryText);
		} catch (SQLException e) {
			System.out.println("Book list failed in prepare");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Execute the query.
		try {
			querySt.setString(1, title);
			querySt.setString(2, chosencat);

			answers = querySt.executeQuery();
		} catch (SQLException e) {
			System.out.println("Book lsit failed in execute");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Variables to hold the column value(s).
		String book = "";

		String year;
		String language;
		int weight;
		int count2 = 1;
		int count3 = 0;

		// Walk through the results and present them.
		try {

			while (answers.next()) {

				book = answers.getString("title");
				year = answers.getString("year");
				language = answers.getString("language");
				weight = answers.getInt("weight");

				b.put(count2, book);
				count2++;
				count3 = count2 - 1;

				System.out.print("\n" + count3 + "." + "| Title: " + book + " | Year: " + year + " | Language: "
						+ language + " | Weight: " + weight + " |");

			}
			System.out.println("\n");

		} catch (SQLException e) {
			System.out.println("Book list failed in cursor.");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Close the cursor.
		try {
			answers.close();
		} catch (SQLException e) {
			System.out.print("Book list failed closing cursor.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

		// We're done with the handle.
		try {
			querySt.close();
		} catch (SQLException e) {
			System.out.print("Book list failed closing the handle.\n");
			System.out.println(e.toString());
			System.exit(0);
		}
	}

	public void MinimumPrice() {
		String queryText = ""; // The SQL text.
		PreparedStatement querySt = null; // The query handle.
		ResultSet answers = null; // A cursor.

		queryText = "SELECT a.cat, b.title, b.year, c.cid as stuff, min(b.price) as price "
				+ "  FROM yrb_book a, yrb_offer b, yrb_member c, yrb_customer d "
				+ "  WHERE a.title = b.title and a.year = b.year and a.cat = ? and b.title = ? and b.club = c.club and c.cid = d.cid and d.cid = ?"
				+ "  group by a.cat, b.title,b.year, c.cid ";

		// Prepare the query.
		try {
			querySt = conDB.prepareStatement(queryText);
		} catch (SQLException e) {
			System.out.println("min price failed in prepare");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Execute the query.
		try {

			querySt.setString(1, chosencat);
			querySt.setString(2, title);
			querySt.setInt(3, custID.intValue());

			answers = querySt.executeQuery();
		} catch (SQLException e) {
			System.out.println("min price failed in execute");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Variables to hold the column value(s).
		String book;

		int custy;
		String catty;

		// Walk through the results and present them.
		try {

			while (answers.next()) {

				price = answers.getDouble("price");
				book = answers.getString("title");
				year = answers.getInt("year");

				System.out.print("\n" + "'" + book + "'" + " Cost: $" + price);

			}
			System.out.println("\n");

		} catch (SQLException e) {
			System.out.println("min price failed in cursor.");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Close the cursor.
		try {
			answers.close();
		} catch (SQLException e) {
			System.out.print("min price failed closing cursor.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

		// We're done with the handle.
		try {
			querySt.close();
		} catch (SQLException e) {
			System.out.print("min price failed closing the handle.\n");
			System.out.println(e.toString());
			System.exit(0);
		}
	}

	public void TotalPrice() {
		String queryText = ""; // The SQL text.
		PreparedStatement querySt = null; // The query handle.
		ResultSet answers = null; // A cursor.

		queryText = "SELECT a.title, SUM( ? * b.price) as tprice, c.club "
				+ "  FROM yrb_book a, yrb_offer b, yrb_member c, yrb_customer d"
				+ "  WHERE a.title = b.title and  a.year = b.year and a.cat = ? and b.title = ? and b.club = c.club and c.cid = d.cid and d.cid = ? "
				+ "  group by a.title,b.price,c.club" + " having b.price = ?";

		// Prepare the query.
		try {
			querySt = conDB.prepareStatement(queryText);
		} catch (SQLException e) {
			System.out.println("totalprice failed in prepare");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Execute the query.
		try {

			querySt.setString(2, chosencat);
			querySt.setString(3, title);
			querySt.setInt(4, custID.intValue());
			querySt.setDouble(5, price);
			querySt.setInt(1, q);

			answers = querySt.executeQuery();
		} catch (SQLException e) {
			System.out.println("total price failed in execute");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Variables to hold the column value(s).

		String book;
		double tprice;

		// Walk through the results and present them.
		try {

			if (answers.next()) {

				book = answers.getString("title");
				tprice = answers.getDouble("tprice");
				club = answers.getString("club");

				System.out.printf("\nThe total price of the purchase:  $%.2f", tprice);

			}

			System.out.println("\n");

		} catch (SQLException e) {
			System.out.println("total price failed in cursor.");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Close the cursor.
		try {
			answers.close();
		} catch (SQLException e) {
			System.out.print("totalprice failed closing cursor.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

		// We're done with the handle.
		try {
			querySt.close();
		} catch (SQLException e) {
			System.out.print("total price failed closing the handle.\n");
			System.out.println(e.toString());
			System.exit(0);
		}
	}

	public void InsertPurchase() {
		String queryText = ""; // The SQL text.
		PreparedStatement querySt = null; // The query handle.

		queryText = "insert into yrb_purchase" + "(cid, club, title, year, when, qnty)"
				+ " values (? , ? , ?, ?, ?, ?)";
		// Prepare the query.
		try {
			querySt = conDB.prepareStatement(queryText);
		} catch (SQLException e) {
			System.out.println("inser failed in prepare");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Execute the query.
		try {

			querySt.setInt(1, custID.intValue());
			querySt.setString(2, club);
			querySt.setString(3, title);
			querySt.setInt(4, year);
			querySt.setTimestamp(5, currentTimestamp);
			querySt.setInt(6, q);

			querySt.executeUpdate();
			System.out
					.println("\n" + "New purchase of " + q + " copy(ies) of " + title + " made at " + currentTimestamp);
		} catch (SQLException e) {
			System.out.println("inser failed in execute");
			System.out.println(e.toString());
			System.exit(0);
		}

		try {
			querySt.close();
		} catch (SQLException e) {
			System.out.print("insert failed closing the handle.\n");
			System.out.println(e.toString());
			System.exit(0);
		}
	}

	public void updateInfo() {
		String queryText = ""; // The SQL text.
		PreparedStatement querySt = null; // The query handle.

		queryText = "update yrb_customer" + " set name = ?, city = ?" + " where cid = ?";
		// Prepare the query.
		try {
			querySt = conDB.prepareStatement(queryText);
		} catch (SQLException e) {
			System.out.println("insert failed in prepare");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Execute the query.
		try {

			querySt.setString(1, newName);
			querySt.setString(2, newCity);
			querySt.setInt(3, custID.intValue());

			querySt.executeUpdate();
			System.out.println("\n" + "Updated info: " + "\n" + "New Name: " + newName + "\n" + "New City: " + newCity);
		} catch (SQLException e) {
			System.out.println("insert failed in execute");
			System.out.println(e.toString());
			System.exit(0);
		}

		try {
			querySt.close();
		} catch (SQLException e) {
			System.out.print("insert failed closing the handle.\n");
			System.out.println(e.toString());
			System.exit(0);
		}
	}

	public static void main(String[] args) {
		YRBAPP ct = new YRBAPP();
	}
}
