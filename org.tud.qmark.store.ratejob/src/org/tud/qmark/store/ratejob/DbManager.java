package org.tud.qmark.store.ratejob;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Helper class to read and store APK files and profiling data from and to the
 * DB.
 * 
 * @author maquiz, Claas Wilke
 */
public class DbManager {

	/** Connection URL to the DB. */
	private static String connectionURL = "jdbc:mysql://141.76.65.185:3306/qmark";

	/** DB login. */
	private static String login = "QMark";

	/** DB Password. */
	private static String password = "tigermilk";

	/** The current DB {@link Connection} if any. */
	private Connection connection = null;

	/** The {@link PrintStream} used to log events of this {@link DbManager}. */
	private PrintStream console;

	/** The current {@link PreparedStatement} if any. */
	private PreparedStatement statement = null;

	/**
	 * Creates a new {@link DbManager}.
	 * 
	 * @param console
	 *            The {@link PrintStream} used to log events of this
	 *            {@link DbManager}.
	 */
	public DbManager(PrintStream console) {
		this.console = console;
	}

	/**
	 * Fetches the package IDs of apps whose rates shall be updated from the DB
	 * (apps with oldest rate come first).
	 * 
	 * @param maxApps
	 *            The maximum number of apps to be fetched.
	 * @return A {@link List} of package IDs for apps to be updated.
	 */
	public List<String> getAppsForUpdate(int maxApps) {

		try {
			StringBuilder query = new StringBuilder();
			query.append("SELECT packageId\n");
			query.append("FROM app\n");
			query.append("ORDER BY ratingDate ASC \n");
			query.append("LIMIT 0 , ?\n");
			statement = connection.prepareStatement(query.toString());

			statement.setInt(1, maxApps);

			ResultSet rs = statement.executeQuery();
			List<String> result = new ArrayList<String>(maxApps);

			while (rs.next()) {
				result.add(rs.getString(1));
			}
			// end while.

			return result;
		}

		catch (Exception e) {
			reportError("Error during fetching of apps to be updated: "
					+ e.getMessage());
			return Collections.emptyList();
		}
	}

	/**
	 * Updates the ratings of a given set of apps.
	 * 
	 * @param rates
	 *            The rates of the apps to be updated identified by their
	 *            package ID.
	 */
	public void updateAppRates(Map<String, Float> rates) {

		try {
			StringBuilder query = new StringBuilder();

			query.append("UPDATE app\n");
			query.append("SET rating = ?,\n");
			query.append("    ratingDate = now()\n");
			query.append("WHERE packageId = ?");

			statement = connection.prepareStatement(query.toString());

			for (String appId : rates.keySet()) {
				statement.setFloat(1, rates.get(appId));
				statement.setString(2, appId);

				int status = statement.executeUpdate();

				if (status <= 0)
					reportError("Error during update of rate for package ID '"
							+ appId + "'.");
				// no else.
			}
			// end for.
		}

		catch (Exception e) {
			reportError("Error during update of app rates: " + e.getMessage());
		}
	}

	/**
	 * Helper method to create a {@link Connection} to the DB.
	 * 
	 * @throws Exception
	 */
	public void createConnection() throws Exception {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(connectionURL, login,
					password);
		}

		catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Helper method to close the current DB connection.
	 * 
	 * @throws Exception
	 */
	public void closeConnection() throws Exception {
		try {
			if (null != statement) {
				statement.close();
				statement = null;
			}
			// no else.

			if (null != connection) {
				connection.close();
				connection = null;
			}
			// no else.
		}

		catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Logs an error to the console of this {@link DbManager}.
	 * 
	 * @param msg
	 *            The error message.
	 */
	private void reportError(String msg) {
		console.println(new Date().toString() + " - Error: " + msg);
	}
}
