package org.tud.qmark.jouleunit.service.db;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.qualitune.jouleunit.EnergyProfile;
import org.qualitune.jouleunit.PowerRate;
import org.qualitune.jouleunit.coordinator.TestCaseProfile;
import org.qualitune.jouleunit.coordinator.TestSuiteProfile;

/**
 * Helper class to read and store APK files and profiling data from and to the
 * DB.
 * 
 * @author maquiz, Claas Wilke
 */
public class DbManager {

	/** Represents the different types a test run can have. */
	public enum TestRunType {
		FINISHED, FAILED, SCHEDULED, RUNNING
	};

	/** Connection URL to the DB. */
	private static String connectionURL = "jdbc:jtds:sqlserver://141.76.65.185:1433/QMark;tds=8.0;lastupdatecount=true";

	/** DB login. */
	private static String login = "jboss";

	/** DB Password. */
	private static String password = "tigermilk";

	/** The current DB {@link Connection} if any. */
	private Connection connection = null;

	/** The {@link PrintStream} used to log events of this {@link DbManager}. */
	private PrintStream console;

	/** The current {@link PreparedStatement} if any. */
	private PreparedStatement statement = null;

	// loading MySQL driver
	static {
		try {
			Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

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
	 * Helper method to load an APK file that shall be profiled from the DB.
	 * 
	 * @param id
	 *            The ID of the APK file (version) in the DB.
	 * @param APKtoSavelocal
	 *            The path to where the APK shall be saved in the local file
	 *            system.
	 */
	public void loadAutFromDB(int id, String APKtoSavelocal) {
		try {
			createConnection();

			StringBuilder query = new StringBuilder();
			query.append("SELECT content\n");
			query.append("FROM binarycontent AS b, Version AS vs\n");
			query.append("WHERE vs.versionID = ?\n");
			query.append("AND vs.apkBinaryID = b.binaryID\n");

			statement = connection.prepareStatement(query.toString());
			statement.setInt(1, id);

			ResultSet resultSet = statement.executeQuery();
			loadApkFromDB(APKtoSavelocal, resultSet);

			closeConnection();
		}

		catch (Exception e) {
			reportError("Error during loading AUT APK: " + e.getMessage());
			e.printStackTrace();

			try {
				closeConnection();
			}

			catch (Exception e2) {
				reportError("Error during loading AUT APK: " + e2.getMessage());
				e2.printStackTrace();
			}
		}
	}

	/**
	 * Helper method to load an APK file that shall be used as test suite from
	 * the DB.
	 * 
	 * @param id
	 *            The ID of the APK file (test suite) in the DB.
	 * @param APKtoSavelocal
	 *            The path to where the APK shall be saved in the local file
	 *            system.
	 */
	public void loadTestApkFromDB(int id, String APKtoSavelocal) {
		try {
			createConnection();

			StringBuilder query = new StringBuilder();
			query.append("SELECT content\n");
			query.append("FROM binarycontent AS b, testsuite AS ts\n");
			query.append("WHERE ts.testSuiteID = ?\n");
			query.append("AND ts.apkBinaryID = b.binaryID\n");

			statement = connection.prepareStatement(query.toString());
			statement.setInt(1, id);

			ResultSet resultSet = statement.executeQuery();
			loadApkFromDB(APKtoSavelocal, resultSet);
			// no else.

			closeConnection();
		}

		catch (Exception e) {
			reportError("Error during loading test APK: " + e.getMessage());
			e.printStackTrace();

			try {
				closeConnection();
			}

			catch (Exception e2) {
				reportError("Error during loading test APK: " + e2.getMessage());
				e2.printStackTrace();
			}
		}
	}

	/**
	 * Loads a {@link DbTestRun} from the DB identified by its ID.
	 * 
	 * @param id
	 *            The {@link DbTestRun}s ID.
	 */
	public DbTestRun loadTestRunfromDB(int id) {
		DbTestRun result = null;

		try {
			createConnection();

			StringBuilder query = new StringBuilder();
			query.append("SELECT tr.hwProfiling, tr.idleTime, vs.versionID, a.packageID, ts.testSuiteID, ts.packageID, tr.numberOfRuns, tr.testScript\n");
			query.append("FROM TestRun AS tr, Version AS vs, App as a, TestSuite AS ts\n");
			query.append("WHERE tr.testRunID = ?\n");
			query.append("AND tr.testSuiteID = ts.testSuiteID\n");
			query.append("AND ts.versionID = vs.versionID\n");
			query.append("AND vs.appID = a.appID\n");

			statement = connection.prepareStatement(query.toString());
			statement.setInt(1, id);

			ResultSet rs = statement.executeQuery();

			if (rs.next()) {
				boolean hwBooleanOn = rs.getInt(1) == 1;
				int idleTime = rs.getInt(2);
				int autID = rs.getInt(3);
				String autPackageID = rs.getString(4);
				int testApkID = rs.getInt(5);
				String testPackageID = rs.getString(6);
				int noOfRuns = rs.getInt(7);
				String testScript = rs.getString(8);

				result = new DbTestRun(id, hwBooleanOn, idleTime, autID,
						autPackageID, testApkID, testPackageID, noOfRuns,
						testScript);
			}

			else {
				reportError("Invalid test run id: " + id);
			}

			closeConnection();

		}

		catch (Exception e) {
			reportError("Error during loading TestRun data: " + e.getMessage());
			e.printStackTrace();

			try {
				closeConnection();
			}

			catch (Exception e2) {
				reportError("Error during loading TestRun data: "
						+ e2.getMessage());
				e2.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * Saves given {@link TestSuiteProfile} as the result of a {@link DbTestRun}
	 * in the DB.
	 * 
	 * @param testSuiteProfile
	 *            The {@link TestSuiteProfile} whose result data shall be saved.
	 * @param testRun
	 *            The {@link DbTestRun} the {@link TestSuiteProfile} belongs to.
	 */
	public void saveTestRunToDB(TestSuiteProfile testSuiteProfile,
			DbTestRun testRun) {
		try {

			createConnection();

			savePowerRatesToDB(testSuiteProfile, testRun.getTestRunID());
			saveTestDataToDB(testSuiteProfile, testRun.getTestRunID());

			if (testRun.isHardwareProfilingEnabled()) {
				if (testSuiteProfile.getCpuFrequencies().size() > 0)
					saveCpuDataToDB(testSuiteProfile, testRun.getTestRunID());
				// no else.

				if (testSuiteProfile.getLcdBrightness().size() > 0)
					saveDisplayDataToDB(testSuiteProfile,
							testRun.getTestRunID());
				// no else.

				if (testSuiteProfile.getWiFiTraffic().size() > 0)
					saveWiFiDataToDB(testSuiteProfile, testRun.getTestRunID());
				// no else.
			}
			// no else.
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				closeConnection();
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}
	}

	/**
	 * Sets a given {@link DbTestRun} as finished in the DB.
	 * 
	 * @param loggedConsole
	 *            The logged information from the console documenting the test
	 *            run.
	 * @param testRun
	 *            The {@link DbTestRun} the {@link TestSuiteProfile} belongs to.
	 */
	public void setTestRunFinishedInDB(String loggedConsole, DbTestRun testRun,
			TestRunType type) {
		try {
			createConnection();

			String query = "SELECT typeID FROM type WHERE name = '"
					+ type.toString().toLowerCase() + "TestRun" + "';";
			statement = connection.prepareStatement(query);
			ResultSet rs = statement.executeQuery();

			int typeID;
			if (rs.next())
				typeID = rs.getInt(1);
			else
				return;

			query = "UPDATE TestRun SET typeID = " + typeID + ", console = '"
					+ loggedConsole + "' WHERE testRunId = "
					+ testRun.getTestRunID() + ";";
			exportData(query);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				closeConnection();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	/**
	 * Helper method to create a {@link Connection} to the DB.
	 * 
	 * @throws Exception
	 */
	private void createConnection() throws Exception {
		try {
			connection = DriverManager.getConnection(connectionURL, login,
					password);
		}

		catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Helper method to create a query to insert the CPU data of a given
	 * {@link TestSuiteProfile} into the DB.
	 * 
	 * @param testSuiteProfile
	 *            The {@link TestSuiteProfile} whose CPU data be saved in the
	 *            CSV file.
	 * @param testRunID
	 *            The ID of the {@link DbTestRun} the {@link TestSuiteProfile}
	 *            belongs to.
	 * @return The created query.
	 */
	private String createCpuDataInserQuery(TestSuiteProfile testSuiteProfile,
			int testRunID) {

		StringBuilder query = new StringBuilder();

		Map<Long, Long[]> cpuFrequencies = testSuiteProfile.getCpuFrequencies();

		Iterator<Long> it = cpuFrequencies.keySet().iterator();

		while (it.hasNext()) {
			Long timestamp = it.next();
			Long frequencies[] = cpuFrequencies.get(timestamp);

			for (int index = 0; index < frequencies.length; index++) {
				query.append("INSERT INTO ResultCPU (frequence, [number], [time], testRunId) VALUES ");
				query.append("(" + frequencies[index] + ", ");
				query.append((index + 1) + ", ");
				query.append("(" + timestamp + " + "
						+ testSuiteProfile.getPutTimeStampOffset() + "), ");
				query.append(testRunID + ");\n");
			}
			// end for.
		}

		return query.toString();
	}

	/**
	 * Helper method to create a query to insert the display data of a given
	 * {@link TestSuiteProfile} ind the DB.
	 * 
	 * @param testSuiteProfile
	 *            The {@link TestSuiteProfile} whose display data be saved in
	 *            the DB.
	 * @param testRunID
	 *            The ID of the {@link DbTestRun} the {@link TestSuiteProfile}
	 *            belongs to.
	 * @return The created query as a {@link String}.
	 */
	private String createDisplayDataInsertQuery(
			TestSuiteProfile testSuiteProfile, int testRunID) {

		StringBuilder query = new StringBuilder();

		Iterator<Long> it = testSuiteProfile.getLcdBrightness().keySet()
				.iterator();

		while (it.hasNext()) {
			Long timestamp = it.next();

			query.append("INSERT INTO ResultDisplay (brightness, [time], testRunId) VALUES ");
			query.append("("
					+ testSuiteProfile.getLcdBrightness().get(timestamp) + ", ");
			query.append("(" + timestamp + " + "
					+ testSuiteProfile.getPutTimeStampOffset() + "), ");
			query.append(testRunID + ");\n");
		}
		// end while.

		return query.toString();
	}

	/**
	 * Helper method to create a query to insert the {@link TestCaseProfile}
	 * data into the DB.
	 * 
	 * @param testSuiteProfile
	 *            The {@link TestSuiteProfile} whose {@link TestCaseProfile}
	 *            shall be saved in the CSV file.
	 * @param testRunID
	 *            The ID of the {@link DbTestRun} the {@link TestSuiteProfile}
	 *            belongs to.
	 * @return The query as a String
	 */
	private String createTestCaseInsertQuery(TestSuiteProfile testSuiteProfile,
			int testRunID) {

		StringBuilder query = new StringBuilder();

		Iterator<TestCaseProfile> it = testSuiteProfile.getTestCaseProfiles()
				.iterator();

		while (it.hasNext()) {
			TestCaseProfile testCaseProfile = it.next();

			query.append("INSERT INTO TestCase (name, tag, result, [start], [stop], testRunId) VALUES ");
			query.append("('" + testCaseProfile.getId() + "', ");

			if (null != testCaseProfile.getTag())
				query.append("'" + testCaseProfile.getTag() + "', ");
			else
				query.append("NULL, ");

			query.append((testCaseProfile.isFailed() ? "0" : "1") + ", ");
			query.append("(" + testCaseProfile.getStartTime() + " + "
					+ testSuiteProfile.getPutTimeStampOffset() + "), ");
			query.append("(" + testCaseProfile.getEndTime() + " + "
					+ testSuiteProfile.getPutTimeStampOffset() + "), ");
			query.append(testRunID + ");\n");
		}
		// end for.

		return query.toString();
	}

	/**
	 * Helper method to create a query exporting the {@link PowerRate} data to
	 * the DB.
	 * 
	 * @param testSuiteProfile
	 *            The {@link TestSuiteProfile} whose {@link PowerRate}s shall be
	 *            saved in the DB.
	 * @param testRunID
	 *            The ID of the {@link DbTestRun} the {@link TestSuiteProfile}
	 *            belongs to.
	 * @return The created query.
	 */
	private String createPowerRateInsertQuery(
			TestSuiteProfile testSuiteProfile, int testRunID) {

		StringBuilder query = new StringBuilder();

		Iterator<PowerRate> it = testSuiteProfile
				.getEnergyProfile()
				.getSignificantValues(EnergyProfile.START_EVENT_ID,
						EnergyProfile.END_EVENT_ID).iterator();

		while (it.hasNext()) {
			PowerRate powerRate = it.next();

			query.append("INSERT INTO ResultPowerRate (power, [time], testRunId) VALUES ");
			query.append("(" + Math.round(powerRate.getPowerRate()) + ", ");
			query.append("(" + powerRate.getTimeStamp() + " / 1000000) + ("
					+ testSuiteProfile.getTrTimeStampOffsetFromNanos() + "), ");
			query.append(testRunID + ");\n");
		}
		// end for.

		return query.toString();
	}

	/**
	 * Helper method to create a query exporting the WiFi data of a given
	 * {@link TestSuiteProfile} to the DB.
	 * 
	 * @param testSuiteProfile
	 *            The {@link TestSuiteProfile} whose WiFi data shall be saved in
	 *            the DB.
	 * @param testRunID
	 *            The ID of the {@link DbTestRun} the {@link TestSuiteProfile}
	 *            belongs to.
	 * @return The created query as a {@link String}.
	 */
	private String createWiFiDataInsertQuery(TestSuiteProfile testSuiteProfile,
			int testRunID) {

		StringBuilder query = new StringBuilder();

		Iterator<Long> it = testSuiteProfile.getWiFiTraffic().keySet()
				.iterator();

		while (it.hasNext()) {
			Long timestamp = it.next();

			query.append("INSERT INTO ResultWifi (traffic, [time], testRunID) VALUES ");
			query.append("(" + testSuiteProfile.getWiFiTraffic().get(timestamp)
					+ ", ");
			query.append("(" + timestamp + " + "
					+ testSuiteProfile.getPutTimeStampOffset() + "), ");
			query.append(testRunID + ");\n");
		}

		return query.toString();
	}

	/**
	 * Helper method to close the current DB connection.
	 * 
	 * @throws Exception
	 */
	private void closeConnection() throws Exception {
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
	 * Helper method executing a query to export profiling data.
	 * 
	 * @param query
	 *            The query to be executed.
	 */
	private void exportData(String query) {

		try {
			statement = connection.prepareStatement(query);
			int s = statement.executeUpdate();

			if (s <= 0)
				reportError("Error during saving profiling data in the DB.");
			// no else.
		}

		catch (Exception e) {
			reportError("Error during export of profiling data: "
					+ e.getMessage());
			e.printStackTrace();

			try {
				closeConnection();
			}

			catch (Exception e2) {
				reportError("Error during export of profiling data: "
						+ e2.getMessage());
				e2.printStackTrace();
			}
		}
	}

	/**
	 * Helper method to load an APK file from the DB (by a given
	 * {@link ResultSet}).
	 * 
	 * @param APKtoSavelocal
	 *            The path where to save the APK in the local file system.
	 * @param resultSet
	 *            The {@link ResultSet}.
	 * @throws SQLException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void loadApkFromDB(String APKtoSavelocal, ResultSet resultSet)
			throws SQLException, FileNotFoundException, IOException {

		if (resultSet.next()) {

			File apk = new File(APKtoSavelocal);
			FileOutputStream fos = new FileOutputStream(apk);

			byte[] buffer = new byte[1];
			InputStream is = resultSet.getBinaryStream(1);

			while (null != is && is.read(buffer) > 0)
				fos.write(buffer);
			// end while.

			fos.close();

			reportProgress("Loaded APK to localhost");
		}
		// no else.
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

	/**
	 * Logs a progress message to the console of this {@link DbManager}.
	 * 
	 * @param msg
	 *            The message.
	 */
	private void reportProgress(String msg) {
		console.println(new Date().toString() + " - " + msg);
	}

	/**
	 * Helper method to save a query in a File.
	 */
	@SuppressWarnings("unused")
	private File saveQueryInFile(String query, String file) {

		try {
			File queryFile = new File(file);
			if (!queryFile.exists())
				queryFile.createNewFile();
			// no else.

			/* Dump query to the file. */
			try {
				FileWriter fileWriter = new FileWriter(queryFile);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter,
						1024);

				bufferedWriter.append(query);

				try {
					bufferedWriter.close();
					fileWriter.close();
				}

				catch (IOException e) {
					reportError("Closing of file failed.");
					e.printStackTrace();
				}
			}

			catch (IOException e) {
				reportError("Export into file failed.");
				e.printStackTrace();
				return null;
			}

			return queryFile;
		}

		catch (IOException e) {
			reportError("Creation of file failed.");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Saves the CPU data of a given {@link TestSuiteProfile} in the DB.
	 * 
	 * @param testSuiteProfile
	 *            The {@link TestSuiteProfile} whose CPU data shall be saved.
	 * @param testRunID
	 *            The ID of the {@link DbTestRun} the {@link TestSuiteProfile}
	 *            belongs to.
	 */
	private void saveCpuDataToDB(TestSuiteProfile testSuiteProfile,
			int testRunID) {

		reportProgress("Create CPU query ...");
		String query = createCpuDataInserQuery(testSuiteProfile, testRunID);
		reportProgress("Execute CPU query ...");
		exportData(query);
	}

	/**
	 * Saves the display data of a given {@link TestSuiteProfile} in the DB.
	 * 
	 * @param testSuiteProfile
	 *            The {@link TestSuiteProfile} whose display data shall be
	 *            saved.
	 * @param testRunID
	 *            The ID of the {@link DbTestRun} the {@link TestSuiteProfile}
	 *            belongs to.
	 */
	private void saveDisplayDataToDB(TestSuiteProfile testSuiteProfile,
			int testRunID) {

		reportProgress("Create display query ...");
		String query = createDisplayDataInsertQuery(testSuiteProfile, testRunID);
		reportProgress("Execute display query ...");
		exportData(query);
	}

	/**
	 * Saves the test cases of a given {@link TestSuiteProfile} in the DB.
	 * 
	 * @param testSuiteProfile
	 *            The {@link TestSuiteProfile} whose {@link TestCaseProfile}s
	 *            shall be saved.
	 * @param testRunID
	 *            The ID of the {@link DbTestRun} the {@link TestSuiteProfile}
	 *            belongs to.
	 */
	private void saveTestDataToDB(TestSuiteProfile testSuiteProfile,
			int testRunID) {
		reportProgress("Create test case query ...");
		if (testSuiteProfile.getTestCaseProfiles().size() > 0) {
			String query = createTestCaseInsertQuery(testSuiteProfile,
					testRunID);
			reportProgress("Execute test case query ...");
			exportData(query);
		} else {
			reportProgress("... no test cases for export found.");
		}
	}

	/**
	 * Saves the {@link PowerRate}s of a given {@link TestSuiteProfile} in the
	 * DB.
	 * 
	 * @param testSuiteProfile
	 *            The {@link TestSuiteProfile} whose {@link PowerRate}s shall be
	 *            saved.
	 * @param testRunID
	 *            The ID of the {@link DbTestRun} the {@link TestSuiteProfile}
	 *            belongs to.
	 */
	private void savePowerRatesToDB(TestSuiteProfile testSuiteProfile,
			int testRunID) {
		reportProgress("Create power rate query ...");
		String query = createPowerRateInsertQuery(testSuiteProfile, testRunID);
		reportProgress("Execute power rate query ...");
		exportData(query);
	}

	/**
	 * Saves the WiFi data of a given {@link TestSuiteProfile} in the DB.
	 * 
	 * @param testSuiteProfile
	 *            The {@link TestSuiteProfile} whose WiFi data shall be saved.
	 * @param testRunID
	 *            The ID of the {@link DbTestRun} the {@link TestSuiteProfile}
	 *            belongs to.
	 */
	private void saveWiFiDataToDB(TestSuiteProfile testSuiteProfile,
			int testRunID) {
		reportProgress("Create WiFi query ...");
		String query = createWiFiDataInsertQuery(testSuiteProfile, testRunID);
		reportProgress("Execute WiFi CPU query ...");
		exportData(query);
	}
}